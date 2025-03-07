package com.cardone.dscatalog.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cardone.dscatalog.dto.CategoryDTO;
import com.cardone.dscatalog.dto.ProductDTO;
import com.cardone.dscatalog.entities.Category;
import com.cardone.dscatalog.entities.Product;
import com.cardone.dscatalog.exceptions.DatabaseException;
import com.cardone.dscatalog.exceptions.ResourceNotFoundException;
import com.cardone.dscatalog.projection.ProductProjection;
import com.cardone.dscatalog.repositories.CategoryRepository;
import com.cardone.dscatalog.repositories.ProductRepository;
import com.cardone.dscatalog.util.Util;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(Pageable pageable, String name, String categoryIds) {

        List<Long> catIds = Arrays.asList();
        //Valida se foi informada categoria
        if (!categoryIds.equals("0")) {
            //Formata a lista de string em Long para passar para função de busca
            String[] vet = categoryIds.split(",");
            List<String> listCat = Arrays.asList(vet);
            catIds = listCat.stream().map(Long::parseLong).toList();
        }

        //Executa a consulta para buscar os produtos de acordo com os filtros
        Page<ProductProjection> list = repository.searchProducts(pageable, name, catIds);

        //Monta uma lista de Product Ids para buscar as categorias
        List<Long> productIds = list.map(e -> e.getId()).toList();

        //Busca as categorias dos produtos encontrados na busca acima
        List<Product> products = repository.searchProductsWithCategories(productIds);
        
        //Chamo a função para ordenar os produtos de acordo com o que foi passado na busca inicial
        products = Util.replace(list.getContent(), products);
        
        //Convertendo a lista de produtos para uma lista de ProductsDTO
        List<ProductDTO> result = products.stream().map(e -> new ProductDTO(e, e.getCategories())).toList();

        //Cria um novo objeto Page com a lista de produtos encontrados, variave list vem do retorno da busca inincial
        Page<ProductDTO> pageResult = new PageImpl<>(result, list.getPageable(), list.getTotalElements());
        return pageResult;
    }

    @Transactional(readOnly = true)
    public ProductDTO findByID(Long id) {

        Product result = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado!"));
        return new ProductDTO(result, result.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = new Product();
        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new ProductDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Produto não encontrado " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteProduct(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(Pageable pageable) {
        Page<Product> list = repository.findAll(pageable);
        return list.map(e -> new ProductDTO(e));
    }

    private void copyDtoToEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());

        entity.getCategories().clear();
        for (CategoryDTO catDto : dto.getCategories()) {
            Category cat = categoryRepository.getReferenceById(catDto.getId());
            entity.getCategories().add(cat);

        }
    }
}

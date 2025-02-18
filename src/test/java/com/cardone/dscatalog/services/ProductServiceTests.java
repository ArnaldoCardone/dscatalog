package com.cardone.dscatalog.services;


import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cardone.dscatalog.TestFactory;
import com.cardone.dscatalog.dto.ProductDTO;
import com.cardone.dscatalog.entities.Category;
import com.cardone.dscatalog.entities.Product;
import com.cardone.dscatalog.exceptions.DatabaseException;
import com.cardone.dscatalog.exceptions.ResourceNotFoundException;
import com.cardone.dscatalog.repositories.CategoryRepository;
import com.cardone.dscatalog.repositories.ProductRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private Long existingId ;
    private Long nonExistingId ;
    private Long dependentId;
    private PageImpl<Product> page;  //Para simular o retorno de um Pageable
    private Product product;
    private ProductDTO productDTO; 
    private Category category;

    @BeforeEach
    void setUp() throws Exception {
        //Os dados foram mockados, não estão cadastrados no banco, só para os testes unitários
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        product = TestFactory.createProduct(); //Cria um produto para ser utilizado nos testes
        productDTO = TestFactory.createProductDTO(); //Cria um produtoDTO para ser utilizado nos testes
        category = TestFactory.createCategory(); //Cria uma categoria para ser utilizada nos testes

        page = new PageImpl<>(List.of(product)); //Cria uma lista de produtos para simular o retorno de um Pageable
       
        //Configuração do comportamento do mockito para a exclusao de um produto
        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(existingId);

        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);

        Mockito.when(repository.existsById(dependentId)).thenReturn(true);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        //Simula o retorno de uma objeto paginado retornado pelo FindAll
        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        //Simula o comportamento da ação de salvar um produto
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        //Simula o comportamento da ação de buscar um produto por Id existente
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));

        //Simula o comportamento da ação de buscar um produto por Id inexistente
       // Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(nonExistingId);
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        //Simula o comportamento verificar se o produto existe com o id informando para alteração
        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.doThrow(EntityNotFoundException.class).when(repository).getReferenceById(nonExistingId);
        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);

    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        
        Assertions.assertDoesNotThrow(() -> {
            service.deleteProduct(existingId);
        });
        //Verifica se o deleById foi chamado uma vez
        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThorwExceptionWhenIdDoesNotExists() {
        
        Assertions.assertThrows(ResourceNotFoundException.class,() -> {
            service.deleteProduct(nonExistingId);
        });
    }

    @Test
    public void deleteShouldThorwDatabaseExceptionWhenIdDependentId() {
        
        Assertions.assertThrows(DatabaseException.class,() -> {
            service.deleteProduct(dependentId);
        });
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        //Chama o metodo de busca paginada
        Pageable pageable =PageRequest.of(0, 10);        
        Page<ProductDTO> result = service.findAllPaged(pageable);
        
        //Valida se o retorno não é nulo
        Assertions.assertNotNull(result);
        //Verifica se foi chamado pelo menos uma vez o findAll no Service
        Mockito.verify(repository, Mockito.times(1)).findAll((Pageable) ArgumentMatchers.any());
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        
        ProductDTO result = service.findByID(existingId);
        //Valida se o item existe na base de dados
        Assertions.assertNotNull(result);
        //Verifica se foi chamado pelo menos uma vez o findById no Service
        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void findByIdShoulThorwsResourceNotFoundWhenIdDoesNotExists() {
        
        Assertions.assertThrows(ResourceNotFoundException.class,() -> {
            ProductDTO result = service.findByID(nonExistingId);
        });
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {
        
        ProductDTO result = service.update(existingId, productDTO);
        //Valida se o item existe na base de dados
        Assertions.assertNotNull(result);
        //Verifica se foi chamado pelo menos uma vez o findById no Service
        
    }

    @Test
    public void updateShouldReturnEmptyWhenIdDoesNotExists() {
        
        Assertions.assertThrows(ResourceNotFoundException.class,() -> {
            
            ProductDTO result = service.update(nonExistingId, productDTO);
        });
        
        
    }
}

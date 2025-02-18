package com.cardone.dscatalog.services;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.cardone.dscatalog.dto.ProductDTO;
import com.cardone.dscatalog.exceptions.ResourceNotFoundException;
import com.cardone.dscatalog.repositories.ProductRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional //Garante que os dados serão independentes entre os testes, voltando sempre ao estado inicial da base
public class ProductServiceITTests {
    
    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private Long existingId ;
    private Long nonExistingId ;
    private Long countTotalProducts;


    @BeforeEach
    public void setUp() throws Exception {
    
         existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldThowsResourceNotFoudWhenIdDoesNotExists() {
        
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.deleteProduct(nonExistingId);
        });
        Assertions.assertEquals(countTotalProducts , repository.count());
    }

    @Test
    public void deleteShouldDeleteProductWhenIdExists() {
        service.deleteProduct(existingId);

        //Verifica se a quantidade de produtos no banco é igual a quantidade total de produtos inicial - 1
        Assertions.assertEquals(countTotalProducts - 1, repository.count());
    }

    @Test
    public void findAllPagedShouldReturnPageWhenPage0Size10() {
    
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAllPaged(pageRequest);

        //Verifica se a pagina possui elementos
        Assertions.assertFalse(result.isEmpty());
        //Verifica se é a pagina 0
        Assertions.assertEquals(0,result.getNumber());
        //Verifica se a quantidade de elementos na pagina é 10
        Assertions.assertEquals(10,result.getSize());
        //Verifica se a quantidade total de elementos é igual a quantidade total de produtos
        Assertions.assertEquals(countTotalProducts,result.getTotalElements());
    }

    @Test
    public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExists() {
    
        PageRequest pageRequest = PageRequest.of(50, 10);
        Page<ProductDTO> result = service.findAllPaged(pageRequest);

        //Verifica se a pagina possui elementos
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findAllPagedShouldSortedPageWhenSortByName() {
    
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
        Page<ProductDTO> result = service.findAllPaged(pageRequest);

        //Verifica se a pagina possui elementos recuperados
        Assertions.assertFalse(result.isEmpty());
        //Valida a ordenação dos elementos
        //Verifica se o primeiro elemento da pagina é igual a "Macbook Pro"
        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
        //Verifica se o segundo elemento da pagina é igual a "PC Gamer"
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
    }

   
}

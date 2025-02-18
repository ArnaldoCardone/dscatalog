package com.cardone.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.cardone.dscatalog.TestFactory;
import com.cardone.dscatalog.entities.Product;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;
    
    private Long existingId ;
    private Long nonExistingId ;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        
        repository.deleteById(existingId);

        Optional<Product> result = repository.findById(existingId);
        //Valida se existe o item excluido
        Assertions.assertFalse(result.isPresent());
        Assertions.assertTrue(result.isEmpty());
    }
    @Test
    public void deleteShouldDeleteObjectWhenIdNotExists() {
        
        repository.deleteById(nonExistingId);

        Optional<Product> result = repository.findById(nonExistingId);
        //Valida se existe o item excluido
        Assertions.assertFalse(result.isPresent());
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        Product product = TestFactory.createProduct();
        product.setId(null);

        product = repository.save(product);

        //Testa se salvou o produto e retornou um Id
        Assertions.assertNotNull(product.getId());
        //Como temos uma massa de 25 produtos, validamos se foi criado o proximo id (26)
        Assertions.assertEquals(countTotalProducts+1, product.getId());
    }
    @Test
    public void findByIdShouldReturnProductWhenIdExists() {
        
        Optional<Product> result = repository.findById(existingId);
        //Valida se o item existe na base de dados
        Assertions.assertTrue(result.isPresent());
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    public void findByIdShouldReturnEmptyWhenIdNotExists() {
        
        Optional<Product> result = repository.findById(nonExistingId);
        //Valida se a pesquisa trouxe um item vazio
        Assertions.assertFalse(result.isPresent());
        Assertions.assertTrue(result.isEmpty());
    }

}

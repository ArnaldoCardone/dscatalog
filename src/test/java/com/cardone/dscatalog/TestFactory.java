package com.cardone.dscatalog;

import com.cardone.dscatalog.dto.ProductDTO;
import com.cardone.dscatalog.entities.Category;
import com.cardone.dscatalog.entities.Product;


public class TestFactory {
    
    public static Product createProduct() {
        Product product = new Product(1L, "Phone", "Good Phone", 800.0, "https://img.com/img.png");
        product.getCategories().add(new Category(2L, "Electronics"));
        return product;
    }

    public static ProductDTO createProductDTO() {
        Product product = createProduct();
        return new ProductDTO(product, product.getCategories());
    }

    public static Category createCategory() {
        return new Category(1L, "Electronics");
    }
}

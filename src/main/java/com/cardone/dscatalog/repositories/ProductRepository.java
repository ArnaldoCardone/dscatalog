package com.cardone.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cardone.dscatalog.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
}

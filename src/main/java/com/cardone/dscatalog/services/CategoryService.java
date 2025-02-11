package com.cardone.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cardone.dscatalog.repositories.CategoryRepository;
import com.cardone.dscatalog.dto.CategoryDTO;
import com.cardone.dscatalog.entities.Category;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    public List<CategoryDTO> findAll() {

        List<Category> list = repository.findAll();
        return list.stream().map(e -> new CategoryDTO(e)).toList();
    }
    
}

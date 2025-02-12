package com.cardone.dscatalog.dto;

import com.cardone.dscatalog.entities.Category;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotBlank;


public class CategoryDTO {
    private Long id;

    @Size(min=3,max = 80, message = "Tamanho entre 3 e 80 caracteres.")
    @NotBlank(message = "Campo obrigatório!")
    private String name;
    
    public CategoryDTO() {
    }
    
    public CategoryDTO(Category entity) {
        id = entity.getId();
        name = entity.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
}

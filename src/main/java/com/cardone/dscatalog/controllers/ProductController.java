package com.cardone.dscatalog.controllers;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.cardone.dscatalog.dto.ProductDTO;
import com.cardone.dscatalog.services.ProductService;

@RestController
@RequestMapping(value = "/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping()
    public ResponseEntity<Page<ProductDTO>> findAll(Pageable pageable, 
                   @RequestParam(value="name", defaultValue="") String name, 
                   @RequestParam(value="categoryId", defaultValue="0") String categoryId) {
        //Parametros pageable: page, size, sort
        
        Page<ProductDTO> list = productService.findAllPaged(pageable, name, categoryId);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable(value ="id") Long id) {
        ProductDTO result = productService.findByID(id);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @PostMapping
    public ResponseEntity<ProductDTO> insertProduct(@Valid @RequestBody ProductDTO ProductDto) {
        ProductDTO result = productService.insert(ProductDto);
        //Monta a URL para acessar o recurso criado
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(uri).body(result);
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable(value ="id") Long id, @Valid @RequestBody ProductDTO ProductDto) {
        ProductDTO result = productService.update(id, ProductDto);
        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable(value ="id") Long id) {
         productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}

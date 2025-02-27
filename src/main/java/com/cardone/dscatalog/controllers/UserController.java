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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.cardone.dscatalog.dto.UserDTO;
import com.cardone.dscatalog.dto.UserInsertDTO;
import com.cardone.dscatalog.dto.UserUpdateDTO;
import com.cardone.dscatalog.services.UserService;

@RestController
@RequestMapping(value = "/users")
public class UserController {

     @Autowired
    private UserService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable) {
        //Parametros pageable: page, size, sort
        
        Page<UserDTO> list = service.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable(value ="id") Long id) {
        UserDTO result = service.findByID(id);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<UserDTO> insertUser(@Valid @RequestBody UserInsertDTO userInsertDTO) {
        UserDTO result = service.insert(userInsertDTO);
        //Monta a URL para acessar o recurso criado
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(uri).body(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateProduct(@PathVariable(value ="id") Long id, @Valid @RequestBody UserUpdateDTO dto) {
        UserDTO result = service.update(id, dto);
        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> deleteProduct(@PathVariable(value ="id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}

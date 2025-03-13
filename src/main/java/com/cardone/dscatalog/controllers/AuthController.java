package com.cardone.dscatalog.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cardone.dscatalog.dto.EmailDTO;
import com.cardone.dscatalog.dto.NewPasswordDTO;
import com.cardone.dscatalog.services.AuthService;


@RestController
@RequestMapping(value = "/auth")
public class AuthController {

     @Autowired
    private AuthService service;

    @GetMapping(value = "/recover-token")
    public ResponseEntity<Void> createRecoveryToken(@Valid @RequestBody EmailDTO dto) {
        service.createRecoveryToken(dto);
        
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/new-password")
    public ResponseEntity<Void> saveNewPassword(@Valid @RequestBody NewPasswordDTO dto) {
        service.saveNewPassword(dto);
        
        return ResponseEntity.noContent().build();
    }
    
}

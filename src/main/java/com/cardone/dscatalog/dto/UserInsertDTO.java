package com.cardone.dscatalog.dto;

import com.cardone.dscatalog.services.validation.UserInsertValid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@UserInsertValid
public class UserInsertDTO extends UserDTO{

    //Possibilidade de validar utilizando uma expressão regular
    //@Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message="A senha deve conter pelo menos uma letra maiúscula, uma minúscula e um dígito")
    @NotBlank(message = "Campo obrigatório")
    @Size(message="A senha deve ter no mínimo 8 caracteres", min=8)
    private String password;

    public UserInsertDTO() {
        super();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
}

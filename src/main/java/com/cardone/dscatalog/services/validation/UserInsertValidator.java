package com.cardone.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cardone.dscatalog.controllers.exceptions.FieldMessage;
import com.cardone.dscatalog.dto.UserInsertDTO;
import com.cardone.dscatalog.entities.User;
import com.cardone.dscatalog.repositories.UserRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {
	
    @Autowired
    private UserRepository repository;

	@Override
	public void initialize(UserInsertValid ann) {
	}

	@Override
	public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {
		
		List<FieldMessage> list = new ArrayList<>();
		
		//Busca se o email já existe, caso retorne Null inclui o erro na lista
        User user = repository.findByEmail(dto.getEmail());
		if (user != null) {
			list.add(new FieldMessage("email", "Email já existe"));
		}

		//Percorre a lista e inclui os itens na lista de erros do framework (BeansValidation)
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		// Se a lista de erros estiver vazia, significa que não houve erro de validação
		return list.isEmpty();
	}
}

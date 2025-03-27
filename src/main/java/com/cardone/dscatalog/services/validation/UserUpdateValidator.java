package com.cardone.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.cardone.dscatalog.controllers.exceptions.FieldMessage;
import com.cardone.dscatalog.dto.UserUpdateDTO;
import com.cardone.dscatalog.entities.User;
import com.cardone.dscatalog.repositories.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {
	
	@Autowired
	private HttpServletRequest request;

    @Autowired
    private UserRepository repository;

	@Override
	public void initialize(UserUpdateValid ann) {
	}

	@Override
	public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {
		
		// Recupera o PathVariable da requisição
		@SuppressWarnings("unchecked")
		var uriVars = (Map<String,String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		long userId = Long.parseLong(uriVars.get("id"));

		List<FieldMessage> list = new ArrayList<>();
		
		//Busca se o email já existe e se o usuário é o mesmo que veio na requisção, caso retorne Null inclui o erro na lista
        User user = repository.findByEmail(dto.getEmail());
		if (user != null && userId != user.getId()) {
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

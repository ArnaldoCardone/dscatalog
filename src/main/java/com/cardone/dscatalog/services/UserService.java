package com.cardone.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cardone.dscatalog.dto.RolesDTO;
import com.cardone.dscatalog.dto.UserDTO;
import com.cardone.dscatalog.dto.UserInsertDTO;
import com.cardone.dscatalog.entities.Roles;
import com.cardone.dscatalog.entities.User;
import com.cardone.dscatalog.exceptions.DatabaseException;
import com.cardone.dscatalog.exceptions.ResourceNotFoundException;
import com.cardone.dscatalog.repositories.RoleRepository;
import com.cardone.dscatalog.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
     @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        List<User> list = repository.findAll();
        return list.stream().map(e -> new UserDTO(e)).toList();
    }

    @Transactional(readOnly = true)
    public UserDTO findByID(Long id) {

        User result = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario não encontrado!"));
        return new UserDTO(result);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User entity = new User();
        copyDtoToEntity(dto,entity);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity = repository.save(entity);
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        try {
            User entity = repository.getReferenceById(id);
            copyDtoToEntity(dto,entity);
            entity = repository.save(entity);
            return new UserDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Usuario não encontrado " + id);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario não encontrado " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable) {
        Page<User> list = repository.findAll(pageable);
        return list.map(e -> new UserDTO(e));
    }

    private void copyDtoToEntity(UserDTO dto, User entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        //entity.setPassword(password);

        entity.getRoles().clear();
        for (RolesDTO rolesDto : dto.getRoles()) {
            Roles role = roleRepository.getReferenceById(rolesDto.getId());
            entity.getRoles().add(role);
        }
    }
}

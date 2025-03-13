package com.cardone.dscatalog.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cardone.dscatalog.dto.EmailDTO;
import com.cardone.dscatalog.dto.NewPasswordDTO;
import com.cardone.dscatalog.entities.PasswordRecover;
import com.cardone.dscatalog.entities.User;
import com.cardone.dscatalog.exceptions.ResourceNotFoundException;
import com.cardone.dscatalog.repositories.PasswordRecoverRepository;
import com.cardone.dscatalog.repositories.UserRepository;

@Service
public class AuthService {

    //Recupera a variavel de ambiente do application.properties com a expiração do token 
    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;

    //Recupera a variavel de ambiente do application.properties com a expiração do token 
    @Value("${email.password-recover.uri}")
    private String urlChangePassword;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordRecoverRepository passwordRecoverRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void createRecoveryToken(EmailDTO dto) {
        //Valida se o email existe na base
        User user = userRepository.findByEmail(dto.getEmail());
        if (user == null) {
            throw new ResourceNotFoundException("Email não encontrado");
        }

        //Gerar um token e salvar no banco
        PasswordRecover entity = new PasswordRecover();
        entity.setEmail(dto.getEmail());
        entity.setToken(UUID.randomUUID().toString());  //Gera um token aleatório
        entity.setExpiration(Instant.now().plusSeconds(60 * tokenMinutes)); //seta a validade em 30 minutos
        entity = passwordRecoverRepository.save(entity);

        //Envia o email com a chave criada
        String sBody = "Clique no link para recuperar a senha\n\n "
                + urlChangePassword + entity.getToken()
                + "\n\nO link expira em " + tokenMinutes + " minutos";

        emailService.sendEmail(dto.getEmail(), "Recuperação de senha", sBody);

    }

    @Transactional
    public void saveNewPassword(NewPasswordDTO dto) {
        //Valida se o token existe na base
        List<PasswordRecover> result = passwordRecoverRepository.searchValidTokens(dto.getToken(), Instant.now());
        if (result.size() == 0) {
            throw new ResourceNotFoundException("Token inválido!");
        }

        //Busca o usuário associado ao  token e atualiza a senha do usuário
        User user = userRepository.findByEmail(result.get(0).getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));  //criptografa a senha antes de salvar
        userRepository.save(user);

        //Exclui o token
        passwordRecoverRepository.delete(result.get(0));
    }

    protected User authenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("user");
            return userRepository.findByEmail(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Invalid user");
        }
    }

}

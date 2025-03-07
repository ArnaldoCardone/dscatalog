package com.cardone.dscatalog.controllers;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cardone.dscatalog.TestFactory;
import com.cardone.dscatalog.dto.ProductDTO;
import com.cardone.dscatalog.exceptions.DatabaseException;
import com.cardone.dscatalog.exceptions.ResourceNotFoundException;
import com.cardone.dscatalog.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(value = ProductController.class, excludeAutoConfiguration={SecurityAutoConfiguration.class} )
//Exclui a configuração de segurança para não precisar de um token para acessar os endpoints
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    private PageImpl<ProductDTO> page;
    private ProductDTO productDTO;
    private Long existingId;
    private Long noExistingId;
    private Long dependentId;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        noExistingId = 2L;
        dependentId = 3L;

        productDTO = TestFactory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO));
        Mockito.when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
        // Simula para qualquer ID que for passado, retornar o productDTO
        Mockito.when(service.findByID(existingId)).thenReturn(productDTO);
        // Simula para qualquer ID que não exista, lançar uma exceção
        Mockito.when(service.findByID(noExistingId)).thenThrow(ResourceNotFoundException.class);

        // Mock alteração para o ID existente, retornar o productDTO
        Mockito.when(service.update(ArgumentMatchers.eq(existingId), ArgumentMatchers.any())).thenReturn(productDTO);
        // Alteração para um ID que não exista, lançar uma exceção
        Mockito.when(service.update(ArgumentMatchers.eq(noExistingId), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);

        //Mock para deleção de produtos (quando o metodo do Service é void primeiro a ação e depois o service)
        Mockito.doNothing().when(service).deleteProduct(existingId);
        //Mock para quando o ID não existir, lançar uma exceção
        Mockito.doThrow(ResourceNotFoundException.class).when(service).deleteProduct(noExistingId);
        //Mock para quando o ID for dependente, lançar uma exceção
        Mockito.doThrow(DatabaseException.class).when(service).deleteProduct(dependentId);

        // Mock alteração para inclusão de umo novo produto, retornar o productDTO
        Mockito.when(service.insert(ArgumentMatchers.any())).thenReturn(productDTO);
    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        mockMvc.perform(get("/products/list")).andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception {
        //Valida se o retorno é 200 e se existe o id e o nome no Json do response
        mockMvc.perform(get("/products/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        //Valida e retorna 404 para o Id que não existe
        mockMvc.perform(get("/products/{id}", noExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

        //Converte o objeto para Json
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        //Executa a chamada passando o ID existente e o Json do Request
        mockMvc.perform(put("/products/{id}", existingId)
                .content(jsonBody).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdNotExists() throws Exception {

        //Converte o objeto para Json
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        //Executa a chamada passando o ID inexistente e o Json do Request e recebe o retorno 404 (not found)
        mockMvc.perform(put("/products/{id}", noExistingId)
                .content(jsonBody).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldReturnProductDTO() throws Exception {

        //Converte o objeto para Json
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        //Executa a chamada passando Json do Request e valida se teve retorno 201 e foi criado o ID e o nome
        mockMvc.perform(post("/products")
                .content(jsonBody).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        //Valida se o retorno é 200 e se existe o id e o nome no Json do response
        mockMvc.perform(delete("/products/{id}", existingId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        //Valida e retorna 404 para o Id que não existe
        mockMvc.perform(delete("/products/{id}", noExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnDatabaseExceptionWhenIdDependents() throws Exception {
        //Valida e retorna BadRequest para o Id que é dependente
        mockMvc.perform(delete("/products/{id}", dependentId))
                .andExpect(status().isBadRequest());
    }
}

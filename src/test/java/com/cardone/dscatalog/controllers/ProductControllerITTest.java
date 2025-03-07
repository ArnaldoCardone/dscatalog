package com.cardone.dscatalog.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.cardone.dscatalog.TestFactory;
import com.cardone.dscatalog.dto.ProductDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerITTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
	private TokenUtil tokenUtil;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;
    private ProductDTO productDTO;

    private String username, password, bearerToken;

    @BeforeEach
    public void setUp() throws Exception {

        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
        
        username = "maria@gmail.com";
		password = "123456";
		
		bearerToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
    }

    @Test
    public void findAllPagedShouldReturnSortedPageWhenSortByName() throws Exception {
     
        //Faz a chamada GET para a rota /products?page=0&size=10&sort=name,asc e valida se os produtos voltaram ordenados por nome
        mockMvc.perform(get("/products/list?page=0&size=10&sort=name,asc")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()) //Verifica se o status retornado é 200
            .andExpect(jsonPath("$.content").exists()) //Verifica se o content existe
            .andExpect(jsonPath("$.totalElements").value(countTotalProducts)) //Verifica se o total de elementos é igual a 25
            .andExpect(jsonPath("$.content[0].name").value("Macbook Pro"))
            .andExpect(jsonPath("$.content[1].name").value("PC Gamer"))
            .andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
        //Instancia um novo productDTO
        productDTO = TestFactory.createProductDTO();

        //Salva os nomes que serão alterados e para comparar depois da atualização
        String expectdName = productDTO.getName();
        Double expectedValue = productDTO.getPrice();
        //Converte o productDTO para JSON
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(put("/products/{id}", existingId)
            .header("Authorization", "Bearer " + bearerToken)
            .content(jsonBody)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value(expectdName))
            .andExpect(jsonPath("$.price").value(expectedValue));
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        //Instancia um novo productDTO
        productDTO = TestFactory.createProductDTO();

        //Converte o productDTO para JSON
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(put("/products/{id}", nonExistingId)
            .header("Authorization", "Bearer " + bearerToken)
            .content(jsonBody)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}

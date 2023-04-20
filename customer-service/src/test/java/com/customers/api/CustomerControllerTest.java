package com.customers.api;

import com.customers.dto.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CustomerControllerTest {
    private final String URL = "/api/v1/customers";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testGetAllCustomersShouldReturnOk() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get(URL)
        ).andExpect(status().isOk());
    }

    @Test
    void testGetCustomerByEmailShouldReturnOk() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get(URL + "/md.ahmed@gmail.com")
        ).andExpect(status().isOk());
    }

    @Test
    void registerNewCustomerShouldReturnCreatedCode() throws Exception {
        var newCustomer = new Customer(0, "bahaa ahmed", "bahaa@gmail.com", randomUUID().toString());

        mockMvc.perform(
                MockMvcRequestBuilders.post(URL + "/registration")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(newCustomer))
        ).andExpect(status().isCreated());
    }
}
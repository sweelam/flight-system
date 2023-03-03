package com.flight.service;

import com.flight.dto.CustomerResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomerService {
    private final RestTemplate restTemplate;

    public CustomerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean customerFound(String customerEmail) {
        try {
            restTemplate.getForObject("http://localhost:8888/api/v1/customers/" + customerEmail, CustomerResponse.class);
        } catch (RestClientException e) {
            return false;
        }
        return true;
    }

}

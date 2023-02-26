package com.customers.api;

import com.customers.dto.Customer;
import com.customers.exceptions.CustomerApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {
    private final List<Customer> allCustomers = List.of(
            new Customer(1, "Mohamed Ahmed", "md.ahmed@gmail.com", UUID.randomUUID().toString()),
            new Customer(1, "Alex Mon", "alex.mon@gmail.com", UUID.randomUUID().toString()),
            new Customer(1, "Mia Andrey", "mia.andrey@gmail.com", UUID.randomUUID().toString()),
            new Customer(1, "Hesham Masoud", "hesham.masoud@gmail.com", UUID.randomUUID().toString())
    );
    @GetMapping("")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(allCustomers);
    }

    @GetMapping("/{email}")
    public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email) {
        var customerOtp =  allCustomers.stream()
                .filter(customer -> email.equals(customer.email()))
                .findAny();

        if (!customerOtp.isPresent())
            throw new CustomerApiException("No customer found with provided email", HttpStatus.NOT_FOUND);

    return ResponseEntity.ok(customerOtp.get());
    }
}

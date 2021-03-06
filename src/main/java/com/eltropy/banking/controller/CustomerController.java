package com.eltropy.banking.controller;

import com.eltropy.banking.constants.ErrorConstants;
import com.eltropy.banking.entity.Account;
import com.eltropy.banking.entity.Customer;
import com.eltropy.banking.exceptions.AccountNotFoundException;
import com.eltropy.banking.exceptions.CustomerNotFounException;
import com.eltropy.banking.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    public static final String CLASS_NAME = CustomerController.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    CustomerService customerService;

    @GetMapping("{id}")
    public ResponseEntity<Object> retrieveCustomer(@PathVariable long id) {

        Customer customer = null;

        try {
            customer = customerService.getCustomer(id);
        } catch (CustomerNotFounException e){
            logger.info(ErrorConstants.NO_CUSTOMER_FOUND_WITH_ID, id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.ok(customer);
    }

    @PostMapping
    public ResponseEntity<Object> createCustomer(@RequestBody Customer customer) {

        Customer createdCustomer = customerService.createCustomer(customer);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdCustomer.getCustomerId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> updateCustomer(@RequestBody Customer customer, @PathVariable long id) {

        Customer savedCustomer = null;

        try {
            savedCustomer = customerService.updateCustomer(customer, id);
        } catch (CustomerNotFounException e) {
            logger.info(ErrorConstants.NO_CUSTOMER_FOUND_WITH_ID, id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.ok(savedCustomer);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteCustomer(@PathVariable long id) {

        try {
            customerService.deleteCustomer(id);
        } catch (CustomerNotFounException e) {
            logger.info(ErrorConstants.EMPLOYEE_NOT_FOUND_WITH_ID, id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return new ResponseEntity<>(id, HttpStatus.ACCEPTED);
    }


    @PostMapping("/link_account/{id}")
    public ResponseEntity<Object> linkAccount(@PathVariable long id, @RequestBody Account account) {

        Customer savedCustomer = null;
        try {
            savedCustomer = customerService.linkAccounts(id, account);
        } catch (CustomerNotFounException | AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.ok(savedCustomer);

    }

}
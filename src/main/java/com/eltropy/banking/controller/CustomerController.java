package com.eltropy.banking.controller;

import com.eltropy.banking.constants.CustomerStatus;
import com.eltropy.banking.constants.ErrorConstants;
import com.eltropy.banking.entity.Account;
import com.eltropy.banking.entity.Customer;
import com.eltropy.banking.repository.AccountRepository;
import com.eltropy.banking.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

import static com.eltropy.banking.constants.ErrorConstants.NO_ACCOUNT_FOUND_WITH_ID;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    public static final String CLASS_NAME = CustomerController.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    AccountRepository accountRepository;

    @GetMapping("{id}")
    public ResponseEntity<Object> retrieveCustomer(@PathVariable long id) {
        Optional<Customer> customerOptional = customerRepository.findById(id);

        if (!customerOptional.isPresent()) {
            logger.info(ErrorConstants.NO_CUSTOMER_FOUND_WITH_ID, id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorConstants.NO_CUSTOMER_FOUND_WITH_ID_2 + id);
        }

        return ResponseEntity.ok(customerOptional.get());
    }

    @PostMapping
    public ResponseEntity<Object> createCustomer(@RequestBody Customer customer) {

        customer.setStatus(CustomerStatus.ACTIVE.name());
        Customer savedCustomer = customerRepository.save(customer);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedCustomer.getCustomerId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> updatePhoneDetails(@RequestBody Customer customer, @PathVariable long id) {

        Optional<Customer> customerOptional = customerRepository.findById(id);

        if (!customerOptional.isPresent()) {
            logger.info(ErrorConstants.NO_CUSTOMER_FOUND_WITH_ID, id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorConstants.NO_CUSTOMER_FOUND_WITH_ID_2 + id);
        }

        customer.setCustomerId(id);

        Customer savedData = customerRepository.save(customer);
        return ResponseEntity.ok(savedData);
    }

    @DeleteMapping("{id}")
    public void deleteCustomer(@PathVariable long id) {
        customerRepository.deleteById(id);
    }


    @PostMapping("/link_account/{id}")
    public ResponseEntity<Object> linkAccount(@PathVariable long id, @RequestBody Account account) {

        Optional<Customer> customerOptional = customerRepository.findById(id);
        Optional<Account> accountOptional = accountRepository.findById(account.getAccountId());

        if (!customerOptional.isPresent()) {
            logger.info(ErrorConstants.NO_CUSTOMER_FOUND_WITH_ID, id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorConstants.NO_CUSTOMER_FOUND_WITH_ID_2 + id);
        }

        if (!accountOptional.isPresent()) {
            logger.info(NO_ACCOUNT_FOUND_WITH_ID, account.getAccountId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorConstants.NO_ACCOUNT_FOUND_WITH_ID1
                    + account.getAccountId());
        }

        Customer customer = customerOptional.get();
        Account dbAccount = accountOptional.get();
        customer.getAccounts().add(dbAccount);
        Customer savedCustomer = customerRepository.save(customer);


        return ResponseEntity.ok(savedCustomer);

    }

}
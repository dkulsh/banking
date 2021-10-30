package com.eltropy.banking.controller;

import com.eltropy.banking.constants.AccountStatus;
import com.eltropy.banking.constants.AccountType;
import com.eltropy.banking.entity.Account;
import com.eltropy.banking.exceptions.AccountNotFoundException;
import com.eltropy.banking.exceptions.InvalidAccountTypeException;
import com.eltropy.banking.repository.AccountRepository;
import com.eltropy.banking.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/account")
public class AccountController {

    public static final String CLASS_NAME = AccountController.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @PostMapping
    public ResponseEntity<Object> createAccount(@RequestBody Account account) {

        try {
            accountService.validateAccount(account);
        } catch (InvalidAccountTypeException e) {
            logger.info(e.getMessage(), CLASS_NAME);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        Account savedAccount = accountService.createAccount(account);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedAccount.getAccountId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> retrieveAccountDetails(@PathVariable long id) {

        Account account = null;
        try {
            account = accountService.getAccount(id);
        } catch (AccountNotFoundException e) {
            logger.info("No account found with id - {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.ok(account);
    }

}

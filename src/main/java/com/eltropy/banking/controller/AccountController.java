package com.eltropy.banking.controller;

import com.eltropy.banking.constants.AccountStatus;
import com.eltropy.banking.constants.AccountType;
import com.eltropy.banking.entity.Account;
import com.eltropy.banking.exceptions.InvalidAccountTypeException;
import com.eltropy.banking.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/account")
public class AccountController {

    public static final String CLASS_NAME = AccountController.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    AccountRepository accountRepository;

    private Set<String> accountTypes = new HashSet<>();

    @PostConstruct
    public void init(){
        for (AccountType type : AccountType.values()){
            accountTypes.add(type.name());
        }
    }

    @PostMapping
    public ResponseEntity<Object> createAccount(@RequestBody Account account) {

        try {
            validateInput(account);
        } catch (InvalidAccountTypeException e) {
            logger.info(e.getMessage(), CLASS_NAME);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        account.setStatus(AccountStatus.ACTIVE.name());
        account.setStartDate(Date.from(Instant.now()));
        Account savedAccount = accountRepository.save(account);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedAccount.getAccountId()).toUri();

        return ResponseEntity.created(location).build();
    }

    private boolean validateInput(Account account) throws InvalidAccountTypeException {

        if (! accountTypes.contains(account.getType())) {
            logger.info(account.getType() + " is not a valid account type", CLASS_NAME);
            throw new InvalidAccountTypeException(account.getType() + " is not a valid account type");
        }

        return true;
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> retrieveAccountDetails(@PathVariable long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);

        if (!accountOptional.isPresent()) {
            logger.info("No account found with id - " + id, CLASS_NAME);
            logger.info("No account found with id - ", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No account found with id - " + id);
        }

        return ResponseEntity.ok(accountOptional.get());
    }

}

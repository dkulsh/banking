package com.eltropy.banking.controller;

import com.eltropy.banking.entity.Account;
import com.eltropy.banking.entity.Transaction;
import com.eltropy.banking.entity.TransferFunds;
import com.eltropy.banking.exceptions.AccountNotFoundException;
import com.eltropy.banking.exceptions.InsufficientBalanceException;
import com.eltropy.banking.scheduler.InterestScheduler;
import com.eltropy.banking.service.AccountService;
import com.eltropy.banking.service.TransactionService;
import com.eltropy.banking.util.TransferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    public static final String CLASS_NAME = TransactionController.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    TransactionService transactionService;

    @Autowired
    AccountService accountService;

    @Autowired
    TransferUtil transferUtil;

    @Autowired
    InterestScheduler interestScheduler;

    @GetMapping("/balance/{id}")
    public ResponseEntity<String> retrieveBalance(@PathVariable long id) {

        Account account = null;
        try {
            account = accountService.getAccount(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.ok("Account Balance is " + account.getAccountBalance());
    }

    @PostMapping("/transfer")
    public ResponseEntity<Object> transferFunds(@RequestBody TransferFunds transferFunds) {

//        Basic validations. Account valid, sufficient balance etc.
        try {
            transferUtil.validateTransaction(transferFunds);
        } catch (AccountNotFoundException | InsufficientBalanceException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

//        Trigger transfer.
        List<Account> updatedAccounts = null;
        try {
            updatedAccounts = transactionService.transfer(transferFunds);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(updatedAccounts);
    }

    @GetMapping(value = "/statement/{accountId}",
            produces = MediaType.APPLICATION_PDF_VALUE)
    public void getStatement(@PathVariable long accountId,
                                               @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
                                               @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate,
                                               HttpServletResponse response) {

        List<Transaction> transactions = transactionService.getTransactions(accountId, fromDate, toDate);

        transactionService.generatePdf(transactions, response);
    }

    @GetMapping("/triggerInterest")
    public ResponseEntity<Object> calculateInterest(){

        interestScheduler.addInterest();
        return ResponseEntity.ok().build();
    }
}

package com.eltropy.banking.util;

import com.eltropy.banking.entity.Account;
import com.eltropy.banking.entity.TransferFunds;
import com.eltropy.banking.exceptions.AccountNotFoundException;
import com.eltropy.banking.exceptions.InsufficientBalanceException;
import com.eltropy.banking.repository.AccountRepository;
import com.eltropy.banking.repository.TransactionalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.eltropy.banking.constants.ErrorConstants.NO_ACCOUNT_FOUND_WITH_ID;

@Component
public class TransferUtil {

    public static final String CLASS_NAME = TransferUtil.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(TransferUtil.class);

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionalRepository transactionalRepository;

    public void validateTransaction(TransferFunds transferFunds) throws AccountNotFoundException, InsufficientBalanceException {

        Optional<Account> fromAccountOptional = accountRepository.findById(transferFunds.getFromAccount());
        Optional<Account> toAccountOptional = accountRepository.findById(transferFunds.getToAccount());

        if (!fromAccountOptional.isPresent()) {
            logger.error(NO_ACCOUNT_FOUND_WITH_ID, transferFunds.getFromAccount());
            throw new AccountNotFoundException("No account found with id - " + transferFunds.getFromAccount());
        }

        if (!toAccountOptional.isPresent()) {
            logger.error(NO_ACCOUNT_FOUND_WITH_ID, transferFunds.getToAccount());
            throw new AccountNotFoundException("No account found with id - " + transferFunds.getToAccount());
        }

        if (transferFunds.getAmount() > fromAccountOptional.get().getAccountBalance()) {
            logger.info("Insufficient balance :: {}", transferFunds.getFromAccount());
            throw new InsufficientBalanceException("Insufficient balance");
        }

    }
}
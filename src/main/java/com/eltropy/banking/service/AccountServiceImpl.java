package com.eltropy.banking.service;

import com.eltropy.banking.constants.AccountStatus;
import com.eltropy.banking.constants.AccountType;
import com.eltropy.banking.entity.Account;
import com.eltropy.banking.exceptions.AccountNotFoundException;
import com.eltropy.banking.exceptions.InvalidAccountTypeException;
import com.eltropy.banking.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AccountServiceImpl implements AccountService{

    public static final String CLASS_NAME = AccountServiceImpl.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    AccountRepository accountRepository;

    private Set<String> accountTypes = new HashSet<>();

//    Read all account types in the memory
    @PostConstruct
    public void init(){
        for (AccountType type : AccountType.values()){
            accountTypes.add(type.name());
        }
    }

    @Override
    public boolean validateAccount(Account account) throws InvalidAccountTypeException {

        if (! accountTypes.contains(account.getType())) {
            logger.info("{} is not a valid account type", account.getType());
            throw new InvalidAccountTypeException(account.getType() + " is not a valid account type");
        }

        return true;
    }

    @Override
    public Account createAccount(Account account){

        account.setStatus(AccountStatus.ACTIVE.name());
        account.setStartDate(Date.from(Instant.now()));
        return accountRepository.save(account);
    }

    @Override
    public Account getAccount(long id) throws AccountNotFoundException {

        Optional<Account> accountOptional = accountRepository.findById(id);

        if (accountOptional.isEmpty()) {
            logger.info("No account found with id - {}", id);
            throw new AccountNotFoundException("No account found with id - " + id);
        }

        return accountOptional.get();
    }

}

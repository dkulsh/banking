package com.eltropy.banking.service;

import com.eltropy.banking.entity.Account;
import com.eltropy.banking.exceptions.AccountNotFoundException;
import com.eltropy.banking.exceptions.InvalidAccountTypeException;

public interface AccountService {

    boolean validateAccount(Account account) throws InvalidAccountTypeException;

    Account createAccount(Account account);

    Account getAccount(long id) throws AccountNotFoundException;
}

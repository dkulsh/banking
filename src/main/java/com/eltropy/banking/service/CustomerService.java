package com.eltropy.banking.service;

import com.eltropy.banking.entity.Account;
import com.eltropy.banking.entity.Customer;
import com.eltropy.banking.exceptions.AccountNotFoundException;
import com.eltropy.banking.exceptions.CustomerNotFounException;

public interface CustomerService {

    Customer getCustomer(long id) throws CustomerNotFounException;

    Customer createCustomer(Customer customer);

    Customer updateCustomer(Customer customer, long id) throws CustomerNotFounException;

    Customer linkAccounts(long id, Account account) throws CustomerNotFounException, AccountNotFoundException;

    Customer deleteCustomer(long id) throws CustomerNotFounException;
}

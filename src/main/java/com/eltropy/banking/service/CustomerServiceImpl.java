package com.eltropy.banking.service;

import com.eltropy.banking.constants.CustomerStatus;
import com.eltropy.banking.constants.ErrorConstants;
import com.eltropy.banking.controller.CustomerController;
import com.eltropy.banking.entity.Account;
import com.eltropy.banking.entity.Customer;
import com.eltropy.banking.exceptions.AccountNotFoundException;
import com.eltropy.banking.exceptions.CustomerNotFounException;
import com.eltropy.banking.repository.AccountRepository;
import com.eltropy.banking.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.eltropy.banking.constants.ErrorConstants.NO_ACCOUNT_FOUND_WITH_ID;

@Service
public class CustomerServiceImpl implements CustomerService {

    public static final String CLASS_NAME = CustomerServiceImpl.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    AccountRepository accountRepository;

    @Override
    public Customer getCustomer(long id) throws CustomerNotFounException {
        Optional<Customer> customerOptional = customerRepository.findById(id);

        if (!customerOptional.isPresent()) {
            logger.info(ErrorConstants.NO_CUSTOMER_FOUND_WITH_ID, id);
            throw new CustomerNotFounException(ErrorConstants.NO_CUSTOMER_FOUND_WITH_ID_2 + id);
        }

        return customerOptional.get();
    }

    @Override
    public Customer createCustomer(Customer customer) {
        customer.setStatus(CustomerStatus.ACTIVE.name());
        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Customer customer, long id) throws CustomerNotFounException {
        Optional<Customer> customerOptional = customerRepository.findById(id);

        if (!customerOptional.isPresent()) {
            logger.info(ErrorConstants.NO_CUSTOMER_FOUND_WITH_ID, id);
            throw new CustomerNotFounException(ErrorConstants.NO_CUSTOMER_FOUND_WITH_ID_2 + id);
        }

        customer.setCustomerId(id);
        return customerRepository.save(customer);
    }

    @Override
    public Customer linkAccounts(long customerid, Account account) throws CustomerNotFounException, AccountNotFoundException {

        Optional<Customer> customerOptional = customerRepository.findById(customerid);
        Optional<Account> accountOptional = accountRepository.findById(account.getAccountId());

        if (!customerOptional.isPresent()) {
            logger.info(ErrorConstants.NO_CUSTOMER_FOUND_WITH_ID, customerid);
            throw new CustomerNotFounException(ErrorConstants.NO_CUSTOMER_FOUND_WITH_ID_2 + customerid);
        }

        if (!accountOptional.isPresent()) {
            logger.info(NO_ACCOUNT_FOUND_WITH_ID, account.getAccountId());
            throw new AccountNotFoundException(ErrorConstants.NO_ACCOUNT_FOUND_WITH_ID1
                    + account.getAccountId());
        }

        Customer customer = customerOptional.get();
        Account dbAccount = accountOptional.get();
        customer.getAccounts().add(dbAccount);
        return customerRepository.save(customer);

    }
}

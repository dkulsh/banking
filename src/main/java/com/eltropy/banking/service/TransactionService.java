package com.eltropy.banking.service;

import com.eltropy.banking.entity.Account;
import com.eltropy.banking.entity.Transaction;
import com.eltropy.banking.entity.TransferFunds;
import com.eltropy.banking.exceptions.InsufficientBalanceException;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

public interface TransactionService {

    List<Account> transfer(TransferFunds transferFunds) throws InsufficientBalanceException;

    void generatePdf(List<Transaction> transactions, HttpServletResponse response);

    List<Transaction> getTransactions(long accountId, Date fromDate, Date toDate);

}

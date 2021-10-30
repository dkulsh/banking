package com.eltropy.banking.service;

import com.eltropy.banking.constants.TransactionType;
import com.eltropy.banking.entity.Account;
import com.eltropy.banking.entity.Transaction;
import com.eltropy.banking.entity.TransferFunds;
import com.eltropy.banking.exceptions.InsufficientBalanceException;
import com.eltropy.banking.repository.AccountRepository;
import com.eltropy.banking.repository.TransactionalRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService{

    public static final String CLASS_NAME = TransactionServiceImpl.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Autowired
    TransactionalRepository transactionalRepository;

    @Autowired
    AccountRepository accountRepository;

    @Transactional
    public List<Account> transfer(TransferFunds transferFunds) throws InsufficientBalanceException {

//        Method is transactional and takes a lock on the DB rows.
//        Multiple transfers can happen until they don't involve the same account
        Account fromAccount = accountRepository.findByIdAndLock(transferFunds.getFromAccount()).get();
        Account toAccount = accountRepository.findByIdAndLock(transferFunds.getToAccount()).get();

        if (transferFunds.getAmount() > fromAccount.getAccountBalance()) {
            logger.info("Insufficient balance :: {}", transferFunds.getFromAccount());
            throw new InsufficientBalanceException("Insufficient balance");
        }

        fromAccount.setAccountBalance(fromAccount.getAccountBalance() - transferFunds.getAmount());
        toAccount.setAccountBalance(toAccount.getAccountBalance() + transferFunds.getAmount());

        Transaction debit = new Transaction(TransactionType.DEBIT.name(), new Date(), fromAccount.getAccountId(), transferFunds.getAmount());
        Transaction credit = new Transaction(TransactionType.CREDIT.name(), new Date(), toAccount.getAccountId(), transferFunds.getAmount());

        transactionalRepository.saveAll(List.of(debit, credit));
        return accountRepository.saveAll(List.of(fromAccount, toAccount));

    }

    @Override
    public List<Transaction> getTransactions(long accountId, Date fromDate, Date toDate) {
        return transactionalRepository.getTransactions(accountId, fromDate, toDate);
    }

    @Override
    public void generatePdf(List<Transaction> transactions, HttpServletResponse response){

        Document document = new Document();

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){

            PdfWriter.getInstance(document, byteArrayOutputStream);

            //open
            document.open();

            for (Transaction transaction : transactions) {
                Paragraph p = new Paragraph();
                p.add(transaction.toString());
                p.setAlignment(Element.ALIGN_LEFT);
                document.add(p);
                document.add(Chunk.NEWLINE);
            }

            Font f = new Font();
            f.setStyle(Font.BOLD);
            f.setSize(8);

            document.close();

            try (InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray())) {
                IOUtils.copy(inputStream, response.getOutputStream());
            }

        } catch (DocumentException | IOException e) {
            logger.error("Exception while creating creating statement pdf :: {}", e.getMessage());
            e.printStackTrace();
        }
    }


}

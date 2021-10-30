package com.eltropy.banking.service;

import com.eltropy.banking.constants.ErrorConstants;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        Account fromAccount = accountRepository.findById(transferFunds.getFromAccount()).get();
        Account toAccount = accountRepository.findById(transferFunds.getToAccount()).get();

        List<Account> accountList = Stream.of(fromAccount, toAccount)
                .sorted((o1, o2) -> (int) (o1.getAccountId() - o2.getAccountId()))
                .collect(Collectors.toList());

//        Locking both accounts. So transfers can happen on other accounts.

//        Locking in a sequence to avoid Thread Deadlock
        synchronized (accountList.get(0)) {
            synchronized (accountList.get(1)) {

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
        }

    }

    @Override
    public List<Transaction> getTransactions(long accountId, Date fromDate, Date toDate) {
        return transactionalRepository.getTransactions(accountId, fromDate, toDate);
    }

    public void generatePdf(List<Transaction> transactions, HttpServletResponse response){

        Document document = new Document();

        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());) {

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

            IOUtils.copy(inputStream, response.getOutputStream());

        } catch (DocumentException | IOException e) {
            logger.error(ErrorConstants.EXCEPTION_WHILE_CREATING_CREATING_STATEMENT_PDF, e.getMessage());
            e.printStackTrace();
        }
    }


}

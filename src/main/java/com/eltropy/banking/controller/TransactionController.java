package com.eltropy.banking.controller;

import com.eltropy.banking.constants.ErrorConstants;
import com.eltropy.banking.constants.TransactionType;
import com.eltropy.banking.entity.Account;
import com.eltropy.banking.entity.Transaction;
import com.eltropy.banking.entity.TransferFunds;
import com.eltropy.banking.repository.AccountRepository;
import com.eltropy.banking.repository.TransactionalRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.eltropy.banking.constants.ErrorConstants.NO_ACCOUNT_FOUND_WITH_ID;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    public static final String CLASS_NAME = TransactionController.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionalRepository transactionalRepository;

    @GetMapping("/balance/{id}")
    public ResponseEntity<Object> retrieveBalance(@PathVariable long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);

        if (!accountOptional.isPresent()) {
            logger.error(NO_ACCOUNT_FOUND_WITH_ID, id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No account found with id - " + id);
        }

        return ResponseEntity.ok("Account Balance is " + accountOptional.get().getAccountBalance());
    }

    @PostMapping("/transfer")
    public synchronized ResponseEntity<Object> transferFunds(@RequestBody TransferFunds transferFunds) {

        Optional<Account> fromAccountOptional = accountRepository.findById(transferFunds.getFromAccount());
        Optional<Account> toAccountOptional = accountRepository.findById(transferFunds.getToAccount());

        if (!fromAccountOptional.isPresent()) {
            logger.error(NO_ACCOUNT_FOUND_WITH_ID, transferFunds.getFromAccount());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No account found with id - " + transferFunds.getFromAccount());
        }

        if (!toAccountOptional.isPresent()) {
            logger.error(NO_ACCOUNT_FOUND_WITH_ID, transferFunds.getFromAccount());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No account found with id - " + transferFunds.getToAccount());
        }

        if (transferFunds.getAmount() <= fromAccountOptional.get().getAccountBalance()) {
            List<Account> updatedAccounts = transfer(transferFunds.getAmount(), fromAccountOptional.get(), toAccountOptional.get());
            return ResponseEntity.ok(updatedAccounts);
        } else {
            logger.info("Insufficient balance :: {}", transferFunds.getFromAccount());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient balance");
        }
    }

    @Transactional
    private List<Account> transfer(long amount, Account fromAccount, Account toAccount){

        fromAccount.setAccountBalance(fromAccount.getAccountBalance() - amount);
        toAccount.setAccountBalance(toAccount.getAccountBalance() + amount);

        Transaction debit = new Transaction(TransactionType.DEBIT.name(), new Date(), fromAccount.getAccountId(), amount);
        Transaction credit = new Transaction(TransactionType.CREDIT.name(), new Date(), toAccount.getAccountId(), amount);

        transactionalRepository.saveAll(List.of(debit, credit));
        return accountRepository.saveAll(List.of(fromAccount, toAccount));
    }

    @GetMapping(value = "/statement/{accountId}",
            produces = MediaType.APPLICATION_PDF_VALUE)
    public void getStatement(@PathVariable long accountId,
                                               @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
                                               @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate,
                                               HttpServletResponse response) {

        List<Transaction> transactions = transactionalRepository.getTransactions(accountId, fromDate, toDate);

        generatePdf(transactions, response);

    }

    private void generatePdf(List<Transaction> transactions, HttpServletResponse response){

        Document document = new Document();

        try {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
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

            InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            IOUtils.copy(inputStream, response.getOutputStream());
            inputStream.close();

        } catch (DocumentException | IOException e) {
            logger.error(ErrorConstants.EXCEPTION_WHILE_CREATING_CREATING_STATEMENT_PDF, e.getMessage());
            e.printStackTrace();
        }
    }

}

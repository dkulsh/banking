package com.eltropy.banking.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long transactionId;

    private String type;

    @Temporal(TemporalType.TIMESTAMP)
    private Date transferDate;

    private Long accountId;

    private Long amount;

    public Transaction() {
    }

    public Transaction(String type, Date transferDate, Long accountId, Long amount) {
        this.type = type;
        this.transferDate = transferDate;
        this.accountId = accountId;
        this.amount = amount;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(Date transferDate) {
        this.transferDate = transferDate;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", type='" + type + '\'' +
                ", transferDate=" + transferDate +
                ", accountId=" + accountId +
                ", amount=" + amount +
                '}';
    }
}

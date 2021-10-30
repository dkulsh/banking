package com.eltropy.banking.entity;

public class TransferFunds {

    private double amount;
    private Long fromAccount;
    private Long toAccount;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Long getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Long fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Long getToAccount() {
        return toAccount;
    }

    public void setToAccount(Long toAccount) {
        this.toAccount = toAccount;
    }
}

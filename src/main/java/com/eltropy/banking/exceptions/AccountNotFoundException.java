package com.eltropy.banking.exceptions;

public class AccountNotFoundException extends Exception{

    private String message;

    public AccountNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}

package com.eltropy.banking.exceptions;

public class InsufficientBalanceException extends Exception{

    private final String message;

    public InsufficientBalanceException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}

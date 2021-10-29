package com.eltropy.banking.exceptions;

public class InvalidAccountTypeException extends Exception{

    private String message;

    public InvalidAccountTypeException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

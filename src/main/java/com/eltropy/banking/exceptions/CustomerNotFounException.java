package com.eltropy.banking.exceptions;

public class CustomerNotFounException extends Exception{

    private final String message;

    public CustomerNotFounException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

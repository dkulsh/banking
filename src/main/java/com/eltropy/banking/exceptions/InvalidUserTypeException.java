package com.eltropy.banking.exceptions;

public class InvalidUserTypeException extends Exception{

    private final String message;

    public InvalidUserTypeException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}

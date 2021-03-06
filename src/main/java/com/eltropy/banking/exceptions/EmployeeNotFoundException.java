package com.eltropy.banking.exceptions;

public class EmployeeNotFoundException extends Exception{

    private final String message;

    public EmployeeNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}

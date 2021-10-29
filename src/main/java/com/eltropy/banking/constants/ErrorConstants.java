package com.eltropy.banking.constants;

public class ErrorConstants {

    private ErrorConstants() {
    }

    public static final String NO_CUSTOMER_FOUND_WITH_ID = "No customer found with id - {}";
    public static final String NO_CUSTOMER_FOUND_WITH_ID_2 = "No customer found with id - ";
    public static final String NO_ACCOUNT_FOUND_WITH_ID1 = "No account found with id - ";
    public static final String NO_ACCOUNT_FOUND_WITH_ID = "No account found with id - {}";
    public static final String ROLE_TO_ACCESS_REFRESH_TRIGERRED = "Role to Access refresh trigerred.";
    public static final String EXCEPTION_WHILE_CREATING_CREATING_STATEMENT_PDF = "Exception while creating creating statement pdf :: {}";
    public static final String IS_INVALID = "{} is invalid";
    public static final String NOT_BEGIN_WITH_BEARER_STRING = "JWT Token does not begin with Bearer String";
    public static final String HAS_EXPIRED = "JWT Token has expired";
    public static final String INTEREST_CALCULATION_TRIGERRED = "Interest calculation trigerred";
    public static final String INTEREST_CALCULATION_COMPLETED = "Interest calculation completed";
    public static final String NOT_FOUND_WITH_USERNAME = "User not found with username: {}";
    public static final String WITH_USERNAME = "User not found with username: ";
}

package com.eltropy.banking.service;

import com.eltropy.banking.entity.User;
import com.eltropy.banking.exceptions.InvalidUserTypeException;

public interface UserService {

    User createUser(User user) throws InvalidUserTypeException;
}

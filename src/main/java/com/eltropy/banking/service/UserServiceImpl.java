package com.eltropy.banking.service;

import com.eltropy.banking.constants.ErrorConstants;
import com.eltropy.banking.constants.UserTypes;
import com.eltropy.banking.controller.UserController;
import com.eltropy.banking.entity.User;
import com.eltropy.banking.exceptions.InvalidUserTypeException;
import com.eltropy.banking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService{

    public static final String CLASS_NAME = UserServiceImpl.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    private Set<String> userTypes = new HashSet<>();

    @PostConstruct
    public void init(){
        for (UserTypes type : UserTypes.values()){
            userTypes.add(type.name());
        }
    }

    @Override
    public User createUser(User user) throws InvalidUserTypeException {

        if (! userTypes.contains(user.getType())) {
            logger.error(ErrorConstants.IS_INVALID, user.getType());
            throw new InvalidUserTypeException(user.getType() + " is invalid");
        }
        user.setPassword(bcryptEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}

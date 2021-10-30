package com.eltropy.banking.controller;

import com.eltropy.banking.constants.ErrorConstants;
import com.eltropy.banking.constants.UserTypes;
import com.eltropy.banking.entity.User;
import com.eltropy.banking.exceptions.InvalidUserTypeException;
import com.eltropy.banking.repository.UserRepository;
import com.eltropy.banking.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    public static final String CLASS_NAME = UserController.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<Object> createAdmin(@RequestBody User user) {

        User savedUser = null;
        try {
            savedUser = userService.createUser(user);
        } catch (InvalidUserTypeException e) {
            logger.info(ErrorConstants.IS_INVALID, user.getType());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedUser.getPk()).toUri();

        return ResponseEntity.created(location).build();
    }

}

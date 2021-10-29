package com.eltropy.banking.controller;

import com.eltropy.banking.constants.UserTypes;
import com.eltropy.banking.entity.User;
import com.eltropy.banking.repository.UserRepository;
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

    @PostMapping
    public ResponseEntity<Object> createAdmin(@RequestBody User user) {

        if (! userTypes.contains(user.getType())) {
            logger.error(user.getType() + " is invalid", CLASS_NAME);
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(user.getType() + " is invalid");
        }
        user.setPassword(bcryptEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedUser.getPk()).toUri();

        return ResponseEntity.created(location).build();
    }

}

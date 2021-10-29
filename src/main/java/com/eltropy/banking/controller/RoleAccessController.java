package com.eltropy.banking.controller;

import com.eltropy.banking.entity.Account;
import com.eltropy.banking.repository.RoleAccessRepository;
import com.eltropy.banking.service.RoleAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/role")
public class RoleAccessController {

    public static final String CLASS_NAME = RoleAccessController.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(RoleAccessController.class);

    @Autowired
    RoleAccessService roleAccessService;

    @GetMapping("/refresh")
    public ResponseEntity retrieveAccountDetails() {

        roleAccessService.refresh();
        logger.info("Role to Access refresh trigerred.", CLASS_NAME);
        return ResponseEntity.ok().build();
    }
}

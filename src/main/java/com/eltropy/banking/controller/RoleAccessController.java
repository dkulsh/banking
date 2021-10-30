package com.eltropy.banking.controller;

import com.eltropy.banking.constants.ErrorConstants;
import com.eltropy.banking.service.RoleAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/role")
public class RoleAccessController {

    public static final String CLASS_NAME = RoleAccessController.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(RoleAccessController.class);

    @Autowired
    RoleAccessService roleAccessService;

    @GetMapping("/refresh")
    public ResponseEntity retrieveAccountDetails() {

//        Refresh all the role access data from DB
        roleAccessService.refresh();
        logger.info(ErrorConstants.ROLE_TO_ACCESS_REFRESH_TRIGERRED);
        return ResponseEntity.ok().build();
    }
}

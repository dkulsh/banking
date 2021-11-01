package com.eltropy.banking.controller;

import com.eltropy.banking.constants.ErrorConstants;
import com.eltropy.banking.entity.Employee;
import com.eltropy.banking.exceptions.EmployeeNotFoundException;
import com.eltropy.banking.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    public static final String CLASS_NAME = EmployeeController.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<Object> createEmployee(@RequestBody Employee employee) {

        Employee savedEmployee = null;

        try {
            savedEmployee = employeeService.createEmployee(employee);
        } catch (UsernameNotFoundException e) {
            logger.info(ErrorConstants.NOT_FOUND_WITH_USERNAME, employee.getUserName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedEmployee.getEmployeId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteEmployee(@PathVariable long id) {

        try {
            employeeService.deleteEmployee(id);
        } catch (EmployeeNotFoundException e) {
            logger.info(ErrorConstants.EMPLOYEE_NOT_FOUND_WITH_ID, id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return new ResponseEntity<>(id, HttpStatus.ACCEPTED);

    }

}

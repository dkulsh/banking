package com.eltropy.banking.controller;

import com.eltropy.banking.constants.EmployeeStatus;
import com.eltropy.banking.entity.Employee;
import com.eltropy.banking.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    public static final String CLASS_NAME = EmployeeController.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    EmployeeRepository employeeRepository;

    @PostMapping
    public ResponseEntity<Object> createEmployee(@RequestBody Employee employee) {

        employee.setStatus(EmployeeStatus.ACTIVE.name());
        Employee savedEmployee = employeeRepository.save(employee);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedEmployee.getEmployeId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteEmployee(@PathVariable long id) {

        Optional<Employee> employeeOptional = employeeRepository.findById(id);

        if (!employeeOptional.isPresent()) {
            logger.info("Employee not found with id = " + id, CLASS_NAME);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee not found with id = " + id);
        }

        Employee employee = employeeOptional.get();
        employee.setStatus(EmployeeStatus.DELETED.name());
        employeeRepository.save(employee);

//        return ResponseEntity.accepted().build();
        return new ResponseEntity<>(id, HttpStatus.ACCEPTED);

    }

}

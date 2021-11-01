package com.eltropy.banking.service;

import com.eltropy.banking.entity.Employee;
import com.eltropy.banking.exceptions.EmployeeNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface EmployeeService {

    Employee createEmployee(Employee employee) throws UsernameNotFoundException;

    Employee deleteEmployee(long id) throws EmployeeNotFoundException;
}

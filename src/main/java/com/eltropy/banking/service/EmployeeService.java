package com.eltropy.banking.service;

import com.eltropy.banking.entity.Employee;
import com.eltropy.banking.exceptions.EmployeeNotFoundException;

public interface EmployeeService {

    Employee createEmployee(Employee employee);

    Employee deleteEmployee(long id) throws EmployeeNotFoundException;
}

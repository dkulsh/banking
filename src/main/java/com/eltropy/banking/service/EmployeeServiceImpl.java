package com.eltropy.banking.service;

import com.eltropy.banking.constants.EmployeeStatus;
import com.eltropy.banking.constants.ErrorConstants;
import com.eltropy.banking.entity.Employee;
import com.eltropy.banking.entity.User;
import com.eltropy.banking.exceptions.EmployeeNotFoundException;
import com.eltropy.banking.repository.EmployeeRepository;
import com.eltropy.banking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;
import static com.eltropy.banking.constants.ErrorConstants.WITH_USERNAME;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    public static final String CLASS_NAME = EmployeeServiceImpl.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public Employee createEmployee(Employee employee) throws UsernameNotFoundException {

        if (employee.getUserName() != null) {
            User user = userRepository.findByUsername(employee.getUserName());

            if (user == null) {
                logger.info(ErrorConstants.NOT_FOUND_WITH_USERNAME, employee.getUserName());
                throw new UsernameNotFoundException(WITH_USERNAME + employee.getUserName());
            }

            employee.setUserId(user.getPk());
        }

        employee.setStatus(EmployeeStatus.ACTIVE.name());
        return employeeRepository.save(employee);
    }

    @Override
    public Employee deleteEmployee(long id) throws EmployeeNotFoundException {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);

        if (!employeeOptional.isPresent()) {
            logger.info(ErrorConstants.EMPLOYEE_NOT_FOUND_WITH_ID, id);
            throw new EmployeeNotFoundException("Employee not found with id = " + id);
        }

        Employee employee = employeeOptional.get();
        employee.setStatus(EmployeeStatus.DELETED.name());
        return employeeRepository.save(employee);
    }
}

package com.eltropy.banking.service;

import com.eltropy.banking.constants.UserTypes;
import com.eltropy.banking.entity.RoleAccess;
import com.eltropy.banking.repository.RoleAccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

@Service
public class RoleAccessService {

    @Autowired
    RoleAccessRepository roleAccessRepository;

    private Map<String, List<String>> rolesToAccessMap = new HashMap<>();

    @PostConstruct
    public void init(){

//        Pre-defined roles in the system. These can be changed on runtime from the DB and a refresh call
        RoleAccess roleAccess1 = new RoleAccess(UserTypes.ADMIN.name(), "employee");
        RoleAccess roleAccess2 = new RoleAccess(UserTypes.EMPLOYEE.name(), "customer");
        RoleAccess roleAccess3 = new RoleAccess(UserTypes.EMPLOYEE.name(), "account");
        RoleAccess roleAccess4 = new RoleAccess(UserTypes.EMPLOYEE.name(), "transaction");

        roleAccessRepository.saveAll(List.of(roleAccess1, roleAccess2, roleAccess3, roleAccess4));
        refresh();
    }

    public void refresh() {

        List<RoleAccess> roleAccessList = roleAccessRepository.findAll();

        Map<String, List<String>> localRolesToAccessMap = roleAccessList
                .stream()
                .collect(groupingBy(RoleAccess::getRole, mapping(RoleAccess::getAccess, Collectors.toList())));

        rolesToAccessMap = localRolesToAccessMap;
    }

    public Map<String, List<String>> getRolesToAccessMap() {
        return rolesToAccessMap;
    }

}

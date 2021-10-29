package com.eltropy.banking.repository;

import com.eltropy.banking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByStatus(String status);
}

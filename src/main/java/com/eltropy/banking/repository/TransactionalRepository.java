package com.eltropy.banking.repository;

import com.eltropy.banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionalRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId AND date(t.transferDate) >= :startDate " +
            "AND date(t.transferDate) <= :endDate ")
    List<Transaction> getTransactions(Long accountId, Date startDate, Date endDate);
}

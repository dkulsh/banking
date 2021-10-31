package com.eltropy.banking.repository;

import com.eltropy.banking.entity.RoleAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleAccessRepository extends JpaRepository<RoleAccess, Long> {
}

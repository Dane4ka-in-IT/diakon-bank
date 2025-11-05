package com.diakonbank.bankapi.service.repository;

import com.diakonbank.bankapi.service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByOwnerUserId(Long ownerUserId);
    Optional<Account> findByExternalAccountId(String externalAccountId);
} 



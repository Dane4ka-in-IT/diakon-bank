package com.diakonbank.bankapiservice.repository;

import com.diakonbank.commondto.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    Optional<Account> findByExternalAccountId(String externalAccountId);
} 



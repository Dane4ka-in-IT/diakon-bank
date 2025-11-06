package com.diakonbank.financialpulse.service.repository;

import com.diakonbank.financialpulse.service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByOwnerUserId(Long ownerUserId);
}

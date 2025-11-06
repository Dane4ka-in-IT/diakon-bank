package com.diakonbank.authservice.repository;

import com.diakonbank.authservice.entity.BankConnection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankConnectionRepository extends JpaRepository<BankConnection, Long> {
} 
package com.diakonbank.authservice.repository;
import com.diakonbank.authservice.entity.BankConnection;
import com.diakonbank.authservice.repository.BankConnectionRepository;
import com.diakonbank.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface BankConnectionRepository extends JpaRepository<BankConnection, Long> {
    Optional<BankConnection> findByUserAndBankIdentifier(User user, String bankIdentifier);
}
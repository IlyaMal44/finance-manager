package com.promoit.finance.finance_manager.domain.repository;

import com.promoit.finance.finance_manager.domain.entity.TransactionEntity;
import com.promoit.finance.finance_manager.domain.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    Optional<TransactionEntity> findByIdAndWallet(UUID transactionEntity, WalletEntity wallet);
}

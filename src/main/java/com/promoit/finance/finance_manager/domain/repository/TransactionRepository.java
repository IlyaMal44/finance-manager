package com.promoit.finance.finance_manager.domain.repository;

import com.promoit.finance.finance_manager.domain.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
}

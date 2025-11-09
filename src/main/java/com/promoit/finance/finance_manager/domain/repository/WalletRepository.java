package com.promoit.finance.finance_manager.domain.repository;

import com.promoit.finance.finance_manager.domain.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletRepository extends JpaRepository<WalletEntity, UUID> {
}

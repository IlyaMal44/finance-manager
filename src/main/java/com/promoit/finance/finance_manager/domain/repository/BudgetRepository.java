package com.promoit.finance.finance_manager.domain.repository;

import com.promoit.finance.finance_manager.domain.entity.BudgetEntity;
import com.promoit.finance.finance_manager.domain.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<BudgetEntity, UUID> {
    Optional<BudgetEntity> findByWalletAndCategory(WalletEntity wallet, String category);
}

package com.promoit.finance.finance_manager.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Лимиты по категориям
 */
@Entity
@Data
@NoArgsConstructor
public class WalletEntity {
    @Id
    @UuidGenerator
    private UUID id;

    private double balance = 0.0;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wallet")
    private List<TransactionEntity> transactions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wallet")
    private List<BudgetEntity> budgets = new ArrayList<>();
}

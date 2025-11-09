package com.promoit.finance.finance_manager.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;


/**
 * Состояние кошелька.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BudgetEntity {
    @Id
    @UuidGenerator
    private UUID id;

    private String category;
    private Double limitAmount;
    @Builder.Default
    private Double currentSpent = 0.0;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private WalletEntity wallet;

}
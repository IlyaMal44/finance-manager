package com.promoit.finance.finance_manager.domain.entity;


import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntity {
    @Id
    @UuidGenerator
    private UUID id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private Double amount;
    private String category;
    private String description;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private WalletEntity wallet;
}

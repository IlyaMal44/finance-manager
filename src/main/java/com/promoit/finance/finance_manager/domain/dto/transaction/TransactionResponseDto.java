package com.promoit.finance.finance_manager.domain.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для ответа с данными транзакции
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDto {
    /**
     * Уникальный идентификатор транзакции
     */
    private UUID id;

    /**
     * Тип транзакции (доход/расход)
     */
    private TransactionType type;

    /**
     * Сумма транзакции
     */
    private Double amount;

    /**
     * Категория транзакции
     */
    private String category;

    /**
     * Описание транзакции
     */
    private String description;

    /**
     * Дата и время совершения транзакции
     */
    private LocalDateTime date;

    /**
     * Идентификатор кошелька
     */
    private UUID walletId;

    /**
     * Новый баланс кошелька после транзакции
     */
    private Double newBalance;
}
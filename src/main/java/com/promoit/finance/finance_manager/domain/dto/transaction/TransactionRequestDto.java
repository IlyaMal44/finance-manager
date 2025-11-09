package com.promoit.finance.finance_manager.domain.dto.transaction;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO для запроса на создание транзакции
 */
@Data
@AllArgsConstructor
@Builder
public class TransactionRequestDto {
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
}
package com.promoit.finance.finance_manager.domain.dto.budget;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * DTO для ответа с данными бюджета
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponseDto {
    /**
     * Уникальный идентификатор бюджета
     */
    private UUID id;

    /**
     * Категория расходов для бюджета
     */
    private String category;

    /**
     * Установленный лимит расходов
     */
    private Double limitAmount;

    /**
     * Текущая сумма потраченных средств
     */
    private Double currentSpent;

    /**
     * Оставшийся лимит (вычисляемое поле)
     */
    private Double remainingAmount;

    /**
     * Идентификатор кошелька владельца
     */
    private UUID walletId;

    public Double getRemainingAmount() {
        return limitAmount - currentSpent;
    }
}
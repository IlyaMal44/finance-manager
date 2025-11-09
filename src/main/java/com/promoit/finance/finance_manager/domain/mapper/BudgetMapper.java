package com.promoit.finance.finance_manager.domain.mapper;

import com.promoit.finance.finance_manager.domain.dto.budget.BudgetResponseDto;
import com.promoit.finance.finance_manager.domain.entity.BudgetEntity;
import com.promoit.finance.finance_manager.domain.entity.WalletEntity;

/**
 * Маппер для преобразования между BudgetEntity и Budget DTO
 */
public class BudgetMapper {

    /**
     * Преобразует сущность бюджета в DTO для ответа
     */
    public static BudgetResponseDto toDto(BudgetEntity entity) {
        return BudgetResponseDto.builder()
                .id(entity.getId())
                .category(entity.getCategory())
                .limitAmount(entity.getLimitAmount())
                .currentSpent(entity.getCurrentSpent())
                .walletId(entity.getWallet().getId())
                // remainingAmount вычисляется автоматически через getter
                .build();
    }

    /**
     * Создает новую сущность бюджета из базовых данных
     */
    public static BudgetEntity toEntity(String category, Double limitAmount, WalletEntity wallet) {
        return BudgetEntity.builder()
                .category(category)
                .limitAmount(limitAmount)
                .currentSpent(0.0)
                .wallet(wallet)
                .build();
    }
}
package com.promoit.finance.finance_manager.domain.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO с полной финансовой статистикой по кошельку.
 * Содержит общие суммы, детализацию по категориям и статус бюджетов.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponseDto {
    /**
     * Общая сумма всех доходов за период
     */
    private Double totalIncome;

    /**
     * Общая сумма всех расходов за период
     */
    private Double totalExpense;

    /**
     * Текущий баланс кошелька (доходы - расходы)
     */
    private Double balance;

    /**
     * Суммы доходов сгруппированные по категориям
     * Key: название категории, Value: сумма доходов
     */
    private Map<String, Double> incomeByCategory;

    /**
     * Суммы расходов сгруппированные по категориям
     * Key: название категории, Value: сумма расходов
     */
    private Map<String, Double> expenseByCategory;

    /**
     * Статус бюджетов по категориям
     * Key: название категории, Value: детальная информация о бюджете
     */
    private Map<String, BudgetStatus> budgetStatus;

    /**
     * Детальная информация о статусе бюджета категории
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetStatus {
        /**
         * Установленный лимит бюджета
         */
        private Double limitAmount;

        /**
         * Фактически потраченная сумма в категории
         */
        private Double currentSpent;

        /**
         * Оставшийся лимит (limitAmount - currentSpent)
         */
        private Double remaining;

        /**
         * Флаг превышения бюджета
         */
        private Boolean exceeded;

        /**
         * Процент использования бюджета (0-100+)
         */
        private Double usagePercentage;
    }
}
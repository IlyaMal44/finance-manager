package com.promoit.finance.finance_manager.domain.mapper;

import com.promoit.finance.finance_manager.domain.dto.statistics.StatisticsResponseDto;

import java.util.Map;

public class StatisticsMapper {

   public static StatisticsResponseDto toDto(
            Double totalIncome,
            Double totalExpense,
            Double balance,
            Map<String, Double> incomeByCategory,
            Map<String, Double> expenseByCategory,
            Map<String, StatisticsResponseDto.BudgetStatus> budgetStatus
    ) {
        return StatisticsResponseDto.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(balance)
                .incomeByCategory(incomeByCategory)
                .expenseByCategory(expenseByCategory)
                .budgetStatus(budgetStatus)
                .build();
    }
}

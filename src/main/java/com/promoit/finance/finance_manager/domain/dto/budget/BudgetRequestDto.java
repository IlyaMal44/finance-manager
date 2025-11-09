package com.promoit.finance.finance_manager.domain.dto.budget;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса на установку/обновление бюджета
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequestDto {
    /**
     * Название категории для бюджета
     */
    @NotBlank(message = "Категория обязательна для заполнения")
    private String category;

    /**
     * Максимально допустимая сумма расходов
     */
    @Positive(message = "Лимит бюджета должен быть положительным числом")
    private Double limitAmount;
}
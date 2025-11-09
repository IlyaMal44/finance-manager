package com.promoit.finance.finance_manager.domain.dto.transaction;

import jakarta.validation.constraints.*;
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
    @NotNull(message = "Тип транзакции обязателен")
    private TransactionType type;

    /**
     * Сумма транзакции
     */
    @NotNull(message = "Сумма обязательна")
    @Positive(message = "Сумма должна быть положительным числом")
    @DecimalMin(value = "0.01", message = "Сумма должна быть не менее 0.01")
    private Double amount;

    /**
     * Категория транзакции
     */
    @NotBlank(message = "Категория обязательна")
    @Size(min = 1, max = 100, message = "Категория должна быть от 1 до 100 символов")
    private String category;

    /**
     * Описание транзакции
     */
    @Size(max = 255, message = "Описание не должно превышать 255 символов")
    private String description;
}
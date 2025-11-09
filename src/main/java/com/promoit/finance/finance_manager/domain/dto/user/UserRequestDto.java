package com.promoit.finance.finance_manager.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для запроса регистрации/логина пользователя
 */
@Data
@AllArgsConstructor
public class UserRequestDto {
    /**
     * Имя пользователя для входа в систему
     */
    @NotBlank(message = "Имя пользователя обязательно для заполнения")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    private String username;

    /**
     * Пароль пользователя
     */
    @NotBlank(message = "Пароль обязателен для заполнения")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String password;
}
package com.promoit.finance.finance_manager.domain.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для запроса регистрации пользователя
 */
@Data
@AllArgsConstructor
public class LoginRequestDto {

    /**
     * Адрес электронной почты пользователя
     */
    @NotBlank(message = "Адрес электронной почты обязательно для заполнения")
    @Email
    private String mail;

    /**
     * Пароль пользователя
     */
    @NotBlank(message = "Пароль обязателен для заполнения")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String password;
}
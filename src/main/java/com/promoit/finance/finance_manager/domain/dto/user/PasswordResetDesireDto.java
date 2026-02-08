package com.promoit.finance.finance_manager.domain.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для изменения пароля
 */
@Data
@AllArgsConstructor
public class PasswordResetDesireDto {
    /**
     * Адрес электронной почты пользователя
     */
    @NotBlank(message = "Адрес электронной почты обязательно для заполнения")
    @Email
    private String mail;
}

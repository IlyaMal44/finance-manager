package com.promoit.finance.finance_manager.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PasswordResetConfirmRequestDto {
    /**
     * Временный токен пользователя для входа в систему
     */
    @NotNull(message = "Временный токен пользователя обязателен для заполнения")
    private UUID token;

    /**
     * Пароль пользователя
     */
    @NotBlank(message = "Пароль обязателен для заполнения")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String newPassword;
}

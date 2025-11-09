package com.promoit.finance.finance_manager.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

/**
 * DTO для ответа с данными пользователя
 */
@Data
@AllArgsConstructor
public class UserResponseDto {
    /**
     * Уникальный идентификатор пользователя
     */
    private UUID id;

    /**
     * Имя пользователя
     */
    private String username;

    /**
     * Идентификатор кошелька пользователя
     */
    private UUID walletId;
}
package com.promoit.finance.finance_manager.domain.dto.user;


import com.promoit.finance.finance_manager.domain.entity.WalletEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

/**
 * DTO для внутренней работы с аутентификацией
 */
@Data
@AllArgsConstructor
public class UserAuthDto {
    /**
     * Уникальный идентификатор пользователя
     */
    private UUID id;

    /**
     * Имя пользователя
     */
    private String username;

    /**
     * Зашифрованный пароль
     */
    private String password;

    /**
     * Кошелек пользователя
     */
    private WalletEntity wallet;
}
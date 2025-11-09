package com.promoit.finance.finance_manager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    /**
     * Уведомляет о превышении установленного бюджета в категории.
     * @param username имя пользователя для персонализации уведомления
     * @param category категория расходов где превышен бюджет
     * @param spent фактическая сумма потраченная в категории
     */
    public void notifyBudgetExceeded(String username, String category, Double spent) {
        log.warn("Предупреждение для {}: Превышен бюджет в категории '{}'. Потрачено: {} руб.", username, category, spent);
    }

    /**
     * Уведомляет о приближении к лимиту бюджета (80% использования).
     * @param username имя пользователя для персонализации уведомления
     * @param category категория расходов с высоким процентом использования
     * @param percentage процент использования бюджета (0-100)
     */
    public void notifyBudgetWarning(String username, String category, Double percentage) {
        log.info("Внимание для {}: Категория '{}' использовала {}% бюджета", username, category, percentage);
    }

    /**
     * Уведомляет об отрицательном балансе кошелька.
     * @param username имя пользователя с отрицательным балансом
     */
    public void notifyNegativeBalance(String username) {
        log.error("Внимание для {}: Обнаружен отрицательный баланс!", username);
    }
}
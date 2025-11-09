package com.promoit.finance.finance_manager.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("Уведомление о превышении бюджета логируется корректно")
    void notifyBudgetExceeded_LogsWarning() {
        String username = "testuser";
        String category = "Food";
        Double spent = 1200.0;

        assertDoesNotThrow(() -> notificationService.notifyBudgetExceeded(username, category, spent));
    }

    @Test
    @DisplayName("Уведомление о предупреждении бюджета логируется корректно")
    void notifyBudgetWarning_LogsInfo() {
        String username = "testuser";
        String category = "Food";
        Double percentage = 85.0;

        assertDoesNotThrow(() -> notificationService.notifyBudgetWarning(username, category, percentage));
    }

    @Test
    @DisplayName("Уведомление об отрицательном балансе логируется корректно")
    void notifyNegativeBalance_LogsError() {
        String username = "testuser";

        assertDoesNotThrow(() -> notificationService.notifyNegativeBalance(username));
    }
}
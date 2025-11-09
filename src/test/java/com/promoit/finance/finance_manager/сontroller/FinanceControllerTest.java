package com.promoit.finance.finance_manager.сontroller;

import com.promoit.finance.finance_manager.controller.FinanceController;
import com.promoit.finance.finance_manager.domain.dto.statistics.StatisticsResponseDto;
import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionRequestDto;
import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionResponseDto;
import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionType;
import com.promoit.finance.finance_manager.service.FinanceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class FinanceControllerTest {
    @Mock
    private FinanceService financeService;
    @InjectMocks
    private FinanceController financeController;

    @Test
    @DisplayName("Добавление транзакции через контроллер")
    void addTransaction_Success() {
        UUID walletId = UUID.randomUUID();
        TransactionRequestDto request = TransactionRequestDto.builder()
                .type(TransactionType.INCOME)
                .amount(500.0)
                .category("Salary")
                .description("Monthly salary")
                .build();
        TransactionResponseDto response = TransactionResponseDto.builder()
                .id(UUID.randomUUID())
                .type(TransactionType.INCOME)
                .amount(500.0)
                .category("Salary")
                .description("Monthly salary")
                .date(LocalDateTime.now())
                .build();

        when(financeService.addTransaction(walletId, request)).thenReturn(response);
        TransactionResponseDto result = financeController.addTransaction(walletId, request);

        assertNotNull(result);
        assertEquals(response, result);
        verify(financeService).addTransaction(walletId, request);
    }

    @Test
    @DisplayName("Получение статистики через контроллер с фильтрами")
    void getStatistics_WithFilters_Success() {
        UUID walletId = UUID.randomUUID();
        List<String> categories = Arrays.asList("Food", "Transport");
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        StatisticsResponseDto response = StatisticsResponseDto.builder()
                .totalIncome(5000.0)
                .totalExpense(3000.0)
                .balance(2000.0)
                .build();

        when(financeService.getStatistics(walletId, categories, startDate, endDate)).thenReturn(response);
        StatisticsResponseDto result = financeController.getStatistics(walletId, categories, startDate, endDate);

        assertNotNull(result);
        assertEquals(response, result);
        verify(financeService).getStatistics(walletId, categories, startDate, endDate);
    }
}
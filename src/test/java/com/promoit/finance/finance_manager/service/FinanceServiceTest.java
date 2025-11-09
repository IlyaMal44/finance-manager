package com.promoit.finance.finance_manager.service;

import com.promoit.finance.finance_manager.domain.dto.budget.BudgetRequestDto;
import com.promoit.finance.finance_manager.domain.dto.budget.BudgetResponseDto;
import com.promoit.finance.finance_manager.domain.dto.statistics.StatisticsResponseDto;
import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionRequestDto;
import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionResponseDto;
import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionType;
import com.promoit.finance.finance_manager.domain.entity.BudgetEntity;
import com.promoit.finance.finance_manager.domain.entity.TransactionEntity;
import com.promoit.finance.finance_manager.domain.entity.WalletEntity;
import com.promoit.finance.finance_manager.domain.exception.wallet.InsufficientFundsException;
import com.promoit.finance.finance_manager.domain.exception.wallet.InvalidAmountException;
import com.promoit.finance.finance_manager.domain.exception.wallet.WalletNotFoundException;
import com.promoit.finance.finance_manager.domain.repository.BudgetRepository;
import com.promoit.finance.finance_manager.domain.repository.TransactionRepository;
import com.promoit.finance.finance_manager.domain.repository.UserRepository;
import com.promoit.finance.finance_manager.domain.repository.WalletRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceServiceTest {
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private FinanceService financeService;

    @Test
    @DisplayName("Успешное добавление транзакции дохода")
    void addTransaction_Income_Success() {
        UUID walletId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity();
        wallet.setId(walletId);
        wallet.setBalance(100.0);

        TransactionRequestDto request = TransactionRequestDto.builder()
                .type(TransactionType.INCOME)
                .amount(50.0)
                .category("Salary")
                .description("Monthly salary")
                .build();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(TransactionEntity.class))).thenAnswer(invocation -> {
            TransactionEntity transaction = invocation.getArgument(0);
            transaction.setId(UUID.randomUUID());
            return transaction;
        });
        when(walletRepository.save(any(WalletEntity.class))).thenReturn(wallet);
        TransactionResponseDto result = financeService.addTransaction(walletId, request);

        assertNotNull(result);
        assertEquals(150.0, wallet.getBalance());
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    @Test
    @DisplayName("Успешное добавление транзакции расхода")
    void addTransaction_Expense_Success() {
        UUID walletId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity();
        wallet.setId(walletId);
        wallet.setBalance(100.0);

        TransactionRequestDto request = TransactionRequestDto.builder()
                .type(TransactionType.EXPENSE)
                .amount(30.0)
                .category("Food")
                .description("Lunch")
                .build();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(budgetRepository.findByWalletAndCategory(wallet, "Food")).thenReturn(Optional.empty());
        when(transactionRepository.save(any(TransactionEntity.class))).thenAnswer(invocation -> {
            TransactionEntity transaction = invocation.getArgument(0);
            transaction.setId(UUID.randomUUID());
            return transaction;
        });
        when(walletRepository.save(any(WalletEntity.class))).thenReturn(wallet);

        TransactionResponseDto result = financeService.addTransaction(walletId, request);

        assertNotNull(result);
        assertEquals(70.0, wallet.getBalance());
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    @Test
    @DisplayName("Добавление транзакции с несуществующим кошельком вызывает исключение")
    void addTransaction_WalletNotFound_ThrowsException() {
        UUID walletId = UUID.randomUUID();
        TransactionRequestDto request = TransactionRequestDto.builder()
                .type(TransactionType.INCOME)
                .amount(50.0)
                .category("Salary")
                .build();

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> financeService.addTransaction(walletId, request));
        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }

    @Test
    @DisplayName("Добавление транзакции с отрицательной суммой вызывает исключение")
    void addTransaction_NegativeAmount_ThrowsException() {
        UUID walletId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity();
        wallet.setId(walletId);
        wallet.setBalance(100.0);

        TransactionRequestDto request = TransactionRequestDto.builder()
                .type(TransactionType.INCOME)
                .amount(-50.0)
                .category("Salary")
                .build();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidAmountException.class, () -> financeService.addTransaction(walletId, request));
        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }

    @Test
    @DisplayName("Добавление расходной транзакции при недостаточном балансе вызывает исключение")
    void addTransaction_InsufficientFunds_ThrowsException() {
        UUID walletId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity();
        wallet.setId(walletId);
        wallet.setBalance(20.0);

        TransactionRequestDto request = TransactionRequestDto.builder()
                .type(TransactionType.EXPENSE)
                .amount(50.0)
                .category("Food")
                .build();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        assertThrows(InsufficientFundsException.class, () -> financeService.addTransaction(walletId, request));
        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }

    @Test
    @DisplayName("Установка бюджета для новой категории")
    void setBudget_NewCategory_Success() {
        UUID walletId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity();
        wallet.setId(walletId);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(budgetRepository.findByWalletAndCategory(wallet, "Food")).thenReturn(Optional.empty());
        when(budgetRepository.save(any(BudgetEntity.class))).thenAnswer(invocation -> {
            BudgetEntity budget = invocation.getArgument(0);
            budget.setId(UUID.randomUUID());
            return budget;
        });

        BudgetResponseDto result = financeService.setBudget(walletId, "Food", 1000.0);

        assertNotNull(result);
        verify(budgetRepository).save(any(BudgetEntity.class));
    }

    @Test
    @DisplayName("Обновление существующего бюджета")
    void setBudget_ExistingCategory_Success() {
        UUID walletId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity();
        wallet.setId(walletId);

        BudgetEntity existingBudget = BudgetEntity.builder()
                .id(UUID.randomUUID())
                .category("Food")
                .limitAmount(500.0)
                .currentSpent(200.0)
                .wallet(wallet)
                .build();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(budgetRepository.findByWalletAndCategory(wallet, "Food")).thenReturn(Optional.of(existingBudget));
        when(budgetRepository.save(any(BudgetEntity.class))).thenReturn(existingBudget);

        BudgetResponseDto result = financeService.setBudget(walletId, "Food", 1500.0);

        assertNotNull(result);
        verify(budgetRepository).save(existingBudget);
    }

    @Test
    @DisplayName("Установка нескольких бюджетов")
    void setBudgets_MultipleBudgets_Success() {
        UUID walletId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity();
        wallet.setId(walletId);

        List<BudgetRequestDto> requests = Arrays.asList(
                new BudgetRequestDto("Food", 1000.0),
                new BudgetRequestDto("Transport", 500.0)
        );

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(budgetRepository.findByWalletAndCategory(any(), anyString())).thenReturn(Optional.empty());
        when(budgetRepository.save(any(BudgetEntity.class))).thenAnswer(invocation -> {
            BudgetEntity budget = invocation.getArgument(0);
            budget.setId(UUID.randomUUID());
            return budget;
        });

        List<BudgetResponseDto> results = financeService.setBudgets(walletId, requests);

        assertEquals(2, results.size());
        verify(budgetRepository, times(2)).save(any(BudgetEntity.class));
    }

    @Test
    @DisplayName("Получение статистики без фильтров")
    void getStatistics_NoFilters_Success() {
        UUID walletId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity();
        wallet.setId(walletId);

        List<TransactionEntity> transactions = Arrays.asList(
                createTransaction(TransactionType.INCOME, 1000.0, "Salary", LocalDateTime.now().minusDays(1)),
                createTransaction(TransactionType.EXPENSE, 300.0, "Food", LocalDateTime.now())
        );
        wallet.setTransactions(transactions);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        StatisticsResponseDto result = financeService.getStatistics(walletId, null, null, null);

        assertNotNull(result);
        assertEquals(1000.0, result.getTotalIncome());
        assertEquals(300.0, result.getTotalExpense());
        assertEquals(700.0, result.getBalance());
    }

    @Test
    @DisplayName("Получение статистики с фильтром по категориям")
    void getStatistics_WithCategoryFilter_Success() {
        UUID walletId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity();
        wallet.setId(walletId);

        List<TransactionEntity> transactions = Arrays.asList(
                createTransaction(TransactionType.INCOME, 1000.0, "Salary", LocalDateTime.now()),
                createTransaction(TransactionType.EXPENSE, 300.0, "Food", LocalDateTime.now()),
                createTransaction(TransactionType.EXPENSE, 200.0, "Transport", LocalDateTime.now())
        );
        wallet.setTransactions(transactions);

        List<String> categories = List.of("Food");
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        StatisticsResponseDto result = financeService.getStatistics(walletId, categories, null, null);

        assertNotNull(result);
        assertEquals(0.0, result.getTotalIncome()); // Salary не входит в фильтр
        assertEquals(300.0, result.getTotalExpense()); // Только Food
    }

    @Test
    @DisplayName("Получение статистики с фильтром по датам")
    void getStatistics_WithDateFilter_Success() {
        UUID walletId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity();
        wallet.setId(walletId);

        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);

        List<TransactionEntity> transactions = Arrays.asList(
                createTransaction(TransactionType.INCOME, 1000.0, "Salary", LocalDateTime.now().minusDays(3)), // в периоде
                createTransaction(TransactionType.EXPENSE, 300.0, "Food", LocalDateTime.now()) // вне периода
        );
        wallet.setTransactions(transactions);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        StatisticsResponseDto result = financeService.getStatistics(walletId, null, startDate, endDate);

        assertNotNull(result);
        assertEquals(1000.0, result.getTotalIncome());
        assertEquals(0.0, result.getTotalExpense()); // Расход вне периода
    }


    private TransactionEntity createTransaction(TransactionType type, Double amount, String category, LocalDateTime date) {
        return TransactionEntity.builder()
                .id(UUID.randomUUID())
                .type(type)
                .amount(amount)
                .category(category)
                .description("Test transaction")
                .date(date)
                .build();
    }
}
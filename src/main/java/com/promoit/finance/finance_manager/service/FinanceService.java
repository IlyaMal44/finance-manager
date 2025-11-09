package com.promoit.finance.finance_manager.service;

import com.promoit.finance.finance_manager.domain.dto.budget.BudgetRequestDto;
import com.promoit.finance.finance_manager.domain.dto.budget.BudgetResponseDto;
import com.promoit.finance.finance_manager.domain.dto.statistics.StatisticsResponseDto;
import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionRequestDto;
import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionResponseDto;
import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionType;
import com.promoit.finance.finance_manager.domain.dto.user.UserAuthDto;
import com.promoit.finance.finance_manager.domain.entity.BudgetEntity;
import com.promoit.finance.finance_manager.domain.entity.TransactionEntity;
import com.promoit.finance.finance_manager.domain.entity.WalletEntity;
import com.promoit.finance.finance_manager.domain.exception.user.UserNotFoundException;
import com.promoit.finance.finance_manager.domain.exception.wallet.InsufficientFundsException;
import com.promoit.finance.finance_manager.domain.exception.wallet.InvalidAmountException;
import com.promoit.finance.finance_manager.domain.exception.wallet.WalletNotFoundException;
import com.promoit.finance.finance_manager.domain.mapper.BudgetMapper;
import com.promoit.finance.finance_manager.domain.mapper.StatisticsMapper;
import com.promoit.finance.finance_manager.domain.mapper.TransactionMapper;
import com.promoit.finance.finance_manager.domain.mapper.TransferMapper;
import com.promoit.finance.finance_manager.domain.repository.BudgetRepository;
import com.promoit.finance.finance_manager.domain.repository.TransactionRepository;
import com.promoit.finance.finance_manager.domain.repository.UserRepository;
import com.promoit.finance.finance_manager.domain.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FinanceService {
    private final BudgetRepository budgetRepository;
    private final WalletRepository walletRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public FinanceService(
            UserRepository userRepository,
            BudgetRepository budgetRepository,
            WalletRepository walletRepository,
            NotificationService notificationService,
            TransactionRepository transactionRepository
    ) {
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
        this.walletRepository = walletRepository;
        this.notificationService = notificationService;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Добавляет новую финансовую операцию (доход или расход).
     * @param walletId уникальный идентификатор кошелька для операции
     * @param request  DTO с данными для создания транзакции
     * @return созданная сущность транзакции
     */
    public TransactionResponseDto addTransaction(UUID walletId, TransactionRequestDto request) {
        WalletEntity wallet = walletRepository.findById(walletId).orElseThrow(
                () -> new WalletNotFoundException("Кошелек с ID '" + walletId + "' не найден")
        );
        if (request.getAmount() <= 0) {
            throw new InvalidAmountException("Сумма должна быть положительной");
        }
        switch (request.getType()) {
            case INCOME -> wallet.setBalance(wallet.getBalance() + request.getAmount());
            case EXPENSE -> {
                double balance = wallet.getBalance();
                if (balance < request.getAmount()) {
                    double required = request.getAmount() - balance;
                    throw new InsufficientFundsException(
                            "Недостаточно средств. Баланс: " + balance + "₽, требуется: " + required + "₽"
                    );
                }
                wallet.setBalance(balance - request.getAmount());
                updateBudgetAndCheckLimit(wallet, request.getCategory(), request.getAmount());
            }
        }

        TransactionEntity transaction = TransactionMapper.toEntity(wallet, request);
        TransactionEntity savedTransaction = transactionRepository.save(transaction);
        wallet.getTransactions().add(savedTransaction);
        walletRepository.save(wallet);

        if (wallet.getBalance() < 0) {
            notificationService.notifyNegativeBalance(wallet.getUser().getUsername());
        }
        return TransactionMapper.toDto(transaction);
    }

    /**
     * Обновляет сумму потраченных средств в бюджете категории и проверяет лимиты.
     * @param wallet   кошелек в котором произошла расходная операция
     * @param category категория расходов для обновления бюджета
     * @param amount   сумма расхода для добавления к текущим тратам бюджета
     */
    private void updateBudgetAndCheckLimit(WalletEntity wallet, String category, Double amount) {
        Optional<BudgetEntity> budgetOption = budgetRepository.findByWalletAndCategory(wallet, category);

        if (budgetOption.isPresent()) {
            BudgetEntity budget = budgetOption.get();
            budget.setCurrentSpent(budget.getCurrentSpent() + amount);
            budgetRepository.save(budget);

            if (budget.getCurrentSpent() > budget.getLimitAmount()) {
                notificationService.notifyBudgetExceeded(wallet.getUser().getUsername(), category, budget.getCurrentSpent());
            }

            // Уведомление при 80% лимита
            double usagePercentage = (budget.getCurrentSpent() / budget.getLimitAmount()) * 100;
            if (usagePercentage >= 80 && usagePercentage < 100) {
                notificationService.notifyBudgetWarning(wallet.getUser().getUsername(), category, usagePercentage);
            }
        }
    }

    /**
     * Устанавливает или обновляет бюджетное ограничение для одной категории расходов.
     * @param walletId уникальный идентификатор кошелька для установки бюджета
     * @param category категория расходов для которой устанавливается бюджет
     * @param limitAmount максимально допустимая сумма расходов в категории
     * @return BudgetResponseDto с данными созданного или обновленного бюджета
     */
    public BudgetResponseDto setBudget(UUID walletId, String category, Double limitAmount) {
        BudgetEntity savedBudget = setSingleBudget(walletId, category, limitAmount);
        return BudgetMapper.toDto(savedBudget);
    }

    /**
     * Устанавливает или обновляет бюджетные ограничения для нескольких категорий расходов.
     * @param walletId уникальный идентификатор кошелька для установки бюджетов
     * @param requests список DTO с данными бюджетов для установки
     * @return List<BudgetResponseDto> список DTO с данными всех созданных или обновленных бюджетов
     */
    public List<BudgetResponseDto> setBudgets(UUID walletId, List<BudgetRequestDto> requests) {
        return requests.stream()
                .map(request -> setSingleBudget(walletId, request.getCategory(), request.getLimitAmount()))
                .map(BudgetMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Внутренний вспомогательный метод для установки или обновления одного бюджета.
     * @param walletId уникальный идентификатор кошелька
     * @param category категория расходов для бюджета
     * @param limitAmount максимальный лимит расходов для категории
     * @return BudgetEntity сохраненная сущность бюджета с установленным лимитом
     */
    private BudgetEntity setSingleBudget(UUID walletId, String category, Double limitAmount) {
        WalletEntity wallet = walletRepository.findById(walletId).orElseThrow(
                () -> new WalletNotFoundException("Кошелек с ID '" + walletId + "' не найден")
        );
        Optional<BudgetEntity> existingBudget = budgetRepository.findByWalletAndCategory(wallet, category);

        BudgetEntity budget = existingBudget.isPresent()
                ? existingBudget.get()      // Если существует бюджет, то обновляем
                : BudgetMapper.toEntity(category, limitAmount, wallet); // Если не существует, то создаем

        budget.setLimitAmount(limitAmount);
        return budgetRepository.save(budget);
    }

    /**
     * Получает детальную финансовую статистику по кошельку с возможностью фильтрации.
     * @param walletId идентификатор кошелька для анализа
     * @param categories список категорий для фильтрации (опционально)
     * @param startDate начальная дата периода (опционально)
     * @param endDate конечная дата периода (опционально)
     * @return StatisticsResponseDto с полной финансовой статистикой
     */
    public StatisticsResponseDto getStatistics(
            UUID walletId, List<String> categories, LocalDateTime startDate, LocalDateTime endDate
    ) {
        WalletEntity wallet = walletRepository.findById(walletId).orElseThrow(
                () -> new WalletNotFoundException("Кошелек с ID '" + walletId + "' не найден")
        );
        List<TransactionEntity> transactions = wallet.getTransactions();

        // Фильтрация транзакций по дате для общей статистики и расчета сумм
        if (startDate != null && endDate != null) {
            transactions = transactions.stream()
                    .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }
        // Фильтрация транзакций по категориям для общей статистики и расчета сумм
        if (categories != null && !categories.isEmpty()) {
            transactions = transactions.stream()
                    .filter(t -> categories.contains(t.getCategory()))
                    .collect(Collectors.toList());
        }
        // Расчет общей статистики на основе отфильтрованных транзакций
        double totalIncome = calculateTotalIncome(transactions);
        double totalExpense = calculateTotalExpense(transactions);
        double balance = totalIncome - totalExpense;
        // Статистика по категориям на основе отфильтрованных транзакций
        Map<String, Double> incomeByCategory = calculateIncomeByCategory(transactions);
        Map<String, Double> expenseByCategory = calculateExpenseByCategory(transactions);
        // Расчет статуса бюджетов с дополнительной фильтрацией для отображения
        Map<String, StatisticsResponseDto.BudgetStatus> budgetStatus = calculateBudgetStatus(
                wallet, expenseByCategory, categories,startDate,endDate
        );

        return StatisticsMapper.toDto(totalIncome,totalExpense,balance,incomeByCategory,expenseByCategory,budgetStatus);
    }

    /**
     * Вычисляет общую сумму доходов из списка транзакций
     */
    private double calculateTotalIncome(List<TransactionEntity> transactions) {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(TransactionEntity::getAmount)
                .sum();
    }

    /**
     * Вычисляет общую сумму расходов из списка транзакций
     */
    private double calculateTotalExpense(List<TransactionEntity> transactions) {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(TransactionEntity::getAmount)
                .sum();
    }

    /**
     * Группирует доходы по категориям
     */
    private Map<String, Double> calculateIncomeByCategory(List<TransactionEntity> transactions) {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .collect(Collectors.groupingBy(
                        TransactionEntity::getCategory,
                        Collectors.summingDouble(TransactionEntity::getAmount)
                ));
    }

    /**
     * Группирует расходы по категориям
     */
    private Map<String, Double> calculateExpenseByCategory(List<TransactionEntity> transactions) {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        TransactionEntity::getCategory,
                        Collectors.summingDouble(TransactionEntity::getAmount)
                ));
    }

    /**
     * Рассчитывает статус бюджетов на основе фактических расходов за период.
     * Фильтрует отображаемые бюджеты по следующим правилам:
     * - Если указаны категории: показываем только бюджеты запрошенных категорий
     * - Если указан период: показываем только бюджеты, у которых были транзакции в указанный период
     * - Расчёт потраченной суммы (currentSpent) всегда учитывает применённые фильтры (даты/категории)
     *
     * @param wallet кошелек для анализа бюджетов
     * @param expenseByCategory мапа расходов по категориям (уже отфильтрованная по периоду и категориям)
     * @param categories список категорий для фильтрации бюджетов (null = все категории)
     * @param startDate начальная дата периода для фильтрации бюджетов (null = без фильтра по дате)
     * @param endDate конечная дата периода для фильтрации бюджетов (null = без фильтра по дате)
     * @return Map<String, StatisticsResponseDto.BudgetStatus> статусов бюджетов, отфильтрованная по указанным категориям и периоду
     */
    private Map<String, StatisticsResponseDto.BudgetStatus> calculateBudgetStatus(
            WalletEntity wallet,
            Map<String, Double> expenseByCategory,
            List<String> categories,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        Map<String, StatisticsResponseDto.BudgetStatus> budgetStatus = new HashMap<>();

        for (BudgetEntity budget : wallet.getBudgets()) {
            String category = budget.getCategory();
            // фильтруем БЮДЖЕТЫ по запрошенным категориям
            if (categories != null && !categories.isEmpty() && !categories.contains(category)) {
                continue;
            }
            // фильтруем БЮДЖЕТЫ по запрошенным периодам
            if (startDate != null && endDate != null) {
                boolean hasTransactionsInPeriod = wallet.getTransactions().stream()
                        .filter(t -> t.getType() == TransactionType.EXPENSE)
                        .filter(t -> category.equals(t.getCategory()))
                        .anyMatch(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate));
                if (!hasTransactionsInPeriod) {
                    continue;
                }
            }
            double spent = expenseByCategory.getOrDefault(category, 0.0);
            double remaining = budget.getLimitAmount() - spent;
            double usagePercentage = budget.getLimitAmount() > 0 ? (spent / budget.getLimitAmount()) * 100 : 0;
            boolean exceeded = remaining < 0;

            StatisticsResponseDto.BudgetStatus status = StatisticsResponseDto.BudgetStatus.builder()
                    .limitAmount(budget.getLimitAmount())
                    .currentSpent(spent)
                    .remaining(remaining)
                    .exceeded(exceeded)
                    .usagePercentage(Math.round(usagePercentage * 100.0) / 100.0)
                    .build();

            budgetStatus.put(category, status);
        }
        return budgetStatus;
    }

    /**
     * Создает транзакции: EXPENSE у отправителя и INCOME у получателя
     * @param fromUsername логин пользователя-отправителя
     * @param toUsername логин пользователя-получателя
     * @param amount сумма перевода
     * @param description описание операции
     */
    public void transfer(String fromUsername, String toUsername, Double amount, String description) {
        UserAuthDto fromUser = userRepository.findByUsername(fromUsername).orElseThrow(
                () -> new UserNotFoundException("Sender with name " + fromUsername + " not found")
        );
        UserAuthDto toUser = userRepository.findByUsername(toUsername).orElseThrow(
                () -> new UserNotFoundException("Recipient with name " + toUsername + " not found")
        );

        if (fromUser.getWallet().getBalance() < amount) {
            throw new InsufficientFundsException("Недостаточно средств");
        }

        // Списание у отправителя
        addTransaction(
                fromUser.getWallet().getId(),
                TransferMapper.toDto(TransactionType.EXPENSE, amount, "Transfer", description + " to " + toUsername)
        );
        // Зачисление получателю
        addTransaction(
                toUser.getWallet().getId(),
                TransferMapper.toDto(TransactionType.INCOME, amount, "Transfer", description + " from " + fromUsername)
        );
    }

}
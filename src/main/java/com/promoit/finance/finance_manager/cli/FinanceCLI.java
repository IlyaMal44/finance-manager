package com.promoit.finance.finance_manager.cli;

import com.promoit.finance.finance_manager.domain.dto.statistics.StatisticsResponseDto;
import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionRequestDto;
import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionType;
import com.promoit.finance.finance_manager.domain.dto.user.UserResponseDto;
import com.promoit.finance.finance_manager.service.FinanceService;
import com.promoit.finance.finance_manager.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.Scanner;
import java.util.UUID;

@Profile("!test")
@Component
public class FinanceCLI implements CommandLineRunner {

    private final FinanceService financeService;
    private final UserService userService;
    private final Scanner scanner;

    private UUID currentWalletId;
    private String currentUsername;

    public FinanceCLI(FinanceService financeService, UserService userService) {
        this.financeService = financeService;
        this.userService = userService;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("------ЗАПУСК ФИНАНСОВОГО МЕНЕДЖЕРА------");
        while (true) {
            if (currentUsername == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showAuthMenu() {
        System.out.println("МЕНЮ АУТЕНТИФИКАЦИИ:");
        System.out.println("1 - Регистрация");
        System.out.println("2 - Вход");
        System.out.println("0 - Выход");
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> register();
            case "2" -> login();
            case "0" -> {
                System.out.println("До свидания!");
                System.exit(0);
            }
            default -> System.out.println("Неверный выбор");
        }
    }

    private void showMainMenu() {
        System.out.println("ГЛАВНОЕ МЕНЮ (" + currentUsername + "):");
        System.out.println("1 - Добавить доход");
        System.out.println("2 - Добавить расход");
        System.out.println("3 - Установить бюджет");
        System.out.println("4 - Перевод");
        System.out.println("5 - Статистика");
        System.out.println("0 - Выход");
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> addIncome();
            case "2" -> addExpense();
            case "3"  -> setBudget();
            case "4"  -> transfer();
            case "5"  -> showStatistics();
            case "0"  -> logout();
            default -> System.out.println("Неверный выбор");
        }
    }

    private void register() {
        System.out.println("РЕГИСТРАЦИЯ");
        System.out.print("Имя пользователя: ");
        String username = scanner.nextLine();
        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        try {
            UserResponseDto response = userService.register(username, password);
            currentUsername = response.getUsername();
            currentWalletId = response.getWalletId();
            System.out.println("Регистрация успешна!");
            System.out.println("ID кошелька: " + currentWalletId);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void login() {
        System.out.println("ВХОД");
        System.out.print("Имя пользователя: ");
        String username = scanner.nextLine();
        System.out.print("Пароль: ");
        String password = scanner.nextLine();

        try {
            UserResponseDto response = userService.authenticate(username, password);
            currentUsername = response.getUsername();
            currentWalletId = response.getWalletId();
            System.out.println("Вход успешен! Добро пожаловать, " + username);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void logout() {
        currentUsername = null;
        currentWalletId = null;
        System.out.println("Выход выполнен");
    }

    private void addIncome() {
        System.out.println("ДОБАВЛЕНИЕ ДОХОДА");
        System.out.print("Сумма: ");
        double amount = Double.parseDouble(scanner.nextLine());
        System.out.print("Категория: ");
        String category = scanner.nextLine();
        System.out.print("Описание: ");
        String description = scanner.nextLine();

        try {
            TransactionRequestDto request = TransactionRequestDto.builder()
                    .type(TransactionType.INCOME)
                    .amount(amount)
                    .category(category)
                    .description(description)
                    .build();

            financeService.addTransaction(currentWalletId, request);
            System.out.println("Доход добавлен!");
            showCurrentBalance();
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void addExpense() {
        System.out.println("ДОБАВЛЕНИЕ РАСХОДА");
        System.out.print("Сумма: ");
        double amount = Double.parseDouble(scanner.nextLine());
        System.out.print("Категория: ");
        String category = scanner.nextLine();
        System.out.print("Описание: ");
        String description = scanner.nextLine();

        try {
            TransactionRequestDto request = TransactionRequestDto.builder()
                    .type(TransactionType.EXPENSE)
                    .amount(amount)
                    .category(category)
                    .description(description)
                    .build();

            financeService.addTransaction(currentWalletId, request);
            System.out.println("Расход добавлен!");
            showCurrentBalance();
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void setBudget() {
        System.out.println("УСТАНОВКА БЮДЖЕТА");
        System.out.print("Категория: ");
        String category = scanner.nextLine();
        System.out.print("Лимит: ");
        double limit = Double.parseDouble(scanner.nextLine());

        try {
            financeService.setBudget(currentWalletId, category, limit);
            System.out.println("Бюджет установлен!");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void transfer() {
        System.out.println("ПЕРЕВОД");
        System.out.print("Получатель: ");
        String toUser = scanner.nextLine();
        System.out.print("Сумма: ");
        double amount = Double.parseDouble(scanner.nextLine());
        System.out.print("Описание: ");
        String description = scanner.nextLine();

        try {
            financeService.transfer(currentUsername, toUser, amount, description);
            System.out.println("Перевод выполнен!");
            showCurrentBalance();
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void showStatistics() {
        System.out.println("СТАТИСТИКА");
        try {
            StatisticsResponseDto stats = financeService.getStatistics(currentWalletId, null, null, null);

            System.out.println("Общий доход: " + stats.getTotalIncome() + " ₽");
            System.out.println("Общий расход: " + stats.getTotalExpense() + " ₽");
            System.out.println("Текущий баланс: " + stats.getBalance() + " ₽");

            System.out.println("Доходы по категориям:");
            stats.getIncomeByCategory().forEach((category, amount) ->
                    System.out.println("  " + category + ": " + amount + " ₽"));

            System.out.println("Расходы по категориям:");
            stats.getExpenseByCategory().forEach((category, amount) ->
                    System.out.println("  " + category + ": " + amount + " ₽"));

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void showCurrentBalance() {
        try {
            StatisticsResponseDto stats = financeService.getStatistics(currentWalletId, null, null, null);
            System.out.println("Текущий баланс: " + stats.getBalance() + " ₽");
        } catch (Exception e) {
            System.out.println("Не удалось получить баланс: " + e.getMessage());
        }
    }
}
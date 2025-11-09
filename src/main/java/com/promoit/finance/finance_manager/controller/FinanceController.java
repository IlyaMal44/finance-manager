package com.promoit.finance.finance_manager.controller;

import com.promoit.finance.finance_manager.domain.dto.budget.BudgetRequestDto;
import com.promoit.finance.finance_manager.domain.dto.budget.BudgetResponseDto;
import com.promoit.finance.finance_manager.domain.dto.statistics.StatisticsResponseDto;
import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionRequestDto;
import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionResponseDto;
import com.promoit.finance.finance_manager.service.ExportService;
import com.promoit.finance.finance_manager.service.FinanceService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/finance")
public class FinanceController {
    private final FinanceService financeService;
    private final ExportService exportService;

    public FinanceController(FinanceService financeService, ExportService exportService) {
        this.financeService = financeService;
        this.exportService = exportService;
    }

    /**
     * Устанавливает или обновляет бюджетное ограничение для ОДНОЙ категории расходов.
     *
     * @param walletId UUID идентификатор кошелька
     * @param request  DTO с данными бюджета
     * @return BudgetResponseDto установленный бюджет с детальной информацией
     */
    @PostMapping("/{walletId}/budget")
    public BudgetResponseDto setBudget(
            @PathVariable UUID walletId,
            @RequestBody BudgetRequestDto request
    ) {
        return financeService.setBudget(walletId, request.getCategory(), request.getLimitAmount());
    }

    /**
     * Устанавливает или обновляет бюджетные ограничения для НЕСКОЛЬКИХ категорий расходов.
     *
     * @param walletId UUID идентификатор кошелька
     * @param requests список DTO с данными бюджетов
     * @return список BudgetResponseDto с установленными бюджетами
     */
    @PostMapping("/{walletId}/budgets")
    public List<BudgetResponseDto> setBudgets(
            @PathVariable UUID walletId,
            @RequestBody List<BudgetRequestDto> requests
    ) {
        return financeService.setBudgets(walletId, requests);
    }

    /**
     * Создает новую финансовую транзакцию для указанного кошелька.
     *
     * @param walletId UUID идентификатор кошелька
     * @param request  DTO с данными транзакции
     * @return TransactionResponseDto созданная транзакция с детальной информацией
     */
    @PostMapping("/{walletId}/transaction")
    public TransactionResponseDto addTransaction(
            @PathVariable UUID walletId,
            @RequestBody TransactionRequestDto request
    ) {
        return financeService.addTransaction(walletId, request);
    }

    /**
     * Получает детальную финансовую статистику по кошельку. Поддерживает фильтрацию по периоду и категориям.
     *
     * @param walletId   идентификатор кошелька
     * @param categories список категорий для фильтрации (опционально)
     * @param startDate  начальная дата периода в формате ISO (опционально)
     * @param endDate    конечная дата периода в формате ISO (опционально)
     * @return StatisticsResponseDto с полной финансовой статистикой
     */
    @GetMapping("/{walletId}/statistics")
    public StatisticsResponseDto getStatistics(
            @PathVariable UUID walletId,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        return financeService.getStatistics(walletId, categories, startDate, endDate);
    }

    /**
     * Создает детальный финансовый отчет в JSON формате:
     *   - Автоматически скачивает файл после генерации
     *   - Поддерживает фильтрацию по произвольному периоду
     *   - Форматирует данные для удобного анализа
     *
     * @param walletId UUID кошелька для анализа
     * @param startDate начальная дата периода (включительно)
     * @param endDate конечная дата периода (включительно)
     * @param filename название файла (без расширения .json)
     * @return ResponseEntity<Resource> JSON файл для скачивания с финансовой статистикой
     */
    @PostMapping("/{walletId}/export/download")
    public ResponseEntity<Resource> exportAndDownload(
            @PathVariable UUID walletId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "report") String filename) {

        try {
            exportService.exportToJson(walletId, startDate, endDate, filename);
            Path filePath = Paths.get("exports/" + filename + ".json");
            Resource resource = new InputStreamResource(Files.newInputStream(filePath));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".json")
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .contentLength(Files.size(filePath))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    /**
     * Создает две связанные транзакции: списание у отправителя и зачисление получателю
     *
     * @param fromUser    логин пользователя-отправителя (обязательный)
     * @param toUser      логин пользователя-получателя (обязательный)
     * @param amount      сумма перевода, должна быть положительной (обязательный)
     * @param description назначение платежа (необязательный, по умолчанию "Transfer")
     */
    @PostMapping("/transfer")
    public void transfer(
            @RequestParam(required = true) String fromUser,
            @RequestParam(required = true) String toUser,
            @RequestParam(required = true) Double amount,
            @RequestParam(defaultValue = "Transfer") String description
    ) {
        financeService.transfer(fromUser, toUser, amount, description);
    }
}
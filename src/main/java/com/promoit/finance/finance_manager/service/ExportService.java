package com.promoit.finance.finance_manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.promoit.finance.finance_manager.domain.dto.statistics.StatisticsResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Сервис для экспорта финансовых отчетов в JSON формате.
 * Предоставляет способ сохранения финансовой статистики в файл для дальнейшего анализа или резервного копирования.
 */
@Service
@Slf4j
public class ExportService {
    private final FinanceService financeService;
    private final ObjectMapper objectMapper;
    private final String exportDirectory = "exports/";

    public ExportService(FinanceService financeService) {
        this.financeService = financeService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        createExportDirectory();
    }

    /**
     * Экспортирует финансовую статистику за указанный период в JSON файл.
     * @param walletId идентификатор кошелька
     * @param startDate начальная дата периода (включительно)
     * @param endDate конечная дата периода (включительно)
     * @param filename название файла без расширения
     */
    public void exportToJson(UUID walletId, LocalDate startDate, LocalDate endDate, String filename) {
        try {
            log.info("Экспорт статистики за период {}-{}", startDate, endDate);

            StatisticsResponseDto stats = financeService.getStatistics(
                    walletId, null, startDate.atStartOfDay(), endDate.atTime(23, 59, 59)
            );

            Map<String, Object> exportData = new HashMap<>();
            exportData.put("metadata", createMetadata(startDate, endDate));
            exportData.put("statistics", stats);

            String filePath = exportDirectory + filename + ".json";
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(filePath).toFile(), exportData);

            log.info("Отчет за период экспортирован в: {}", filePath);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка экспорта отчета за период: " + e.getMessage(), e);
        }
    }

    /**
     * Создает метаданные для отчета за период
     */
    private Map<String, Object> createMetadata(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("periodStart", startDate.toString());
        metadata.put("periodEnd", endDate.toString());
        metadata.put("exportDate", LocalDate.now().toString());
        metadata.put("formatVersion", "1.0");
        metadata.put("generatedBy", "Finance Manager");
        return metadata;
    }

    /**
     * Создает директорию для экспортов если не существует
     */
    private void createExportDirectory() {
        try {
            Files.createDirectories(Paths.get(exportDirectory));
            log.debug("Директория для экспортов создана: {}", exportDirectory);
        } catch (IOException e) {
            log.warn("Не удалось создать директорию для экспортов: {}", exportDirectory);
        }
    }

}
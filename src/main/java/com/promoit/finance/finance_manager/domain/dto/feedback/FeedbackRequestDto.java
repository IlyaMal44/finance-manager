package com.promoit.finance.finance_manager.domain.dto.feedback;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO для отправки обратной связи от пользователей
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequestDto {

    @NotNull(message = "Категория обращения обязательна")
    private FeedbackCategory category;

    @NotBlank(message = "Сообщение не может быть пустым")
    @Size(min = 10, max = 2000, message = "Сообщение должно быть от 10 до 2000 символов")
    private String message;

    private String username;
    private String userMail;

    /**
     * Категории обратной связи
     */
    @Getter
    public enum FeedbackCategory {
        BUG("Ошибка"),
        FEATURE("Предложение функции"),
        QUESTION("Вопрос"),
        OTHER("Другое");

        private final String description;

        FeedbackCategory(String description) {
            this.description = description;
        }
    }

}
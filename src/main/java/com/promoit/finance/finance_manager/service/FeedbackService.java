package com.promoit.finance.finance_manager.service;

import com.promoit.finance.finance_manager.domain.dto.feedback.FeedbackRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedbackService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;
    @Value("${feedback.recipient.email:}")
    private String recipientEmail;

    /**
     * Отправляет обратную связь на email
     */
    public void sendFeedback(FeedbackRequestDto request) {
        log.info("Получена обратная связь: категория={}, пользователь={}", request.getCategory(), request.getUsername());
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            String toEmail = (recipientEmail != null && !recipientEmail.isEmpty()) ? recipientEmail : fromEmail;
            message.setTo(toEmail);

            String subject = String.format(
                    "[%s] Обратная связь от %s",
                    request.getCategory().getDescription(),
                    request.getUsername() != null ? request.getUsername() : "Неизвестный пользователь"
            );
            message.setSubject(subject);
            String emailBody = formatFeedbackMessage(request);
            message.setText(emailBody);
            mailSender.send(message);
            log.info("Обратная связь отправлена на email от пользователя: {}", request.getUsername());
        } catch (Exception e) {
            log.error("Ошибка отправки email с обратной связью", e);
        }
    }

    /**
     * Форматирует сообщение обратной связи для email
     */
    private String formatFeedbackMessage(FeedbackRequestDto feedback) {
        StringBuilder sb = new StringBuilder();
        sb.append("Категория: ").append(feedback.getCategory().getDescription()).append("\n");
        sb.append("Пользователь: ").append(feedback.getUsername() != null ? feedback.getUsername() : "Неизвестно").append("\n");
        if (feedback.getUserMail() != null && !feedback.getUserMail().isEmpty()) {
            sb.append("Почта пользователя: ").append(feedback.getUserMail()).append("\n");
        }
        sb.append("Дата: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).append("\n");
        sb.append("═══════════════════════════════════════\n\n");
        sb.append("Сообщение:\n");
        sb.append(feedback.getMessage());
        sb.append("\n\n═══════════════════════════════════════\n");
        sb.append("Это автоматическое сообщение от системы обратной связи.");
        return sb.toString();
    }
}
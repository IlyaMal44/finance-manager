package com.promoit.finance.finance_manager.controller;

import com.promoit.finance.finance_manager.domain.dto.feedback.FeedbackRequestDto;
import com.promoit.finance.finance_manager.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;

    /**
     * Отправляет обратную связь на email
     */
    @PostMapping
    public void sendFeedback(@Valid @RequestBody FeedbackRequestDto request) {
        feedbackService.sendFeedback(request);
    }

}
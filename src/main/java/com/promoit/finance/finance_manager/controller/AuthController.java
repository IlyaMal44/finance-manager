package com.promoit.finance.finance_manager.controller;

import com.promoit.finance.finance_manager.domain.dto.user.*;
import com.promoit.finance.finance_manager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Создает учетную запись пользователя и автоматически инициализирует для него кошелек с нулевым балансом.
     *
     * @param request DTO с данными для регистрации.
     * @return UserResponseDto с данными созданного пользователя
     */
    @PostMapping("/register")
    public UserResponseDto register(@Valid @RequestBody RegisterRequestDto request) {
        return userService.register(request);
    }

    /**
     * Выполняет аутентификацию пользователя в системе.
     *
     * @param request DTO с учетными данными пользователя (username и password)
     * @return UserResponseDto с данными аутентифицированного пользователя
     */
    @PostMapping("/login")
    public UserResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        return userService.authenticate(request.getMail(), request.getPassword());
    }

    /**
     * Создает ссылку с временным токеном в url и отправляет ее пользователю на указанный почтовый адрес.
     *
     * @param request DTO с учетными данными пользователя (username и password)
     */
    @PostMapping("/password-reset/desire")
    public void passwordResetDesire(@Valid @RequestBody PasswordResetDesireDto request) {
        userService.passwordResetDesire(request);
    }

    /**
     * Подтверждает сброс пароля пользователя с использованием временного токена.
     * @param request DTO с временным токеном и новым паролем
     * @return UserResponseDto с обновленными данными пользователя
     */
    @PostMapping("/password-reset/confirm")
    public UserResponseDto passwordResetConfirm(@Valid @RequestBody PasswordResetConfirmRequestDto request) {
       return userService.passwordResetConfirm(request);
    }


}
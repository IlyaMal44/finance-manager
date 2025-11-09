package com.promoit.finance.finance_manager.controller;

import com.promoit.finance.finance_manager.domain.dto.user.UserRequestDto;
import com.promoit.finance.finance_manager.domain.dto.user.UserResponseDto;
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
     * @param request DTO с данными для регистрации.
     * @return UserResponseDto с данными созданного пользователя (id, username, walletId)
     */
    @PostMapping("/register")
    public UserResponseDto register(@Valid @RequestBody UserRequestDto request) {
        return userService.register(request.getUsername(), request.getPassword());
    }

    /**
     * Выполняет аутентификацию пользователя в системе.
     * @param request DTO с учетными данными пользователя (username и password)
     * @return UserResponseDto с данными аутентифицированного пользователя
     */
    @PostMapping("/login")
    public UserResponseDto login(@Valid @RequestBody UserRequestDto request) {
        return userService.authenticate(request.getUsername(), request.getPassword());
    }
}
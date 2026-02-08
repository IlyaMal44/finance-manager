package com.promoit.finance.finance_manager.service;

import com.promoit.finance.finance_manager.domain.dto.user.UserAuthDto;
import com.promoit.finance.finance_manager.domain.dto.user.RegisterRequestDto;
import com.promoit.finance.finance_manager.domain.dto.user.UserResponseDto;
import com.promoit.finance.finance_manager.domain.entity.UserEntity;
import com.promoit.finance.finance_manager.domain.entity.WalletEntity;
import com.promoit.finance.finance_manager.domain.exception.user.MailAlreadyExistsException;
import com.promoit.finance.finance_manager.domain.exception.user.UserNotFoundException;
import com.promoit.finance.finance_manager.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    void register_Success() {
        String username = "testuser";
        String password = "password";
        String mail = "existinguser@mail.ru";
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        RegisterRequestDto request = new RegisterRequestDto(mail, username, password);

        when(userRepository.findUserAndReturnDtoByMail(mail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(userId);
            WalletEntity wallet = new WalletEntity();
            wallet.setId(walletId);
            wallet.setUser(user);
            user.setWallet(wallet);
            return user;
        });

        UserResponseDto result = userService.register(request);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(username, result.getUsername());
        assertEquals(walletId, result.getWalletId());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Регистрация с существующим именем пользователя вызывает исключение")
    void register_UserAlreadyExists_ThrowsException() {
        String username = "existinguser";
        String mail = "existinguser@mail.ru";
        String password = "password";
        RegisterRequestDto request = new RegisterRequestDto(mail, username, password);

        when(userRepository.findUserAndReturnDtoByMail(mail)).thenReturn(Optional.of(mock(UserAuthDto.class)));

        assertThrows(MailAlreadyExistsException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Аутентификация несуществующего пользователя вызывает исключение")
    void authenticate_UserNotFound_ThrowsException() {
        String username = "nonexistent";
        String password = "password";

        when(userRepository.findUserAndReturnDtoByMail(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.authenticate(username, password));
    }

    @Test
    @DisplayName("Успешная аутентификация пользователя")
    void authenticate_Success() {
        String username = "testuser";
        String password = "password";
        String mail = "existinguser@mail.ru";
        UUID userId = UUID.randomUUID();

        UserAuthDto userAuthDto = new UserAuthDto(userId, username, mail,"encodedPassword", new WalletEntity());

        when(userRepository.findUserAndReturnDtoByMail(username)).thenReturn(Optional.of(userAuthDto));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);

        UserResponseDto result = userService.authenticate(username, password);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(username, result.getUsername());
    }

}
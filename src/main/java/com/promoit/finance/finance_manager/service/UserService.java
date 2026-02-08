package com.promoit.finance.finance_manager.service;

import com.promoit.finance.finance_manager.domain.dto.user.*;
import com.promoit.finance.finance_manager.domain.entity.PasswordResetTokenEntity;
import com.promoit.finance.finance_manager.domain.entity.UserEntity;
import com.promoit.finance.finance_manager.domain.entity.WalletEntity;
import com.promoit.finance.finance_manager.domain.exception.user.MailAlreadyExistsException;
import com.promoit.finance.finance_manager.domain.exception.user.UserIncorrectPassword;
import com.promoit.finance.finance_manager.domain.exception.user.UserNotFoundException;
import com.promoit.finance.finance_manager.domain.repository.PasswordResetTokenRepository;
import com.promoit.finance.finance_manager.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${app.frontendBaseUrl}")
    private String frontendBaseUrl;
    @Value("${app.mailFrom}")
    private String mailFrom;

    /**
     * Регистрирует нового пользователя в системе финансового менеджера.
     *
     * @param request DTO с данными для регистрации.
     * @return UserResponseDto с данными созданного пользователя и его кошелька
     */
    public UserResponseDto register(RegisterRequestDto request) {
        if (userRepository.findUserAndReturnDtoByMail(request.getMail()).isPresent()) {
            throw new MailAlreadyExistsException("Mail already exists");
        }
        UserEntity user = new UserEntity();
        user.setMail(request.getMail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        WalletEntity wallet = new WalletEntity();
        wallet.setBalance(0.0);
        wallet.setUser(user);

        user.setWallet(wallet);

        UserEntity savedUser = userRepository.save(user);

        return new UserResponseDto(savedUser.getId(), savedUser.getUsername(), savedUser.getMail(), savedUser.getWallet().getId());
    }

    /**
     * Аутентифицирует пользователя в системе. Валидация пользователя с указанным username и соответствие пароля.
     *
     * @param mail     адрес электронной почты пользователя для аутентификации
     * @param password пароль для проверки
     * @return UserResponseDto с данными пользователя в случае успешной аутентификации
     */
    public UserResponseDto authenticate(String mail, String password) {
        UserAuthDto user = userRepository.findUserAndReturnDtoByMail(mail).orElseThrow(
                () -> new UserNotFoundException("User with mail " + mail + " not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserIncorrectPassword("Invalid password");
        }
        return new UserResponseDto(user.getId(), user.getUsername(), user.getMail(), user.getWallet().getId());
    }

    /**
     * Обрабатывает запрос на сброс пароля пользователя. Метод выполняет следующие действия:
     * 1. Проверяет существование пользователя с указанным email (если почта отсутствует - то игнорируем, чтобы не было перебора почт)
     * 2. Генерирует уникальный токен сброса пароля с временем жизни 15 минут
     * 3. Сохраняет токен в базе данных
     * 4. Отправляет email со ссылкой для сброса пароля
     */
    public void passwordResetDesire(PasswordResetDesireDto request) {

        UserEntity userEntity = userRepository.findEntityByMail(request.getMail()).orElse(null);
        if (userEntity == null) {
            log.info("Указанная почта не существует для изменения пароля {}", request.getMail());
            return;
        }

        PasswordResetTokenEntity passwordResetTokenEntity =  PasswordResetTokenEntity.builder()
                .token(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .user(userEntity).build();
        passwordResetTokenRepository.save(passwordResetTokenEntity);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(mailFrom);
        msg.setTo(request.getMail());
        msg.setSubject("Сброс пароля");
        msg.setText("""
                Вы запросили сброс пароля.
                
                Откройте ссылку, чтобы задать новый пароль:
                %s
                
                Если это были не вы — просто игнорируйте письмо.
                """.formatted(frontendBaseUrl + "/reset-password?token=" + passwordResetTokenEntity.getToken()));
        mailSender.send(msg);
    }

    /**
     * Подтверждает сброс пароля пользователя с использованием временного токена. Метод выполняет следующие проверки:
     * А) 1. Существование токена в базе данных / 2. Срок действия токена (не более 15 минут) / 3. Статус токена (не должен быть использован ранее)
     * Б) 1. Хеширует и сохраняет новый пароль пользователя / 2. Помечает токен как использованный / 3. Возвращает обновленные данные пользователя
     * @param request DTO с временным токеном и новым паролем
     * @return UserResponseDto с обновленными данными пользователя
     */
    public UserResponseDto passwordResetConfirm(PasswordResetConfirmRequestDto request) {
        PasswordResetTokenEntity tokenEntity = passwordResetTokenRepository
                .findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Неверный токен"));

        if (tokenEntity.isUsed()) {
            throw new IllegalArgumentException("Токен уже использован");
        }
        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Срок действия токена истек");
        }

        UserEntity user = tokenEntity.getUser();

        // Обновляем пароль
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // Помечаем токен как использованный
        tokenEntity.setUsed(true);
        passwordResetTokenRepository.save(tokenEntity);

        return new UserResponseDto(user.getId(), user.getUsername(), user.getMail(), user.getWallet().getId());
    }

}
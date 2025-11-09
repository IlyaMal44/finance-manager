package com.promoit.finance.finance_manager.service;

import com.promoit.finance.finance_manager.domain.dto.user.UserAuthDto;
import com.promoit.finance.finance_manager.domain.dto.user.UserResponseDto;
import com.promoit.finance.finance_manager.domain.entity.UserEntity;
import com.promoit.finance.finance_manager.domain.entity.WalletEntity;
import com.promoit.finance.finance_manager.domain.exception.user.UserAlreadyExistsException;
import com.promoit.finance.finance_manager.domain.exception.user.UserIncorrectPassword;
import com.promoit.finance.finance_manager.domain.exception.user.UserNotFoundException;
import com.promoit.finance.finance_manager.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Регистрирует нового пользователя в системе финансового менеджера.
     * @param username уникальное имя пользователя для входа в систему
     * @param password пароль пользователя (будет закодирован перед сохранением)
     * @return UserResponseDto с данными созданного пользователя и его кошелька
     */
    public UserResponseDto register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        WalletEntity wallet = new WalletEntity();
        wallet.setBalance(0.0);
        wallet.setUser(user);

        user.setWallet(wallet);

        UserEntity savedUser = userRepository.save(user);

        return new UserResponseDto(savedUser.getId(), savedUser.getUsername(), savedUser.getWallet().getId());
    }

    /**
     * Аутентифицирует пользователя в системе. Валидация пользователя с указанным username и соответствие пароля.
     * @param username имя пользователя для аутентификации
     * @param password пароль для проверки
     * @return UserResponseDto с данными пользователя в случае успешной аутентификации
     */
    public UserResponseDto authenticate(String username, String password) {
        UserAuthDto user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User with name " + username + " not found")
        );
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserIncorrectPassword("Invalid password");
        }
        return new UserResponseDto(user.getId(), user.getUsername(), user.getWallet().getId());
    }
}
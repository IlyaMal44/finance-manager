package com.promoit.finance.finance_manager.domain.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserIncorrectPassword extends RuntimeException {
    public UserIncorrectPassword(String message) {
        super(message);
    }
}
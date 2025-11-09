package com.promoit.finance.finance_manager.domain.exception.user;


public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
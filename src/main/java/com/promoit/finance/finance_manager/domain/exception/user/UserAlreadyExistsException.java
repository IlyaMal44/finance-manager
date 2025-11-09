package com.promoit.finance.finance_manager.domain.exception.user;


public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
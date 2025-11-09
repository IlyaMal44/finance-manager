package com.promoit.finance.finance_manager.domain.exception.user;


public class UserIncorrectPassword extends RuntimeException {
    public UserIncorrectPassword(String message) {
        super(message);
    }
}
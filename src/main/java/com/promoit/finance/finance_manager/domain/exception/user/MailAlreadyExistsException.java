package com.promoit.finance.finance_manager.domain.exception.user;


public class MailAlreadyExistsException extends RuntimeException {
    public MailAlreadyExistsException(String message) {
        super(message);
    }
}
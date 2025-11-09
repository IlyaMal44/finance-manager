package com.promoit.finance.finance_manager.domain.exception.wallet;


public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
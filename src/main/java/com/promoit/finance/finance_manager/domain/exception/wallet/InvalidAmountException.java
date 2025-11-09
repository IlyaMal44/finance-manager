package com.promoit.finance.finance_manager.domain.exception.wallet;


public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
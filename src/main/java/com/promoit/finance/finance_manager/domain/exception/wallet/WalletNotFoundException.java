package com.promoit.finance.finance_manager.domain.exception.wallet;


public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(String message) {
        super(message);
    }
}
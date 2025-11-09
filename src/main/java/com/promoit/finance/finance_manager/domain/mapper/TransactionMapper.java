package com.promoit.finance.finance_manager.domain.mapper;

import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionRequestDto;
import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionResponseDto;
import com.promoit.finance.finance_manager.domain.entity.TransactionEntity;
import com.promoit.finance.finance_manager.domain.entity.WalletEntity;
import java.time.LocalDateTime;

public class TransactionMapper {

    public static TransactionEntity toEntity(WalletEntity wallet, TransactionRequestDto request) {
        return TransactionEntity.builder()
                .type(request.getType())
                .amount(request.getAmount())
                .category(request.getCategory())
                .description(request.getDescription())
                .date(LocalDateTime.now())
                .wallet(wallet)
                .build();
    }

    public static TransactionResponseDto toDto(TransactionEntity entity) {
        return TransactionResponseDto.builder()
                .id(entity.getId())
                .type(entity.getType())
                .amount(entity.getAmount())
                .category(entity.getCategory())
                .description(entity.getDescription())
                .date(entity.getDate())
                .walletId(entity.getWallet().getId())
                .newBalance(entity.getWallet().getBalance()) // текущий баланс после операции
                .build();
    }
}
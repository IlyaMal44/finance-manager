package com.promoit.finance.finance_manager.domain.mapper;

import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionRequestDto;
import com.promoit.finance.finance_manager.domain.dto.transaction.TransactionType;

public class TransferMapper {

    public static TransactionRequestDto toDto(TransactionType type,Double amount,String category,String description){
        return TransactionRequestDto.builder()
                .type(type)
                .amount(amount)
                .category(category)
                .description(description)
                .build();
    }

}

package com.promoit.finance.finance_manager.domain.repository;


import com.promoit.finance.finance_manager.domain.dto.user.UserAuthDto;
import com.promoit.finance.finance_manager.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserAuthDto> findByUsername(String username);
}

package com.promoit.finance.finance_manager.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

/**
 * Данные пользователя.
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private WalletEntity wallet;
}
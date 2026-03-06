package com.example.minibankbackend.transaction.model;



import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="transactions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private TxType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private TxStatus status;

    @Column(nullable=false, precision=19, scale=2)
    private BigDecimal amount;

    private String description;

    @Column(nullable=false)
    private Instant createdAt;
}

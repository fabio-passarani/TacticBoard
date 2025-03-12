package com.tacticboard.core.model.entity.payment;

import com.tacticboard.core.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private String currency;
    
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;
    
    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "invoice_number")
    private String invoiceNumber;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    public enum PaymentMethod {
        CREDIT_CARD,
        PAYPAL,
        BANK_TRANSFER,
        APPLE_PAY,
        GOOGLE_PAY,
        CRYPTO
    }
    
    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED,
        CANCELLED
    }
}
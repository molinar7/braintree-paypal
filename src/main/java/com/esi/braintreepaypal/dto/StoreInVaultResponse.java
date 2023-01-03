package com.esi.braintreepaypal.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class StoreInVaultResponse {
    private String customerId;
    private String paymentMethodToken;
    private boolean isSuccess;
    private BigDecimal amount;
    private String transactionId;
    private String paymentMethodType;
}

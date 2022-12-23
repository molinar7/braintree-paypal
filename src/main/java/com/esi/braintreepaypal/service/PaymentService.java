package com.esi.braintreepaypal.service;

import com.braintreegateway.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private static BraintreeGateway gateway = new BraintreeGateway(
            Environment.SANDBOX,
            "5w4rm77ybnfqpvjb",
            "ncsthn8ncs8m8wmn",
            "84c8c248d0e202a82265ed14d04ba95b"
    );

    public void checkout(String nonceFromTheClient, BigDecimal amount) {

        TransactionRequest request = new TransactionRequest()
                .amount(amount)
                .paymentMethodNonce(nonceFromTheClient)
                //  .deviceData(request.queryParams("device_data"))
                .orderId("Mapped to PayPal Invoice Number")
                .options()
                .submitForSettlement(true)
                .paypal()
                .customField("PayPal custom field")
                .description("Description for PayPal email receipt")
                .done()
                .storeInVaultOnSuccess(true)
                .done();

        Result<Transaction> saleResult = gateway.transaction().sale(request);

        if (saleResult.isSuccess()) {
            Transaction transaction = saleResult.getTarget();
            System.out.println("Success ID: " + transaction.getId());
        } else {
            System.out.println("Message: " + saleResult.getMessage());
        }

    }

    public void storeTransactionInVault(String nonceFromTheClient, BigDecimal amount, String customerId) {

        TransactionRequest request = new TransactionRequest()
                .amount(amount)
                .paymentMethodNonce(nonceFromTheClient)
                .customer()
                .id(customerId)
                .done()
                .options()
                .storeInVaultOnSuccess(true)
                .done();

        Result<Transaction> result = gateway.transaction().sale(request);
        if (result.isSuccess()) {
            Transaction transaction = result.getTarget();
            System.out.println("Success ID: " + transaction.getId());
        } else {
            System.out.println("Message: " + result.getMessage());
        }
    }

    public void useVaultedPaymentMethod(String customerId, String paymentMethodToken, BigDecimal amount) {

        Result<Transaction> result = null;
        if (customerId != null) {
            TransactionRequest request = new TransactionRequest()
                    .amount(amount)
                    .customerId(customerId)
                    .options().submitForSettlement(true).done();

            result = gateway.transaction().sale(request);
        } else if (paymentMethodToken != null) {
            TransactionRequest request = new TransactionRequest()
                    .amount(amount)
                    .paymentMethodToken(paymentMethodToken)
                    .options().submitForSettlement(true).done();

            result = gateway.transaction().sale(request);
        }

        if (result.isSuccess()) {
            Transaction transaction = result.getTarget();
            System.out.println("Success ID: " + transaction.getId());
        } else {
            System.out.println("Message: " + result.getMessage());
        }
    }


}

package com.esi.braintreepaypal.service;

import com.braintreegateway.*;
import com.esi.braintreepaypal.dto.StoreInVaultResponse;
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

    public StoreInVaultResponse storeTransactionInVault(String nonceFromTheClient, BigDecimal amount, String customerId) {

        Result<Transaction> result = null;
        if (checkIfCustomerExist(customerId)) { //Existing customer with new payment method
            TransactionRequest request = new TransactionRequest()
                    .amount(amount)
                    .paymentMethodNonce(nonceFromTheClient)
                    .customerId(customerId)
                    .options()
                    .storeInVaultOnSuccess(true)
                    .done();
            result = gateway.transaction().sale(request);

        } else { // New customer with new payment method
            TransactionRequest request = new TransactionRequest()
                    .amount(amount)
                    .paymentMethodNonce(nonceFromTheClient)
                    .customer()
                    .id(customerId)
                    .done()
                    .options()
                    .storeInVaultOnSuccess(true)
                    .done();

            result = gateway.transaction().sale(request);

        }

        StoreInVaultResponse response = new StoreInVaultResponse();
        response.setCustomerId(customerId);
        response.setAmount(amount);

        if (result.isSuccess()) {
            Transaction transaction = result.getTarget();
            response.setSuccess(true);
            if (transaction.getPayPalDetails() != null) {
                response.setPaymentMethodToken(transaction.getPayPalDetails().getToken());
            } else if (transaction.getCreditCard() != null) {
                response.setPaymentMethodToken(transaction.getCreditCard().getToken());
            }
            System.out.println("Success ID: " + transaction.getId());
        } else {
            response.setSuccess(false);
            System.out.println("Message: " + result.getMessage());
        }

        return response;
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

    private boolean checkIfCustomerExist(String customerId) {
        CustomerSearchRequest request = new CustomerSearchRequest()
                .id().is(customerId);

        return gateway.customer().search(request).getIds().size() > 0;

    }


}

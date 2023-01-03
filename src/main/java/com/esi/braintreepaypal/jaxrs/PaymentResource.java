package com.esi.braintreepaypal.jaxrs;


import com.esi.braintreepaypal.dto.StoreInVaultResponse;
import com.esi.braintreepaypal.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(path = "/api/v1/paymentMethods")
public class PaymentResource {

    @Autowired
    PaymentService paymentService;


    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }


    @PostMapping("/checkout")
    public void checkout(@RequestParam("nonceFromTheClient") String nonceFromTheClient,
                         @RequestParam("amount") BigDecimal amount) {
        paymentService.checkout(nonceFromTheClient, amount);

    }

    @PostMapping("/storeInVault")
    public StoreInVaultResponse storeTransactionInVault(@RequestParam("nonceFromTheClient") String nonceFromTheClient,
                                                        @RequestParam("amount") BigDecimal amount, @RequestParam(value = "customerId", required = false) String customerId) {
        return paymentService.newPaymentMethodAuthorization(nonceFromTheClient, amount, customerId);

    }

    @PostMapping("/vaultedPayment")
    public StoreInVaultResponse useVaultedPaymentMethod( @RequestParam("amount") BigDecimal amount,
            @RequestParam(value = "paymentMethodToken", required = false) String paymentMethodToken) {
        return paymentService.vaultedPaymentMethodAuthorization(paymentMethodToken, amount);

    }

    @PostMapping("/submitForSettlement")
    public void submitForSettlement( @RequestParam("transactionId") String transactionId) {
        paymentService.submitForSettlement(transactionId);

    }


}

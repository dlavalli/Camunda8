package com.lavalliere.daniel.projects.service;

public class CreditCardService {

    public void chargeCreditCard() {
        System.out.println("Charging the credit card...");
    }

    // The service that handles the received credit card requests
    public String chargeCreditCard(
            final String reference,
            final Double amount,
            final String cardNumber,
            final String cardExpiryDate,
            final String cardCVC
    ) {
        System.out.println("Starting transaction: " + reference);
        System.out.println("Card number: " + cardNumber);
        System.out.println("Card Expiry Date: " + cardExpiryDate);
        System.out.println("Card CVC: " + cardCVC);
        System.out.println("Amount: " + amount);

        final String confirmation = String.valueOf(System.currentTimeMillis());
        System.out.println("Successful transaction: " + confirmation);
        return confirmation;
    }
}

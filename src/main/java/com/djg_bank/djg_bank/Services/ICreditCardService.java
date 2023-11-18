package com.djg_bank.djg_bank.Services;

import org.springframework.http.ResponseEntity;

public interface ICreditCardService {
    ResponseEntity<?> createCreditCard(Long id, String cardType);
    ResponseEntity<?> deleteCreditCard(Long id);
}

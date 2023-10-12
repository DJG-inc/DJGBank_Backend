package com.djg_bank.djg_bank.Services;

import org.springframework.http.ResponseEntity;

public interface IDebitCardService {
    ResponseEntity<?> createDebitCard(Long id, String card_type);
}

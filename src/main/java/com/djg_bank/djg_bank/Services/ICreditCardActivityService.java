package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.CreditCardActivityDTO;
import org.springframework.http.ResponseEntity;
public interface ICreditCardActivityService {
    ResponseEntity<?> createCreditCardActivity(Long id, CreditCardActivityDTO creditCardActivityDTO);
}

package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.SavingsAccountDTO;
import org.springframework.http.ResponseEntity;

public interface ISavingAccountsService {

    ResponseEntity<?> createSavingAccount(Long id, SavingsAccountDTO savingsAccountDTO);
}

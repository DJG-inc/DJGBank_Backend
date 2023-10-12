package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.TransactionsDTO;
import org.springframework.http.ResponseEntity;

public interface ITransactionsService {

    ResponseEntity<?> createTransaction(Long id, TransactionsDTO transactionsDTO);

}

package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.LoanDTO;
import org.springframework.http.ResponseEntity;

public interface ILoanService {
    ResponseEntity<?> save(Long id, LoanDTO loanDTO);
    ResponseEntity<?> findAll();
    ResponseEntity<?> findById(Long id);
    ResponseEntity<?> payLoan(Long User_id, Long Loan_id);
}

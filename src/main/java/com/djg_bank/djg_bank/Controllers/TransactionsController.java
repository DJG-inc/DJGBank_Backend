package com.djg_bank.djg_bank.Controllers;

import com.djg_bank.djg_bank.DTOs.TransactionsDTO;
import com.djg_bank.djg_bank.Services.ITransactionsService;
import com.djg_bank.djg_bank.Security.JwtUtils;
import com.djg_bank.djg_bank.Services.Implementations.TransactionsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class TransactionsController {
    private final ITransactionsService transactionsService;

    private final JwtUtils jwtUtils;

    public TransactionsController(TransactionsService transactionsService, JwtUtils jwtUtils) {
        this.transactionsService = transactionsService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody TransactionsDTO transactionsDTO) {
        try {
            if (jwtUtils.validateJwtToken(token)) {
                return transactionsService.createTransaction(id, transactionsDTO);
            } else {
                return ResponseEntity.badRequest().body("Token inválido");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

package com.djg_bank.djg_bank.Controllers;

import com.djg_bank.djg_bank.DTOs.SavingsAccountDTO;
import com.djg_bank.djg_bank.Services.ISavingAccountsService;
import com.djg_bank.djg_bank.Security.JwtUtils;
import com.djg_bank.djg_bank.Services.Implementations.SavingAccountsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/saving-accounts")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class SavingAccountsController {

    private final ISavingAccountsService savingAccountsService;
    private final JwtUtils jwtUtils;

    public SavingAccountsController(SavingAccountsService savingAccountsService, JwtUtils jwtUtils) {
        this.savingAccountsService = savingAccountsService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody SavingsAccountDTO savingsAccountDTO) {
        try {
            if (jwtUtils.validateJwtToken(token)) {
                return savingAccountsService.createSavingAccount(id, savingsAccountDTO);
            } else {
                return ResponseEntity.badRequest().body("Error al crear la cuenta de ahorros");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear la cuenta de ahorros");
        }
    }


}

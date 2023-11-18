package com.djg_bank.djg_bank.Controllers;

import com.djg_bank.djg_bank.DTOs.LoanDTO;
import com.djg_bank.djg_bank.Services.ILoanService;
import com.djg_bank.djg_bank.Security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loan")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class LoanController {

    private final ILoanService loanService;
    private final JwtUtils jwtUtils;

    public LoanController(ILoanService loanService, JwtUtils jwtUtils) {
        this.loanService = loanService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody LoanDTO loanDTO) {
        try {
            if (jwtUtils.validateJwtToken(token)) {
                return loanService.save(id, loanDTO);
            } else {
                return ResponseEntity.badRequest().body("Error al crear el préstamo");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear el préstamo");
        }
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity<?> pay(@RequestHeader("Authorization") String tokenString, @PathVariable Long id, @RequestBody Long loanId) {
        try {
            if (jwtUtils.validateJwtToken(tokenString)) {
                return loanService.payLoan(id, loanId);
            } else {
                return ResponseEntity.badRequest().body("Error al pagar el préstamo");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al pagar el préstamo");
        }
    }


}

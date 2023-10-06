package com.djg_bank.djg_bank.Controllers;

import com.djg_bank.djg_bank.DTOs.LoanDTO;
import com.djg_bank.djg_bank.Security.JwtUtils;
import com.djg_bank.djg_bank.Services.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.OptionalDouble;

@RestController
@RequestMapping("/api/loan")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class LoanController {

    private final LoanService loanService;
    private final JwtUtils jwtUtils;

    public LoanController(LoanService loanService, JwtUtils jwtUtils) {
        this.loanService = loanService;
        this.jwtUtils = jwtUtils;
    }

    //falta poner la verificacion del token
    @PostMapping("/create/{id}")
    public ResponseEntity<?> create(@PathVariable Long id, @RequestBody LoanDTO loanDTO) {
        try {
            return loanService.save(id, loanDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear el préstamo");
        }
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity<?> pay(@PathVariable Long id, @RequestBody Long loanId) {
        try {
            return loanService.payLoan(id, loanId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al pagar el préstamo");
        }
    }


}

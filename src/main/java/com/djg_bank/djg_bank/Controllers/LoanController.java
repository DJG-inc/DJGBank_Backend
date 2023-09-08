package com.djg_bank.djg_bank.Controllers;

import com.djg_bank.djg_bank.DTOs.LoanDTO;
import com.djg_bank.djg_bank.Services.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loan")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<?> create(@PathVariable Long id, @RequestBody LoanDTO loanDTO) {
        try {
            return loanService.save(id, loanDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear el préstamo");
        }
    }


}

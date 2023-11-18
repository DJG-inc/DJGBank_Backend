package com.djg_bank.djg_bank.Controllers;

import com.djg_bank.djg_bank.DTOs.CreditCardActivityDTO;
import com.djg_bank.djg_bank.Services.ICreditCardActivityService;
import com.djg_bank.djg_bank.Security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/creditcardactivity")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class CreditCardActivityController {
    private final ICreditCardActivityService creditCardActivityService;
    private final JwtUtils jwtUtils;

    public CreditCardActivityController(ICreditCardActivityService creditCardActivityService, JwtUtils jwtUtils) {
        this.creditCardActivityService = creditCardActivityService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody CreditCardActivityDTO creditCardActivityDTO) {
        try {
            if (jwtUtils.validateJwtToken(token)) {
                return creditCardActivityService.createCreditCardActivity(id, creditCardActivityDTO);
            } else {
                return ResponseEntity.badRequest().body("Error al crear la actividad de la tarjeta de crédito");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear la actividad de la tarjeta de crédito");
        }
    }
}

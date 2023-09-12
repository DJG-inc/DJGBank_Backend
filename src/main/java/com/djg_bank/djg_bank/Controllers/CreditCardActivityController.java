package com.djg_bank.djg_bank.Controllers;

import com.djg_bank.djg_bank.DTOs.CreditCardActivityDTO;
import com.djg_bank.djg_bank.Security.JwtUtils;
import com.djg_bank.djg_bank.Services.CreditCardActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/creditcardactivity")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class CreditCardActivityController {
    private final CreditCardActivityService creditCardActivityService;
    private final JwtUtils jwtUtils;

    public CreditCardActivityController(CreditCardActivityService creditCardActivityService, JwtUtils jwtUtils) {
        this.creditCardActivityService = creditCardActivityService;
        this.jwtUtils = jwtUtils;
    }

    //falta poner la verificacion del token
    @PostMapping("/create/{id}")
    public ResponseEntity<?> create(@PathVariable Long id, @RequestBody CreditCardActivityDTO creditCardActivityDTO) {
        try {
            return creditCardActivityService.createCreditCardActivity(id, creditCardActivityDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear la actividad de la tarjeta de crédito");
        }
    }
}

package com.djg_bank.djg_bank.Controllers;

import com.djg_bank.djg_bank.Security.JwtUtils;
import com.djg_bank.djg_bank.Services.DebitCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/debitcard")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class DebitCardController {

    private final DebitCardService debitCardService;
    private final JwtUtils jwtUtils;

    public DebitCardController(DebitCardService debitCardService, JwtUtils jwtUtils) {
        this.debitCardService = debitCardService;
        this.jwtUtils = jwtUtils;
    }

    //falta poner la verificacion del token
    @PostMapping("/create/{id}")
    public ResponseEntity<?> create(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        try {
            String cardType = requestBody.get("cardType");
            return debitCardService.createDebitCard(id, cardType);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear la tarjeta de crédito");
        }
    }
}

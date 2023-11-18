package com.djg_bank.djg_bank.Controllers;

import com.djg_bank.djg_bank.Services.ICreditCardService;
import com.djg_bank.djg_bank.Security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/creditcard")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class CreditCardController {

    private final ICreditCardService creditCardService;

    private final JwtUtils jwtUtils;

    public CreditCardController(ICreditCardService creditCardService, JwtUtils jwtUtils) {
        this.creditCardService = creditCardService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        try {
              if (jwtUtils.validateJwtToken(token)) {
                String cardType = requestBody.get("cardType");
                return creditCardService.createCreditCard(id, cardType);
              } else {
                return ResponseEntity.badRequest().body("Error al crear la tarjeta de crédito");
              }
          } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error al crear la tarjeta de crédito");
          }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            if (jwtUtils.validateJwtToken(token)) {
                return creditCardService.deleteCreditCard(id);
            } else {
                return ResponseEntity.badRequest().body("Error al eliminar la tarjeta de crédito");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar la tarjeta de crédito");
        }
    }

}

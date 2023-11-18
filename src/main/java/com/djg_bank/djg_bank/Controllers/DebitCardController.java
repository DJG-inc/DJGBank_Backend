package com.djg_bank.djg_bank.Controllers;

import com.djg_bank.djg_bank.Services.IDebitCardService;
import com.djg_bank.djg_bank.Security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/debitcard")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class DebitCardController {

    private final IDebitCardService debitCardService;
    private final JwtUtils jwtUtils;

    public DebitCardController(IDebitCardService debitCardService, JwtUtils jwtUtils) {
        this.debitCardService = debitCardService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        try {
           if (jwtUtils.validateJwtToken(token)) {
               String cardType = requestBody.get("cardType");
               return debitCardService.createDebitCard(id, cardType);
           } else {
               return ResponseEntity.badRequest().body("Error al crear la tarjeta de débito");
           }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear la tarjeta de débito");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            if (jwtUtils.validateJwtToken(token)) {
                return debitCardService.deleteDebitCard(id);
            } else {
                return ResponseEntity.badRequest().body("Error al eliminar la tarjeta de débito");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar la tarjeta de débito");
        }
    }
}

package com.djg_bank.djg_bank.Controllers;

import com.djg_bank.djg_bank.DTOs.IpAdressDTO;
import com.djg_bank.djg_bank.Security.JwtUtils;
import com.djg_bank.djg_bank.Services.IipAdressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ipadress")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class IpAdressController {

    private final IipAdressService ipAdressService;

    public IpAdressController(IipAdressService ipAdressService) {
        this.ipAdressService = ipAdressService;
    }

    @PostMapping("/registerIp/{id}")
    public ResponseEntity<?> create(@PathVariable Long id, @RequestBody IpAdressDTO ipAdressDTO) {
        try {
            return ipAdressService.save(id, ipAdressDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear la direccion ip");
        }
    }

    @PostMapping("/verifyIp/{userId}")
    public ResponseEntity<?> verifyIp(@PathVariable Long userId, @RequestBody Map<String, String> requestBody) {
        try {
            String ip = requestBody.get("ip");
            return ipAdressService.verifyIp(userId, ip);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al verificar la direccion ip");
        }
    }

    @PostMapping("/verifyCode/{userId}/{verificationCode}")
    public ResponseEntity<?> addIpAndVerifyCode(@PathVariable Long userId, @PathVariable String verificationCode, @RequestBody Map<String, String> requestBody) {
        try {
            String newIp = requestBody.get("newIp");
            return ipAdressService.addIpAndVerifyCode(userId, newIp, verificationCode);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al verificar la direccion ip");
        }
    }
}

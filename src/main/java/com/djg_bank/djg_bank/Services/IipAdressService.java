package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.IpAdressDTO;
import org.springframework.http.ResponseEntity;

public interface IipAdressService {
    ResponseEntity<?> save(Long id, IpAdressDTO ipAdressDTO);
    ResponseEntity<?> verifyIp(Long id, String ip);
    ResponseEntity<?> addIpAndVerifyCode(Long userId, String newIp, String verificationCode);
}

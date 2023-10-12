package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.UserDTO;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    ResponseEntity<?> register(UserDTO userDTO);
    ResponseEntity<?> resentConfrmationemail(String token);
    ResponseEntity<?> confirmEmail(String token);
    ResponseEntity<?> completeRegister(Long id, UserDTO userDTO);
    ResponseEntity<?> login(UserDTO userDTO);
    ResponseEntity<?> findById(Long id);
    ResponseEntity<?> findAll();
    ResponseEntity<?> updateUser(Long id, UserDTO userDTO);
    ResponseEntity<?> forgotPassword(String email);
    ResponseEntity<?> resetPassword(String token, String password);
}

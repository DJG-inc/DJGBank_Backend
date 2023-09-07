package com.djg_bank.djg_bank.Controllers;

import com.djg_bank.djg_bank.DTOs.UserDTO;
import com.djg_bank.djg_bank.Security.JwtUtils;
import com.djg_bank.djg_bank.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        if (jwtUtils.validateJwtToken(token)) {
            return userService.findById(id);
        } else {
            return ResponseEntity.badRequest().body("Token no válido");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll(@RequestHeader("Authorization") String token) {
        if (jwtUtils.validateJwtToken(token)) {
            return userService.findAll();
        } else {
            return ResponseEntity.badRequest().body("Token no válido");
        }
    }

}

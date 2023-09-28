package com.djg_bank.djg_bank.Controllers;

import com.djg_bank.djg_bank.DTOs.UserDTO;
import com.djg_bank.djg_bank.Security.JwtUtils;
import com.djg_bank.djg_bank.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class  UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        try {
            return userService.register(userDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar el usuario");
        }
    }

    @PostMapping("/confirm-email/{token}")
    public ResponseEntity<?> confirmEmail (@PathVariable String token) {
        try {
            return userService.confirmEmail(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al confirmar el email");
        }
    }

    @PostMapping("/complete-register/{id}")
    public ResponseEntity<?> completeRegister(@PathVariable Long id, @RequestBody UserDTO updatedUserDTO){
        try{
            return userService.completeRegister(id, updatedUserDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al completar el registor del usuario");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        try {
            return userService.login(userDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al iniciar sesión");
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            if (jwtUtils.validateJwtToken(token)) {
                return userService.findById(id);
            } else {
                return ResponseEntity.badRequest().body("Token no válido");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al obtener el usuario");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll( String token) {
        try {
            return userService.findAll();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al obtener los usuarios");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody UserDTO updatedUserDTO) {
        ResponseEntity<?> response;
        try {
            if (jwtUtils.validateJwtToken(token)) {
                response = userService.updateUser(id, updatedUserDTO);
            } else {
                response = ResponseEntity.badRequest().body("Token no válido");
            }
        } catch (Exception e) {
            response = ResponseEntity.badRequest().body("Error al actualizar el usuario");
        }
        return response;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> requestBody) {
        try {
            String email = requestBody.get("email");
            return userService.forgotPassword(email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al recuperar la contraseña");
        }
    }

    @PostMapping("/restore-password/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable String token, @RequestBody Map<String, String> requestBody) {
        try {
            String password = requestBody.get("password");
            return userService.resetPassword(token, password);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al resetear la contraseña");
        }
    }

    @PostMapping("/resend-email/{token}")
    public ResponseEntity<?> resendEmail(@PathVariable String token) {
        try {
            return userService.resentConfrmationemail(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al reenviar el email");
        }
    }

}

package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.UserDTO;
import com.djg_bank.djg_bank.Mapper.UserMapper;
import com.djg_bank.djg_bank.Models.UserModel;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
import com.djg_bank.djg_bank.Security.Bcrypt;
import com.djg_bank.djg_bank.Security.JwtUtils;
import com.djg_bank.djg_bank.Utils.ErrorResponse;
import io.micrometer.common.util.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;
    private final Bcrypt bcrypt;
    private final JwtUtils jwtUtils;

    public UserService(IUserRepository userRepository, UserMapper userMapper, Bcrypt bcrypt, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.bcrypt = bcrypt;
        this.jwtUtils = jwtUtils;
    }

    public ResponseEntity<?> register(UserDTO userDTO) {
        try {
            // Validación de datos
            if (StringUtils.isEmpty(userDTO.getEmail()) || StringUtils.isEmpty(userDTO.getPassword())) {
                return new ResponseEntity<>(new ErrorResponse("Correo electrónico y contraseña son obligatorios"), HttpStatus.BAD_REQUEST);
            }

            if (!isValidEmail(userDTO.getEmail())) {
                return new ResponseEntity<>(new ErrorResponse("Formato de correo electrónico no válido"), HttpStatus.BAD_REQUEST);
            }

            // Verificar si el correo ya está en uso
            UserModel existingUser = userRepository.findByEmail(userDTO.getEmail());
            if (existingUser != null) {
                return new ResponseEntity<>(new ErrorResponse("Ya existe un usuario con ese correo"), HttpStatus.BAD_REQUEST);
            }

            // Hash de la contraseña
            String password = userDTO.getPassword();
            String passwordBcrypt = bcrypt.passwordEncoder().encode(password);
            userDTO.setPassword(passwordBcrypt);

            // Guardar el usuario
            UserModel userModel = userMapper.toUSerModel(userDTO);
            UserModel savedUser = userRepository.save(userModel);

            return new ResponseEntity<>(userMapper.toUserDTO(savedUser), HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ErrorResponse("Error de integridad de datos al registrar el usuario"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Error al registrar el usuario: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    private boolean isValidEmail(String email) {
        // Expresión regular para validar una dirección de correo electrónico básica.
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }

    public ResponseEntity<?> Login(UserDTO userDTO) {
        try {
            // Validación de datos
            if (StringUtils.isEmpty(userDTO.getEmail()) || StringUtils.isEmpty(userDTO.getPassword())) {
                return new ResponseEntity<>(new ErrorResponse("Correo electrónico y contraseña son obligatorios"), HttpStatus.BAD_REQUEST);
            }

            // Verificar si el correo ya está en uso
            UserModel existingUser = userRepository.findByEmail(userDTO.getEmail());
            if (existingUser == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese correo"), HttpStatus.BAD_REQUEST);
            }

            // Verificar si la contraseña es correcta
            if (!bcrypt.passwordEncoder().matches(userDTO.getPassword(), existingUser.getPassword())) {
                return new ResponseEntity<>(new ErrorResponse("Contraseña incorrecta"), HttpStatus.BAD_REQUEST);
            }

            // Generar el token por el id del usuario
            String token = jwtUtils.generateJwtToken(existingUser.getId());
            existingUser.setToken(token);

            return new ResponseEntity<>(existingUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Error al iniciar sesión: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> findById(Long id) {
        try {
            UserModel user = this.userRepository.findById(id).orElse(null);
            if (user != null) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese ID"), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception error) {
            return new ResponseEntity<>(new ErrorResponse("Error al obtener el usuario"), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> findAll() {
        try {
            return new ResponseEntity<>(this.userRepository.findAll(), HttpStatus.OK);
        } catch (Exception error) {
            return new ResponseEntity<>(new ErrorResponse("Error al obtener los usuarios"), HttpStatus.BAD_REQUEST);
        }
    }

}
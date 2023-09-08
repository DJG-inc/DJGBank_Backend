package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.UserDTO;
import com.djg_bank.djg_bank.Mapper.UserMapper;
import com.djg_bank.djg_bank.Models.UserModel;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
import com.djg_bank.djg_bank.Security.Bcrypt;
import com.djg_bank.djg_bank.Security.JwtUtils;
import com.djg_bank.djg_bank.Utils.ErrorResponse;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

            // Parsear la fecha de nacimiento al formato esperado
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy"); // Formato de entrada
            Date parsedDate = inputDateFormat.parse(userDTO.getDate_of_birth());

            // Hash de la contraseña
            String password = userDTO.getPassword();
            String passwordBcrypt = bcrypt.passwordEncoder().encode(password);
            userDTO.setPassword(passwordBcrypt);

            // Guardar el usuario
            UserModel userModel = userMapper.toUserModel(userDTO);
            userModel.setDate_of_birth(String.valueOf(parsedDate));
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

    public ResponseEntity<?> login(UserDTO userDTO) {
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


    @Transactional
    public ResponseEntity<?> updateUser(Long id, UserDTO updatedUserDTO) {
        try {
            // Verificar si el usuario existe
            UserModel userToUpdate = this.userRepository.findById(id).orElse(null);
            if (userToUpdate == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese ID"), HttpStatus.BAD_REQUEST);
            }

            // Actualizar todos los campos del usuario existente en función de los datos proporcionados en updatedUserDTO
            if (updatedUserDTO.getEmail() != null) {
                // Validar el formato de correo electrónico si es necesario
                if (!isValidEmail(updatedUserDTO.getEmail())) {
                    return new ResponseEntity<>(new ErrorResponse("Formato de correo electrónico no válido"), HttpStatus.BAD_REQUEST);
                }
                userToUpdate.setEmail(updatedUserDTO.getEmail());
            }

            if (updatedUserDTO.getDate_of_birth() != null) {
                // Validar el formato de fecha de nacimiento si es necesario
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date parsedDate = dateFormat.parse(updatedUserDTO.getDate_of_birth());
                    userToUpdate.setDate_of_birth(String.valueOf(parsedDate));
                } catch (ParseException e) {
                    return new ResponseEntity<>(new ErrorResponse("Formato de fecha de nacimiento no válido"), HttpStatus.BAD_REQUEST);
                }
            }

            // Actualizar el resto de campos
            userToUpdate.setUser_id(updatedUserDTO.getUser_id());
            userToUpdate.setFirst_name(updatedUserDTO.getFirst_name());
            userToUpdate.setLast_name(updatedUserDTO.getLast_name());
            userToUpdate.setAddress(updatedUserDTO.getAddress());
            userToUpdate.setPhone_number(updatedUserDTO.getPhone_number());


            // Encriptar la contraseña si es necesario
            String password = updatedUserDTO.getPassword();
            if (StringUtils.isNotEmpty(password)) {
                String passwordBcrypt = bcrypt.passwordEncoder().encode(password);
                userToUpdate.setPassword(passwordBcrypt);
            }

            // Guardar los cambios en el usuario existente
            UserModel updatedUser = userRepository.save(userToUpdate);

            return new ResponseEntity<>(userMapper.toUserDTO(updatedUser), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ErrorResponse("Error de integridad de datos al actualizar el usuario"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Error al actualizar el usuario: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}

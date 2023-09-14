package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.UserDTO;
import com.djg_bank.djg_bank.Mapper.UserMapper;
import com.djg_bank.djg_bank.Models.UserModel;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
import com.djg_bank.djg_bank.Security.Bcrypt;
import com.djg_bank.djg_bank.Security.JwtUtils;
import com.djg_bank.djg_bank.Utils.EmailService;
import com.djg_bank.djg_bank.Utils.ErrorResponse;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.apache.coyote.Response;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;
    private final Bcrypt bcrypt;
    private final JwtUtils jwtUtils;

    private final EmailService emailService;

    public UserService(IUserRepository userRepository, UserMapper userMapper, Bcrypt bcrypt, JwtUtils jwtUtils, EmailService emailService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.bcrypt = bcrypt;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
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

            // Verificar si el usuario ya existe
            UserModel existingUser = userRepository.findByEmail(userDTO.getEmail());
            if (existingUser != null) {
                return new ResponseEntity<>(new ErrorResponse("El usuario ya existe"), HttpStatus.BAD_REQUEST);
            }

            // Hash de la contraseña
            String password = userDTO.getPassword();
            String passwordBcrypt = bcrypt.passwordEncoder().encode(password);
            userDTO.setPassword(passwordBcrypt);

            // Guardar el usuario
            UserModel userModel = userMapper.toUserModel(userDTO);
            userModel.setStatus("pending");
            UserModel savedUser = userRepository.save(userModel);

            String token = jwtUtils.generateJwtToken(savedUser.getId());

            // Enviar correo electrónico de bienvenida
            try {
                String subject = "Bienvenido a DJG Bank";
                String email_content = getEmailContent(token);

                emailService.sendEmail(savedUser.getEmail(), subject, email_content);
            } catch (Exception e) {
                return new ResponseEntity<>(new ErrorResponse("Error al enviar el correo electrónico de confirmación"), HttpStatus.BAD_REQUEST);
            }
            System.out.println(token);
            return new ResponseEntity<>(userMapper.toUserDTO(savedUser), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Error al registrar el usuario: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    private static String getEmailContent(String token) {
        String confirmation_url = "http://localhost:4200/reset-password/" + token;
        return "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "    <link href=\"https://fonts.googleapis.com/css2?family=Goldman&display=swap\" rel=\"stylesheet\">" +
                        "</head>" +
                        "<body>" +
                        "    <div style='background-color: #191A15; font-family: Goldman, sans-serif; text-align: center; color: #ffffff; padding: 20px; width: 100%;'>" +
                        "        <img src=\"https://www.dropbox.com/scl/fi/2nqt4izlkkc7il74un235/Group-1.png?rlkey=7n6wz5zp54xs0lavohntrso4h&raw=1\" alt=\"Descripción de la imagen\" style='max-width: 100%; height: auto;'>" +
                        "        <h1 style='color: #ffffff; font-size: 5vw; margin: 20px 0;'>Has empezado tu vida económica con los <span style='color: #B6E72B;'>mejores</span></h1>" +
                        "        <p style='color: #ffffff; font-size: 20px;'>Bienvenido, nos alegra saber que estas con nosotros, ahora falta poco, porfavor confirma tu correo en el siguiente enlace:</p>" +
                        "        <a href=\"" + confirmation_url + "\" style='color: #B6E72B; font-size: 20px; text-decoration: none;'>Confirmar correo</a>" +
                        "        <h2 style='color: #ffffff; font-size: 4vw; margin: 20px 0;'>Bank <span style='color: #B6E72B;'>easy</span>, bank <span style='color: #B6E72B;'>DJG</span>.</h2>" +
                        "    </div>" +
                        "</body>" +
                        "</html>";
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private static final Pattern USERID_PATTERN = Pattern.compile("^[1-9][0-9]{7,9}$");

    private boolean isValidUser_id(String user_id) {
        if (user_id == null) {
            return false;
        }
        return USERID_PATTERN.matcher(user_id).matches();
    }

    public ResponseEntity<?> confirmEmail(String token) {
        try {
            // Validación de datos
            if (StringUtils.isEmpty(token)) {
                return new ResponseEntity<>(new ErrorResponse("Token es obligatorio"), HttpStatus.BAD_REQUEST);
            }

            // Verificar si el token es válido
            if (!jwtUtils.validateJwtToken(token)) {
                return new ResponseEntity<>(new ErrorResponse("Token no válido"), HttpStatus.BAD_REQUEST);
            }

            // Verificar si el usuario existe
            Long userId = jwtUtils.getIdFromJwtToken(token);
            UserModel userToUpdate = this.userRepository.findById(userId).orElse(null);
            if (userToUpdate == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese ID"), HttpStatus.BAD_REQUEST);
            }

            // Actualizar el estado del usuario
            userToUpdate.setStatus("confirmed");

            // Guardar los cambios en el usuario existente
            UserModel updatedUser = userRepository.save(userToUpdate);

            //enviar correo de confirmacion
            try {
                String subject = "Bienvenido a DJG Bank";
                String email_content =
                        "<!DOCTYPE html>" +
                                "<html>" +
                                "<head>" +
                                "    <link href=\"https://fonts.googleapis.com/css2?family=Goldman&display=swap\" rel=\"stylesheet\">" +
                                "</head>" +
                                "<body>" +
                                "    <div style='background-color: #191A15; font-family: Goldman, sans-serif; text-align: center; color: #ffffff; padding: 20px; width: 100%;'>" +
                                "        <img src=\"https://www.dropbox.com/scl/fi/2nqt4izlkkc7il74un235/Group-1.png?rlkey=7n6wz5zp54xs0lavohntrso4h&raw=1\" alt=\"Descripción de la imagen\" style='max-width: 100%; height: auto;'>" +
                                "        <h1 style='color: #ffffff; font-size: 5vw; margin: 20px 0;'>Has empezado tu vida económica con los <span style='color: #B6E72B;'>mejores</span></h1>" +
                                "        <p style='color: #ffffff; font-size: 20px;'>Bienvenido, nos alegra tenerte con nosotros, lo que sigue ahora es terminar tu registro, porfavor ingresa a la siguiente dirección:</p>" +
                                "        <a href=\"http://localhost:4200/login\" style='color: #B6E72B; font-size: 20px; text-decoration: none;'>Iniciar sesión</a>" +
                                "        <h2 style='color: #ffffff; font-size: 4vw; margin: 20px 0;'>Bank <span style='color: #B6E72B;'>easy</span>, bank <span style='color: #B6E72B;'>DJG</span>.</h2>" +
                                "    </div>" +
                                "</body>" +
                                "</html>";

                emailService.sendEmail(updatedUser.getEmail(), subject, email_content);
            } catch (Exception e) {
                return new ResponseEntity<>(new ErrorResponse("Error al enviar el correo electrónico de confirmación"), HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Error al confirmar el correo electrónico: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ErrorResponse("Correo electrónico confirmado correctamente"), HttpStatus.OK);
    }

    public ResponseEntity<?> login(UserDTO userDTO) {
        try {
            // Validación de datos
            if (StringUtils.isEmpty(userDTO.getUser_id()) || StringUtils.isEmpty(userDTO.getPassword())) {
                return new ResponseEntity<>(new ErrorResponse("Cédula y contraseña son obligatorios"), HttpStatus.BAD_REQUEST);
            }

            // Verificar si el correo ya está en uso
            UserModel existingUser = userRepository.findByUser_id(userDTO.getUser_id());
            if (existingUser == null) {
                return new ResponseEntity<>(new ErrorResponse("Las credenciales proporcionadas son incorrectas. Por favor, inténtalo de nuevo."), HttpStatus.BAD_REQUEST);
            }

            // Verificar si la contraseña es correcta
            if (!bcrypt.passwordEncoder().matches(userDTO.getPassword(), existingUser.getPassword())) {
                return new ResponseEntity<>(new ErrorResponse("Las credenciales proporcionadas son incorrectas. Por favor, inténtalo de nuevo."), HttpStatus.BAD_REQUEST);
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
                if (isValidEmail(updatedUserDTO.getEmail())) {
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

    public ResponseEntity<?> forgotPassword(String email) {
        try {
            // Validación de datos
            if (StringUtils.isEmpty(email)) {
                return new ResponseEntity<>(new ErrorResponse("Correo electrónico es obligatorio"), HttpStatus.BAD_REQUEST);
            }

            // Verificar si el correo ya está en uso
            UserModel existingUser = userRepository.findByEmail(email);
            if (existingUser == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese correo electrónico"), HttpStatus.BAD_REQUEST);
            }

            // Generar el token por el id del usuario
            String token = jwtUtils.generateJwtToken(existingUser.getId());

            // Enviar correo electrónico de restablecimiento de contraseña
            try {
                String subject = "Restablecer contraseña";
                String email_content =
                        "<!DOCTYPE html>" +
                                "<html>" +
                                "<head>" +
                                "    <link href=\"https://fonts.googleapis.com/css2?family=Goldman&display=swap\" rel=\"stylesheet\">" +
                                "</head>" +
                                "<body>" +
                                "    <div style='background-color: #191A15; font-family: Goldman, sans-serif; text-align: center; color: #ffffff; padding: 20px; width: 100%;'>" +
                                "        <img src=\"https://www.dropbox.com/scl/fi/2nqt4izlkkc7il74un235/Group-1.png?rlkey=7n6wz5zp54xs0lavohntrso4h&raw=1\" alt=\"Descripción de la imagen\" style='max-width: 100%; height: auto;'>" +
                                "        <h1 style='color: #ffffff; font-size: 5vw; margin: 20px 0;'>Has solicitado restablecer tu contraseña en DJG Bank <span style='color: #B6E72B;'>easy</span></h1>" +
                                "        <p style='color: #ffffff; font-size: 20px;'>Hola " + existingUser.getFirst_name() + ",</p>" +
                                "        <p style='color: #ffffff; font-size: 20px;'>Para restablecer tu contraseña, haz click en el siguiente enlace:</p>" +
                                "        <a href=\"http://localhost:4200/reset-password/" + token + "\" style='color: #B6E72B; font-size: 20px; text-decoration: none;'>Restablecer contraseña</a>" +
                                "        <p style='color: #ffffff; font-size: 20px;'>Si no has solicitado restablecer tu contraseña, ignora este correo.</p>" +
                                "        <p style='color: #ffffff; font-size: 20px;'>Saludos,</p>" +
                                "        <p style='color: #ffffff; font-size: 20px;'>El equipo de DJG Bank</p>" +
                                "    </div>" +
                                "</body>" +
                                "</html>";

                emailService.sendEmail(existingUser.getEmail(), subject, email_content);
            } catch (Exception e) {
                return new ResponseEntity<>(new ErrorResponse("Error al enviar el correo electrónico de restablecimiento de contraseña"), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(new ErrorResponse("Se ha enviado un correo electrónico a " + email + " con instrucciones para restablecer tu contraseña"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Error al restablecer la contraseña: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> resetPassword(String token, String password) {
        try {
            // Validación de datos
            if (StringUtils.isEmpty(token) || StringUtils.isEmpty(password)) {
                return new ResponseEntity<>(new ErrorResponse("Token y contraseña son obligatorios"), HttpStatus.BAD_REQUEST);
            }

            // Verificar si el token es válido
            if (!jwtUtils.validateJwtToken(token)) {
                return new ResponseEntity<>(new ErrorResponse("Token no válido"), HttpStatus.BAD_REQUEST);
            }

            // Verificar si el usuario existe
            Long userId = jwtUtils.getIdFromJwtToken(token);
            UserModel userToUpdate = this.userRepository.findById(userId).orElse(null);
            if (userToUpdate == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese ID"), HttpStatus.BAD_REQUEST);
            }

            // Encriptar la contraseña
            String passwordBcrypt = bcrypt.passwordEncoder().encode(password);
            userToUpdate.setPassword(passwordBcrypt);

            // Guardar los cambios en el usuario existente
            UserModel updatedUser = userRepository.save(userToUpdate);

            //enviar correo de confirmacion
            try {
                String subject = "Contraseña restablecida";
                String email_content =
                        "<!DOCTYPE html>" +
                                "<html>" +
                                "<head>" +
                                "    <link href=\"https://fonts.googleapis.com/css2?family=Goldman&display=swap\" rel=\"stylesheet\">" +
                                "</head>" +
                                "<body>" +
                                "    <div style='background-color: #191A15; font-family: Goldman, sans-serif; text-align: center; color: #ffffff; padding: 20px; width: 100%;'>" +
                                "        <img src=\"https://www.dropbox.com/scl/fi/2nqt4izlkkc7il74un235/Group-1.png?rlkey=7n6wz5zp54xs0lavohntrso4h&raw=1\" alt=\"Descripción de la imagen\" style='max-width: 100%; height: auto;'>" +
                                "        <h1 style='color: #ffffff; font-size: 5vw; margin: 20px 0;'>Tu contraseña ha sido restablecida en DJG Bank <span style='color: #B6E72B;'>easy</span></h1>" +
                                "        <p style='color: #ffffff; font-size: 20px;'>Hola " + updatedUser.getFirst_name() + ",</p>" +
                                "        <p style='color: #ffffff; font-size: 20px;'>Tu contraseña ha sido restablecida correctamente.</p>" +
                                "        <p style='color: #ffffff; font-size: 20px;'>Saludos,</p>" +
                                "        <p style='color: #ffffff; font-size: 20px;'>El equipo de DJG Bank</p>" +
                                "    </div>" +
                                "</body>" +
                                "</html>";

                emailService.sendEmail(updatedUser.getEmail(), subject, email_content);
            } catch (Exception e) {
                return new ResponseEntity<>(new ErrorResponse("Error al enviar el correo electrónico de confirmación"), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(userMapper.toUserDTO(updatedUser), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ErrorResponse("Error de integridad de datos al actualizar el usuario"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Error al actualizar el usuario: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
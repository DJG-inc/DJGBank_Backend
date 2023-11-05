package com.djg_bank.djg_bank.Services.Implementations;

import com.djg_bank.djg_bank.DTOs.IpAdressDTO;
import com.djg_bank.djg_bank.Mapper.IpAdressMapper;
import com.djg_bank.djg_bank.Models.IpAdressModel;
import com.djg_bank.djg_bank.Models.UserModel;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
import com.djg_bank.djg_bank.Repositories.IipAdressRepository;
import com.djg_bank.djg_bank.Security.Bcrypt;
import com.djg_bank.djg_bank.Services.IipAdressService;
import com.djg_bank.djg_bank.Utils.EmailService;
import com.djg_bank.djg_bank.Utils.ErrorResponse;
import com.djg_bank.djg_bank.Utils.ResourcesBank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class IpAdressService implements IipAdressService {

    private final IipAdressRepository ipAdressRepository;
    private final IpAdressMapper ipAdressMapper;
    private final IUserRepository userRepository;
    private final Bcrypt bcrypt;
    private final ResourcesBank resourcesBank;
    private final EmailService emailService;

    public IpAdressService(IipAdressRepository ipAdressRepository, IpAdressMapper ipAdressMapper, IUserRepository userRepository, Bcrypt bcrypt, ResourcesBank resourcesBank, EmailService emailService) {
        this.ipAdressRepository = ipAdressRepository;
        this.ipAdressMapper = ipAdressMapper;
        this.userRepository = userRepository;
        this.bcrypt = bcrypt;
        this.resourcesBank = resourcesBank;
        this.emailService = emailService;
    }

    @Override
    public ResponseEntity<?> save(Long id, IpAdressDTO ipAdressDTO) {
        try {
            // Buscar el usuario por su ID
            UserModel user = this.userRepository.findById(id).orElse(null);
            if (user == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese ID"), HttpStatus.BAD_REQUEST);
            }

            // Verificar que el usuario no tenga una IP registrada
            IpAdressModel ipAdress = this.ipAdressRepository.findByUser(user);
            if (ipAdress != null) {
                return new ResponseEntity<>(new ErrorResponse("El usuario ya tiene una IP registrada"), HttpStatus.BAD_REQUEST);
            }

            // Verificar que la IP no esté registrada
            IpAdressModel ipAdress2 = this.ipAdressRepository.findByIp(ipAdressDTO.getIp());
            if (ipAdress2 != null) {
                return new ResponseEntity<>(new ErrorResponse("La IP ya está registrada"), HttpStatus.BAD_REQUEST);
            }

            //hashear la ip
            String ip = ipAdressDTO.getIp();
            String ipHashed = bcrypt.passwordEncoder().encode(ip);
            ipAdressDTO.setIp(ipHashed);

            // Guardar la IP en la base de datos
            IpAdressModel newIpAdress = this.ipAdressMapper.toIpAdressModel(ipAdressDTO);
            newIpAdress.setUser(user);
            this.ipAdressRepository.save(newIpAdress);

            // Retornar la IP
            return new ResponseEntity<>(newIpAdress, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Error al registrar la IP"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> verifyIp(Long userId, String ip) {
        try {
            // Buscar la IP en la base de datos del usuario
            UserModel user = this.userRepository.findById(userId).orElse(null);
            if (user == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese ID"), HttpStatus.BAD_REQUEST);
            }

            // Verificar que la IP sea la misma
            IpAdressModel ipAdress = this.ipAdressRepository.findByUser(user);
            if (ipAdress == null) {
                return new ResponseEntity<>(new ErrorResponse("El usuario no tiene una IP registrada"), HttpStatus.BAD_REQUEST);
            }

            if (!bcrypt.passwordEncoder().matches(ip, ipAdress.getIp())) {
                // Si no es la misma, enviar un correo al usuario
                try {
                    String subject = "Inicio de sesión desconocido";
                    String verification_code = resourcesBank.generateRandomCode();
                    String email_content =
                            "<! DOCTYPE html>" +
                                    "<html>" +
                                    "<head>" +
                                    " <link href=\"https://fonts.googleapis.com/css2?family=Goldman&display=swap\" rel=\"stylesheet\">" +
                                    "</head>" +
                                    "<body>" +
                                    " <div style='background-color: #191A15; font-family: Goldman, sans-serif; text-align: center; color: #ffffff; padding: 20px; width: 100%;' >" +
                                    " <img src=\"https://www.dropbox.com/scl/fi/2nqt4izlkkc7il74un235/Group-1.png?rlkey=7n6wz5zp54xs0lavohntrso4h&raw=1\" alt=\"Descripción de la imagen\" style='max-width: 100%; height: auto;' >" +
                                    " <h1 style='color: #ffffff; font-size: 5vw; margin: 20px 0;' >nuevo inicio de sesion desde <span style='color: #B6E72B;' >\'" + ip + "\'</span></h1>" +
                                    " <p style='color: #ffffff; font-size: 20px;' >Si fuiste tu este es tu codigo de confirmacion: <span style='color: #B6E72B;' >\'" + verification_code + "\'</span></p>" + "<p style='color: #ffffff; font-size: 20px;' >Si no fuiste tu, cambia tu contraseña inmediatamente</p>" +
                                    " <a href=\"http://localhost:5173/verifyCode/" + ipAdress.getUser().getId() + "/" + verification_code + "\" style='color: #B6E72B; font-size: 20px;' >Verificar</a>" +
                                    " <h2 style='color: #ffffff; font-size: 4vw; margin: 20px 0;' >Bank <span style='color: #B6E72B;' >easy</span>, bank <span style='color: #B6E72B;' >DJG</span>.</h2>" +
                                    " </div>" +
                                    "</body>" +
                                    "</html>";
                    emailService.sendEmail(user.getEmail(), subject, email_content);
                    user.setVerification_code(verification_code);
                    userRepository.save(user);

                } catch (Exception e) {
                    return new ResponseEntity<>(new ErrorResponse("Error al enviar el correo"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

            return new ResponseEntity<>("Código de verificación enviado con éxito", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Error al verificar la IP"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> addIpAndVerifyCode(Long userId, String newIp, String verificationCode) {
        try {
            // Buscar el usuario por su ID
            UserModel user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese ID"), HttpStatus.BAD_REQUEST);
            }

            // Verificar el código de verificación
            if (!verificationCode.equals(user.getVerification_code())) {
                return new ResponseEntity<>(new ErrorResponse("Código de verificación incorrecto"), HttpStatus.BAD_REQUEST);
            }

            // Verificar si la nueva IP está registrada
            IpAdressModel ipAdress = ipAdressRepository.findByIp(newIp);
            if (ipAdress != null) {
                return new ResponseEntity<>(new ErrorResponse("La nueva IP ya está registrada"), HttpStatus.BAD_REQUEST);
            }

            //hashear la ip
            String ipHashed = bcrypt.passwordEncoder().encode(newIp);
            newIp = ipHashed;

            // Crear un nuevo objeto IpAdressModel
            IpAdressModel newIpAdress = new IpAdressModel();
            newIpAdress.setIp(newIp);
            newIpAdress.setUser(user);

            // Guardar la nueva IP en la base de datos
            ipAdressRepository.save(newIpAdress);

            // Borrar el código de verificación
            user.setVerification_code(null);
            userRepository.save(user);

            return new ResponseEntity<>("Nueva IP registrada con éxito", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Error al agregar la nueva IP"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

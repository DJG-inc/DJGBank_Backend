package com.djg_bank.djg_bank.Services.Implementations;

import com.djg_bank.djg_bank.DTOs.CreditCardDTO;
import com.djg_bank.djg_bank.Services.ICreditCardService;
import com.djg_bank.djg_bank.Mapper.CreditCardMapper;
import com.djg_bank.djg_bank.Models.CreditCardModel;
import com.djg_bank.djg_bank.Models.UserModel;
import com.djg_bank.djg_bank.Repositories.ICreditCardRepository;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
import com.djg_bank.djg_bank.Utils.ErrorResponse;
import com.djg_bank.djg_bank.Utils.ResourcesBank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class CreditCardService implements ICreditCardService {
    private final ICreditCardRepository creditCardRepository;
    private final CreditCardMapper creditCardMapper;
    private final IUserRepository userRepository;
    private final ResourcesBank resourcesBank;

    public CreditCardService(ICreditCardRepository creditCardRepository, CreditCardMapper creditCardMapper, IUserRepository userRepository, ResourcesBank resourcesBank) {
        this.creditCardRepository = creditCardRepository;
        this.creditCardMapper = creditCardMapper;
        this.userRepository = userRepository;
        this.resourcesBank = resourcesBank;
    }

    @Override
    public ResponseEntity<?> createCreditCard(Long id, String cardType) {
        try {
            // Buscar el usuario por su ID
            UserModel user = this.userRepository.findById(id).orElse(null);
            if (user == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese ID"), HttpStatus.BAD_REQUEST);
            }

            // Obtener la fecha de nacimiento del usuario como String en el formato "1/1/2000"
            String dateOfBirthString = user.getDate_of_birth();

            // Parsear la fecha de nacimiento en el formato adecuado
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date dateOfBirth = dateFormat.parse(dateOfBirthString);

            // Calcular la edad del usuario a partir de la fecha de nacimiento
            int age = this.resourcesBank.calculateAge(dateOfBirth);

            // Verificar que el usuario tenga al menos 18 años
            if (age < 18) {
                return new ResponseEntity<>(new ErrorResponse("El usuario debe tener al menos 18 años para solicitar un préstamo"), HttpStatus.BAD_REQUEST);
            }

            // Validar el tipo de tarjeta (Visa o MasterCard)
            if (this.resourcesBank.isValidCardType(cardType)) {
                return new ResponseEntity<>(new ErrorResponse("Tipo de tarjeta no válido"), HttpStatus.BAD_REQUEST);
            }

            // Generar un número de tarjeta válido según el tipo seleccionado
            String cardNumber = this.resourcesBank.generateValidCardNumber(cardType);

            // Crear la tarjeta de crédito con valores automáticos
            CreditCardModel creditCardModel = new CreditCardModel();
            creditCardModel.setCard_number(cardNumber);
            creditCardModel.setCard_type(cardType);

            // Generar y establecer la fecha de vencimiento (expiry_date) automáticamente
            Date expiryDate = this.resourcesBank.generateExpiryDate();
            creditCardModel.setExpiry_date(expiryDate);

            // Generar y establecer un CVV aleatorio de tres dígitos
            String cvv = this.resourcesBank.generateRandomCVV();
            creditCardModel.setCvv(cvv);

            // Establecer la fecha de emisión (date_issued) como la fecha actual
            Date dateIssued = new Date();
            creditCardModel.setDate_issued(dateIssued);

            // Establecer el límite de crédito (credit_limit) y el saldo actual (current_debt) con valores iniciales
            creditCardModel.setCredit_limit(50000.0);
            creditCardModel.setCurrent_debt(0.0);

            // Asociar la tarjeta de crédito al usuario
            creditCardModel.setUser(user);

            // Guardar la tarjeta de crédito en la base de datos
            this.creditCardRepository.save(creditCardModel);

            // Retornar la tarjeta de crédito creada en forma de DTO
            CreditCardDTO creditCardDTO = this.creditCardMapper.toCreditCardDTO(creditCardModel);
            return new ResponseEntity<>(creditCardDTO, HttpStatus.CREATED);

        } catch (Exception error) {
            // Manejar errores y retornar una respuesta apropiada
            System.out.println(error);
            return new ResponseEntity<>(new ErrorResponse("Error al crear la tarjeta de crédito"), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> deleteCreditCard(Long id) {
        try {
            // Buscar la tarjeta de crédito por su ID
            CreditCardModel creditCard = this.creditCardRepository.findById(id).orElse(null);
            if (creditCard == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe una tarjeta de crédito con ese ID"), HttpStatus.BAD_REQUEST);
            }

            //verificar que la tarjeta de credito no tenga deuda
            if(creditCard.getCurrent_debt() > 0){
                return new ResponseEntity<>(new ErrorResponse("La tarjeta de crédito tiene deuda"), HttpStatus.BAD_REQUEST);
            }

            long user_id = creditCard.getUser().getId();

            // Buscar el usuario por su ID
            UserModel user = this.userRepository.findById(user_id).orElse(null);
            if (user == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese ID"), HttpStatus.BAD_REQUEST);
            }

            // Eliminar la tarjeta de crédito del usuario
            user.getCredit_cards().remove(creditCard);

            // Eliminar la tarjeta de crédito de la base de datos
            this.creditCardRepository.delete(creditCard);

            // Retornar la tarjeta de crédito eliminada
            return new ResponseEntity<>(creditCard, HttpStatus.OK);
        } catch (Exception error) {
            // Manejar errores y retornar una respuesta apropiada
            System.out.println(error);
            return new ResponseEntity<>(new ErrorResponse("Error al eliminar la tarjeta de crédito"), HttpStatus.BAD_REQUEST);
        }
    }

}

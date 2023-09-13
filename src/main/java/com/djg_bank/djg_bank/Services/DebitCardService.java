package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.DebitCardsDTO;
import com.djg_bank.djg_bank.Mapper.DebitCardsMapper;
import com.djg_bank.djg_bank.Models.DebitCardsModel;
import com.djg_bank.djg_bank.Models.SavingsAccountModel;
import com.djg_bank.djg_bank.Models.UserModel;
import com.djg_bank.djg_bank.Repositories.IDebitCardsRepository;
import com.djg_bank.djg_bank.Repositories.ISavingsAccountRepository;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
import com.djg_bank.djg_bank.Utils.ErrorResponse;
import com.djg_bank.djg_bank.Utils.ResourcesBank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class DebitCardService {
    private final IDebitCardsRepository debitCardRepository;
    private final DebitCardsMapper debitCardMapper;
    private final ISavingsAccountRepository savingsAccountRepository;
    private final IUserRepository userRepository;
    private final ResourcesBank resourcesBank;

    public DebitCardService(IDebitCardsRepository debitCardRepository, DebitCardsMapper debitCardMapper, ISavingsAccountRepository savingsAccountRepository, IUserRepository userRepository, ResourcesBank resourcesBank) {
        this.debitCardRepository = debitCardRepository;
        this.debitCardMapper = debitCardMapper;
        this.savingsAccountRepository = savingsAccountRepository;
        this.userRepository = userRepository;
        this.resourcesBank = resourcesBank;
    }

    public ResponseEntity<?> createDebitCard(Long id, String cardType) {
        try {
            // Buscar la cuenta de ahorros por su ID
            SavingsAccountModel savingsAccount = this.savingsAccountRepository.findById(id).orElse(null);
            if (savingsAccount == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe una cuenta de ahorros con ese ID"), HttpStatus.BAD_REQUEST);
            }

            //buscar el usuario por su ID
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

            // Crear la tarjeta de débito
            DebitCardsModel debitCard = new DebitCardsModel();
            debitCard.setCard_number(cardNumber);
            debitCard.setCard_type(cardType);

            // Generar y establecer la fecha de vencimiento (expiry_date) automáticamente
            Date expiryDate = this.resourcesBank.generateExpiryDate();
            debitCard.setExpiry_date(expiryDate);

            // Generar y establecer un CVV aleatorio de tres dígitos
            String cvv = this.resourcesBank.generateRandomCVV();
            debitCard.setCvv(cvv);

            // Establecer la fecha de emisión (date_issued) como la fecha actual
            Date dateIssued = new Date();
            debitCard.setDate_issued(dateIssued);

            // Asociar la tarjeta de débito a la cuenta de ahorros
            debitCard.setSavings_account(savingsAccount);

            // Guardar la tarjeta de débito en la base de datos
            debitCard = this.debitCardRepository.save(debitCard);

            // Mapear la tarjeta de débito a un DTO
            DebitCardsDTO debitCardDTO = this.debitCardMapper.toDebitCardsDTO(debitCard);

            // Retornar la tarjeta de débito creada
            return new ResponseEntity<>(debitCardDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.printf("Error al crear la tarjeta de débito: %s\n", e.getMessage());
            return new ResponseEntity<>(new ErrorResponse("Error al crear la tarjeta de débito"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

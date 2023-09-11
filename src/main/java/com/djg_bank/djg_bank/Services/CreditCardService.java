package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.CreditCardDTO;
import com.djg_bank.djg_bank.Mapper.CreditCardMapper;
import com.djg_bank.djg_bank.Models.CreditCardModel;
import com.djg_bank.djg_bank.Models.UserModel;
import com.djg_bank.djg_bank.Repositories.ICreditCardRepository;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
import com.djg_bank.djg_bank.Utils.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@Service
public class CreditCardService {
    private final ICreditCardRepository creditCardRepository;
    private final CreditCardMapper creditCardMapper;
    private final IUserRepository userRepository;

    public CreditCardService(ICreditCardRepository creditCardRepository, CreditCardMapper creditCardMapper, IUserRepository userRepository) {
        this.creditCardRepository = creditCardRepository;
        this.creditCardMapper = creditCardMapper;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> createCreditCard(Long id, String cardType) {
        try {
            // Buscar el usuario por su ID
            UserModel user = this.userRepository.findById(id).orElse(null);
            if (user == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese ID"), HttpStatus.BAD_REQUEST);
            }

            // Validar el tipo de tarjeta (Visa o MasterCard)
            if (!isValidCardType(cardType)) {
                return new ResponseEntity<>(new ErrorResponse("Tipo de tarjeta no válido"), HttpStatus.BAD_REQUEST);
            }

            // Generar un número de tarjeta válido según el tipo seleccionado
            String cardNumber = generateValidCardNumber(cardType);

            // Crear la tarjeta de crédito con valores automáticos
            CreditCardModel creditCardModel = new CreditCardModel();
            creditCardModel.setCard_number(cardNumber);
            creditCardModel.setCard_type(cardType);

            // Generar y establecer la fecha de vencimiento (expiry_date) automáticamente
            Date expiryDate = generateExpiryDate();
            creditCardModel.setExpiry_date(expiryDate);

            // Generar y establecer un CVV aleatorio de tres dígitos
            String cvv = generateRandomCVV();
            creditCardModel.setCvv(cvv);

            // Establecer la fecha de emisión (date_issued) como la fecha actual
            Date dateIssued = new Date();
            creditCardModel.setDate_issued(dateIssued);

            // Establecer el límite de crédito (credit_limit) y el saldo actual (current_debt) con valores iniciales
            creditCardModel.setCredit_limit(1000.0);
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

    private boolean isValidCardType(String cardType) {
        // Validar que el tipo de tarjeta sea "Visa" o "MasterCard"
        if (cardType == null) {
            return false;
        }
        else return cardType.equalsIgnoreCase("Visa") || cardType.equalsIgnoreCase("MasterCard");
    }

    private String generateValidCardNumber(String cardType) {
        // Generar un número de tarjeta válido según el tipo seleccionado (Visa o MasterCard)
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        cardNumber.append(cardType.equalsIgnoreCase("Visa") ? "4" : "5"); // Prefijo para Visa o MasterCard

        for (int i = 0; i < 15; i++) {
            cardNumber.append(random.nextInt(10)); // Generar los 15 dígitos restantes
        }

        // Aplicar el algoritmo de Luhn para generar el dígito de verificación
        cardNumber.append(generateLuhnDigit(cardNumber.toString()));

        return cardNumber.toString();
    }

    private int generateLuhnDigit(String cardNumber) {
        // Implementar el algoritmo de Luhn para generar el dígito de verificación
        int sum = 0;
        boolean doubleDigit = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            doubleDigit = !doubleDigit;
        }

        return (sum * 9) % 10;
    }

    private Date generateExpiryDate() {
        // Generar la fecha de vencimiento como 5 años a partir de la fecha actual
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.YEAR, 5);
        return calendar.getTime();
    }

    private String generateRandomCVV() {
        // Generar un CVV aleatorio de tres dígitos
        Random random = new Random();
        int cvv = random.nextInt(1000); // Número aleatorio de 0 a 999
        return String.format("%03d", cvv); // Formatear como cadena de tres dígitos
    }
}

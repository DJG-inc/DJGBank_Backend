package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.CreditCardActivityDTO;
import com.djg_bank.djg_bank.Mapper.CreditCardActivityMapper;
import com.djg_bank.djg_bank.Models.CreditCardActivityModel;
import com.djg_bank.djg_bank.Models.CreditCardModel;
import com.djg_bank.djg_bank.Models.UserModel;
import com.djg_bank.djg_bank.Repositories.ICreditCardActivityRepository;
import com.djg_bank.djg_bank.Repositories.ICreditCardRepository;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
import com.djg_bank.djg_bank.Utils.ErrorResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CreditCardActivityService {
    private final ICreditCardActivityRepository creditCardActivityRepository;
    private final CreditCardActivityMapper creditCardActivityMapper;
    private final ICreditCardRepository creditCardRepository;
    private final IUserRepository userRepository;

    public CreditCardActivityService(ICreditCardActivityRepository creditCardActivityRepository, CreditCardActivityMapper creditCardActivityMapper, ICreditCardRepository creditCardRepository, IUserRepository userRepository) {
        this.creditCardActivityRepository = creditCardActivityRepository;
        this.creditCardActivityMapper = creditCardActivityMapper;
        this.creditCardRepository = creditCardRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResponseEntity<?> createCreditCardActivity(Long id, CreditCardActivityDTO creditCardActivityDTO) {
        try {
            // Buscar la tarjeta de crédito por su ID
            CreditCardModel creditCard = this.creditCardRepository.findById(id).orElse(null);
            if (creditCard == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe una tarjeta de crédito con ese ID"), HttpStatus.BAD_REQUEST);
            }

            UserModel user = creditCard.getUser();

            // Verificar que el monto de la actividad sea mayor a 0
            if (creditCardActivityDTO.getAmount() <= 0) {
                return new ResponseEntity<>(new ErrorResponse("El monto de la actividad debe ser mayor a 0"), HttpStatus.BAD_REQUEST);
            }

            // Verificar que el tipo de actividad sea válido
            if (!isValidActivityType(creditCardActivityDTO.getType())) {
                return new ResponseEntity<>(new ErrorResponse("Tipo de actividad no válido"), HttpStatus.BAD_REQUEST);
            }

            //verificar que el monto de la actividad sea menor al limite de la tarjeta
            if (creditCardActivityDTO.getAmount() > creditCard.getCredit_limit()) {
                return new ResponseEntity<>(new ErrorResponse("El monto de la actividad debe ser menor al limite de la tarjeta"), HttpStatus.BAD_REQUEST);
            }

            //la fecha de la actividad es la fecha actual
            Date date = new Date();
            creditCardActivityDTO.setDate_of_transaction(date);

//            Verificar que sea CHARGE o CASH_ADVANCE y que el monto sumado con la deuda actucal no exceda el limite de la tarjeta
            if ((creditCardActivityDTO.getType().equals("CHARGE") || creditCardActivityDTO.getType().equals("CASH_ADVANCE")) && creditCardActivityDTO.getAmount() + creditCard.getCurrent_debt() > creditCard.getCredit_limit()) {
                return new ResponseEntity<>(new ErrorResponse("El monto de la actividad sumado con la deuda actual de la tarjeta debe ser menor al limite de la tarjeta"), HttpStatus.BAD_REQUEST);
            }

            //verificar que el tipo de actividad sea un pago y que el monto sea menor a la deuda actual de la tarjeta
            if (creditCardActivityDTO.getType().equals("PAYMENT") && creditCardActivityDTO.getAmount() > creditCard.getCurrent_debt()) {
                return new ResponseEntity<>(new ErrorResponse("El monto del pago debe ser menor a la deuda actual de la tarjeta"), HttpStatus.BAD_REQUEST);
            }

            //la descripcion de la actividad es el tipo de actividad mas el monto de la actividad en string concatenado con un espacio en medio de ambos datos (ejemplo: CHARGE 1000)
            creditCardActivityDTO.setDescription(creditCardActivityDTO.getType() + " " + creditCardActivityDTO.getAmount());

            //establecer el nuevo limite de la tarjeta
            switch (creditCardActivityDTO.getType()) {
                case "CHARGE" ->
                        creditCard.setCurrent_debt(creditCard.getCurrent_debt() + creditCardActivityDTO.getAmount());
                case "PAYMENT" -> {
                    creditCard.setCurrent_debt(creditCard.getCurrent_debt() - creditCardActivityDTO.getAmount());
                    user.getSavings_account().setBalance(user.getSavings_account().getBalance() - creditCardActivityDTO.getAmount());
                }
                case "CASH_ADVANCE" -> {
                    creditCard.setCurrent_debt(creditCard.getCurrent_debt() + creditCardActivityDTO.getAmount());
//                Update the savings account balance
                    user.getSavings_account().setBalance(user.getSavings_account().getBalance() + creditCardActivityDTO.getAmount());
                }
            }

            //guardar la tarjeta de credito y el usuario
            this.userRepository.save(user);
            this.creditCardRepository.save(creditCard);

            //guardar la actividad de la tarjeta de credito
            CreditCardActivityModel creditCardActivity = this.creditCardActivityMapper.toCreditCardActivityModel(creditCardActivityDTO);
            creditCardActivity.setCredit_card(creditCard);
            this.creditCardActivityRepository.save(creditCardActivity);

            return new ResponseEntity<>(creditCardActivity, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(new ErrorResponse("Error al crear la actividad de la tarjeta de crédito"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean isValidActivityType(String activityType) {
        return activityType.equals("CHARGE") || activityType.equals("PAYMENT") || activityType.equals("CASH_ADVANCE");
    }

}
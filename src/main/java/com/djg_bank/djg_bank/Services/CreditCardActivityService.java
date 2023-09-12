package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.CreditCardActivityDTO;
import com.djg_bank.djg_bank.Mapper.CreditCardActivityMapper;
import com.djg_bank.djg_bank.Models.CreditCardActivityModel;
import com.djg_bank.djg_bank.Models.CreditCardModel;
import com.djg_bank.djg_bank.Repositories.ICreditCardActivityRepository;
import com.djg_bank.djg_bank.Repositories.ICreditCardRepository;
import com.djg_bank.djg_bank.Utils.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CreditCardActivityService {
    private final ICreditCardActivityRepository creditCardActivityRepository;
    private final CreditCardActivityMapper creditCardActivityMapper;
    private final ICreditCardRepository creditCardRepository;

    public CreditCardActivityService(ICreditCardActivityRepository creditCardActivityRepository, CreditCardActivityMapper creditCardActivityMapper, ICreditCardRepository creditCardRepository) {
        this.creditCardActivityRepository = creditCardActivityRepository;
        this.creditCardActivityMapper = creditCardActivityMapper;
        this.creditCardRepository = creditCardRepository;
    }

    public ResponseEntity<?> createCreditCardActivity(Long id, CreditCardActivityDTO creditCardActivityDTO) {
        try {
            // Buscar la tarjeta de crédito por su ID
            CreditCardModel creditCard = this.creditCardRepository.findById(id).orElse(null);
            if (creditCard == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe una tarjeta de crédito con ese ID"), HttpStatus.BAD_REQUEST);
            }

            // Verificar que el monto de la actividad sea mayor a 0
            if (creditCardActivityDTO.getAmount() <= 0) {
                return new ResponseEntity<>(new ErrorResponse("El monto de la actividad debe ser mayor a 0"), HttpStatus.BAD_REQUEST);
            }

            // Verificar que el tipo de actividad sea válido
            if (!isValidActivityType(creditCardActivityDTO.getType())) {
                return new ResponseEntity<>(new ErrorResponse("Tipo de actividad no válido"), HttpStatus.BAD_REQUEST);
            }

            //verificar que el monto de la actividad sea menor al limite de la tarjeta
            if(creditCardActivityDTO.getAmount() > creditCard.getCredit_limit()){
                return new ResponseEntity<>(new ErrorResponse("El monto de la actividad debe ser menor al limite de la tarjeta"), HttpStatus.BAD_REQUEST);
            }

            //la fecha de la actividad es la fecha actual
            Date date = new Date();
            creditCardActivityDTO.setDate_of_transaction(date);

            //verificar que el tipo de actividad sea un pago y que el monto sea menor al limite de la tarjeta
            if(creditCardActivityDTO.getType().equals("PAYMENT") && creditCardActivityDTO.getAmount() > creditCard.getCredit_limit()){
                return new ResponseEntity<>(new ErrorResponse("El monto de la actividad debe ser menor al limite de la tarjeta"), HttpStatus.BAD_REQUEST);
            }

            //la descripcion de la actividad es el tipo de actividad mas el monto de la actividad en string concatenado con un espacio en medio de ambos datos (ejemplo: CHARGE 1000)
            creditCardActivityDTO.setDescription(creditCardActivityDTO.getType() + " " + creditCardActivityDTO.getAmount());

            //establecer el nuevo limite de la tarjeta
            if(creditCardActivityDTO.getType().equals("CHARGE")){
                creditCard.setCredit_limit(creditCard.getCredit_limit() - creditCardActivityDTO.getAmount());
            }else{
                creditCard.setCredit_limit(creditCard.getCredit_limit() + creditCardActivityDTO.getAmount());
            }

            //guardar la tarjeta de credito
            this.creditCardRepository.save(creditCard);

            //guardar la actividad de la tarjeta de credito
            CreditCardActivityModel creditCardActivity = this.creditCardActivityMapper.toCreditCardActivityModel(creditCardActivityDTO);
            creditCardActivity.setCredit_card(creditCard);
            this.creditCardActivityRepository.save(creditCardActivity);

            return new ResponseEntity<>(creditCardActivity, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Error al crear la actividad de la tarjeta de crédito"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean isValidActivityType(String activityType) {
        return activityType.equals("CHARGE") || activityType.equals("PAYMENT");
    }


}

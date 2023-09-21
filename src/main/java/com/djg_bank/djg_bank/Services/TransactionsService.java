package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.TransactionsDTO;
import com.djg_bank.djg_bank.Mapper.TransactionsMapper;
import com.djg_bank.djg_bank.Models.DebitCardsModel;
import com.djg_bank.djg_bank.Models.SavingsAccountModel;
import com.djg_bank.djg_bank.Models.TransactionsModel;
import com.djg_bank.djg_bank.Repositories.IDebitCardsRepository;
import com.djg_bank.djg_bank.Repositories.ISavingsAccountRepository;
import com.djg_bank.djg_bank.Repositories.ITransactionsRepository;
import com.djg_bank.djg_bank.Utils.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TransactionsService {

    private final ITransactionsRepository transactionsRepository;

    private final TransactionsMapper transactionsMapper;

    private final IDebitCardsRepository debitCardsRepository;
    private final ISavingsAccountRepository savingsAccountRepository;

    public TransactionsService(ITransactionsRepository transactionsRepository, TransactionsMapper transactionsMapper, IDebitCardsRepository debitCardsRepository, ISavingsAccountRepository savingsAccountRepository) {
        this.transactionsRepository = transactionsRepository;
        this.transactionsMapper = transactionsMapper;
        this.debitCardsRepository = debitCardsRepository;
        this.savingsAccountRepository = savingsAccountRepository;
    }

    public ResponseEntity<?> createTransaction(Long id, TransactionsDTO transactionsDTO) {
        try {
            // Buscar la tarjeta de débito por su ID
            DebitCardsModel debitCards = this.debitCardsRepository.findById(id).orElse(null);
            if (debitCards == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe una tarjeta de débito con ese ID"), HttpStatus.BAD_REQUEST);
            }
            // verificar que el monto de la transaccion sea mayor a 0
            if (transactionsDTO.getAmount() <= 0){
                return new ResponseEntity<>(new ErrorResponse("El monto de la transaccion debe ser mayor a 0"), HttpStatus.BAD_REQUEST);
            }

            Date date = new Date();
            transactionsDTO.setDate_of_transaction(date);

            // verificar la descripcion de la transaccion
            if (transactionsDTO.getDescription().length() > 100){
                return new ResponseEntity<>(new ErrorResponse("La descripcion de la transaccion no puede ser mayor a 100 caracteres"), HttpStatus.BAD_REQUEST);
            }

            // verificar que el monto de la transaccion sea menor al monto de la tarjeta
            if (transactionsDTO.getAmount() < debitCards.getSavings_account().getBalance()){
                return new ResponseEntity<>(new ErrorResponse("El monto de la transaccion debe ser menor al monto de la tarjeta"), HttpStatus.BAD_REQUEST);
            }

            // establecer el nuevo balance de la tarjeta
            debitCards.getSavings_account().setBalance(debitCards.getSavings_account().getBalance() - transactionsDTO.getAmount());

            // restar el monto de la transaccion al balance de la cuenta de ahorros
            SavingsAccountModel savingsAccountModel = debitCards.getSavings_account();
            savingsAccountModel.setBalance(savingsAccountModel.getBalance() - transactionsDTO.getAmount());
            this.savingsAccountRepository.save(savingsAccountModel);

            // Guardar la transaccion
            this.debitCardsRepository.save(debitCards);

            // Guardar la transaccion en la tarjeta de debito
            TransactionsModel transactionsModel = this.transactionsMapper.toTransactionsModel(transactionsDTO);
            transactionsModel.setDebit_card(debitCards);
            this.transactionsRepository.save(transactionsModel);

            return new ResponseEntity<>(transactionsModel, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Error al crear la transaccion"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}

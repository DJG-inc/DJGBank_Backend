package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.TransactionsDTO;
import com.djg_bank.djg_bank.Mapper.TransactionsMapper;
import com.djg_bank.djg_bank.Models.DebitCardsModel;
import com.djg_bank.djg_bank.Models.SavingsAccountModel;
import com.djg_bank.djg_bank.Models.TransactionsModel;
import com.djg_bank.djg_bank.Models.UserModel;
import com.djg_bank.djg_bank.Repositories.IDebitCardsRepository;
import com.djg_bank.djg_bank.Repositories.ISavingsAccountRepository;
import com.djg_bank.djg_bank.Repositories.ITransactionsRepository;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
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
    private final IUserRepository userRepository;

    public TransactionsService(ITransactionsRepository transactionsRepository, TransactionsMapper transactionsMapper, IDebitCardsRepository debitCardsRepository, ISavingsAccountRepository savingsAccountRepository, IUserRepository userRepository) {
        this.transactionsRepository = transactionsRepository;
        this.transactionsMapper = transactionsMapper;
        this.debitCardsRepository = debitCardsRepository;
        this.savingsAccountRepository = savingsAccountRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> createTransaction(Long id, TransactionsDTO transactionsDTO) {
        try {
            // Buscar la tarjeta de débito por su ID
            DebitCardsModel debitCards = this.debitCardsRepository.findById(id).orElse(null);
            if (debitCards == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe una tarjeta de débito con ese ID"), HttpStatus.BAD_REQUEST);
            }

            // Buscar la cuenta de ahorros por su número
            SavingsAccountModel savingsAccount = this.savingsAccountRepository.findByNumber(transactionsDTO.getNumber_of_savings_account()).orElse(null);
            if (savingsAccount == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe una cuenta de ahorros con ese número"), HttpStatus.BAD_REQUEST);
            }

            //buscar usuario por el user_id
            UserModel user = this.userRepository.findByUser_id(transactionsDTO.getUser_id());
            if (user == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese user_id"), HttpStatus.BAD_REQUEST);
            }

            //verificar que la tarjeta de debito pertenezca al usuario
            if (debitCards.getSavings_account().getUser().getUser_id() != user.getUser_id()) {
                return new ResponseEntity<>(new ErrorResponse("La tarjeta de débito no pertenece al usuario"), HttpStatus.BAD_REQUEST);
            }

            //verificar que la cuenta de ahorros pertenezca al usuario
            if (savingsAccount.getUser().getUser_id() != user.getUser_id()) {
                return new ResponseEntity<>(new ErrorResponse("La cuenta de ahorros no pertenece al usuario"), HttpStatus.BAD_REQUEST);
            }

            //verificar que el monto sea mayor a 0
            if (transactionsDTO.getAmount() <= 0) {
                return new ResponseEntity<>(new ErrorResponse("El monto debe ser mayor a 0"), HttpStatus.BAD_REQUEST);
            }

            //verificar que el monto sea menor al saldo de la cuenta de ahorros
            if (transactionsDTO.getAmount() > savingsAccount.getBalance()) {
                return new ResponseEntity<>(new ErrorResponse("El monto debe ser menor al saldo de la cuenta de ahorros"), HttpStatus.BAD_REQUEST);
            }

            //verificar que el monto sea menor al saldo de la tarjeta de debito
            if (transactionsDTO.getAmount() > debitCards.getSavings_account().getBalance()) {
                return new ResponseEntity<>(new ErrorResponse("El monto debe ser menor al saldo de la tarjeta de debito"), HttpStatus.BAD_REQUEST);
            }

            // la fecha de la transaccion es la fecha actual
            Date date = new Date();
            transactionsDTO.setDate_of_transaction(date);

            //la descripcion es la que viene en el request
            transactionsDTO.setDescription(transactionsDTO.getDescription());

            //crear la transaccion
            this.transactionsRepository.save(this.transactionsMapper.toTransactionsModel(transactionsDTO));

            //actualizar el saldo de la cuenta de ahorros
            savingsAccount.setBalance(savingsAccount.getBalance() - transactionsDTO.getAmount());
            this.savingsAccountRepository.save(savingsAccount);

            //actualizar el saldo de la tarjeta de debito
            debitCards.getSavings_account().setBalance(debitCards.getSavings_account().getBalance() - transactionsDTO.getAmount());
            this.savingsAccountRepository.save(debitCards.getSavings_account());

            //retornar la transaccion creada
            return new ResponseEntity<>(transactionsDTO, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

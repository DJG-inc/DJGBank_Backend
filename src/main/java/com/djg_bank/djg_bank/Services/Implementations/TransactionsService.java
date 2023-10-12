package com.djg_bank.djg_bank.Services.Implementations;

import com.djg_bank.djg_bank.DTOs.TransactionsDTO;
import com.djg_bank.djg_bank.Services.ITransactionsService;
import com.djg_bank.djg_bank.Mapper.TransactionsMapper;
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
import java.util.Objects;

@Service
public class TransactionsService implements ITransactionsService {

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

    @Override
    public ResponseEntity<?> createTransaction(Long id, TransactionsDTO transactionsDTO) {
        try {
            System.out.println(transactionsDTO);

            UserModel user = this.userRepository.findById(id).orElse(null);
            if (user == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese ID"), HttpStatus.BAD_REQUEST);
            }

            SavingsAccountModel savingsAccount = user.getSavings_account();

            // Verify the savings account belongs to the user
            if (!Objects.equals(savingsAccount.getUser().getId(), user.getId())) {
                return new ResponseEntity<>(new ErrorResponse("La cuenta de ahorros no pertenece al usuario"), HttpStatus.BAD_REQUEST);
            }

            UserModel user_to_send = this.userRepository.findByUser_id(transactionsDTO.getUser_id());
            if (user_to_send == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese user_id"), HttpStatus.BAD_REQUEST);
            }

            System.out.println(transactionsDTO.getNumber_of_savings_account());
            SavingsAccountModel savingsAccount_to_send = this.savingsAccountRepository.findByAccount_number(transactionsDTO.getNumber_of_savings_account()).orElse(null);
            System.out.println(savingsAccount_to_send);
            if (savingsAccount_to_send == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe una cuenta de ahorros con ese número"), HttpStatus.BAD_REQUEST);
            }

            // Verify the recipient's savings account belongs to them
            if (!Objects.equals(savingsAccount_to_send.getUser().getId(), user_to_send.getId())) {
                return new ResponseEntity<>(new ErrorResponse("La cuenta de ahorros no pertenece al usuario"), HttpStatus.BAD_REQUEST);
            }

            // Ensure amount is greater than 0
            if (transactionsDTO.getAmount() <= 0) {
                return new ResponseEntity<>(new ErrorResponse("El monto debe ser mayor a 0"), HttpStatus.BAD_REQUEST);
            }

            // Ensure amount is less than or equal to the sender's balance
            if (transactionsDTO.getAmount() > savingsAccount.getBalance()) {
                return new ResponseEntity<>(new ErrorResponse("El monto debe ser menor al saldo de la cuenta de ahorros"), HttpStatus.BAD_REQUEST);
            }

            Date date = new Date();
            transactionsDTO.setDate_of_transaction(date);

            // Description from the request
            transactionsDTO.setDescription(transactionsDTO.getDescription());

            // Create and save the transaction
            TransactionsModel newTransaction = this.transactionsMapper.toTransactionsModel(transactionsDTO);
            newTransaction.setSavings_account(savingsAccount);
            this.transactionsRepository.save(newTransaction);

            // Update the sender's savings account balance
            savingsAccount.setBalance(savingsAccount.getBalance() - transactionsDTO.getAmount());
            this.savingsAccountRepository.save(savingsAccount);

            // Update the recipient's savings account balance
            savingsAccount_to_send.setBalance(savingsAccount_to_send.getBalance() + transactionsDTO.getAmount());
            this.savingsAccountRepository.save(savingsAccount_to_send);

            return new ResponseEntity<>(transactionsDTO, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.djg_bank.djg_bank.Services.Implementations;

import com.djg_bank.djg_bank.DTOs.SavingsAccountDTO;
import com.djg_bank.djg_bank.Services.ISavingAccountsService;
import com.djg_bank.djg_bank.Mapper.SavingAccountMapper;
import com.djg_bank.djg_bank.Models.SavingsAccountModel;
import com.djg_bank.djg_bank.Models.UserModel;
import com.djg_bank.djg_bank.Repositories.ISavingsAccountRepository;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
import com.djg_bank.djg_bank.Utils.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class SavingAccountsService implements ISavingAccountsService {

    private final ISavingsAccountRepository savingsAccountRepository;
    private final SavingAccountMapper savingAccountMapper;
    private final IUserRepository userRepository;

    public SavingAccountsService(ISavingsAccountRepository savingsAccountRepository, SavingAccountMapper savingAccountMapper, IUserRepository userRepository) {
        this.savingsAccountRepository = savingsAccountRepository;
        this.savingAccountMapper = savingAccountMapper;
        this.userRepository = userRepository;
    }

    //crear una cuenta de ahorros
    public ResponseEntity<?> createSavingAccount(Long id, SavingsAccountDTO savingsAccountDTO) {
        try {
            // Buscar el usuario por su ID
            UserModel user = this.userRepository.findById(id).orElse(null);
            if (user == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese ID"), HttpStatus.BAD_REQUEST);
            }

            //el startDate es la fecha actual
            Date startDate = new Date();
            //convertir la fecha actual a string
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", java.util.Locale.US);
            String strDate = formatter.format(startDate);

            //setear la fecha actual al loanDTO
            savingsAccountDTO.setCreated_at(strDate);

            // Setear el balance inicial
            savingsAccountDTO.setBalance(0.0);

            // Setear el interés
            savingsAccountDTO.setInterest_rate(0.0);

            SavingsAccountModel savingsAccountModel = savingAccountMapper.toSavingsAccountModel(savingsAccountDTO);
            String account_number = user.getFirst_name().charAt(0) + user.getLast_name().charAt(0) + user.getUser_id();
            savingsAccountModel.setAccount_number(account_number);
            savingsAccountModel.setUser(user);

            SavingsAccountModel savingsAccountModelSaved = savingsAccountRepository.save(savingsAccountModel);

            return new ResponseEntity<>(savingAccountMapper.toSavingsAccountDTO(savingsAccountModelSaved), HttpStatus.CREATED);
        } catch (Exception error) {
            // Mostrar en consola el error
            System.out.println(error);
            return new ResponseEntity<>(new ErrorResponse("Error al crear la cuenta de ahorros"), HttpStatus.BAD_REQUEST);
        }
    }


}

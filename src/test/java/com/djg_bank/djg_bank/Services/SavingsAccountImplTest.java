package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.SavingsAccountDTO;
import com.djg_bank.djg_bank.Mapper.SavingAccountMapper;
import com.djg_bank.djg_bank.Mapper.UserMapper;
import com.djg_bank.djg_bank.Models.SavingsAccountModel;
import com.djg_bank.djg_bank.Models.UserModel;
import com.djg_bank.djg_bank.Repositories.ISavingsAccountRepository;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
import com.djg_bank.djg_bank.Services.Implementations.SavingAccountsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SavingsAccountImplTest {
    @InjectMocks
    private SavingAccountsService savingAccountsService;

    @Mock
    private ISavingsAccountRepository savingsAccountRepository;
    @Mock
    private IUserRepository userRepository;
    @Mock
    private SavingAccountMapper savingAccountMapper;

    @BeforeEach
    void setUp() {
        when(savingAccountMapper.toSavingsAccountModel(any(SavingsAccountDTO.class))).thenAnswer(invocation -> {
            SavingsAccountDTO dto = invocation.getArgument(0);
            SavingsAccountModel model = new SavingsAccountModel();
            // Map fields from dto to model
            model.setId(dto.getId());
            model.setAccount_number(dto.getAccount_number());
            model.setBalance(dto.getBalance());
            model.setCreated_at(dto.getCreated_at());
            model.setInterest_rate(dto.getInterest_rate());
            // Add other necessary field mappings
            return model;
        });

        when(savingAccountMapper.toSavingsAccountDTO(any(SavingsAccountModel.class))).thenAnswer(invocation -> {
            SavingsAccountModel model = invocation.getArgument(0);
            SavingsAccountDTO dto = new SavingsAccountDTO();
            // Map fields from model to dto
            dto.setId(model.getId());
            dto.setAccount_number(model.getAccount_number());
            dto.setBalance(model.getBalance());
            dto.setCreated_at(model.getCreated_at());
            dto.setInterest_rate(model.getInterest_rate());
            // Add other necessary field mappings
            return dto;
        });

        when(savingsAccountRepository.save(any(SavingsAccountModel.class))).thenAnswer(invocation -> {
            SavingsAccountModel savingsAccountModel = invocation.getArgument(0);
            return SavingsAccountModel.builder()
                    .id(savingsAccountModel.getId())
                    .account_number(savingsAccountModel.getAccount_number())
                    .balance(savingsAccountModel.getBalance())
                    .created_at(savingsAccountModel.getCreated_at())
                    .interest_rate(savingsAccountModel.getInterest_rate())
                    .build();
        });

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(new UserModel() {{
            setEmail("camargogustavoa@gmail.com");
            setUser_id("1001994147");
            setPassword("HashedPassword");
            setId(1L);
            setStatus("Active");
            setFirst_name("Gustavo");
            setLast_name("Camargo");
            setDate_of_birth("07/12/2001");
            setAddress("Calle 123");
            setPhone_number("12345678");
        }}));

    }

    @Test
    void testCreateSavingAccount() {
        SavingsAccountDTO savingsAccountDTO = new SavingsAccountDTO();
        savingsAccountDTO.setId(1L);

        ResponseEntity<?> response = savingAccountsService.createSavingAccount(1L, savingsAccountDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response should be CREATED");
    }
}

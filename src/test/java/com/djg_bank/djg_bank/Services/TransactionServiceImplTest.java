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
import com.djg_bank.djg_bank.Services.Implementations.TransactionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TransactionServiceImplTest {
    @InjectMocks
    private TransactionsService transactionsService;

    @Mock
    private ITransactionsRepository transactionsRepository;

    @Mock
    private TransactionsMapper transactionsMapper;

    @Mock
    private IDebitCardsRepository debitCardsRepository;

    @Mock
    private ISavingsAccountRepository savingsAccountRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private DebitCardsModel debitCardsModel;

    @BeforeEach
    void setUp() {
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
            setSavings_account(new SavingsAccountModel() {{
                setId(1L);
                setAccount_number("123456789");
                setBalance(1000000.0);
                setInterest_rate(0.01);
                setCreated_at("07/12/2001");
                setUser(new UserModel() {{
                    setId(1L);
                }});
                setDebit_cards(List.of(new DebitCardsModel() {{
                    setId(1L);
                    setCard_number("123456789");
                    setCard_type("VISA");
                    setCvv("123");
                    setDate_issued(new Date());
                    setExpiry_date(new Date());
                }}));
            }});
        }}));

        when(userRepository.findByUser_id(any(String.class))).thenReturn(new UserModel() {{
            setEmail("test@test.com");
            setUser_id("1232124214");
            setPassword("HashedPassword");
            setId(2L);
            setStatus("Active");
            setFirst_name("Test");
            setLast_name("Test");
            setDate_of_birth("07/12/2001");
            setAddress("Calle 123");
            setPhone_number("12345678");
            setSavings_account(new SavingsAccountModel() {{
                setId(2L);
                setAccount_number("123456789");
                setBalance(1000000.0);
                setInterest_rate(0.01);
                setCreated_at("07/12/2001");
                setUser(new UserModel() {{
                    setId(2L);
                }});
                setDebit_cards(List.of(new DebitCardsModel() {{
                    setId(2L);
                    setCard_number("123456789");
                    setCard_type("VISA");
                    setCvv("123");
                    setDate_issued(new Date());
                    setExpiry_date(new Date());
                }}));
            }});

        }});

        when(savingsAccountRepository.findByAccount_number(any(String.class))).thenReturn(Optional.of(new SavingsAccountModel() {{
            setId(1L);
            setAccount_number("123456789");
            setBalance(1000000.0);
            setInterest_rate(0.01);
            setCreated_at("07/12/2001");
            setUser(new UserModel() {{
                setId(1L);
            }});
            setDebit_cards(List.of(new DebitCardsModel() {{
                setId(1L);
                setCard_number("123456789");
                setCard_type("VISA");
                setCvv("123");
                setDate_issued(new Date());
                setExpiry_date(new Date());
            }}));
        }}));

        when(savingsAccountRepository.findByAccount_number(any(String.class))).thenReturn(Optional.of(new SavingsAccountModel() {{
            setId(2L);
            setAccount_number("123456789");
            setBalance(1000000.0);
            setInterest_rate(0.01);
            setCreated_at("07/12/2001");
            setUser(new UserModel() {{
                setId(2L);
            }});
            setDebit_cards(List.of(new DebitCardsModel() {{
                setId(2L);
                setCard_number("123456789");
                setCard_type("VISA");
                setCvv("123");
                setDate_issued(new Date());
                setExpiry_date(new Date());
            }}));
        }}));
    }

    @Test
    public void testCreateTransaction_Success() {
        // Arrange
        TransactionsDTO transactionsDTO = new TransactionsDTO();
        transactionsDTO.setUser_id("1232124214");
        transactionsDTO.setNumber_of_savings_account("987654321");
        transactionsDTO.setAmount(500000.0);
        transactionsDTO.setDescription("Test transaction");

        when(transactionsMapper.toTransactionsModel(any(TransactionsDTO.class))).thenReturn(new TransactionsModel());

        // Act
        ResponseEntity<?> response = transactionsService.createTransaction(1L, transactionsDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(transactionsDTO, response.getBody());
    }
}

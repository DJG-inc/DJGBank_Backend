package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.CreditCardActivityDTO;
import com.djg_bank.djg_bank.Mapper.CreditCardActivityMapper;
import com.djg_bank.djg_bank.Models.CreditCardActivityModel;
import com.djg_bank.djg_bank.Models.CreditCardModel;
import com.djg_bank.djg_bank.Models.SavingsAccountModel;
import com.djg_bank.djg_bank.Models.UserModel;
import com.djg_bank.djg_bank.Repositories.ICreditCardActivityRepository;
import com.djg_bank.djg_bank.Repositories.ICreditCardRepository;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
import com.djg_bank.djg_bank.Services.Implementations.CreditCardActivityService;
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
import static org.mockito.Mockito.*;

@SpringBootTest
public class CreditCardActivityServiceImplTest {
    @InjectMocks
    private CreditCardActivityService creditCardActivityService;

    @Mock
    private ICreditCardActivityRepository creditCardActivityRepository;

    @Mock
    private ICreditCardRepository creditCardRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private CreditCardActivityMapper creditCardActivityMapper;

    private UserModel mockUser;
    private SavingsAccountModel mockSavingsAccount;

    @BeforeEach
    void setUp() {
        when(creditCardRepository.findById(any(Long.class))).thenReturn(Optional.of(new CreditCardModel() {
            {
                setId(1L);
                setCredit_limit(1000000.0);
                setCard_type("VISA");
                setCard_number("123456789");
                setCvv("123");
                setExpiry_date(new Date());
                setDate_issued(new Date());
                setCurrent_debt(0.0);
                setUser(new UserModel() {{
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
                    }});
                    setCredit_cards(List.of(new CreditCardModel() {{
                        setId(1L);
                        setCredit_limit(1000000.0);
                        setCard_type("VISA");
                        setCard_number("123456789");
                        setCvv("123");
                        setExpiry_date(new Date());
                        setDate_issued(new Date());
                        setCurrent_debt(0.0);

                        setUser(new UserModel() {{
                            setId(1L);

                        }});
                    }}));
                }});
            }
        }));

        when(creditCardActivityMapper.toCreditCardActivityModel(any(CreditCardActivityDTO.class))).thenAnswer(invocation -> {
            CreditCardActivityDTO dto = invocation.getArgument(0);
            CreditCardActivityModel model = new CreditCardActivityModel();
            // Map fields from dto to model
            model.setId(dto.getId());
            model.setType(dto.getType());
            model.setAmount(dto.getAmount());
            model.setDate_of_transaction(dto.getDate_of_transaction());
            // Add other necessary field mappings
            return model;
        });

        when(creditCardActivityMapper.toCreditCardActivityDTO(any(CreditCardActivityModel.class))).thenAnswer(invocation -> {
            CreditCardActivityModel model = invocation.getArgument(0);
            CreditCardActivityDTO dto = new CreditCardActivityDTO();
            // Map fields from model to dto
            dto.setId(model.getId());
            dto.setType(model.getType());
            dto.setAmount(model.getAmount());
            dto.setDate_of_transaction(model.getDate_of_transaction());
            return dto;
        });

        when(creditCardActivityRepository.save(any(CreditCardActivityModel.class))).thenAnswer(invocation -> {
            CreditCardActivityModel creditCardActivityModel = invocation.getArgument(0);
            return CreditCardActivityModel.builder()
                    .id(creditCardActivityModel.getId())
                    .type(creditCardActivityModel.getType())
                    .amount(creditCardActivityModel.getAmount())
                    .date_of_transaction(creditCardActivityModel.getDate_of_transaction())
                    .build();
        });

        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> {
            UserModel userModel = invocation.getArgument(0);
            return UserModel.builder()
                    .id(userModel.getId())
                    .email(userModel.getEmail())
                    .user_id(userModel.getUser_id())
                    .password(userModel.getPassword())
                    .status(userModel.getStatus())
                    .first_name(userModel.getFirst_name())
                    .last_name(userModel.getLast_name())
                    .date_of_birth(userModel.getDate_of_birth())
                    .address(userModel.getAddress())
                    .phone_number(userModel.getPhone_number())
                    .savings_account(userModel.getSavings_account())
                    .credit_cards(userModel.getCredit_cards())
                    .build();
        });

        when(creditCardRepository.save(any(CreditCardModel.class))).thenAnswer(invocation -> {
            CreditCardModel creditCardModel = invocation.getArgument(0);
            return CreditCardModel.builder()
                    .id(creditCardModel.getId())
                    .credit_limit(creditCardModel.getCredit_limit())
                    .current_debt(creditCardModel.getCurrent_debt())
                    .card_type(creditCardModel.getCard_type())
                    .card_number(creditCardModel.getCard_number())
                    .cvv(creditCardModel.getCvv())
                    .expiry_date(creditCardModel.getExpiry_date())
                    .date_issued(creditCardModel.getDate_issued())
                    .user(creditCardModel.getUser())
                    .build();
        });

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(new UserModel() {
            {
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
                }});
                setCredit_cards(List.of(new CreditCardModel() {{
                    setId(1L);
                    setCredit_limit(1000000.0);
                    setCard_type("VISA");
                    setCard_number("123456789");
                    setCvv("123");
                    setExpiry_date(new Date());
                    setDate_issued(new Date());
                    setCurrent_debt(0.0);
                    setUser(new UserModel() {{
                        setId(1L);

                    }});
                }}));


            }
        }));

    }

    @Test
    public void testCreateCreditCardActivity_Success() {
        CreditCardActivityDTO creditCardActivityDTO = new CreditCardActivityDTO();
        creditCardActivityDTO.setId(1L);
        creditCardActivityDTO.setType("CHARGE");
        creditCardActivityDTO.setAmount(1000000.0);
        creditCardActivityDTO.setDate_of_transaction(new Date());

        ResponseEntity<?> response = creditCardActivityService.createCreditCardActivity(1L, creditCardActivityDTO);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

}




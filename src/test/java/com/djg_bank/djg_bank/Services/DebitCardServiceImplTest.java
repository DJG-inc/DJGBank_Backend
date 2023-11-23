package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.DebitCardsDTO;
import com.djg_bank.djg_bank.Mapper.DebitCardsMapper;
import com.djg_bank.djg_bank.Models.DebitCardsModel;
import com.djg_bank.djg_bank.Models.SavingsAccountModel;
import com.djg_bank.djg_bank.Models.UserModel;
import com.djg_bank.djg_bank.Repositories.IDebitCardsRepository;
import com.djg_bank.djg_bank.Repositories.ISavingsAccountRepository;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
import com.djg_bank.djg_bank.Services.Implementations.DebitCardService;
import com.djg_bank.djg_bank.Utils.ResourcesBank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DebitCardServiceImplTest {
    @InjectMocks
    private DebitCardService debitCardService;

    @Mock
    private IDebitCardsRepository debitCardsRepository;
    @Mock
    private DebitCardsMapper debitCardsMapper;
    @Mock
    private ISavingsAccountRepository savingsAccountRepository;
    @Mock
    private IUserRepository userRepository;
    @Mock
    private ResourcesBank resourcesBank;

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
            }});
        }}));

        when(savingsAccountRepository.findById(any(Long.class))).thenReturn(Optional.of(new SavingsAccountModel() {{
            setId(1L);
            setAccount_number("123456789");
            setBalance(1000000.0);
            setInterest_rate(0.01);
            setCreated_at("07/12/2001");
        }}));

        when(debitCardsMapper.toDebitCardsModel(any(DebitCardsDTO.class))).thenAnswer(invocation -> {
            DebitCardsDTO dto = invocation.getArgument(0);
            DebitCardsModel model = new DebitCardsModel();
            // Map fields from dto to model
            model.setId(dto.getId());
            model.setCard_number(dto.getCard_number());
            model.setCard_type(dto.getCard_type());
            model.setDate_issued(dto.getDate_issued());
            model.setExpiry_date(dto.getExpiry_date());
            model.setCvv(dto.getCvv());
            // Add other necessary field mappings
            return model;
        });

        when(debitCardsMapper.toDebitCardsDTO(any(DebitCardsModel.class))).thenAnswer(invocation -> {
            DebitCardsModel model = invocation.getArgument(0);
            DebitCardsDTO dto = new DebitCardsDTO();
            // Map fields from model to dto
            dto.setId(model.getId());
            dto.setCard_number(model.getCard_number());
            dto.setCard_type(model.getCard_type());
            dto.setDate_issued(model.getDate_issued());
            dto.setExpiry_date(model.getExpiry_date());
            dto.setCvv(model.getCvv());
            return dto;
        });

        when(debitCardsRepository.save(any(DebitCardsModel.class))).thenAnswer(invocation -> {
            DebitCardsModel debitCardsModel = invocation.getArgument(0);
            return DebitCardsModel.builder()
                    .id(debitCardsModel.getId())
                    .card_number(debitCardsModel.getCard_number())
                    .card_type(debitCardsModel.getCard_type())
                    .date_issued(debitCardsModel.getDate_issued())
                    .expiry_date(debitCardsModel.getExpiry_date())
                    .cvv(debitCardsModel.getCvv())
                    .build();
        });
    }

    @Test
    void testCreateDebitCard() {

        when(resourcesBank.calculateAge(any())).thenReturn(18);
        when(resourcesBank.isValidCardType(any())).thenReturn(false);
        when(resourcesBank.generateValidCardNumber(any())).thenReturn("123456789");
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.YEAR, 5);
        when(resourcesBank.generateExpiryDate()).thenReturn(calendar.getTime());
        when(resourcesBank.generateRandomCVV()).thenReturn("123");

        ResponseEntity<?> response = debitCardService.createDebitCard(1L, "Visa");

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response should be CREATED");

    }


}

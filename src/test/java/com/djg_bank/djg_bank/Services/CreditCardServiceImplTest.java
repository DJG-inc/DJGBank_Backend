package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.CreditCardDTO;
import com.djg_bank.djg_bank.Mapper.CreditCardMapper;
import com.djg_bank.djg_bank.Models.CreditCardModel;
import com.djg_bank.djg_bank.Models.SavingsAccountModel;
import com.djg_bank.djg_bank.Models.UserModel;
import com.djg_bank.djg_bank.Repositories.ICreditCardRepository;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
import com.djg_bank.djg_bank.Services.Implementations.CreditCardService;
import com.djg_bank.djg_bank.Utils.ResourcesBank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CreditCardServiceImplTest {

    @InjectMocks
    private CreditCardService creditCardService;

    @Mock
    private ICreditCardRepository creditCardRepository;

    @Mock
    private CreditCardMapper creditCardMapper;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private UserModel userModel;

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

            setCredit_cards(List.of(new CreditCardModel[]{new CreditCardModel() {{
                setId(1L);
                setCard_number("123456789");
                setCard_type("Visa");
                setExpiry_date(new Date());
                setCvv("123");
                setDate_issued(new Date());
                setCredit_limit(50000.0);
                setCurrent_debt(0.0);
            }}}));

        }}));

        when(userModel.getSavings_account()).thenReturn(new SavingsAccountModel() {{
            setId(1L);
            setAccount_number("123456789");
            setBalance(1000000.0);
            setInterest_rate(0.01);
            setCreated_at("07/12/2001");
        }});

        when(creditCardRepository.findById(any(Long.class))).thenReturn(Optional.of(new CreditCardModel() {{
            setId(1L);
            setCard_number("123456789");
            setCard_type("Visa");
            setExpiry_date(new Date());
            setCvv("123");
            setDate_issued(new Date());
            setCredit_limit(50000.0);
            setCurrent_debt(0.0);

            UserModel user = new UserModel() {{
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

                setCredit_cards(List.of(new CreditCardModel[]{new CreditCardModel() {{
                    setId(1L);
                    setCard_number("123456789");
                    setCard_type("Visa");
                    setExpiry_date(new Date());
                    setCvv("123");
                    setDate_issued(new Date());
                    setCredit_limit(50000.0);
                    setCurrent_debt(0.0);
                }}}));
            }};

            setUser(user);

        }}));


        when(resourcesBank.generateExpiryDate()).thenReturn(new Date());

        when(resourcesBank.generateRandomCVV()).thenReturn("123");

        when(resourcesBank.calculateAge(any())).thenReturn(19);

        when(resourcesBank.isValidCardType(any())).thenReturn(false);

        when(resourcesBank.generateValidCardNumber(any())).thenReturn("123456789");

    }

    @Test
    void createCreditCardTest() {
        // Arrange
        Long id = 1L;
        String cardType = "Visa";

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.YEAR, 5);
        when(resourcesBank.generateExpiryDate()).thenReturn(calendar.getTime());

        when(creditCardMapper.toCreditCardDTO(any(CreditCardModel.class))).thenAnswer(i -> {
            CreditCardModel creditCardModel = i.getArgument(0);
            CreditCardDTO creditCardDTO = new CreditCardDTO();

            creditCardDTO.setId(creditCardModel.getId());
            creditCardDTO.setCard_number(creditCardModel.getCard_number());
            creditCardDTO.setCard_type(creditCardModel.getCard_type());
            creditCardDTO.setExpiry_date(creditCardModel.getExpiry_date());
            creditCardDTO.setCvv(creditCardModel.getCvv());
            creditCardDTO.setDate_issued(creditCardModel.getDate_issued());
            creditCardDTO.setCredit_limit(creditCardModel.getCredit_limit());
            creditCardDTO.setCurrent_debt(creditCardModel.getCurrent_debt());

            return creditCardDTO;
        });

        when(creditCardRepository.save(any(CreditCardModel.class))).thenAnswer(i -> {
            CreditCardModel creditCardModel = i.getArgument(0);
            creditCardModel.setId(1L);
            return creditCardModel;
        });

        when(resourcesBank.generateExpiryDate()).thenReturn(new Date());

        when(resourcesBank.generateRandomCVV()).thenReturn("123");

        // Act
        var response = this.creditCardService.createCreditCard(id, cardType);


        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response should be CREATED");
    }

}

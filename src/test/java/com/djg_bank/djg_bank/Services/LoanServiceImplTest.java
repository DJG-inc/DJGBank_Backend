package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.LoanDTO;
import com.djg_bank.djg_bank.Mapper.LoanMapper;
import com.djg_bank.djg_bank.Models.LoanModel;
import com.djg_bank.djg_bank.Models.SavingsAccountModel;
import com.djg_bank.djg_bank.Models.UserModel;
import com.djg_bank.djg_bank.Repositories.ILoanRepository;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
import com.djg_bank.djg_bank.Services.Implementations.LoanService;
import com.djg_bank.djg_bank.Utils.ResourcesBank;
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
public class LoanServiceImplTest {
    @InjectMocks
    private LoanService loanService;

    @Mock
    private ILoanRepository loanRepository;

    @Mock
    private LoanMapper loanMapper;

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

            setLoans(List.of(new LoanModel() {{
                setId(1L);
                setAmount(1000000.0);
                setInterest_rate(0.01);
                setStart_date("07/12/2001");
                setEnd_date(new Date());
                setMonthly_payment(1000.0);
                setUser(new UserModel() {{
                    setId(1L);
                }});
            }}));
        }}));

        when(loanRepository.findById(any(Long.class))).thenReturn(Optional.of(new LoanModel() {{
            setId(1L);
            setAmount(1000000.0);
            setInterest_rate(0.01);
            setStart_date("07/12/2001");
            setMonthly_payment(1000.0);
            setEnd_date(new Date());
            setUser(new UserModel() {{
                setId(1L);
            }});
        }}));

        when(loanMapper.toLoanModel(any(LoanDTO.class))).thenAnswer(invocation -> {
            LoanDTO dto = invocation.getArgument(0);
            LoanModel model = new LoanModel();
            // Map fields from dto to model
            model.setId(dto.getId());
            model.setAmount(dto.getAmount());
            model.setInterest_rate(dto.getInterest_rate());
            model.setStart_date(dto.getStart_date());
            model.setEnd_date(dto.getEnd_date());
            model.setMonthly_payment(dto.getMonthly_payment());
            return model;
        });

        when(loanMapper.toLoanDTO(any(LoanModel.class))).thenAnswer(invocation -> {
            LoanModel model = invocation.getArgument(0);
            LoanDTO dto = new LoanDTO();
            // Map fields from model to dto
            dto.setId(model.getId());
            dto.setAmount(model.getAmount());
            dto.setInterest_rate(model.getInterest_rate());
            dto.setStart_date(model.getStart_date());
            dto.setEnd_date(model.getEnd_date());
            dto.setMonthly_payment(model.getMonthly_payment());
            return dto;
        });

        when(loanRepository.save(any(LoanModel.class))).thenAnswer(invocation -> {
            LoanModel loanModel = invocation.getArgument(0);
            return LoanModel.builder()
                    .id(loanModel.getId())
                    .amount(loanModel.getAmount())
                    .interest_rate(loanModel.getInterest_rate())
                    .start_date(loanModel.getStart_date())
                    .end_date(loanModel.getEnd_date())
                    .monthly_payment(loanModel.getMonthly_payment())
                    .build();
        });

        when(resourcesBank.calculateAge(any())).thenReturn(18);

    }

    @Test
    void testCreateLoan() {
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setAmount(1000000.0);
        loanDTO.setInterest_rate(0.01);
        loanDTO.setMonthly_payment(10000.0);
        loanDTO.setStart_date("07/12/2024");
        loanDTO.setEnd_date(new Date());

        ResponseEntity<?> response = loanService.save(1L, loanDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response should be OK");

    }

    @Test
    void testFindAll() {
        ResponseEntity<?> response = loanService.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response should be OK");
    }

    @Test
    void testFindById() {
        ResponseEntity<?> response = loanService.findById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response should be OK");
    }

    @Test
    void testPayLoan() {
        ResponseEntity<?> response = loanService.payLoan(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response should be OK");
    }
}

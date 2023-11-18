package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.Mapper.UserMapper;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
import com.djg_bank.djg_bank.DTOs.UserDTO;
import com.djg_bank.djg_bank.Models.UserModel;

import com.djg_bank.djg_bank.Security.Bcrypt;
import com.djg_bank.djg_bank.Security.JwtUtils;
import com.djg_bank.djg_bank.Services.Implementations.UserService;
import com.djg_bank.djg_bank.Utils.EmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class UserServiceImplTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private IUserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @Mock
    public Bcrypt bcrypt;
    @Mock
    public PasswordEncoder passwordEncoder;
    @Mock
    public EmailService emailService;
    @Mock
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userMapper, bcrypt, jwtUtils, emailService);
        // Define behavior for bcrypt mock
        when(bcrypt.passwordEncoder()).thenReturn(passwordEncoder);

        // Define behavior for passwordEncoder mock
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(jwtUtils.generateJwtToken(anyLong())).thenReturn("mockedToken");

        when(userMapper.toUserModel(ArgumentMatchers.any(UserDTO.class))).thenAnswer(invocation -> {
            UserDTO arg = invocation.getArgument(0);
            UserModel userModel = new UserModel();
            // Populate userModel with values from arg (UserDTO)
            userModel.setEmail(arg.getEmail());
            userModel.setUser_id(arg.getUser_id());
            userModel.setPassword(arg.getPassword());
            userModel.setId(arg.getId());
            // Set other fields as needed
            return userModel;
        });

        when(userRepository.save(ArgumentMatchers.any(UserModel.class))).thenAnswer(invocation -> {
            UserModel arg = invocation.getArgument(0);
            UserModel savedUser = new UserModel();
            // Populate userModel with values from arg (UserDTO)
            savedUser.setEmail(arg.getEmail());
            savedUser.setUser_id(arg.getUser_id());
            savedUser.setPassword(arg.getPassword());
            savedUser.setId(arg.getId());
            return savedUser;
        });


        when(userMapper.toUserDTO(any(UserModel.class))).thenReturn(new UserDTO());
        when(userRepository.findByEmail(any(String.class))).thenReturn(null);
        when(userRepository.findByUser_id(any(String.class))).thenReturn(null);

    }

    @Test
    void testRegisterNewUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("camargogustavoa@gmail.com");
        userDTO.setUser_id("1001994147");
        userDTO.setPassword("HolaMundo123");
        userDTO.setId(1L);

        ResponseEntity<?> response = userService.register(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response should be CREATED");
    }
    @Test
    void testBadRegisterNewUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(" ");
        userDTO.setUser_id(" ");
        userDTO.setPassword(" ");
        userDTO.setId(1L);

        ResponseEntity<?> response = userService.register(userDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response should be BAD_REQUEST");
    }

    @Test
    void testResendConfirmationEmail() {

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(new UserModel() {{
            setEmail("camargogustavoa@gmail.com");
            setUser_id("1001994147");
            setPassword("HashedPassword");
            setId(1L);
            setStatus("Pending");
        }}));

        // Mock other dependencies as needed...

        ResponseEntity<?> response = userService.resentConfrmationemail("token");

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response should be OK");
    }

    @Test
    void testBadResendConfirmationEmail() {

        when(userRepository.findById(any(Long.class))).thenReturn(null);

        ResponseEntity<?> response = userService.resentConfrmationemail("token");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response should be BAD_REQUEST");

    }

    @Test
    void testConfirmEmail() {

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(new UserModel() {{
            setEmail("camargogustavoa@gmail.com");
            setUser_id("1001994147");
            setPassword("HashedPassword");
            setId(1L);
            setStatus("Pending");
        }}));

        ResponseEntity<?> response = userService.confirmEmail("token");

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response should be OK");
    }

    @Test
    void testBadConfirmEmail() {

        when(userRepository.findById(any(Long.class))).thenReturn(null);

        ResponseEntity<?> response = userService.confirmEmail("token");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response should be BAD_REQUEST");
    }

    @Test
    void testCompleteRegister() {

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(new UserModel() {{
            setEmail("camargogustavoa@gmail.com");
            setUser_id("1001994147");
            setPassword("HashedPassword");
            setId(1L);
            setStatus("Confirmed");
        }}));

        UserDTO userDTO = new UserDTO();
        userDTO.setFirst_name("Gustavo");
        userDTO.setLast_name("Camargo");
        userDTO.setDate_of_birth("07/12/2001");
        userDTO.setAddress("Calle 123");
        userDTO.setPhone_number("12345678");

        ResponseEntity<?> response = userService.completeRegister(1L, userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response should be OK");
    }

    @Test
    void testBadCompleteRegister() {

        when(userRepository.findById(any(Long.class))).thenReturn(null);

        UserDTO userDTO = new UserDTO();
        userDTO.setFirst_name("Gustavo");
        userDTO.setLast_name("Camargo");
        userDTO.setDate_of_birth("07/12/2001");
        userDTO.setAddress("Calle 123");
        userDTO.setPhone_number("12345678");

        ResponseEntity<?> response = userService.completeRegister(1L, userDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response should be BAD_REQUEST");
    }

    @Test
    void testLogin() {

        when(userRepository.findByUser_id(any(String.class))).thenReturn(new UserModel() {{
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
        }});

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("camargogustavoa@gmail.com");
        userDTO.setPassword("HashedPassword");
        userDTO.setUser_id("1001994147");

        when(bcrypt.passwordEncoder().matches(any(String.class), any(String.class))).thenReturn(true);

        ResponseEntity<?> response = userService.login(userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response should be OK");
    }

    @Test
    void testBadLogin() {

        when(userRepository.findByUser_id(any(String.class))).thenReturn(null);

        UserDTO userDTO = new UserDTO();

        ResponseEntity<?> response = userService.login(userDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response should be BAD_REQUEST");

    }

    @Test
    void testFindById() {
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

        ResponseEntity<?> response = userService.findById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response should be OK");
    }

    @Test
    void testBadFindById() {
        when(userRepository.findById(any(Long.class))).thenReturn(null);

        ResponseEntity<?> response = userService.findById(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response should be BAD_REQUEST");
    }

    @Test
    void testFindAll() {
        when(userRepository.findAll()).thenReturn(null);

        ResponseEntity<?> response = userService.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response should be OK");
    }

    @Test
    void testUpdateUser() {
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

        UserDTO userDTO = new UserDTO();
        userDTO.setFirst_name("Daniel");
        userDTO.setLast_name("Garcia");

        ResponseEntity<?> response = userService.updateUser(1L, userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response should be OK");

    }

    @Test
    void testBadUpdateUser() {
        when(userRepository.findById(any(Long.class))).thenReturn(null);

        UserDTO userDTO = new UserDTO();
        userDTO.setFirst_name("Daniel");
        userDTO.setLast_name("Garcia");

        ResponseEntity<?> response = userService.updateUser(1L, userDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Response should be BAD_REQUEST");
    }

    @Test
    void testForgotPassword() {
        when(userRepository.findByEmail(any(String.class))).thenReturn(new UserModel() {{
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
        }});

        ResponseEntity<?> response = userService.forgotPassword("camargogustavoa@gmail.com");

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response should be OK");
    }

    @Test
    void testResetPassword() {
        when(bcrypt.passwordEncoder().matches(any(String.class), any(String.class))).thenReturn(true);
        when(jwtUtils.validateJwtToken(any(String.class))).thenReturn(true);
        when(jwtUtils.getIdFromJwtToken(any(String.class))).thenReturn(1L);

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

        when(userRepository.save(ArgumentMatchers.any(UserModel.class))).thenAnswer(invocation -> {
            UserModel arg = invocation.getArgument(0);
            UserModel savedUser = new UserModel();
            // Populate userModel with values from arg (UserDTO)
            savedUser.setEmail(arg.getEmail());
            savedUser.setUser_id(arg.getUser_id());
            savedUser.setPassword(arg.getPassword());
            savedUser.setId(arg.getId());
            savedUser.setStatus(arg.getStatus());
            savedUser.setFirst_name(arg.getFirst_name());
            savedUser.setLast_name(arg.getLast_name());
            savedUser.setDate_of_birth(arg.getDate_of_birth());
            savedUser.setAddress(arg.getAddress());
            savedUser.setPhone_number(arg.getPhone_number());
            return savedUser;
        });


        ResponseEntity<?> response = userService.resetPassword("token", "newPassword");

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response should be OK");
    }
}

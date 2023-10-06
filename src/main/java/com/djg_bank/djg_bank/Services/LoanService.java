package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.LoanDTO;
import com.djg_bank.djg_bank.Mapper.LoanMapper;
import com.djg_bank.djg_bank.Models.LoanModel;
import com.djg_bank.djg_bank.Models.UserModel;
import com.djg_bank.djg_bank.Repositories.ILoanRepository;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
import com.djg_bank.djg_bank.Utils.ErrorResponse;
import com.djg_bank.djg_bank.Utils.ResourcesBank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class LoanService {
    private final ILoanRepository loanRepository;
    private final LoanMapper loanMapper;
    private final IUserRepository userRepository;

    private final ResourcesBank resourcesBank;

    public LoanService(ILoanRepository loanRepository, LoanMapper loanMapper, IUserRepository userRepository, ResourcesBank resourcesBank) {
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
        this.userRepository = userRepository;
        this.resourcesBank = resourcesBank;
    }

    public ResponseEntity<?> save(Long id, LoanDTO loanDTO) {
        try {
            // Buscar el usuario por su ID
            UserModel user = this.userRepository.findById(id).orElse(null);
            if (user == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese ID"), HttpStatus.BAD_REQUEST);
            }

            // Obtener la fecha de nacimiento del usuario como String en el formato "1/1/2000"
            String dateOfBirthString = user.getDate_of_birth();

            // Parsear la fecha de nacimiento en el formato adecuado
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date dateOfBirth = dateFormat.parse(dateOfBirthString);

            // Calcular la edad del usuario a partir de la fecha de nacimiento
            int age = resourcesBank.calculateAge(dateOfBirth);

            // Verificar que el usuario tenga al menos 18 años
            if (age < 18) {
                return new ResponseEntity<>(new ErrorResponse("El usuario debe tener al menos 18 años para solicitar un préstamo"), HttpStatus.BAD_REQUEST);
            }

            //el startDate es la fecha actual
            Date startDate = new Date();
            //convertir la fecha actual a string
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", java.util.Locale.US);
            String strDate = formatter.format(startDate);
            //setear la fecha actual al loanDTO
            loanDTO.setStart_date(strDate);

            // Crear un nuevo préstamo y asignarle los datos
            LoanModel loanModel = loanMapper.toLoanModel(loanDTO);
            loanModel.setUser(user);

            // Guardar el préstamo en la base de datos
            LoanModel loanSaved = loanRepository.save(loanModel);

            return new ResponseEntity<>(loanSaved, HttpStatus.OK);
        } catch (Exception e) {
            System.out.printf("Error al guardar el préstamo: %s", e.getMessage());
            return new ResponseEntity<>(new ErrorResponse("Error al guardar el préstamo"), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> findAll() {
        try {
            return new ResponseEntity<>(this.loanRepository.findAll(), HttpStatus.OK);
        } catch (Exception error) {
            return new ResponseEntity<>(new ErrorResponse("Error al obtener los préstamos"), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> findById(Long id) {
        try {
            LoanModel loan = this.loanRepository.findById(id).orElse(null);
            if (loan == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un préstamo con ese ID"), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(loan, HttpStatus.OK);
        } catch (Exception error) {
            return new ResponseEntity<>(new ErrorResponse("Error al obtener el préstamo"), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> payLoan(Long userId, Long loanId) {
        try {
            // Buscar el préstamo por su ID
            LoanModel loan = loanRepository.findById(loanId).orElse(null);
            if (loan == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un préstamo con ese ID"), HttpStatus.BAD_REQUEST);
            }

            // Buscar el usuario por su ID
            UserModel user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return new ResponseEntity<>(new ErrorResponse("No existe un usuario con ese ID"), HttpStatus.BAD_REQUEST);
            }

            // Verificar si el préstamo ya está pagado
            if (loan.getAmount() <= 0) {
                return new ResponseEntity<>(new ErrorResponse("El préstamo ya ha sido completamente pagado"), HttpStatus.BAD_REQUEST);
            }

            // Obtener el saldo actual de la cuenta de ahorros del usuario
            Double savingsBalance = user.getSavings_account().getBalance();

            // Obtener el monto del pago mensual
            Double monthlyPayment = loan.getMonthly_payment();

            // Verificar si el saldo de la cuenta de ahorros es suficiente para realizar el pago
            if (savingsBalance >= monthlyPayment) {
                // Restar el monto del pago al saldo de la cuenta de ahorros
                savingsBalance -= monthlyPayment;

                // Actualizar el saldo de la cuenta de ahorros del usuario
                user.getSavings_account().setBalance(savingsBalance);

                // Calcular el nuevo saldo pendiente después del pago
                Double newAmount = loan.getAmount() - monthlyPayment;

                if (newAmount <= 0) {
                    // Marcar el préstamo como completamente pagado
                    newAmount = 0.0;
                    loan.setEnd_date(new Date()); // Puedes establecer la fecha de finalización como la fecha actual
                }

                // Actualizar el saldo pendiente
                loan.setAmount(newAmount);

                // Crear el objeto de respuesta personalizado
                Map<String, Object> response = createPaymentResponse(monthlyPayment, loan, monthlyPayment);

                // Guardar la actualización del préstamo y de la cuenta de ahorros en la base de datos
                loanRepository.save(loan);
                userRepository.save(user);

                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ErrorResponse("Saldo insuficiente en la cuenta de ahorros"), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            System.out.printf("Error al realizar el pago del préstamo: %s", e.getMessage());
            return new ResponseEntity<>(new ErrorResponse("Error al realizar el pago del préstamo"), HttpStatus.BAD_REQUEST);
        }
    }

    private Map<String, Object> createPaymentResponse(Double amountPaid, LoanModel loan, Double monthlyPayment) {
        Map<String, Object> response = new HashMap<>();
        response.put("amountPaid", amountPaid);

        // Calcular el mes del pago en función de la fecha de inicio y el número de cuotas pagadas
        Date startDate;
        try {
            startDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US).parse(loan.getStart_date());
        } catch (ParseException e) {
            startDate = new Date(); // En caso de error, usar la fecha actual
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        int monthsPaid = (int) ((loan.getAmount() / monthlyPayment) * -1); // Número de meses pagados
        calendar.add(Calendar.MONTH, monthsPaid);

        response.put("monthPaid", new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(calendar.getTime()));
        response.put("amountRemaining", loan.getAmount());

        return response;
    }
}


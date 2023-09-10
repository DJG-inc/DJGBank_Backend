package com.djg_bank.djg_bank.Services;

import com.djg_bank.djg_bank.DTOs.LoanDTO;
import com.djg_bank.djg_bank.Mapper.LoanMapper;
import com.djg_bank.djg_bank.Models.LoanModel;
import com.djg_bank.djg_bank.Models.UserModel;
import com.djg_bank.djg_bank.Repositories.ILoanRepository;
import com.djg_bank.djg_bank.Repositories.IUserRepository;
import com.djg_bank.djg_bank.Utils.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class LoanService {
    private final ILoanRepository loanRepository;
    private final LoanMapper loanMapper;
    private final IUserRepository userRepository;

    public LoanService(ILoanRepository loanRepository, LoanMapper loanMapper, IUserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
        this.userRepository = userRepository;
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
            int age = calcularEdad(dateOfBirth);

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

    // Método para calcular la edad a partir de la fecha de nacimiento
    private int calcularEdad(Date dateOfBirth) {
        Calendar dob = Calendar.getInstance();
        dob.setTime(dateOfBirth);

        Calendar currentDate = Calendar.getInstance();

        int age = currentDate.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (currentDate.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
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


}

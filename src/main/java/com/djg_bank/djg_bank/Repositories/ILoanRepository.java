package com.djg_bank.djg_bank.Repositories;

import com.djg_bank.djg_bank.Models.LoanModel;
import com.djg_bank.djg_bank.Models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ILoanRepository extends JpaRepository<LoanModel, Long> {
    Optional<LoanModel> findById(Long id);

    List<LoanModel> findByUser(UserModel user);

    List<LoanModel> findByAmountGreaterThan(Double amount);
    List<LoanModel> findByAmountLessThan(Double amount);

}

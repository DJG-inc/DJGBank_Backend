package com.djg_bank.djg_bank.Repositories;

import com.djg_bank.djg_bank.Models.SavingsAccountModel;
import com.djg_bank.djg_bank.Models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ISavingsAccountRepository extends JpaRepository<SavingsAccountModel, Long> {
    // Find savings account by user
    Optional<SavingsAccountModel> findByUser(UserModel user);
    // Find savings account by number
    Optional<SavingsAccountModel> findByNumber(String number);
}

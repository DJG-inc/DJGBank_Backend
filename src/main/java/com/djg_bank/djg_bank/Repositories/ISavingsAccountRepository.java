package com.djg_bank.djg_bank.Repositories;

import com.djg_bank.djg_bank.Models.SavingsAccountModel;
import com.djg_bank.djg_bank.Models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ISavingsAccountRepository extends JpaRepository<SavingsAccountModel, Long> {
    // Find savings account by user
    Optional<SavingsAccountModel> findByUser(UserModel user);
    // Find savings account by number
    @Query("SELECT s FROM SavingsAccountModel s WHERE s.account_number = :account_number")
    Optional<SavingsAccountModel> findByAccount_number(@Param("account_number") String account_number);

}

package com.djg_bank.djg_bank.Repositories;

import com.djg_bank.djg_bank.Models.CreditCardActivityModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICreditCardActivityRepository extends JpaRepository<CreditCardActivityModel, Long> {
    CreditCardActivityModel findById(long id);
}

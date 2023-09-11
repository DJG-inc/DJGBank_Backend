package com.djg_bank.djg_bank.Repositories;

import com.djg_bank.djg_bank.Models.CreditCardModel;
import com.djg_bank.djg_bank.Models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICreditCardRepository extends JpaRepository<CreditCardModel, Long> {
    CreditCardModel findByUserId(Long userId);

    CreditCardModel findByUser(UserModel user);
}

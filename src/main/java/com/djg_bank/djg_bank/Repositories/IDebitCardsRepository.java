package com.djg_bank.djg_bank.Repositories;

import com.djg_bank.djg_bank.Models.DebitCardsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDebitCardsRepository extends JpaRepository<DebitCardsModel, Long> {
    DebitCardsModel findByUser_Id(String user_id);
}

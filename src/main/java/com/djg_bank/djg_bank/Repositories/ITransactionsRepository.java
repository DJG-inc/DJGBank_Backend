package com.djg_bank.djg_bank.Repositories;

import com.djg_bank.djg_bank.Models.TransactionsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITransactionsRepository extends JpaRepository<TransactionsModel, Long> {
    TransactionsModel findById(long id);
}

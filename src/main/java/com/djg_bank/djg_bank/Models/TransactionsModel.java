package com.djg_bank.djg_bank.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "trasactions")
public class TransactionsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 20)
    private String user_id;

    @Column(name = "number_of_savings_account", nullable = false, length = 20)
    private String number_of_savings_account;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "date_of_transaction", nullable = false)
    private Date date_of_transaction;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private SavingsAccountModel savings_account;
}

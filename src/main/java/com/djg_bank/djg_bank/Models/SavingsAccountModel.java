package com.djg_bank.djg_bank.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "savings_accounts")
public class SavingsAccountModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false)
    private String account_number;

    @Column(name = "balance", nullable = false)
    private Double balance;

    @Column(name = "created_at", nullable = false)
    private String created_at;

    @Column(name = "interest_rate", nullable = false)
    private Double interest_rate;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private UserModel user;

    @OneToMany(mappedBy = "savings_account", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<DebitCardsModel> debit_cards;

    @OneToMany(mappedBy = "savings_account", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<TransactionsModel> transactions;
}

package com.djg_bank.djg_bank.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "savings_account")
@Data
public class SavingsAccountModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "balance", nullable = false)
    private Double balance;

    @Column(name = "created_at", nullable = false)
    private String created_at;

    @Column(name = "interest_rate", nullable = false)
    private Double interest_rate;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @OneToMany(mappedBy = "savings_account", cascade = CascadeType.ALL)
    private List<DebitCardsModel> debit_cards;
}

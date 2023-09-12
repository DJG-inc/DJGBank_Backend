package com.djg_bank.djg_bank.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "debit_cards")
public class DebitCardsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", nullable = false, length = 16)
    private String card_number;

    @Column(name = "expiry_date", nullable = false)
    private Date expiry_date;

    @Column(name = "cvv", nullable = false, length = 3)
    private String cvv;

    @Column(name = "date_issued", nullable = false)
    private Date date_issued;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "savings_account_id", nullable = false)
    private SavingsAccountModel savings_account;

    @OneToMany(mappedBy = "debit_card", cascade = CascadeType.ALL)
    private List<TransactionsModel> transactions;

}

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
@Table(name = "credit_card_activities")
public class CreditCardActivityModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "date_of_transaction", nullable = false)
    private Date date_of_transaction;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "type", nullable = false)
    private String type;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "credit_card_id", nullable = false)
    private CreditCardModel credit_card;
}

package com.djg_bank.djg_bank.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "credit_card")
@Data
public class CreditCardModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", nullable = false, length = 100)
    private String card_number;

    @Column(name = "card_type", nullable = false, length = 100)
    private String card_type;

    @Column(name = "expiry_date", nullable = false)
    private Date expiry_date;

    @Column(name = "cvv", nullable = false, length = 3)
    private String cvv;

    @Column(name = "date_issued", nullable = false)
    private Date date_issued;

    @Column(name = "credit_limit", nullable = false)
    private Double credit_limit;

    @Column(name = "current_debt", nullable = false)
    private Double current_debt;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @OneToMany(mappedBy = "credit_card", cascade = CascadeType.ALL)
    private List<CreditCardActivityModel> credit_card_activity;
}

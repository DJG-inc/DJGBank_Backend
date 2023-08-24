package com.djg_bank.djg_bank.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "users")
@Data
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 100)
    private String user_id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String first_name;

    @Column(name = "last_name", nullable = false, length = 100)
    private String last_name;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "date_of_birth", nullable = false, length = 100)
    private String date_of_birth;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "phone_number", nullable = false, length = 100)
    private String phone_number;

    @Transient
    private String token;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<LoanModel> loans;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CreditCardModel> credit_cards;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private SavingsAccountModel savings_account;

}

package com.djg_bank.djg_bank.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = true, length = 100)
    private String user_id;

    @Column(name = "first_name", nullable = true, length = 100)
    private String first_name;

    @Column(name = "last_name", nullable = true, length = 100)
    private String last_name;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "date_of_birth", nullable = true, length = 100)
    private String date_of_birth;

    @Column(name = "address", nullable = true, length = 100)
    private String address;

    @Column(name = "phone_number", nullable = true, length = 100)
    private String phone_number;

    @Column(name = "status", nullable = false, length = 100)
    private String status = "pending";

    @Transient
    private String token;

    @Transient
    private String confirmation_token;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<LoanModel> loans;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CreditCardModel> credit_cards;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private SavingsAccountModel savings_account;

}

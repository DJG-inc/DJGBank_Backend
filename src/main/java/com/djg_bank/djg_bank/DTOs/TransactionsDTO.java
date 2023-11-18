package com.djg_bank.djg_bank.DTOs;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TransactionsDTO {
    private Long id;
    private String user_id;
    private String number_of_savings_account;
    private Double amount;
    private Date date_of_transaction;
    private String description;
}

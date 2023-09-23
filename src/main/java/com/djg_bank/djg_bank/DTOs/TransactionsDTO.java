package com.djg_bank.djg_bank.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
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

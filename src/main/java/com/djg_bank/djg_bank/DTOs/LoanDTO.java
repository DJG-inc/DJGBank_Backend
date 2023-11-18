package com.djg_bank.djg_bank.DTOs;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LoanDTO {
    private Long id;
    private Double amount;
    private Double interest_rate;
    private Double monthly_payment;
    private String start_date;
    private Date end_date;
}

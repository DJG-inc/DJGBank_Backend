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
public class LoanDTO {
    private Long id;
    private Double amount;
    private Double interest_rate;
    private Double monthly_payment;
    private Date start_date;
    private Date end_date;
}

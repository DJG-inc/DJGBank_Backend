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
public class SavingsAccountDTO {
    private Long id;
    private Double balance;
    private String created_at;
    private Double interest_rate;
}

package com.djg_bank.djg_bank.DTOs;


import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

public class SavingsAccountDTO {
    private Long id;
    private String account_number;
    private Double balance;
    private String created_at;
    private Double interest_rate;
}

package com.djg_bank.djg_bank.DTOs;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CreditCardDTO {
    private Long id;
    private String card_number;
    private String card_type;
    private Date expiry_date;
    private String cvv;
    private Date date_issued;
    private Double credit_limit;
    private Double current_debt;
}

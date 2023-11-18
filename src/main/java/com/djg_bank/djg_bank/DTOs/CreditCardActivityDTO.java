package com.djg_bank.djg_bank.DTOs;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CreditCardActivityDTO {
    private Long id;
    private Double amount;
    private Date date_of_transaction;
    private String description;
    private String type;
}

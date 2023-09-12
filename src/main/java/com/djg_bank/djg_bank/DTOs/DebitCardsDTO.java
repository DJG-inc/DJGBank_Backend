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
public class DebitCardsDTO {
    private Long id;
    private String card_type;
    private String card_number;
    private Date expiry_date;
    private String cvv;
    private Date date_issued;
}

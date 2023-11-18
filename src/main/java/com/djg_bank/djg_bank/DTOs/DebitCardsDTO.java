package com.djg_bank.djg_bank.DTOs;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

package com.djg_bank.djg_bank.DTOs;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class IpAdressDTO {
    private Long id;
    private String ip;
}

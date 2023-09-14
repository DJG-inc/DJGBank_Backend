package com.djg_bank.djg_bank.DTOs;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder
@ToString
public class UserDTO {
    private Long id;
    private String user_id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String date_of_birth;
    private String address;
    private String phone_number;
    private String status;
}

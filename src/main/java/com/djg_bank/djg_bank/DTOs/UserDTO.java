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
public class UserDTO {
    private Long id;
    private String user_id;
    private String first_Name;
    private String last_Name;
    private String email;
    private String password;
    private Date date_of_birth;
    private String address;
    private String phone_number;
}

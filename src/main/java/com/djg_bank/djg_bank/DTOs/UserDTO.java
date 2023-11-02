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
    private String ip_address;
}


// Let's create a json file for testing the complete-register endpoint:
// {
//     "user_id": "12345678",
//     "first_name": "John",
//     "last_name": "Doe",
//     "date_of_birth": "07/12/2001",
//     "address": "Calle 123",
//     "phone_number": "12345678"
// }
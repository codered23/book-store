package com.example.bookstore.dto.user;

import lombok.Data;

@Data
public class UserRegistrationResponseDto {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String shippingAddress;
}

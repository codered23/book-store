package com.example.bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @Email
    @NotNull
    private String email;
    @NotNull
    private String password;
}

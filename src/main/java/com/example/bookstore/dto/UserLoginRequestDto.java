package com.example.bookstore.dto;

import com.example.bookstore.validation.anotations.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @Email
    @NotNull
    private String email;
    @NotNull @Size(min = 6)
    private String password;
}

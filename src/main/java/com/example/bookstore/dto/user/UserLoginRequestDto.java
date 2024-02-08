package com.example.bookstore.dto.user;

import com.example.bookstore.validation.anotations.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank @Size(min = 6)
    private String password;
}

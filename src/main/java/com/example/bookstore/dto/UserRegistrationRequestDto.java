package com.example.bookstore.dto;

import com.example.bookstore.validation.anotations.Email;
import com.example.bookstore.validation.anotations.FieldMatch;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@FieldMatch(first = "password", second = "repeatPassword", message = "Passwords are not the same")
public class UserRegistrationRequestDto {
    @NotNull
    @Email
    private String email;
    @NotNull
    @Size(min = 6)
    private String password;
    @NotNull
    @Size(min = 6)
    private String repeatPassword;
    @NotNull
    @Size(max = 25)
    private String firstName;
    @NotNull
    @Size(max = 25)
    private String lastName;
    private String shippingAddress;
}

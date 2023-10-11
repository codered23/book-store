package com.example.bookstore.dto;

import com.example.bookstore.validation.FieldMatch;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@FieldMatch(first = "password", second = "repeatPassword", message = "Passwords are not the same")
public class UserRegistrationRequestDto {
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String repeatPassword;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private String shippingAddress;

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }
}

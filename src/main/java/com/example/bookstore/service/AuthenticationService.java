package com.example.bookstore.service;

import com.example.bookstore.dto.UserLoginRequestDto;
import com.example.bookstore.dto.UserLoginResponseDto;
import com.example.bookstore.dto.UserRegistrationRequestDto;
import com.example.bookstore.dto.UserRegistrationResponseDto;

public interface AuthenticationService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto
                                                 requestDto);

    UserLoginResponseDto login(UserLoginRequestDto request);
}

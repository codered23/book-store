package com.example.bookstore.service;

import com.example.bookstore.dto.user.UserLoginRequestDto;
import com.example.bookstore.dto.user.UserLoginResponseDto;
import com.example.bookstore.dto.user.UserRegistrationRequestDto;
import com.example.bookstore.dto.user.UserRegistrationResponseDto;

public interface AuthenticationService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto
                                                 requestDto);

    UserLoginResponseDto login(UserLoginRequestDto request);
}

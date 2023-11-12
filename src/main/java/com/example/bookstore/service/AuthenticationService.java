package com.example.bookstore.service;

import com.example.bookstore.dto.UserLoginRequestDto;
import com.example.bookstore.dto.UserLoginResponseDto;
import com.example.bookstore.dto.UserRegistrationRequestDto;
import com.example.bookstore.dto.UserRegistrationResponseDto;
import com.example.bookstore.exception.RegistrationException;

public interface AuthenticationService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto
                                                 requestDto) throws RegistrationException;

    UserLoginResponseDto login(UserLoginRequestDto request);
}

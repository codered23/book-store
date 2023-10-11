package com.example.bookstore.controller;

import com.example.bookstore.dto.UserLoginRequestDto;
import com.example.bookstore.dto.UserLoginResponseDto;
import com.example.bookstore.dto.UserRegistrationRequestDto;
import com.example.bookstore.dto.UserRegistrationResponseDto;
import com.example.bookstore.exception.RegistrationException;
import com.example.bookstore.mapper.UserMapper;
import com.example.bookstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final UserMapper mapper;
    private final UserService userService;

    public AuthenticationController(UserMapper mapper, UserService userService) {
        this.mapper = mapper;
        this.userService = userService;
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return userService.login(request);
    }

    @PostMapping("/register")
    public UserRegistrationResponseDto register(@RequestBody @Valid
                                                    UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }
}

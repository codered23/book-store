package com.example.bookstore.service.impl;

import com.example.bookstore.dto.UserLoginRequestDto;
import com.example.bookstore.dto.UserLoginResponseDto;
import com.example.bookstore.dto.UserRegistrationRequestDto;
import com.example.bookstore.dto.UserRegistrationResponseDto;
import com.example.bookstore.exception.RegistrationException;
import com.example.bookstore.mapper.UserMapper;
import com.example.bookstore.model.Role;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository, PasswordEncoder encoder) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Unable to complete registration");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(encoder.encode(user.getPassword()));
        Role role = new Role();
        role.setName(Role.RoleName.USER);
        user.addRole(role);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto request) {
        return null;
    }
}

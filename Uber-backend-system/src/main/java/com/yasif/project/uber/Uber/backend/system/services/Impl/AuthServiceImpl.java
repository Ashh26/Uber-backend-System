package com.yasif.project.uber.Uber.backend.system.services.Impl;

import com.yasif.project.uber.Uber.backend.system.dto.DriverDto;
import com.yasif.project.uber.Uber.backend.system.dto.SignupDto;
import com.yasif.project.uber.Uber.backend.system.dto.UserDto;
import com.yasif.project.uber.Uber.backend.system.entities.User;
import com.yasif.project.uber.Uber.backend.system.entities.enums.Role;
import com.yasif.project.uber.Uber.backend.system.exceptions.RuntimeConflictException;
import com.yasif.project.uber.Uber.backend.system.repositories.UserRepository;
import com.yasif.project.uber.Uber.backend.system.services.AuthService;
import com.yasif.project.uber.Uber.backend.system.services.RiderService;
import com.yasif.project.uber.Uber.backend.system.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RiderService riderService;
    private final WalletService walletService;

    @Override
    public void login(String email, String password) {

    }

    @Override
    public UserDto signup(SignupDto signupDto) {
       User user = userRepository.findByEmail(signupDto.getEmail()).orElse(null);
       if(user!=null){
           throw new RuntimeConflictException("User already exist with email "+signupDto.getEmail());
       }

        User mapUser = modelMapper.map(signupDto,User.class);
        mapUser.setRoles(Set.of(Role.RIDER));
        User savedUser = userRepository.save(mapUser);

        // creating user related entities
        riderService.createNewRider(savedUser);

        // after signup create a new wallet
        walletService.createNewWallet(savedUser);

        return modelMapper.map(savedUser,UserDto.class);
    }

    @Override
    public DriverDto onBoardNewDriver(Long userId) {
        return null;
    }
}

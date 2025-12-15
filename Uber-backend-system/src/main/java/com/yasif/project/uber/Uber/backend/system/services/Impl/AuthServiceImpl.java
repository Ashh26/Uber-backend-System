package com.yasif.project.uber.Uber.backend.system.services.Impl;

import com.yasif.project.uber.Uber.backend.system.dto.DriverDto;
import com.yasif.project.uber.Uber.backend.system.dto.SignupDto;
import com.yasif.project.uber.Uber.backend.system.dto.UserDto;
import com.yasif.project.uber.Uber.backend.system.entities.Driver;
import com.yasif.project.uber.Uber.backend.system.entities.User;
import com.yasif.project.uber.Uber.backend.system.entities.enums.Role;
import com.yasif.project.uber.Uber.backend.system.exceptions.ResourceNotFoundException;
import com.yasif.project.uber.Uber.backend.system.exceptions.RuntimeConflictException;
import com.yasif.project.uber.Uber.backend.system.repositories.UserRepository;
import com.yasif.project.uber.Uber.backend.system.services.AuthService;
import com.yasif.project.uber.Uber.backend.system.services.DriverService;
import com.yasif.project.uber.Uber.backend.system.services.RiderService;
import com.yasif.project.uber.Uber.backend.system.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.yasif.project.uber.Uber.backend.system.entities.enums.Role.DRIVER;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RiderService riderService;
    private final WalletService walletService;
    private final DriverService driverService;

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
    public DriverDto onBoardNewDriver(Long userId,String vehicleId) {

        // first let's find the user
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new ResourceNotFoundException("User not found with id:"+userId)
        );

        // then check if the user is already exists
        if(user.getRoles().contains(DRIVER)){
            throw new RuntimeConflictException("User with id:"+userId+" already exists");
        }

        // now let's create a new Driver
        Driver createDriver = Driver.builder()
                .user(user)
                .rating(0.0)
                .vehicleId(vehicleId)
                .available(true)
                .build();

        // before saving make the role of user set to Driver
        user.getRoles().add(DRIVER);

        // save the user into repository
        userRepository.save(user);

        // call the create new driver method of driver service and saved the createdDriver into savedDriver
        Driver savedDriver = driverService.createNewDriver(createDriver);

        return modelMapper.map(savedDriver,DriverDto.class);
    }
}

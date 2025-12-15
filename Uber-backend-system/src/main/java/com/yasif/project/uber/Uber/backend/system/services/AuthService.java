package com.yasif.project.uber.Uber.backend.system.services;

import com.yasif.project.uber.Uber.backend.system.dto.DriverDto;
import com.yasif.project.uber.Uber.backend.system.dto.SignupDto;
import com.yasif.project.uber.Uber.backend.system.dto.UserDto;

public interface AuthService {
    void login(String email,String password);

    UserDto signup(SignupDto signupDto);

    DriverDto onBoardNewDriver(Long userId,String vehicleId);

}

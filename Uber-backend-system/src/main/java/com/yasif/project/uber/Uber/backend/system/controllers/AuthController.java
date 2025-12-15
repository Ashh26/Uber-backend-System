package com.yasif.project.uber.Uber.backend.system.controllers;

import com.yasif.project.uber.Uber.backend.system.dto.DriverDto;
import com.yasif.project.uber.Uber.backend.system.dto.OnboardDriverDto;
import com.yasif.project.uber.Uber.backend.system.dto.SignupDto;
import com.yasif.project.uber.Uber.backend.system.dto.UserDto;
import com.yasif.project.uber.Uber.backend.system.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    ResponseEntity<UserDto> singUp(@RequestBody SignupDto signupDto){
        return new ResponseEntity<>(authService.signup(signupDto), HttpStatus.CREATED);
    }

    @PostMapping("onBoardNewDriver")
    public ResponseEntity<DriverDto> onBoardNewDriver(@PathVariable Long userId,
                                                      @RequestBody
                                                      OnboardDriverDto onboardDriverDto){
        return new ResponseEntity<>(authService.onBoardNewDriver(userId,
                onboardDriverDto.getVehicleId()),HttpStatus.CREATED);
    }

}

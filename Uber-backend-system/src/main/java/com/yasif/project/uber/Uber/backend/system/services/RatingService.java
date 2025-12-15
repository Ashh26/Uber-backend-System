package com.yasif.project.uber.Uber.backend.system.services;

import com.yasif.project.uber.Uber.backend.system.dto.DriverDto;
import com.yasif.project.uber.Uber.backend.system.dto.RiderDto;
import com.yasif.project.uber.Uber.backend.system.entities.Ride;

public interface RatingService {
    DriverDto rateDriver(Ride ride,Integer rating);
    RiderDto rateRider(Ride ride,Integer rating);

    void createNewRating(Ride ride);

}

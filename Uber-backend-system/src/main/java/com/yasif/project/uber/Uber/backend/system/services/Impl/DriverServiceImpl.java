package com.yasif.project.uber.Uber.backend.system.services.Impl;

import com.yasif.project.uber.Uber.backend.system.dto.DriverDto;
import com.yasif.project.uber.Uber.backend.system.dto.RideDto;
import com.yasif.project.uber.Uber.backend.system.dto.RiderDto;
import com.yasif.project.uber.Uber.backend.system.entities.Driver;
import com.yasif.project.uber.Uber.backend.system.entities.Ride;
import com.yasif.project.uber.Uber.backend.system.entities.RideRequest;
import com.yasif.project.uber.Uber.backend.system.entities.Rider;
import com.yasif.project.uber.Uber.backend.system.entities.enums.RideRequestStatus;
import com.yasif.project.uber.Uber.backend.system.entities.enums.RideStatus;
import com.yasif.project.uber.Uber.backend.system.exceptions.ResourceNotFoundException;
import com.yasif.project.uber.Uber.backend.system.repositories.DriverRepository;
import com.yasif.project.uber.Uber.backend.system.services.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final RideRequestService rideRequestService;
    private final DriverRepository driverRepository;
    private final RideService rideService;
    private final ModelMapper modelMapper;
    private final PaymentService paymentService;
    private final RatingService ratingService;

    @Transactional
    @Override
    public RideDto acceptRide(Long rideRequestId) {
        RideRequest rideRequest = rideRequestService.findRideRequestById(rideRequestId);

        if(!rideRequest.getRideRequestStatus().equals(RideRequestStatus.PENDING)){
            throw new RuntimeException(
                    "RideRequest cannot be accepted, status is:"+rideRequest.getRideRequestStatus());
        }

        Driver currentDriver = getCurrentDriver();
        if(!currentDriver.getAvailable()){
            throw new RuntimeException("Driver is not available");
        }


        Driver savedDriver = updateDriverAvailability(currentDriver,false);
        Ride ride = rideService.createNewRide(rideRequest,savedDriver);


        return modelMapper.map(ride,RideDto.class);
    }

    @Override
    public RideDto cancelRide(Long rideId) {

        // get the ride by id
        Ride ride = rideService.getRideById(rideId);

        // get the current driver
        Driver driver = getCurrentDriver();

        //check if the driver of rider is equal to the current driver
        if(!driver.equals(ride.getDriver())){
            throw new RuntimeException("Driver cannot start the ride as he has not accepted it earlier");
        }

        // now check if the RideStatus is CONFIRMED only that we can Cancel otherwise we cannot cancel
        // the ride if RideStatus is CANCELLED,ONGOING,ENDED
        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            throw new RuntimeException("Ride cannot be cancelled, invalid status:"+ride.getRideStatus());
        }

        // Now update the ride status to cancelled
        rideService.updateRideStatus(ride,RideStatus.CANCELLED);

        // and then once the ride is cancelled th driver is available again
        // and save the driver
        updateDriverAvailability(driver,true);

        return modelMapper.map(ride,RideDto.class);
    }

    @Override
    public RideDto startRide(Long rideId,String otp) {

        Ride ride = rideService.getRideById(rideId);
        Driver driver = getCurrentDriver();

        if(!driver.equals(ride.getDriver())){
            throw new RuntimeException("Driver cannot start the ride as he has not accepted it earlier");
        }

        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED)){
            throw new RuntimeException(
                    "Ride status is not CONFIRMED hence cannot be started, status:"+ride.getRideStatus());
        }

        if(!otp.equals(ride.getOtp())){
            throw new RuntimeException("Otp is not valid, Otp:"+otp);
        }

        ride.setStartedAt(LocalDateTime.now());
        Ride savedRide = rideService.updateRideStatus(ride,RideStatus.ONGOING);

        // after the ride has started we have to create payment object
        paymentService.createNewPayment(savedRide);

        return modelMapper.map(savedRide,RideDto.class) ;
    }

    @Override
    @Transactional
    public RideDto endRide(Long rideId) {
        // get the ride by id
        Ride ride = rideService.getRideById(rideId);

        // get the current driver
        Driver driver = getCurrentDriver();

        //check if the driver of rider is equal to the current driver
        if(!driver.equals(ride.getDriver())){
            throw new RuntimeException("Driver cannot start the ride as he has not accepted it earlier");}
        // now check if the RideStatus is ONGOING only that we can End otherwise we cannot End
        // the ride if RideStatus is CANCELLED,CONFIRMED,ENDED
        if(!ride.getRideStatus().equals(RideStatus.ONGOING)){
            throw new RuntimeException(
                    "Ride status is not ONGOING hence cannot be ENDED, invalid status:"
                            +ride.getRideStatus());
        }
        // add the ended time
        ride.setEndedAt(LocalDateTime.now());

        // now the ride is ENDED so we can set the Ride status as ENDED
        Ride savedRide = rideService.updateRideStatus(ride,RideStatus.ENDED);

        // then update the driver's availability to true
        updateDriverAvailability(driver,true);

        // then go to process payment
        paymentService.processPayment(ride);
        return modelMapper.map(savedRide,RideDto.class);
    }

    @Override
    public RiderDto rateRider(Long rideId, Integer rating) {
        // first get the driver and rider
        Driver driver = getCurrentDriver();
        Ride ride = rideService.getRideById(rideId);

        // now match the currentDriver with exact rider
        if (!driver.equals(ride.getDriver())){
            throw new RuntimeException("Driver is not the owner of this ride");
        }

        //If the ride is not in the correct stage, the system stops the rating process and
        // tells the reason — the ride isn’t finished yet, so rating is not allowed now.
        if(!ride.getRideStatus().equals(RideStatus.ONGOING)){
            throw new RuntimeException(
                    "Ride is not ended so we cannot start rating. status:"+ride.getRideStatus());
        }


        return ratingService.rateRider(ride,rating);
    }

    @Override
    public DriverDto getMyProfile() {

        // get the current driver and return it to dto
        Driver currentDriver = getCurrentDriver();

        return modelMapper.map(currentDriver,DriverDto.class);
    }

    @Override
    public Page<RideDto> getAllMyRides(PageRequest pageRequest) {

        // get the current Driver
        Driver currentDriver = getCurrentDriver();

        // and then return the page of RideDto

        return rideService.getAllRidesOfDriver(currentDriver,pageRequest).map(
                ride->modelMapper.map(ride,RideDto.class)
        );
    }

    @Override
    public Driver getCurrentDriver() {
        return driverRepository.findById(2L).orElseThrow(()->new ResourceNotFoundException(
                "Driver not found with id:"+2 ));
    }

    @Override
    public Driver updateDriverAvailability(Driver driver, boolean available) {
        // set the availability and save the driver

        driver.setAvailable(available);
        return driverRepository.save(driver);
    }

    @Override
    public Driver createNewDriver(Driver driver) {
        return driverRepository.save(driver);
    }
}

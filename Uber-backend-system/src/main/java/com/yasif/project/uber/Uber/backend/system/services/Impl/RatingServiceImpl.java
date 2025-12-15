package com.yasif.project.uber.Uber.backend.system.services.Impl;

import com.yasif.project.uber.Uber.backend.system.dto.DriverDto;
import com.yasif.project.uber.Uber.backend.system.dto.RiderDto;
import com.yasif.project.uber.Uber.backend.system.entities.Driver;
import com.yasif.project.uber.Uber.backend.system.entities.Rating;
import com.yasif.project.uber.Uber.backend.system.entities.Ride;
import com.yasif.project.uber.Uber.backend.system.entities.Rider;
import com.yasif.project.uber.Uber.backend.system.exceptions.ResourceNotFoundException;
import com.yasif.project.uber.Uber.backend.system.repositories.DriverRepository;
import com.yasif.project.uber.Uber.backend.system.repositories.RatingRepository;
import com.yasif.project.uber.Uber.backend.system.repositories.RiderRepository;
import com.yasif.project.uber.Uber.backend.system.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final DriverRepository driverRepository;
    private  final ModelMapper modelMapper;
    private final RiderRepository riderRepository;

    @Override
    public DriverDto rateDriver(Ride ride, Integer rating) {

        // first get the driver
        Driver driver = ride.getDriver();

        // now get the rating of that driver
        Rating ratingObj = ratingRepository.findByRide(ride).orElseThrow(
                ()-> new ResourceNotFoundException("Rating not found with id:"+ride.getId())
        );

        // now we check if the driver is already rate the rider
        if(ratingObj.getDriverRating()!=null){
            throw new RuntimeException("Driver has already been rated, cannot rate again");
        }

        // now set the rate and saving into repository
        ratingObj.setDriverRating(rating);
        ratingRepository.save(ratingObj);

        //this block is recomputing the driver’s overall rating based on historical feedback
        // and then aligning the driver entity with the latest aggregated score.

        //Pulls all rating records associated with the given driver from persistence.
        //This is the source-of-truth dataset for performance evaluation.
        Double newRating = ratingRepository.findByDriver(driver)

//        It turns the list into a stream so it can be processed step by step,
//        making it easier to filter, transform, or calculate values
//        like averages in a clean and readable way.
                .stream()
//        It takes only the driver’s rating number from each rating object and
//        ignores the rest of the data, so the system can work only with numbers for calculation.
                .mapToDouble(Rating::getDriverRating)
//        It **adds all the driver ratings together and divides by how many ratings there are**,
//        giving **one average rating that represents overall performance**.
                .average()
                .orElse(0.0);

//        It stores the newly calculated average rating back into the driver,
//        so all parts of the system use the latest and correct rating.
            driver.setRating(newRating);

            // now save the driver
        Driver savedDriver = driverRepository.save(driver);

        return modelMapper.map(savedDriver,DriverDto.class) ;
    }

    @Override
    public RiderDto rateRider(Ride ride, Integer rating) {
        // first get the rider
        Rider rider = ride.getRider();

        // now get the rating of that driver
        Rating ratingObj = ratingRepository.findByRide(ride).orElseThrow(
                ()-> new ResourceNotFoundException("Rating not found with id:"+ride.getId())
        );

        // now we check if the driver is already rate the rider
        if(ratingObj.getRiderRating()!=null){
            throw new RuntimeException("Driver has already been rated, cannot rate again");
        }

        // now set the rate and saving into repository
        ratingObj.setRiderRating(rating);
        ratingRepository.save(ratingObj);

        //this block is recomputing the driver’s overall rating based on historical feedback
        // and then aligning the driver entity with the latest aggregated score.

        //Pulls all rating records associated with the given driver from persistence.
        //This is the source-of-truth dataset for performance evaluation.
        Double newRating = ratingRepository.findByRider(rider)

//        It turns the list into a stream so it can be processed step by step,
//        making it easier to filter, transform, or calculate values
//        like averages in a clean and readable way.
                .stream()
//        It takes only the driver’s rating number from each rating object and
//        ignores the rest of the data, so the system can work only with numbers for calculation.
                .mapToDouble(Rating::getRiderRating)
//        It **adds all the driver ratings together and divides by how many ratings there are**,
//        giving **one average rating that represents overall performance**.
                .average()
                .orElse(0.0);

//        It stores the newly calculated average rating back into the driver,
//        so all parts of the system use the latest and correct rating.
        rider.setRating(newRating);

        // now save the driver
        Rider savedDriver = riderRepository.save(rider);

        return modelMapper.map(savedDriver,RiderDto.class) ;
    }

    @Override
    public void createNewRating(Ride ride) {

    }
}

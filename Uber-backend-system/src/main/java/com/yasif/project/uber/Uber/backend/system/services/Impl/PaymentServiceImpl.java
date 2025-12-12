package com.yasif.project.uber.Uber.backend.system.services.Impl;

import com.yasif.project.uber.Uber.backend.system.entities.Payment;
import com.yasif.project.uber.Uber.backend.system.entities.Ride;
import com.yasif.project.uber.Uber.backend.system.entities.enums.PaymentStatus;
import com.yasif.project.uber.Uber.backend.system.exceptions.ResourceNotFoundException;
import com.yasif.project.uber.Uber.backend.system.repositories.PaymentRepository;
import com.yasif.project.uber.Uber.backend.system.services.PaymentService;
import com.yasif.project.uber.Uber.backend.system.strategies.PaymentStrategyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStrategyManager paymentStrategyManager;

    @Override
    public void processPayment(Ride ride) {

        Payment payment = paymentRepository.findByRide(ride).orElseThrow(
                ()-> new ResourceNotFoundException("Payment not found with id:"+ride.getId())
        );

        // first the paymentStrategy pick the right payment method using paymentStrategy then
        // we go to processPayment method to process the payment

        paymentStrategyManager.paymentStrategy(payment.getPaymentMethod())
                .processPayment(payment);

    }

    @Override
    public Payment createNewPayment(Ride ride) {

        // here we make payment object and build by adding corresponding fields into Ride entity

        Payment payment = Payment.builder()
                .ride(ride)
                .paymentMethod(ride.getPaymentMethod())
                .amount(ride.getFare())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

                // save the payment object into repository and return it
        return paymentRepository.save(payment);
    }

    @Override
    public void updatePaymentStatus(Payment payment, PaymentStatus status) {
        // after the payment is done set the payment status and save into repository
        payment.setPaymentStatus(status);
        paymentRepository.save(payment);
    }
}

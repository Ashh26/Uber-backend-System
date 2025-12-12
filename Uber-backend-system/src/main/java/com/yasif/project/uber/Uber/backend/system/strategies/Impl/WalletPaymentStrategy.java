package com.yasif.project.uber.Uber.backend.system.strategies.Impl;

import com.yasif.project.uber.Uber.backend.system.entities.Driver;
import com.yasif.project.uber.Uber.backend.system.entities.Payment;
import com.yasif.project.uber.Uber.backend.system.entities.Rider;
import com.yasif.project.uber.Uber.backend.system.entities.enums.PaymentStatus;
import com.yasif.project.uber.Uber.backend.system.entities.enums.TransactionMethod;
import com.yasif.project.uber.Uber.backend.system.repositories.PaymentRepository;
import com.yasif.project.uber.Uber.backend.system.services.WalletService;
import com.yasif.project.uber.Uber.backend.system.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


// Rider had 250 and Driver had 500
// Ride cost is 100 and Platform commission is 30
// Rider -> 250-100 = 150
// Driver -> 500+(100-30) = 570

@Service
@RequiredArgsConstructor
public class WalletPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public void processPayment(Payment payment) {
        // first get the Driver and Rider
        Rider rider = payment.getRide().getRider();
        Driver driver = payment.getRide().getDriver();

        // now deduct money from Rider's waller
        walletService.deductMoneyFromWallet(rider.getUser(),payment.getAmount(),null
        ,payment.getRide(), TransactionMethod.RIDE);

        // for ex Driver 100*(1-0.3) = 100*(0.7) = 70
        double driversCut =  payment.getAmount()*(1-PLATFORM_COMMISSION);

        // now add money to the Driver's wallet
        walletService.addMoneyToWallet(driver.getUser(),
                driversCut,null,payment.getRide(),TransactionMethod.RIDE);

        // after that update the payment status to CONFIRMED
        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);


    }
}

package com.yasif.project.uber.Uber.backend.system.strategies.Impl;

import com.yasif.project.uber.Uber.backend.system.entities.Driver;
import com.yasif.project.uber.Uber.backend.system.entities.Payment;
import com.yasif.project.uber.Uber.backend.system.entities.enums.PaymentStatus;
import com.yasif.project.uber.Uber.backend.system.entities.enums.TransactionMethod;
import com.yasif.project.uber.Uber.backend.system.repositories.PaymentRepository;
import com.yasif.project.uber.Uber.backend.system.services.WalletService;
import com.yasif.project.uber.Uber.backend.system.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//underStand the CashStrategy.
//     The Ride amount is 100. showing in rider(Means rider has to pay 100).
//     Now the rider give 100 cash to driver. from which 30rs is the app's commission,
//     which is app 30rs amount deduct from the drivers' wallet (so we need driver's wallet,and do not
//      need rider's wallet).
//     And the driver earn RS 70.

@Service
@RequiredArgsConstructor
public class CashPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;

    @Override
    public void processPayment(Payment payment) {
        // first get the driver
        Driver driver = payment.getRide().getDriver();

        // calculate the amount will be deducted from driver's wallet
        double platformCommission = payment.getAmount()*PLATFORM_COMMISSION;

        // now deduct money from driver's wallet
        walletService.deductMoneyFromWallet(driver.getUser(), platformCommission,null,
                payment.getRide(), TransactionMethod.RIDE);

        // after that update the payment status to CONFIRMED
        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);

    }
}

//10 ratingsCount -> 4.0
//new rating 4.6
//updated rating
//new rating 44.6/11 -> 4.05



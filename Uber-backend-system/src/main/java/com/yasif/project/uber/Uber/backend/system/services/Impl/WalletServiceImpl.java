package com.yasif.project.uber.Uber.backend.system.services.Impl;

import com.yasif.project.uber.Uber.backend.system.entities.Ride;
import com.yasif.project.uber.Uber.backend.system.entities.User;
import com.yasif.project.uber.Uber.backend.system.entities.Wallet;
import com.yasif.project.uber.Uber.backend.system.entities.WalletTransaction;
import com.yasif.project.uber.Uber.backend.system.entities.enums.TransactionMethod;
import com.yasif.project.uber.Uber.backend.system.entities.enums.TransactionType;
import com.yasif.project.uber.Uber.backend.system.exceptions.ResourceNotFoundException;
import com.yasif.project.uber.Uber.backend.system.repositories.WalletRepository;
import com.yasif.project.uber.Uber.backend.system.services.WalletService;
import com.yasif.project.uber.Uber.backend.system.services.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionService walletTransactionService;

    @Override
    @Transactional
    public Wallet addMoneyToWallet(User user, Double amount, String transactionId, Ride ride
                                   , TransactionMethod transactionMethod) {
        // first get the user and then set the balance of wallet
        Wallet wallet = findByUser(user);

        // now set the wallet balance by getting the balance of wallet and add the amount to the wallet
        wallet.setBalance(wallet.getBalance()+amount);

        // Now creating a wallet transaction object
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .transactionId(transactionId)
                .ride(ride)
                .wallet(wallet)
                .transactionType(TransactionType.CREDIT)
                .transactionMethod(transactionMethod)
                .amount(amount)
                .build();
        // creating wallet transaction by walletTransaction object
        walletTransactionService.createNewWalletTransaction(walletTransaction);

        // now save the wallet
        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public Wallet deductMoneyFromWallet(User user, Double amount,String transactionId
    ,Ride ride,TransactionMethod transactionMethod) {
        // first get the user and then set the balance of wallet
        Wallet wallet = findByUser(user);

        // now set the wallet balance by getting the balance of wallet and deduct the amount to the wallet
        wallet.setBalance(wallet.getBalance()-amount);

        // Now creating a wallet transaction object
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .transactionId(transactionId)
                .ride(ride)
                .wallet(wallet)
                .transactionType(TransactionType.DEBIT)
                .transactionMethod(transactionMethod)
                .amount(amount)
                .build();
        // creating wallet transaction by walletTransaction object
        walletTransactionService.createNewWalletTransaction(walletTransaction);

//        wallet.getTransactions().add(walletTransaction);


        // now save the wallet
        return walletRepository.save(wallet);
    }

    @Override
    public void withdrawAllMyMoneyFromWallet() {

    }

    @Override
    public Wallet findWalletById(Long walletId) {
        return walletRepository.findById(walletId).orElseThrow(
                ()-> new ResourceNotFoundException("Wallet not found with id:"+walletId)
        );
    }

    @Override
    public Wallet createNewWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet findByUser(User user) {
        return walletRepository.findByUser(user).orElseThrow(
                ()-> new ResourceNotFoundException("Wallet not found for user with id:"+user.getId())
        );
    }
}

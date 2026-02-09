package com.example.wallet.service;

import com.example.wallet.dto.WalletRequest;
import com.example.wallet.entity.Wallet;
import com.example.wallet.exception.*;
import com.example.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository repository;

    public WalletService(WalletRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Wallet process(WalletRequest req) {

        if (req.amount() <= 0) {
            throw new InvalidAmountException();
        }

        Wallet wallet = repository.findByIdForUpdate(req.walletId())
                .orElseThrow(WalletNotFoundException::new);

        if (req.operationType() == WalletRequest.OperationType.WITHDRAW &&
                wallet.getBalance() < req.amount()) {
            throw new InsufficientFundsException();
        }

        long newBalance = req.operationType() == WalletRequest.OperationType.DEPOSIT
                ? wallet.getBalance() + req.amount()
                : wallet.getBalance() - req.amount();

        wallet.setBalance(newBalance);
        return wallet;
    }

    public Wallet get(UUID id) {
        return repository.findById(id)
                .orElseThrow(WalletNotFoundException::new);
    }
}

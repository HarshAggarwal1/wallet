package com.example.wallet.dto;

import java.util.UUID;

public record WalletRequest(
        UUID walletId,
        OperationType operationType,
        long amount
) {
    public enum OperationType {
        DEPOSIT, WITHDRAW
    }
}

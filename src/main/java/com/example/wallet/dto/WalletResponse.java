package com.example.wallet.dto;

import java.util.UUID;

public record WalletResponse(
        UUID walletId,
        long balance
) {}

package com.example.wallet.controller;

import com.example.wallet.dto.*;
import com.example.wallet.entity.Wallet;
import com.example.wallet.service.WalletService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class WalletController {

    private final WalletService service;

    public WalletController(WalletService service) {
        this.service = service;
    }

    @PostMapping("/wallet")
    public WalletResponse operate(@RequestBody WalletRequest req) {
        Wallet wallet = service.process(req);
        return new WalletResponse(wallet.getId(), wallet.getBalance());
    }

    @GetMapping("/wallets/{id}")
    public WalletResponse get(@PathVariable UUID id) {
        Wallet wallet = service.get(id);
        return new WalletResponse(wallet.getId(), wallet.getBalance());
    }
}

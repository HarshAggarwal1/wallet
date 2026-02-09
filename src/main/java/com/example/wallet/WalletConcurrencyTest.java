package com.example.wallet;

import com.example.wallet.dto.WalletRequest;
import com.example.wallet.entity.Wallet;
import com.example.wallet.repository.WalletRepository;
import com.example.wallet.service.WalletService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WalletConcurrencyTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void concurrentWithdrawsAreSafe() throws Exception {

        UUID walletId = UUID.randomUUID();

        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(1000);
        walletRepository.save(wallet);

        int threads = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    walletService.process(
                            new WalletRequest(
                                    walletId,
                                    WalletRequest.OperationType.WITHDRAW,
                                    1
                            )
                    );
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Wallet updated = walletRepository.findById(walletId).orElseThrow();

        // 100 withdrawals of 1 from 1000
        assertThat(updated.getBalance()).isEqualTo(900);
    }
}

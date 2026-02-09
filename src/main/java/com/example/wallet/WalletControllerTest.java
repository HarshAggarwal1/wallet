package com.example.wallet;

import com.example.wallet.entity.Wallet;
import com.example.wallet.repository.WalletRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID walletId;

    @BeforeEach
    void setup() {
        walletRepository.deleteAll();
        walletId = UUID.randomUUID();

        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(1000);
        walletRepository.save(wallet);
    }

    @Test
    void depositSuccess() throws Exception {
        String request = """
        {
          "walletId": "%s",
          "operationType": "DEPOSIT",
          "amount": 500
        }
        """.formatted(walletId);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500));
    }

    @Test
    void withdrawSuccess() throws Exception {
        String request = """
        {
          "walletId": "%s",
          "operationType": "WITHDRAW",
          "amount": 300
        }
        """.formatted(walletId);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(700));
    }

    @Test
    void withdrawInsufficientFunds() throws Exception {
        String request = """
        {
          "walletId": "%s",
          "operationType": "WITHDRAW",
          "amount": 5000
        }
        """.formatted(walletId);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("INSUFFICIENT_FUNDS"));
    }

    @Test
    void getWalletBalance() throws Exception {
        mockMvc.perform(get("/api/v1/wallets/{id}", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    void walletNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/wallets/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("WALLET_NOT_FOUND"));
    }
}

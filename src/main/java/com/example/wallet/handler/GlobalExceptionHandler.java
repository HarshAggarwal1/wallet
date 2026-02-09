package com.example.wallet.handler;

import com.example.wallet.exception.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    record ErrorResponse(String errorCode, String message) {}

    @ExceptionHandler(WalletNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse notFound() {
        return new ErrorResponse("WALLET_NOT_FOUND", "Wallet does not exist");
    }

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse insufficientFunds() {
        return new ErrorResponse("INSUFFICIENT_FUNDS", "Not enough balance");
    }

    @ExceptionHandler(InvalidAmountException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse invalidAmount() {
        return new ErrorResponse("INVALID_AMOUNT", "Amount must be positive");
    }
}

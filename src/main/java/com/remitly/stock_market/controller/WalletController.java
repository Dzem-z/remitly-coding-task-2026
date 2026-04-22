package com.remitly.stock_market.controller;

import com.remitly.stock_market.model.WalletStockActionRequestDto;
import com.remitly.stock_market.exception.NoStockException;
import com.remitly.stock_market.model.WalletDto;
import com.remitly.stock_market.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/{wallet_id}/stocks/{stock_name}")
    public WalletDto handleStockAction(
            @PathVariable("wallet_id") String walletId,
            @PathVariable("stock_name") String stockName,
            @RequestBody WalletStockActionRequestDto request) {
        return walletService.applyAction(walletId, stockName, request.getType());
    }

    @GetMapping("/{wallet_id}")
    public WalletDto getWallet(@PathVariable("wallet_id") String walletId) {
        return walletService.getWallet(walletId);
    }

    @GetMapping("/{wallet_id}/stocks/{stock_name}")
    public int getStockQuantity(
            @PathVariable("wallet_id") String walletId,
            @PathVariable("stock_name") String stockName) {
        return walletService.getStockQuantity(walletId, stockName);
    }

    @ExceptionHandler(NoStockException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(NoStockException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }
}

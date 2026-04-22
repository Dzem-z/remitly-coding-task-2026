package com.remitly.stock_market.service;

import com.remitly.stock_market.model.ActionType;
import com.remitly.stock_market.model.StockDto;
import com.remitly.stock_market.model.WalletDto;
import com.remitly.stock_market.entity.StockEntity;
import com.remitly.stock_market.entity.WalletEntity;
import com.remitly.stock_market.repository.StockEntityRepository;
import com.remitly.stock_market.repository.WalletEntityRepository;
import com.remitly.stock_market.service.BankService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class WalletService {

    private final WalletEntityRepository walletEntityRepository;
    private final StockEntityRepository stockEntityRepository;
    private final BankService bankService;

    public WalletService(
            WalletEntityRepository walletEntityRepository,
            StockEntityRepository stockEntityRepository,
            BankService bankService
    ) {
        this.walletEntityRepository = walletEntityRepository;
        this.stockEntityRepository = stockEntityRepository;
        this.bankService = bankService;
    }

    @Transactional
    public WalletDto applyAction(String walletId, String stockName, ActionType actionType) {
        if (actionType == null) {
            throw new IllegalArgumentException("Action type is required");
        } else if (actionType == ActionType.BUY) {
            return bankService.sell(walletId, stockName);
        } else if (actionType == ActionType.SELL) {
            return bankService.buy(walletId, stockName);
        } else {
            throw new IllegalArgumentException("Unsupported action type: " + actionType);
        }
    }

    @Transactional(readOnly = true)
    public WalletDto getWallet(String walletId) {
        WalletEntity wallet = walletEntityRepository.findById(walletId)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found: " + walletId));
        return toWalletDto(wallet);
    }

    @Transactional(readOnly = true)
    public int getStockQuantity(String walletId, String stockName) {
        WalletEntity wallet = walletEntityRepository.findById(walletId)
            .orElseThrow(() -> new NoSuchElementException("Wallet not found: " + walletId));

        return stockEntityRepository.findByOwnerIdAndName(wallet.getId(), stockName)
            .map(StockEntity::getQuantity)
            .orElse(0);
    }

    private StockEntity findOrCreateStock(WalletEntity wallet, String stockName) {
        List<StockEntity> stocks = wallet.getStocks();
        for (StockEntity stock : stocks) {
            if (stock.getName().equals(stockName)) {
                return stock;
            }
        }

        StockEntity newStock = new StockEntity(stockName, 0);
        newStock.setOwner(wallet);
        stocks.add(newStock);
        return newStock;
    }

    private WalletDto toWalletDto(WalletEntity entity) {
        List<StockDto> stocks = entity.getStocks().stream()
                .map(stock -> new StockDto(stock.getName(), stock.getQuantity()))
                .toList();
        return new WalletDto(entity.getId(), stocks);
    }
}

package com.remitly.stock_market.service;

import com.remitly.stock_market.entity.BankEntity;
import com.remitly.stock_market.entity.StockEntity;
import com.remitly.stock_market.entity.WalletEntity;
import com.remitly.stock_market.model.ActionType;
import com.remitly.stock_market.model.StockDto;
import com.remitly.stock_market.model.WalletAuditLog;
import com.remitly.stock_market.model.WalletDto;
import com.remitly.stock_market.repository.BankEntityRepository;
import com.remitly.stock_market.repository.StockEntityRepository;
import com.remitly.stock_market.repository.WalletEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BankService {

    private final WalletEntityRepository walletEntityRepository;
    private final BankEntityRepository bankEntityRepository;
    private final StockEntityRepository stockEntityRepository;
    private final WalletAuditLog walletAuditLog;

    public BankService(
            WalletEntityRepository walletEntityRepository,
            BankEntityRepository bankEntityRepository,
            StockEntityRepository stockEntityRepository,
            WalletAuditLog walletAuditLog
    ) {
        this.walletEntityRepository = walletEntityRepository;
        this.bankEntityRepository = bankEntityRepository;
        this.stockEntityRepository = stockEntityRepository;
        this.walletAuditLog = walletAuditLog;
    }

    @Transactional
    public WalletDto sell(String walletId, String stockId) {
        BankEntity bank = getOrCreateBank();
        StockEntity bankStock = stockEntityRepository.findByOwnerIdAndName(bank.getId(), stockId)
                .orElseThrow(() -> new NoSuchElementException("Stock not found in bank: " + stockId));

        if (bankStock.getQuantity() <= 0) {
            throw new IllegalStateException("Stock quantity is 0 for: " + stockId);
        }

        WalletEntity wallet = walletEntityRepository.findById(walletId)
                .orElseGet(() -> walletEntityRepository.save(new WalletEntity(walletId)));

        StockEntity walletStock = findOrCreateWalletStock(wallet, stockId);
        bankStock.setQuantity(bankStock.getQuantity() - 1);
        walletStock.setQuantity(walletStock.getQuantity() + 1);

        stockEntityRepository.save(bankStock);
        if (bankStock.getQuantity() == 0) {
            bank.getStocks().remove(bankStock);
        }
        walletEntityRepository.save(wallet);
        walletAuditLog.addAction(ActionType.BUY, walletId, stockId);

        return toWalletDto(wallet);
    }

    @Transactional
    public WalletDto buy(String walletId, String stockId) {
        BankEntity bank = getOrCreateBank();
        WalletEntity wallet = walletEntityRepository.findById(walletId)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found: " + walletId));

        StockEntity walletStock = findWalletStock(wallet, stockId);
        if (walletStock.getQuantity() <= 0) {
            throw new IllegalStateException("User does not have stock anymore: " + stockId);
        }

        StockEntity bankStock = stockEntityRepository.findByOwnerIdAndName(bank.getId(), stockId)
            .orElseGet(() -> {
                StockEntity created = new StockEntity(stockId, 0);
                created.setOwner(bank);
                bank.getStocks().add(created);
                return stockEntityRepository.save(created);
            });

        walletStock.setQuantity(walletStock.getQuantity() - 1);
        bankStock.setQuantity(bankStock.getQuantity() + 1);

        if(walletStock.getQuantity() == 0) {
            wallet.getStocks().remove(walletStock);
        }

        stockEntityRepository.save(bankStock);
        walletEntityRepository.save(wallet);
        walletAuditLog.addAction(ActionType.SELL, walletId, stockId);

        return toWalletDto(wallet);
    }

    @Transactional(readOnly = true)
    public List<StockDto> getAllStocks() {
        BankEntity bank = getOrCreateBank();
        return stockEntityRepository.findAllByOwnerId(bank.getId()).stream()
                .map(stock -> new StockDto(stock.getName(), stock.getQuantity()))
                .toList();
    }

    @Transactional
    public List<StockDto> replaceStocks(List<StockDto> stocks) {
        BankEntity bank = getOrCreateBank();
        List<StockDto> requestedStocks = stocks == null ? List.of() : stocks;

        for (StockDto stock : requestedStocks) {
            if (stock == null || stock.getName() == null || stock.getName().isBlank()) {
                throw new IllegalArgumentException("Stock name is required");
            }
            if (stock.getQuantity() < 0) {
                throw new IllegalArgumentException("Stock quantity cannot be negative");
            }
        }

        List<StockEntity> bankStocks = stockEntityRepository.findAllByOwnerId(bank.getId());
        stockEntityRepository.deleteAll(bankStocks);

        for (StockDto stock : requestedStocks) {
            StockEntity newBankStock = new StockEntity(stock.getName(), stock.getQuantity());
            newBankStock.setOwner(bank);
            stockEntityRepository.save(newBankStock);
        }

        return getAllStocks();
    }

    @Transactional(readOnly = true)
    public StockDto getStockById(String stockId) {
        BankEntity bank = getOrCreateBank();
        StockEntity stock = stockEntityRepository.findByOwnerIdAndName(bank.getId(), stockId)
                .orElseThrow(() -> new NoSuchElementException("Stock not found in bank: " + stockId));
        return new StockDto(stock.getName(), stock.getQuantity());
    }

    private BankEntity getOrCreateBank() {
        return bankEntityRepository.findById(BankEntity.BANK_ID)
                .orElseGet(() -> bankEntityRepository.save(new BankEntity()));
    }

    private StockEntity findOrCreateWalletStock(WalletEntity wallet, String stockId) {
        for (StockEntity stock : wallet.getStocks()) {
            if (stock.getName().equals(stockId)) {
                return stock;
            }
        }

        StockEntity created = new StockEntity(stockId, 0);
        created.setOwner(wallet);
        wallet.getStocks().add(created);
        return created;
    }

    private StockEntity findWalletStock(WalletEntity wallet, String stockId) {
        return wallet.getStocks().stream()
                .filter(stock -> stock.getName().equals(stockId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Stock not found in wallet: " + stockId));
    }

    private WalletDto toWalletDto(WalletEntity entity) {
        List<StockDto> stocks = entity.getStocks().stream()
                .map(stock -> new StockDto(stock.getName(), stock.getQuantity()))
                .toList();
        return new WalletDto(entity.getId(), stocks);
    }
}

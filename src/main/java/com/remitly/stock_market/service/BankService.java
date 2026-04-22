package com.remitly.stock_market.service;

import com.remitly.stock_market.entity.StockEntity;
import com.remitly.stock_market.entity.WalletEntity;
import com.remitly.stock_market.model.ActionType;
import com.remitly.stock_market.model.StockDto;
import com.remitly.stock_market.model.WalletAuditLog;
import com.remitly.stock_market.model.WalletDto;
import com.remitly.stock_market.repository.StockEntityRepository;
import com.remitly.stock_market.repository.WalletEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class BankService {

	private final WalletEntityRepository walletEntityRepository;
	private final StockEntityRepository stockEntityRepository;
	private final WalletAuditLog walletAuditLog;

	public BankService(
			WalletEntityRepository walletEntityRepository,
			StockEntityRepository stockEntityRepository,
			WalletAuditLog walletAuditLog
	) {
		this.walletEntityRepository = walletEntityRepository;
		this.stockEntityRepository = stockEntityRepository;
		this.walletAuditLog = walletAuditLog;
	}

	@Transactional
	public WalletDto sell(String walletId, String stockId) {
		StockEntity bankStock = stockEntityRepository.findByWalletIsNullAndName(stockId).orElse(null);
		if (bankStock == null) {
			throw new NoSuchElementException("Stock not found in bank: " + stockId);
		}
		if (bankStock.getQuantity() <= 0) {
			throw new IllegalStateException("Stock quantity is 0 for: " + stockId);
		}

		WalletEntity wallet = walletEntityRepository.findById(walletId)
				.orElseGet(() -> walletEntityRepository.save(new WalletEntity(walletId, new ArrayList<>())));
		StockEntity walletStock = findWalletStock(wallet, stockId);

		bankStock.setQuantity(bankStock.getQuantity() - 1);
		walletStock.setQuantity(walletStock.getQuantity() + 1);
		stockEntityRepository.save(bankStock);
		walletEntityRepository.save(wallet);
		walletAuditLog.addAction(ActionType.BUY, walletId, stockId);

		return toWalletDto(wallet);
	}

	@Transactional
	public WalletDto buy(String walletId, String stockId) {
		WalletEntity wallet = walletEntityRepository
            .findById(walletId)
            .orElseThrow(() -> new NoSuchElementException("Wallet not found: " + walletId));

		StockEntity walletStock = findWalletStock(wallet, stockId);
		if (walletStock.getQuantity() <= 0) {
			throw new IllegalStateException("User does not have stock anymore: " + stockId);
		}

		StockEntity bankStock = stockEntityRepository.findByWalletIsNullAndName(stockId)
				.orElseThrow(() -> new NoSuchElementException("Stock not found in bank: " + stockId));

		walletStock.setQuantity(walletStock.getQuantity() - 1);
		bankStock.setQuantity(bankStock.getQuantity() + 1);
		stockEntityRepository.save(bankStock);
		walletEntityRepository.save(wallet);
		walletAuditLog.addAction(ActionType.SELL, walletId, stockId);

		return toWalletDto(wallet);
	}

	public List<StockDto> getAllStocks() {
		return stockEntityRepository.findAllByWalletIsNull().stream()
				.map(stock -> new StockDto(stock.getName(), stock.getQuantity()))
				.toList();
	}

	@Transactional
	public List<StockDto> replaceStocks(List<StockDto> stocks) {
		List<StockDto> requestedStocks = stocks == null ? List.of() : stocks;
		Map<String, Integer> requestedByName = new HashMap<>();

		for (StockDto stock : requestedStocks) {
			if (stock == null || stock.getName() == null || stock.getName().isBlank()) {
				throw new IllegalArgumentException("Stock name is required");
			}
			if (stock.getQuantity() < 0) {
				throw new IllegalArgumentException("Stock quantity cannot be negative");
			}
			requestedByName.put(stock.getName(), stock.getQuantity());
		}

		List<StockEntity> bankStocks = stockEntityRepository.findAllByWalletIsNull();
		for (StockEntity bankStock : bankStocks) {
			Integer quantity = requestedByName.remove(bankStock.getName());
			if (quantity == null) {
				stockEntityRepository.delete(bankStock);
				continue;
			}
			bankStock.setQuantity(quantity);
			stockEntityRepository.save(bankStock);
		}

		for (Map.Entry<String, Integer> entry : requestedByName.entrySet()) {
			stockEntityRepository.save(new StockEntity(entry.getKey(), entry.getValue()));
		}

		return getAllStocks();
	}

	public StockDto getStockById(String stockId) {
		StockEntity stock = stockEntityRepository.findByWalletIsNullAndName(stockId).orElse(null);
		if (stock == null) {
			throw new NoSuchElementException("Stock not found in bank: " + stockId);
		}
		return new StockDto(stock.getName(), stock.getQuantity());
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

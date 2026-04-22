package com.remitly.stock_market.model;

import com.remitly.stock_market.entity.WalletActionEntity;
import com.remitly.stock_market.repository.WalletActionEntityRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
public class WalletAuditLog {

	private final WalletActionEntityRepository walletActionEntityRepository;

	public WalletAuditLog(WalletActionEntityRepository walletActionEntityRepository) {
		this.walletActionEntityRepository = walletActionEntityRepository;
	}

	public synchronized void addAction(ActionType type, String walletId, String stockName) {
		Objects.requireNonNull(type, "type cannot be null");
		Objects.requireNonNull(walletId, "walletId cannot be null");
		Objects.requireNonNull(stockName, "stockName cannot be null");
		walletActionEntityRepository.save(new WalletActionEntity(
				type,
				walletId,
				stockName,
				LocalDateTime.now()
		));
	}

	public synchronized List<WalletActionViewDto> getActions() {
		return walletActionEntityRepository.findAll().stream()
				.map(entity -> new WalletActionViewDto(entity.getType(), entity.getWalletId(), entity.getStockName()))
				.toList();
	}
}

package com.remitly.stock_market.model;

import java.io.Serial;
import java.io.Serializable;

public class WalletActionViewDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private ActionType type;
	private String walletId;
	private String stockName;

	public WalletActionViewDto() {
	}

	public WalletActionViewDto(ActionType type, String walletId, String stockName) {
		this.type = type;
		this.walletId = walletId;
		this.stockName = stockName;
	}

	public ActionType getType() {
		return type;
	}

	public void setType(ActionType type) {
		this.type = type;
	}

	public String getWalletId() {
		return walletId;
	}

	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
}

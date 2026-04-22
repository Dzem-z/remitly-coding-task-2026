package com.remitly.stock_market.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WalletDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private String id;
	private List<StockDto> stocks;

	public WalletDto() {
		this.stocks = new ArrayList<>();
	}

	public WalletDto(String id, List<StockDto> stocks) {
		this.id = id;
		this.stocks = stocks;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<StockDto> getStocks() {
		return stocks;
	}

	public void setStocks(List<StockDto> stocks) {
		this.stocks = stocks;
	}
}
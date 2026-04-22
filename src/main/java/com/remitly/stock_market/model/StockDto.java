package com.remitly.stock_market.model;

import java.io.Serial;
import java.io.Serializable;

public class StockDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private String name;
	private int quantity;

	public StockDto() {
	}

	public StockDto(String name, int quantity) {
		this.name = name;
		this.quantity = quantity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
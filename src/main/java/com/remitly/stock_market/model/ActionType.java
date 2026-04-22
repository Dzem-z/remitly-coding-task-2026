package com.remitly.stock_market.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActionType {
	BUY,
	SELL;

	@JsonCreator
	public static ActionType fromValue(String value) {
		if (value == null) {
			throw new IllegalArgumentException("type cannot be null");
		}
		return ActionType.valueOf(value.trim().toUpperCase());
	}

	@JsonValue
	public String toValue() {
		return name().toLowerCase();
	}
}

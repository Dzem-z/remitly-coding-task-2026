package com.remitly.stock_market.exception;

public class NoStockException extends IllegalArgumentException {

    public NoStockException(String message) {
        super(message);
    }
}

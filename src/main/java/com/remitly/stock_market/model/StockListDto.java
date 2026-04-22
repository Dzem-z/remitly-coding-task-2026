package com.remitly.stock_market.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StockListDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<StockDto> stocks;

    public StockListDto() {
        this.stocks = new ArrayList<>();
    }

    public StockListDto(List<StockDto> stocks) {
        this.stocks = stocks;
    }

    public List<StockDto> getStocks() {
        return stocks;
    }

    public void setStocks(List<StockDto> stocks) {
        this.stocks = stocks;
    }
}

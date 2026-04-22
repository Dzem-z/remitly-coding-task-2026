package com.remitly.stock_market.model;

import java.io.Serial;
import java.io.Serializable;

public class WalletStockActionRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ActionType type;

    public WalletStockActionRequestDto() {
    }

    public WalletStockActionRequestDto(ActionType type) {
        this.type = type;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }
}

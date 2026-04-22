package com.remitly.stock_market.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BANK")
public class BankEntity extends StockOwnerEntity {

    public static final String BANK_ID = "BANK";

    public BankEntity() {
        setId(BANK_ID);
    }
}

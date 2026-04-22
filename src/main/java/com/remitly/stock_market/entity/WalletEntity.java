package com.remitly.stock_market.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("WALLET")
public class WalletEntity extends StockOwnerEntity {

    public WalletEntity() {
    }

    public WalletEntity(String id) {
        setId(id);
    }
}

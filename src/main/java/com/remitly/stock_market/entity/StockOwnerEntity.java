package com.remitly.stock_market.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock_owners")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "owner_type", discriminatorType = DiscriminatorType.STRING)
public abstract class StockOwnerEntity {

    @Id
    private String id;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<StockEntity> stocks = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<StockEntity> getStocks() {
        return stocks;
    }

    public void setStocks(List<StockEntity> stocks) {
        this.stocks = stocks;
    }
}

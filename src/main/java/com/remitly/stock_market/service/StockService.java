package com.remitly.stock_market.service;

import com.remitly.stock_market.model.StockDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StockService {

    private final Map<Long, StockDto> stocks = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    public List<StockDto> findAll() {
        return new ArrayList<>(stocks.values());
    }

    public Optional<StockDto> findById(Long id) {
        return Optional.ofNullable(stocks.get(id));
    }

    public StockDto create(StockDto stock) {
        long id = idSequence.getAndIncrement();
        StockDto created = new StockDto(stock.getName(), stock.getQuantity());
        stocks.put(id, created);
        return created;
    }

    public Optional<StockDto> update(Long id, StockDto stock) {
        if (!stocks.containsKey(id)) {
            return Optional.empty();
        }

        StockDto updated = new StockDto(stock.getName(), stock.getQuantity());
        stocks.put(id, updated);
        return Optional.of(updated);
    }

    public boolean delete(Long id) {
        return stocks.remove(id) != null;
    }
}

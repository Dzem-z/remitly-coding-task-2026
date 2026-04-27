package com.remitly.stock_market.service;


import com.remitly.stock_market.model.LogListDto;
import com.remitly.stock_market.model.WalletActionViewDto;
import com.remitly.stock_market.entity.WalletActionEntity;
import com.remitly.stock_market.repository.WalletActionEntityRepository;

import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class LogService {
    private final WalletActionEntityRepository logRepository;

    public LogService(WalletActionEntityRepository logRepository) {
        this.logRepository = logRepository;
    }

    public LogListDto getAllLogs() {
        return logRepository.findAllOrderedByActionDateTime()
                .stream()
                .map(this::toDto)
                .collect(Collectors.collectingAndThen(Collectors.toList(), LogListDto::new));
    }

    private WalletActionViewDto toDto(WalletActionEntity entity) {
        return new WalletActionViewDto(entity.getType(), entity.getWalletId(), entity.getStockName());
    }
}

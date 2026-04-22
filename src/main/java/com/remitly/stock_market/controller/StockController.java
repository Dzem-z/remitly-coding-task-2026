package com.remitly.stock_market.controller;

import com.remitly.stock_market.model.StockListDto;
import com.remitly.stock_market.service.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final BankService bankService;

    public StockController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping
    public StockListDto getStocks() {
        return new StockListDto(bankService.getAllStocks());
    }

    @PostMapping
    public ResponseEntity<StockListDto> updateStocks(@RequestBody StockListDto request) {
        return ResponseEntity.ok(new StockListDto(bankService.replaceStocks(request.getStocks())));
    }
}

package com.remitly.stock_market.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChaosController {
    @GetMapping("/chaos")
    public void chaos() {
        Runtime.getRuntime().halt(0);
    }
}

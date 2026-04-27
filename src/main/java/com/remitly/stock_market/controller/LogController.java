package com.remitly.stock_market.controller;

import com.remitly.stock_market.model.LogListDto;
import com.remitly.stock_market.service.LogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
public class LogController {
	private final LogService logService;

	public LogController(LogService logService) {
		this.logService = logService;
	}

	@GetMapping
	public LogListDto getAllLogs() {
		return logService.getAllLogs();
	}
}

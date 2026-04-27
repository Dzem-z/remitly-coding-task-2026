package com.remitly.stock_market.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LogListDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<WalletActionViewDto> logs;

    public LogListDto() {
        this.logs = new ArrayList<>();
    }

    public LogListDto(List<WalletActionViewDto> logs) {
        this.logs = logs;
    }

    public List<WalletActionViewDto> getLogs() {
        return logs;
    }

    public void setLogs(List<WalletActionViewDto> logs) {
        this.logs = logs;
    }
}

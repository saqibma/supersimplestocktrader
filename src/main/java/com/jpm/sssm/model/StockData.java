package com.jpm.sssm.model;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * Created by adnan_saqib on 10/12/2016.
 */
public class StockData {
    private String stockSymbol;
    private StockType stockType;
    private OptionalInt lastDividend;
    private OptionalDouble fixedDividend;
    private OptionalInt parValue;

    public StockData(String stockSymbol, StockType stockType, OptionalInt lastDividend, OptionalDouble  fixedDividend, OptionalInt parValue) {
        this.stockSymbol = stockSymbol;
        this.stockType = stockType;
        this.lastDividend = lastDividend;
        this.fixedDividend = fixedDividend;
        this.parValue = parValue;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public StockType getStockType() {
        return stockType;
    }

    public OptionalInt getLastDividend() {
        return lastDividend;
    }

    public OptionalDouble getFixedDividend() {
        return fixedDividend;
    }

    public OptionalInt getParValue() {
        return parValue;
    }

    @Override
    public String toString() {
        return "StockData{" +
                "stockSymbol='" + stockSymbol + '\'' +
                ", stockType='" + stockType + '\'' +
                ", lastDividend=" + lastDividend.orElse(0) +
                ", fixedDividend=" + fixedDividend.orElse(0) +
                ", parValue=" + parValue.orElse(0) +
                '}';
    }
}

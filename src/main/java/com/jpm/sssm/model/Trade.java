package com.jpm.sssm.model;

import com.jpm.sssm.util.IdGeneratorUtil;

import java.time.LocalDateTime;
import java.util.OptionalDouble;
import java.util.OptionalLong;

/**
 * Created by adnan_saqib on 10/12/2016.
 */
public class Trade {

    private long tradeId;
    private OptionalDouble tradePrice;
    private String stocksymbol;
    private int quantityOfShares;
    private TradeType tradeType;
    private LocalDateTime timeStamp;

    public Trade(String stocksymbol, OptionalDouble tradePrice, int quantityOfShares, TradeType tradeType, LocalDateTime timeStamp) {
        this.tradeId = IdGeneratorUtil.generateUniqueId();
        this.stocksymbol = stocksymbol;
        this.tradePrice = tradePrice;
        this.quantityOfShares = quantityOfShares;
        this.tradeType = tradeType;
        this.timeStamp = timeStamp;
    }

    public long getTradeId() {
        return tradeId;
    }

    public OptionalDouble getTradePrice() {
        return tradePrice;
    }

    public String getStocksymbol() {
        return stocksymbol;
    }

    public int getQuantityOfShares() {
        return quantityOfShares;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "tradeId=" + tradeId +
                ", tradePrice=" + tradePrice.orElse(0.0) +
                ", stocksymbol=" + stocksymbol +
                ", quantityOfShares=" + quantityOfShares +
                ", tradeType=" + tradeType +
                ", timeStamp=" + timeStamp +
                '}';
    }
}

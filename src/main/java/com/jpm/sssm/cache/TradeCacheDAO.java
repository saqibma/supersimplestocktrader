package com.jpm.sssm.cache;

import com.jpm.sssm.model.Trade;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by adnan_saqib on 10/12/2016.
 */
public interface TradeCacheDAO<Trade> extends CacheDAO<Trade> {

    public List<Trade> getAllTrades(String stockSymbol, LocalDateTime currentTime, int durationInMins);

    public Trade get(String stockSymbol, Long tradeId);

    public List<Trade> getAllTrades();

    public List<Trade> getAllTrades(String stockSymbol);

    public void flushTrades();
}

package com.jpm.sssm.cache;

import com.jpm.sssm.model.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by adnan_saqib on 10/12/2016.
 */
@Repository
public class TradeCacheDAOImpl implements TradeCacheDAO<Trade> {

    final Logger LOG = LoggerFactory.getLogger(TradeCacheDAOImpl.class);

    ConcurrentMap<String, ConcurrentMap<Long, Trade>> tradesForAllStocksMap = new ConcurrentHashMap<>();

    @Override
    public void saveAll(List<Trade> trades) {
        trades.forEach(trade -> {
            save(trade);
        });
    }

    @Override
    public synchronized void save(Trade trade) {
        // Synchronized to prevent multiple threads to create multiple stock map for trades belong to the same stock simultaneously
        LOG.debug(String.format("Saving trade in cache : %s",
                trade));
        ConcurrentMap<Long, Trade> tradesForOneStockMap = tradesForAllStocksMap.get(trade.getStocksymbol());
        if (tradesForOneStockMap == null) {
            ConcurrentMap<Long, Trade> tradeMap = new ConcurrentHashMap<>();
            tradeMap.putIfAbsent(trade.getTradeId(), trade);
            tradesForAllStocksMap.putIfAbsent(trade.getStocksymbol(), tradeMap);
        } else {
            tradesForOneStockMap.putIfAbsent(trade.getTradeId(), trade);
        }
    }

    @Override
    public Trade get(String id) {
        throw new UnsupportedOperationException("Implementation of get(tradeId) is not required for the given use case");
    }

    @Override
    public Trade get(String stockSymbol, Long tradeId) {
        return tradesForAllStocksMap.get(stockSymbol).get(tradeId);
    }

    @Override
    public List<Trade> getAllTrades(String stockSymbol, LocalDateTime currentTime, int durationInMins) {
        List<Trade> trades = new ArrayList<>();
        tradesForAllStocksMap.get(stockSymbol).forEach((tradeId, trade) -> {
            // filter trades which arrive in last 15 mins
            if (ChronoUnit.MINUTES.between(trade.getTimeStamp(), currentTime) < durationInMins) {
                trades.add(trade);
            }
        });
        LOG.debug(String.format("Get all the trades for stock %s which arrive in last %s",
                stockSymbol, durationInMins));
        return trades;
    }

    @Override
    public List<Trade> getAllTrades(String stockSymbol) {
        List<Trade> trades = new ArrayList<>();
        tradesForAllStocksMap.get(stockSymbol).forEach((tradeId, trade) -> {
            trades.add(trade);
        });
        return trades;
    }

    @Override
    public List<Trade> getAllTrades() {
        List<Trade> trades = new ArrayList<>();
        tradesForAllStocksMap.forEach((stockSymbol, tradeMap) -> trades.addAll(getAllTrades(stockSymbol)));
        return trades;
    }

    @Override
    public void flushTrades() {
        LOG.debug("Flushing all the trades");
        tradesForAllStocksMap = new ConcurrentHashMap<>();
    }
}

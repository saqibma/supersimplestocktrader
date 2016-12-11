package com.jpm.sssm.cache;

import com.jpm.sssm.model.StockData;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Created by adnan_saqib on 10/12/2016.
 */
@Repository
public class StockCacheDAOImpl implements StockCacheDAO<StockData> {

    ConcurrentMap<String,StockData> stockMap = new ConcurrentHashMap<>();

    @Override
    public void saveAll(List<StockData> stocks) {
        stocks.forEach(stock -> stockMap.putIfAbsent(stock.getStockSymbol(),stock));
    }

    @Override
    public void save(StockData stock) {
        stockMap.putIfAbsent(stock.getStockSymbol(),stock);
    }

    @Override
    public StockData get(String stockSymbol) {
        return stockMap.get(stockSymbol);
    }
}

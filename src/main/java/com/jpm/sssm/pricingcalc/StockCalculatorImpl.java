package com.jpm.sssm.pricingcalc;

import com.jpm.sssm.cache.StockCacheDAO;
import com.jpm.sssm.cache.TradeCacheDAO;
import com.jpm.sssm.exception.InvalidStockException;
import com.jpm.sssm.exception.InvalidTradeException;
import com.jpm.sssm.model.StockData;
import com.jpm.sssm.model.StockType;
import com.jpm.sssm.model.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by adnan_saqib on 11/12/2016.
 */
@Service
@ComponentScan(basePackages = "com.jpm.sssm.cache")
public class StockCalculatorImpl implements StockCalculator {

    final Logger LOG = LoggerFactory.getLogger(StockCalculatorImpl.class);

    @Autowired
    TradeCacheDAO<Trade> tradeCacheDAO;

    @Autowired
    StockCacheDAO<StockData> stockCacheDAO;

    @Override
    public double calculateDividendYield(Trade trade) throws InvalidStockException, InvalidTradeException {
        StockData stock = stockCacheDAO.get(trade.getStocksymbol());
        LOG.info(String.format("Calculating dividend yield of stock %s",
                stock));
        if (stock.getStockType() == StockType.COMMON) { // Calculate dividend when stock type is Common
            return stock.getLastDividend()
                    .orElseThrow(InvalidStockException::new)
                    / trade.getTradePrice().orElseThrow(InvalidTradeException::new);
        } else { // Calculate dividend when stock type is Preferred
            return (stock.getFixedDividend().orElseThrow(InvalidStockException::new)
                    * stock.getParValue().orElseThrow(InvalidStockException::new))
                    / trade.getTradePrice().orElseThrow(InvalidTradeException::new);
        }
    }

    @Override
    public double calculatePriceEarningsRatio(Trade trade) throws InvalidStockException, InvalidTradeException {
        StockData stock = stockCacheDAO.get(trade.getStocksymbol());
        LOG.info(String.format("Calculating price earnings ratio of stock %s",
                stock));
        int dividened = stock.getLastDividend()
                .orElseThrow(InvalidStockException::new);

        if (dividened <= 0) new InvalidStockException(stock.getStockSymbol(), "Dividened must be positive");

        return trade.getTradePrice().orElseThrow(InvalidTradeException::new)
                / dividened;
    }

    @Override
    public Trade recordTrade(Trade trade) {
        LOG.info(String.format("Trade recorded is  %s",
                trade));
        tradeCacheDAO.save(trade);
        return tradeCacheDAO.get(trade.getStocksymbol(), trade.getTradeId());
    }

    @Override
    public double calculateVolumeWeightedStock(StockData stock, int durationInMins) {
        LOG.info(String.format("Calculating volume weighted of stock  %s for duration %s",
                stock, durationInMins));
        List<Trade> trades = tradeCacheDAO.getAllTrades(stock.getStockSymbol(), LocalDateTime.now(), durationInMins);
        trades.forEach(trade -> LOG.info(String.format("Trades participating in the calculation of volume weighted of %s : %s",
                stock.getStockSymbol(), trade)));
        double totalStockPrice = trades.stream().map(trade -> {
            double stockPrice = 0.0; //Invalid trades will be ignored if exception occurs
            try {
                stockPrice = trade.getTradePrice().orElseThrow(InvalidTradeException::new) * trade.getQuantityOfShares();
            } catch (InvalidTradeException e) {
                LOG.error(String.format("Invalid trade price for %s", trade),
                        e);
            }
            return stockPrice; //ignoring trades with invalid price while calculating total stock price
        }).reduce(0.0, (x, y) -> x + y); // calculating total stock price

        int totalShares = trades.stream()
                .map(trade -> trade.getTradePrice().isPresent() ?
                        trade.getQuantityOfShares() : 0 //ignoring trades with invalid price while calculating total shares
                )
                .reduce(0, (x, y) -> x + y); // Calculating total number of shares for the given stock

        return (totalStockPrice / totalShares); // calculating volume weighted stock
    }

    @Override
    public double calculateGBCEAllShareIndex() {
        List<Trade> trades = tradeCacheDAO.getAllTrades();
        trades.forEach(trade -> LOG.info(String.format("Calculating GBC All Share Index of : %s",
                trade)));
        double multipliedPrices = trades.stream()
                .map(trade -> {
                            double tradePrice = 0.0;
                            try {
                                tradePrice = trade.getTradePrice().orElseThrow(InvalidTradeException::new);
                            } catch (InvalidTradeException e) {
                                LOG.error(String.format("Invalid trade price for %s", trade),
                                        e);
                            }
                            return tradePrice; //ignoring invalid trades while multiplying all prices
                        }
                ).reduce(1.0, (x, y) -> x * y); // Multiplying all prices to calculate geometric mean

        long tradeCount = trades.stream()
                .map(trade -> trade.getTradePrice())
                .filter(price -> price.isPresent()) //including trades with valid trade price while calculating trade count
                .count();

        return Math.pow(multipliedPrices, 1.0 / tradeCount); //calculating geometric mean of prices for all stocks
    }
}

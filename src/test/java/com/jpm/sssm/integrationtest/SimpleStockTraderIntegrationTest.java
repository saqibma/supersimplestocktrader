package com.jpm.sssm.integrationtest;

import com.jpm.sssm.cache.StockCacheDAOImpl;
import com.jpm.sssm.cache.TradeCacheDAO;
import com.jpm.sssm.exception.InvalidStockException;
import com.jpm.sssm.exception.InvalidTradeException;
import com.jpm.sssm.feeder.StockFeederImpl;
import com.jpm.sssm.loader.StockLoaderImpl;
import com.jpm.sssm.model.StockData;
import com.jpm.sssm.model.StockType;
import com.jpm.sssm.model.Trade;
import com.jpm.sssm.model.TradeType;
import com.jpm.sssm.pricingcalc.StockCalculator;
import com.jpm.sssm.pricingcalc.StockCalculatorImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.OptionalDouble;
import java.util.OptionalInt;


/**
 * Created by adnan_saqib on 11/12/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {StockFeederImpl.class, StockLoaderImpl.class, StockCacheDAOImpl.class,
        StockCalculatorImpl.class
})
public class SimpleStockTraderIntegrationTest {

    final Logger LOG = LoggerFactory.getLogger(SimpleStockTraderIntegrationTest.class);

    @Autowired
    StockCalculator stockCalculator;

    @Autowired
    StockFeederImpl stockFeeder;

    @Autowired
    TradeCacheDAO<Trade> tradeCacheDAO;

    @Before
    public void loadStocks() {
        stockFeeder.pouplateStockDataToCache();
    }

    @Test
    public void testCalculateDividendYieldWhenStockIsCommon() throws InvalidTradeException, InvalidStockException {
        Trade trade = new Trade("ALE", OptionalDouble.of(50.0),10, TradeType.SELL, LocalDateTime.now());
        double dividenedYield = stockCalculator.calculateDividendYield(trade);
        LOG.info("-------------------------------------------------------------------------------");
        LOG.info(String.format("[ Calculated dividend yield for Common stock \"%s\" is : %s/%s = %s ]",
                trade.getStocksymbol(), 23,trade.getTradePrice().getAsDouble(), dividenedYield));
        LOG.info("-------------------------------------------------------------------------------");
        Assert.assertEquals(dividenedYield , 0.46 , 0.0);
    }

    @Test
    public void testCalculateDividendYieldWhenStockIsPreferred() throws InvalidTradeException, InvalidStockException {
        Trade trade = new Trade("GIN", OptionalDouble.of(50.0),10, TradeType.SELL, LocalDateTime.now());
        double dividenedYield = stockCalculator.calculateDividendYield(trade);
        LOG.info("-------------------------------------------------------------------------------");
        LOG.info(String.format("[ Calculated dividend yield for Preferred stock \"%s\" is : (%s*%s)/%s = %s ]",
                trade.getStocksymbol(), "2%",100, trade.getTradePrice().getAsDouble(), dividenedYield));
        LOG.info("-------------------------------------------------------------------------------");
        Assert.assertEquals(dividenedYield , 0.04 , 0.0);
    }

    @Test
    public void testCalculatePriceEarningsRatio() throws InvalidTradeException, InvalidStockException {
        Trade trade = new Trade("ALE", OptionalDouble.of(50.0),10, TradeType.SELL, LocalDateTime.now());
        double priceEarningsRatio = stockCalculator.calculatePriceEarningsRatio(trade);
        LOG.info("-------------------------------------------------------------------------------");
        LOG.info(String.format("[ Calculated price earnings ratio for stock \"%s\" is : %s/%s = %s ]",
                trade.getStocksymbol(), trade.getTradePrice().getAsDouble(),23, priceEarningsRatio));
        LOG.info("-------------------------------------------------------------------------------");
        Assert.assertEquals(priceEarningsRatio , 2.17 , 0.01);
    }

    @Test
    public void testRecordTrade() throws InvalidTradeException, InvalidStockException {
        Trade trade = new Trade("ALE", OptionalDouble.of(50.0),10, TradeType.SELL, LocalDateTime.now());
        Trade recordedTrade = stockCalculator.recordTrade(trade);
        Assert.assertNotNull(recordedTrade);
        LOG.info("-------------------------------------------------------------------------------");
        LOG.info(String.format("[ Trade recorded is %s ]",
                recordedTrade));
        LOG.info("-------------------------------------------------------------------------------");
    }

    @Test
    public void testCalculateVolumeWeightedStock() throws InvalidTradeException, InvalidStockException {
        tradeCacheDAO.flushTrades();
        Trade trade1 = new Trade("ALE", OptionalDouble.of(50.0), 10,
                TradeType.SELL, LocalDateTime.now().minusMinutes(20L)); //First trade of stock ALE arrives 20 mins before now
        stockCalculator.recordTrade(trade1);

        Trade trade2 = new Trade("GIN", OptionalDouble.of(50.0), 10,
                TradeType.SELL, LocalDateTime.now().minusMinutes(10L));//First trade of stock GIN arrives 10 mins before now
        stockCalculator.recordTrade(trade2);

        Trade trade3 = new Trade("ALE", OptionalDouble.of(40.0), 10,
                TradeType.BUY, LocalDateTime.now().minusMinutes(10L));//Second trade of stock ALE arrives 10 mins before now
        stockCalculator.recordTrade(trade3);

        Trade trade4 = new Trade("ALE", OptionalDouble.of(30.0), 20,
                TradeType.BUY, LocalDateTime.now());//Third trade of stock ALE arrives  now
        stockCalculator.recordTrade(trade4);

        StockData stock = new StockData("ALE", StockType.COMMON, OptionalInt.of(23),
                OptionalDouble.of(0.0), OptionalInt.of(60));

        double volumeWeightedStock = stockCalculator.calculateVolumeWeightedStock(stock, 15); // Calculating volume weighted for stock ALE

        LOG.info("-------------------------------------------------------------------------------");
        LOG.info(String.format("[ Volume weighted for stock \"%s\" is : %s ]",
                stock.getStockSymbol(), volumeWeightedStock));
        LOG.info("-------------------------------------------------------------------------------");
        Assert.assertEquals(volumeWeightedStock , 33.33 , 0.01);
    }

    @Test
    public void testCalculateGBCEAllShareIndexForAllStocks() {
        tradeCacheDAO.flushTrades();
        Trade trade1 = new Trade("ALE", OptionalDouble.of(50.0), 10,
                TradeType.SELL, LocalDateTime.now().minusMinutes(20L)); //First trade of stock ALE arrives 20 mins before now
        stockCalculator.recordTrade(trade1); // Storing the trade in trade cache

        Trade trade2 = new Trade("GIN", OptionalDouble.of(50.0), 10,
                TradeType.SELL, LocalDateTime.now().minusMinutes(10L));//First trade of stock GIN arrives 10 mins before now
        stockCalculator.recordTrade(trade2); // Storing the trade in trade cache

        Trade trade3 = new Trade("ALE", OptionalDouble.of(40.0), 10,
                TradeType.BUY, LocalDateTime.now().minusMinutes(10L));//Second trade of stock ALE arrives 10 mins before now
        stockCalculator.recordTrade(trade3); // Storing the trade in trade cache

        Trade trade4 = new Trade("ALE", OptionalDouble.of(30.0), 20,
                TradeType.BUY, LocalDateTime.now());//Third trade of stock ALE arrives now
        stockCalculator.recordTrade(trade4); // Storing the trade in trade cache

        double gbcAllShareIndex = stockCalculator.calculateGBCEAllShareIndex(); // Calculating volume weighted for stock ALE

        LOG.info("-------------------------------------------------------------------------------");
        LOG.info(String.format("[ GBC All Share Index for all stocks : %s ]", gbcAllShareIndex));
        LOG.info("-------------------------------------------------------------------------------");
        Assert.assertEquals(gbcAllShareIndex , 41.61 , 0.01);
    }
}

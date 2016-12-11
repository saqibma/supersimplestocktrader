package com.jpm.sssm.pricingcalc;

import com.jpm.sssm.exception.InvalidStockException;
import com.jpm.sssm.exception.InvalidTradeException;
import com.jpm.sssm.model.StockData;
import com.jpm.sssm.model.Trade;

/**
 * Created by adnan_saqib on 11/12/2016.
 */
public interface StockCalculator {

    public double calculateDividendYield(Trade trade) throws InvalidStockException, InvalidTradeException;

    public double calculatePriceEarningsRatio(Trade trade) throws InvalidStockException, InvalidTradeException;

    public Trade recordTrade(Trade trade);

    public double calculateVolumeWeightedStock(StockData stock, int durationInMins);

    public double calculateGBCEAllShareIndex();
}

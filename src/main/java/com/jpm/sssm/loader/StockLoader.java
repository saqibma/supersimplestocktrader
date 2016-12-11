package com.jpm.sssm.loader;

import com.jpm.sssm.model.StockData;

import java.util.List;

/**
 * Created by adnan_saqib on 10/12/2016.
 */
public interface StockLoader {

    public List<StockData> loadStocks();

}

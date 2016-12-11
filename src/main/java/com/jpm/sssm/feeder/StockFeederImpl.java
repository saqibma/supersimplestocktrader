package com.jpm.sssm.feeder;

import com.jpm.sssm.cache.StockCacheDAO;
import com.jpm.sssm.loader.StockLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

/**
 * Created by adnan_saqib on 10/12/2016.
 */

@Service
@ComponentScan(basePackages = "com.jpm.sssm")
public class StockFeederImpl implements StockFeeder {

    @Autowired
    private StockLoader stockLoader;

    @Autowired
    private StockCacheDAO stockCacheDAO;

    @Override
    public void pouplateStockDataToCache(){
        stockCacheDAO.saveAll(stockLoader.loadStocks());
    }

}

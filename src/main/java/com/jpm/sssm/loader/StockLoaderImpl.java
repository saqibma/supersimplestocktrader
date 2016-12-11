package com.jpm.sssm.loader;

import com.jpm.sssm.model.StockData;
import com.jpm.sssm.model.StockType;
import com.jpm.sssm.util.OptionalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by adnan_saqib on 9/12/2016.
 */
@Component
public class StockLoaderImpl implements StockLoader {

    final Logger LOG = LoggerFactory.getLogger(StockLoaderImpl.class);

    @Override
    public List<StockData> loadStocks(){
        String fileName = "StockData.csv";
        List<StockData> stockList = new ArrayList<>();

        InputStream stockDataInputStream = getClass().getClassLoader().getResourceAsStream(fileName);

        try (BufferedReader br = new BufferedReader (new InputStreamReader(stockDataInputStream, "UTF-8"))) {
            String line = null;
            while((line = br.readLine()) != null){
                String[] data = line.split(Pattern.quote("|"));
                stockList.add(new StockData(data[0],
                        StockType.createStockType(data[1]),
                        OptionalUtil.stringToInt(data[2]),
                        OptionalUtil.safeFixedDividened(data[3]),
                        OptionalUtil.stringToInt(data[4])));
            }
        } catch (IOException e) {
            LOG.error("Problem in loading stocks in cache", e);
        }
        stockList.forEach(stock -> LOG.debug(String.format("Data loaded in cache : %s",
                stock)));
        return stockList;
    }
}

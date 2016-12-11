package com.jpm.sssm.model;

/**
 * Created by adnan_saqib on 11/12/2016.
 */
public enum StockType {
    COMMON,
    PREFERRED;

    public static StockType createStockType(String stockType){
        return "Common".equalsIgnoreCase(stockType) ? COMMON : PREFERRED;
    }
}

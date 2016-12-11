package com.jpm.sssm.exception;

/**
 * Created by adnan_saqib on 10/12/2016.
 */
public class InvalidStockException extends Exception {
    public InvalidStockException(){
        super("The stock is invalid");
    }

    public InvalidStockException(String stockSymbol, String detail){
        super(String.format("The stock \"%s\" is invalid: %s", stockSymbol, detail));
    }
}

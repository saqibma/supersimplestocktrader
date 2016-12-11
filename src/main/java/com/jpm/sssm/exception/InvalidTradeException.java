package com.jpm.sssm.exception;

/**
 * Created by adnan_saqib on 10/12/2016.
 */
public class InvalidTradeException extends Exception {
    public InvalidTradeException(){
        super("The trade is invalid");
    }

    public InvalidTradeException(long tradeId, String detail){
        super(String.format("The stock \"%s\" is invalid: %s", tradeId, detail));
    }
}

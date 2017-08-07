package com.okcoin.rest.manager;

import org.apache.http.HttpException;

import java.io.IOException;

/**
 * Created by xinfan on 2017/7/20.
 */

public abstract class MarketBase {
    public abstract String ticker(String symbol) throws IOException, HttpException;

    public abstract String orders_info(String type, String symbol,
                                       String order_id) throws IOException, HttpException;

    public abstract String kline(String symbol, String type,
                                 String size, String since) throws HttpException, IOException;

    public abstract String trade(String symbol, String type,
                                 String price, String amount) throws HttpException, IOException;

    public abstract String cancel_order(String symbol, String order_id) throws HttpException, IOException;

    public abstract float getOffsetValue();

    public abstract String getAppKey();

    public abstract String getSecretKey();
}

package com.okcoin.rest.manager;

import com.okcoin.rest.stock.IStockRestApi;
import com.okcoin.rest.stock.impl.StockRestApi;

import org.apache.http.HttpException;

import java.io.IOException;

/**
 * Created by xinfan on 2017/7/20.
 */

public class XianHuoMarket extends MarketBase {
    // 申请的secret_key
    String url_prex = "https://www.okcoin.com"; // 注意：请求URL
    // 国际站https://www.okcoin.com
    // ;
    // 国内站https://www.okcoin.cn

    /**
     * get请求无需发送身份认证,通常用于获取行情，市场深度等公共信息
     */
    IStockRestApi stockGet;

    /**
     * post请求需发送身份认证，获取用户个人相关信息时，需要指定api_key,与secret_key并与参数进行签名，
     * 此处对构造方法传入api_key与secret_key,在请求用户相关方法时则无需再传入，
     * 发送post请求之前，程序会做自动加密，生成签名。
     */
    IStockRestApi stockPost;

    public XianHuoMarket() {
        super();
        stockGet = new StockRestApi(url_prex);
        stockPost = new StockRestApi(url_prex, FConfig.api_key, FConfig.secret_key);
    }

    @Override
    public String ticker(String symbol) throws IOException, HttpException {
        return stockGet.ticker(symbol);
    }

    @Override
    public String orders_info(String type, String symbol, String order_id) throws IOException, HttpException {
        return stockPost.orders_info(type, symbol, order_id);
    }

    @Override
    public String kline(String symbol, String type, String size, String since) throws HttpException, IOException {
        return stockGet.kline(symbol, type, size, since);
    }

    @Override
    public String trade(String symbol, String type, String price, String amount) throws HttpException, IOException {
        return stockPost.trade(symbol, type, price, amount);
    }

    @Override
    public String cancel_order(String symbol, String order_id) throws HttpException, IOException {
        return stockPost.cancel_order(symbol, order_id);
    }
}

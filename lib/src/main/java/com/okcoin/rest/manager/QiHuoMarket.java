package com.okcoin.rest.manager;

import com.okcoin.rest.future.IFutureRestApi;
import com.okcoin.rest.future.impl.FutureRestApiV1;
import com.okcoin.rest.stock.impl.StockRestApi;

import org.apache.http.HttpException;

import java.io.IOException;

/**
 * Created by xinfan on 2017/7/20.
 */

public class QiHuoMarket extends MarketBase {
    // 申请的secret_key
    String url_prex = "https://www.okcoin.com"; // 注意：请求URL
    // 国际站https://www.okcoin.com
    // ;
    // 国内站https://www.okcoin.cn

    /**
     * get请求无需发送身份认证,通常用于获取行情，市场深度等公共信息
     */
    IFutureRestApi stockGet;

    /**
     * post请求需发送身份认证，获取用户个人相关信息时，需要指定api_key,与secret_key并与参数进行签名，
     * 此处对构造方法传入api_key与secret_key,在请求用户相关方法时则无需再传入，
     * 发送post请求之前，程序会做自动加密，生成签名。
     */
    IFutureRestApi stockPost;

    private String type = "quarter";

    public QiHuoMarket() {
        super();
        stockGet = new FutureRestApiV1(url_prex);
        stockPost = new FutureRestApiV1(url_prex, FConfig.api_key, FConfig.secret_key);
    }

    @Override
    public String ticker(String symbol) throws IOException, HttpException {
        return stockGet.future_ticker(symbol, type);
    }

    @Override
    public String orders_info(String orderType, String symbol, String order_id) throws IOException, HttpException {
        return stockPost.future_order_info(symbol, type, order_id, orderType, "", "");
    }

    @Override
    public String kline(String symbol, String zhouqi, String size, String since) throws HttpException, IOException {
        return stockGet.kline(symbol, zhouqi, type, size, since);
    }

    @Override
    public String trade(String symbol, String orderType, String price, String amount) throws HttpException, IOException {
        return stockPost.future_trade(symbol, type, price, amount, orderType.equals("buy") ? "1" : "2", "0");
    }

    @Override
    public String cancel_order(String symbol, String order_id) throws HttpException, IOException {
        return stockPost.future_cancel(symbol, type, order_id);
    }

    @Override
    public float getOffsetValue() {
        if (FConfig.MAKERTYPE_BTC.equals(FConfig.getInstance().getMakerType())) {
            return FConfig.OFFSET_BTC;
        }
        return FConfig.OFFSET_LTC;
    }
}

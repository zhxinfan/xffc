package com.okcoin.rest.manager;

/**
 * Created by xinfan on 2017/7/26.
 */

public class FConfig {

    private static FConfig fConfig;

    public static final String MAKERTYPE_BTC = "btc_usd";
    public static final String MAKERTYPE_LTC = "ltc_usd";

    public static final float OFFSET_BTC = 50;
    public static final float OFFSET_LTC = 0.5f;

    public static final String DUO = "buy";
    public static final String KONG = "sell";

    private FConfig() {

    }

    public static FConfig getInstance() {
        if (fConfig == null) {
            fConfig = new FConfig();
        }
        return fConfig;
    }


    private String makerType = "ltc_usd";
    private long time = 60 * 1000 * 3;
    private boolean jiaoyi = false;
    private double prefectxishu = 1;
    private int number = 70;
    private int total = 70;// 多空200个;
    private int junxianCount = 60;
    private boolean isAndroid = true;
    private double order_offset = 0.5;

    public static String api_key = "d99565c3-d264-4347-9442-e63c4fa43f73";  //OKCoin申请的apiKey
    public static String secret_key = "EE341A886C564B046F618FA620094D2E";  //OKCoin申请的apiKey

    public String getMakerType() {
        return makerType;
    }

    public void setMakerType(String makerType) {
        this.makerType = makerType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isJiaoyi() {
        return jiaoyi;
    }

    public void setJiaoyi(boolean jiaoyi) {
        this.jiaoyi = jiaoyi;
    }

    public double getPrefectxishu() {
        return prefectxishu;
    }

    public void setPrefectxishu(double prefectxishu) {
        this.prefectxishu = prefectxishu;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getJunxianCount() {
        return junxianCount;
    }

    public void setJunxianCount(int junxianCount) {
        this.junxianCount = junxianCount;
    }

    public boolean isAndroid() {
        return isAndroid;
    }

    public void setAndroid(boolean android) {
        isAndroid = android;
    }

    public double getOrder_offset() {
        return order_offset;
    }

    public void setOrder_offset(double order_offset) {
        this.order_offset = order_offset;
    }
}

package com.okcoin.rest;

import com.okcoin.rest.entity.TrickerEntity;
import com.okcoin.rest.manager.QiHuoMarket;
import com.okcoin.rest.manager.TrickerManger;
import com.okcoin.rest.manager.XianHuoMarket;

import org.apache.http.HttpException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 现货 REST API 客户端请求
 *
 * @author zhangchi
 */
public class StockClient {

    public static String DUO = "buy";
    public static String KONG = "sell";
    public static String makerType = "ltc_usd";
    public static final long time = 60 * 1000 * 3;
    public static boolean jiaoyi = false;
    public static double prefectxishu = 1;
    public static int number = 25;
    public static int total = 50;// 多空200个;
    public static int junxianCount = 60;
    public static boolean isAndroid = true;
    public static double order_offset = 0.5;

    public StockClient() {
    }

    public static void main(String[] args) throws HttpException, IOException {
        TrickerManger trickerManger = new TrickerManger(new XianHuoMarket());
        while (true) {
            String[] values = trickerManger.calJunxian(makerType, junxianCount, prefectxishu);
            TrickerEntity lastEntity = trickerManger.getLastEntity();
            TrickerEntity lastJunxianEntity = trickerManger.getLastJunxianEntity();
            double orderValue = Double.valueOf(values[0]);
            orderValue = Double.valueOf(String.format("%.3f", orderValue));
            double prefectValue = Double.valueOf(values[1]);
            prefectValue = Double.valueOf(String.format("%.3f", prefectValue));
            String orderType = values[2];
            TrickerManger.showLog(junxianCount + "均线" + "\n当前均线" + lastJunxianEntity.getJunxian() + " \n当前均线差价=" + (lastJunxianEntity.getJunxian() - lastEntity.getLast()) + " \n最新成交价=" + lastEntity.getLast() + "  \n推荐价格="
                    + orderValue + " \n盈利点1=" + (orderValue + prefectValue / 2) + " \n盈利点2="
                    + (orderValue - prefectValue / 2) + " \n系数=" + prefectxishu + (orderType.equals("buy") ? " \n做多" : " \n做空"));
            if (jiaoyi) {
                // --------------------------------开始查询老订单-----------------------------------
                trickerManger.selectDoOder(makerType);
                trickerManger.selectYingliOrder(makerType);
                trickerManger.doOrder(total, number, makerType, orderType, orderValue, prefectValue);

            }
            if (!jiaoyi)
                break;
            try {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                TrickerManger.showLog(format.format(new Date(System.currentTimeMillis())) + "-----------");

                Thread.sleep(time);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

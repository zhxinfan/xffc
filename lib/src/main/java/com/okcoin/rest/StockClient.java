package com.okcoin.rest;

import com.okcoin.rest.entity.TrickerEntity;
import com.okcoin.rest.manager.FConfig;
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

    public StockClient() {
    }

    public static void main(String[] args) throws HttpException, IOException {
        TrickerManger trickerManger = new TrickerManger(new XianHuoMarket());
        while (true) {
            String[] values = trickerManger.calJunxian(FConfig.makerType, FConfig.junxianCount, FConfig.prefectxishu);
            TrickerEntity lastEntity = trickerManger.getLastEntity();
            TrickerEntity lastJunxianEntity = trickerManger.getLastJunxianEntity();
            double orderValue = Double.valueOf(values[0]);
            orderValue = Double.valueOf(String.format("%.3f", orderValue));
            double prefectValue = Double.valueOf(values[1]);
            prefectValue = Double.valueOf(String.format("%.3f", prefectValue));
            String orderType = values[2];
            TrickerManger.showLog(FConfig.junxianCount + "均线" + "\n当前均线" + lastJunxianEntity.getJunxian() + " \n当前均线差价=" + (lastJunxianEntity.getJunxian() - lastEntity.getLast()) + " \n最新成交价=" + lastEntity.getLast() + "  \n推荐价格="
                    + orderValue + " \n盈利点1=" + (orderValue + prefectValue / 2) + " \n盈利点2="
                    + (orderValue - prefectValue / 2) + " \n系数=" + FConfig.prefectxishu + (orderType.equals("buy") ? " \n做多" : " \n做空"));
            if (FConfig.jiaoyi) {
                // --------------------------------开始查询老订单-----------------------------------
                trickerManger.selectDoOder(FConfig.makerType);
                trickerManger.selectYingliOrder(FConfig.makerType);
                trickerManger.doOrder(FConfig.total, FConfig.number, FConfig.makerType, orderType, orderValue, prefectValue);

            }
            if (!FConfig.jiaoyi)
                break;
            try {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                TrickerManger.showLog(format.format(new Date(System.currentTimeMillis())) + "-----------");

                Thread.sleep(FConfig.time);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

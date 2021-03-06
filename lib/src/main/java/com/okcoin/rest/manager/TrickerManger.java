package com.okcoin.rest.manager;

import com.okcoin.rest.entity.Order;
import com.okcoin.rest.entity.QiHuoEntity;
import com.okcoin.rest.entity.TrickerEntity;
import com.okcoin.rest.entity.event.LogEvent;

import org.apache.http.HttpException;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xinfan on 2017/7/20.
 */

public class TrickerManger {

    private MarketBase marketBase;
    private static Map<String, Order> orderMaps = new HashMap<>();
    private static Map<String, Order> yingliMaps = new HashMap<>();

    private TrickerEntity lastEntity;
    private TrickerEntity lastJunxianEntity;

    public TrickerManger(MarketBase marketBase) {
        this.marketBase = marketBase;
    }

    public List<TrickerEntity> getTrickerEntityList(String makerType, String lineType, int junxianCount) throws IOException, HttpException {
        String line = marketBase.kline(makerType, lineType, "" + junxianCount * 2, "");
        // k线
        // System.out.print(line);
        List<TrickerEntity> lists = new ArrayList<>();
        double closeValue = 0;
        JSONArray lineArray = new JSONArray(line);
        for (int i = 0; i < lineArray.length(); i++) {

            JSONArray lineObjectArray = lineArray.getJSONArray(i);
            TrickerEntity entity = new TrickerEntity();
            entity.setTime(lineObjectArray.getLong(0));
            entity.setOpen(lineObjectArray.getDouble(1));
            entity.setHigh(lineObjectArray.getDouble(2));
            entity.setLow(lineObjectArray.getDouble(3));
            entity.setClose(lineObjectArray.getDouble(4));
            if (i < junxianCount) {
                closeValue += entity.getClose();
                entity.setJunxian(0);
            } else {
                closeValue -= lists.get(i - junxianCount).getClose();
                closeValue += entity.getClose();
                entity.setJunxian(closeValue / junxianCount);

                // 最低价高于均线 说明在均线上方
                if (entity.getLow() > entity.getJunxian()) {
                    entity.setChajia(entity.getHigh() - entity.getJunxian());
                    // 最高价小于均价 说明在均线下方
                } else if (entity.getHigh() < entity.getJunxian()) {
                    entity.setChajia(entity.getJunxian() - entity.getLow());
                }
            }
            lists.add(entity);
        }
        return lists;
    }

    public TrickerEntity getTricker(String makerType) {
        TrickerEntity trickerEntity = null;
        try {
            String jsonStr = marketBase.ticker(makerType);

            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject tickerObj = jsonObject.getJSONObject("ticker");
            trickerEntity = new TrickerEntity();
            trickerEntity.setBuy(tickerObj.optDouble("buy"));
            trickerEntity.setHigh(tickerObj.optDouble("high"));
            trickerEntity.setSell(tickerObj.optDouble("sell"));
            trickerEntity.setLow(tickerObj.optDouble("low"));
            trickerEntity.setLast(tickerObj.optDouble("last"));
            trickerEntity.setVol(tickerObj.optDouble("vol"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        }
        return trickerEntity;
    }

    public List<QiHuoEntity> getOrders() {
        List<QiHuoEntity> list = new ArrayList<>();
        try {
            String ordersStr = marketBase.orders_info("1", FConfig.getInstance().getMakerType(), "-1");
            if (ordersStr.contains("orders")) {
                JSONObject object = new JSONObject(ordersStr);
                JSONArray array = object.optJSONArray("orders");
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        QiHuoEntity qiHuoEntity = new QiHuoEntity();
                        qiHuoEntity.setAmount(jsonObject.optInt("amount"));
                        qiHuoEntity.setContract_name(jsonObject.optString("contract_name"));
                        qiHuoEntity.setCreate_date(jsonObject.optLong("create_date"));
                        qiHuoEntity.setDeal_amount(jsonObject.optInt("deal_amount"));
                        qiHuoEntity.setFee(jsonObject.optDouble("fee"));
                        qiHuoEntity.setOrder_id(jsonObject.optInt("order_id"));
                        qiHuoEntity.setPrice(jsonObject.optDouble("price"));
                        qiHuoEntity.setStatus(jsonObject.optInt("status"));
                        qiHuoEntity.setPrice_avg(jsonObject.optDouble("price_avg"));
                        qiHuoEntity.setSymbol(jsonObject.optString("symbol"));
                        qiHuoEntity.setType(jsonObject.optInt("type"));
                        qiHuoEntity.setUnit_amount(jsonObject.optDouble("unit_amount"));
                        list.add(qiHuoEntity);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        }
        return list;
    }

    public String[] calJunxian(String makerType, int junxianCount, double prefectxishu) throws IOException, HttpException {

        lastEntity = new TrickerEntity();
        List<TrickerEntity> lists = getTrickerEntityList(makerType, "15min", junxianCount);
        lastJunxianEntity = lists.get(lists.size() - 1);

        Collections.sort(lists, new TrickerEntity.ChajiaComparator());

        double prefectValue = 0;
        int topCount = 10;

        for (int i = 0; i < topCount; i++) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            TrickerManger.showLog(format.format(new Date(lists.get(i).getTime())) + " " + lists.get(i).getChajia() + "  " + lists.get(i).getHigh());
            prefectValue += lists.get(i).getChajia();
        }
        TrickerManger.showLog("添加系数前=" + (prefectValue / topCount));
        prefectValue = prefectValue / topCount * prefectxishu;
        TrickerManger.showLog("添加系数后=" + prefectValue);
        double orderValue = 0;
        String orderType = null;

        if (lastEntity.getLast() > lastJunxianEntity.getJunxian()) {
            orderType = FConfig.KONG;
            // 做空
            orderValue = lastJunxianEntity.getJunxian() + prefectValue;
        } else {
            orderType = FConfig.DUO;
            // 做多
            orderValue = lastJunxianEntity.getJunxian() - prefectValue;
        }
        return new String[]{"" + orderValue, "" + prefectValue, orderType};
    }


    //查询做单结果
    public void selectDoOder(String makerType) throws IOException, HttpException {
        if (!orderMaps.isEmpty()) {
            String orderBase = "";
            for (String order : orderMaps.keySet()) {
                orderBase += order + ",";
            }
            TrickerManger.showLog("开始查询单子" + orderBase);
            String orderInfo = marketBase.orders_info("1", makerType, orderBase);
            JSONObject tradeJSV1 = new JSONObject(orderInfo);
            if (tradeJSV1.getBoolean("result")) {
                JSONArray array = tradeJSV1.getJSONArray("orders");
                if (marketBase instanceof XianHuoMarket) {
                    if (array.length() > 0) {
                        // 已经成交的单子
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject orderTitemJson = array.getJSONObject(i);
                            int status = orderTitemJson.getInt("status");
                            long orderString = orderTitemJson.getLong("order_id");
                            //完全成交
                            if (status == 2) {
                                Order order = orderMaps.remove("" + orderString);
                                String tradeResult = null;
                                if (lastEntity == null) {
                                    break;
                                }
                                if (order.getType() == "buy") {
                                    //要下的卖单的价格
                                    double orderValue = order.getBasePrice() + order.getPerfecetValue() / 2;
                                    if (orderValue < lastEntity.getLast()) {
                                        TrickerManger.showLog("当前最新价=" + lastEntity.getLast() + " 目标卖价=" + orderValue + " 存在风险，不上架");
                                        continue;
                                    }
                                    tradeResult = marketBase.trade(makerType,
                                            "sell",
                                            "" + orderValue, "" + order.getNumber()
                                    );
                                    TrickerManger.showLog("挂上了盈利单sell" + "" + orderValue);
                                } else {
                                    //要下的买单的价格
                                    double orderValue = order.getBasePrice() - order.getPerfecetValue() / 2;
                                    if (orderValue > lastEntity.getLast()) {
                                        TrickerManger.showLog("当前最新价=" + lastEntity.getLast() + " 目标买价=" + orderValue + " 存在风险，不上架");
                                        continue;
                                    }
                                    tradeResult = marketBase.trade(makerType,
                                            "buy",
                                            "" + orderValue, "" +
                                                    order.getNumber());
                                    TrickerManger.showLog("挂上了盈利单buy" + "" + orderValue);
                                }

                                JSONObject yingliJSON = new JSONObject(tradeResult);
                                if (yingliJSON.getBoolean("result")) {
                                    order.setOrderId("" + yingliJSON.getLong("order_id"));
                                    order.setNumber(order.getNumber());
                                    if (order.getType() == "buy") {
                                        order.setType("sell");
                                        order.setBasePrice(order.getBasePrice() + order.getPerfecetValue() / 2);
                                    } else {
                                        order.setType("buy");
                                        order.setBasePrice(order.getBasePrice() - order.getPerfecetValue() / 2);
                                    }
                                    order.setPerfecetValue(order.getPerfecetValue() / 2);
                                    yingliMaps.put(order.getOrderId(), order);
                                }
                            } else if (status == 0) {
                                TrickerManger.showLog(orderString + "未成交");
                            } else if (status == 1) {
                                TrickerManger.showLog(orderString + "部分成交");
                            }

                        }
                    }
                }

                for (String order : orderMaps.keySet()) {
                    marketBase.cancel_order(makerType, order);
                }
                orderMaps.clear();
            }
        }
    }

    public void selectYingliOrder(String makerType) throws IOException, HttpException {
        if (!yingliMaps.isEmpty() && marketBase instanceof XianHuoMarket) {
            String orderBase = "";
            for (String order : yingliMaps.keySet()) {
                orderBase += order + ",";
            }
            TrickerManger.showLog("开始查询盈利单子" + orderBase);
            String orderInfo = marketBase.orders_info("1", makerType, orderBase);
            JSONObject tradeJSV1 = new JSONObject(orderInfo);
            TrickerManger.showLog("盈利单子数据" + orderInfo);
            if (tradeJSV1.getBoolean("result")) {
                JSONArray array = tradeJSV1.getJSONArray("orders");
                if (array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject orderTitemJson = array.getJSONObject(i);
                        int status = orderTitemJson.getInt("status");
                        long orderString = orderTitemJson.getLong("order_id");
                        if (status == 2) {
                            yingliMaps.remove("" + orderString);
                        }
                    }
                } else {
                    TrickerManger.showLog("没有盈利单子成交");
                }
            }
        }
    }

    public void doOrder(int count, int number, String orderType, double orderValue, String makerType, double huilv) throws IOException, HttpException {
        if (orderType.equals("buy")) {
            orderValue -= count * FConfig.getInstance().getOrder_offset();
        } else {
            orderValue += count * FConfig.getInstance().getOrder_offset();
        }


        // 现货下单交易
        for (int i = 0; i < count; i++) {

            String tradeResult = marketBase.trade(makerType, orderType, "" + orderValue, "" + number);
            if (tradeResult == null) {
                break;
            }
            //TrickerManger.showLog(tradeResult + " \n下单价格" + orderValue * huilv);
            TrickerManger.showLog("\n下单价格" + orderValue * huilv);
            JSONObject tradeJSV1 = new JSONObject(tradeResult);
            if (!tradeJSV1.getBoolean("result")) {
                break;
            }
            if (orderType.equals("buy")) {
                orderValue += FConfig.getInstance().getOrder_offset();
            } else {
                orderValue -= FConfig.getInstance().getOrder_offset();
            }
//            long tradeOrderV1 = tradeJSV1.getLong("order_id");
//            Order order = new Order();
//            order.setBasePrice(orderValue);
//            order.setPerfecetValue(prefectValue);
//            order.setType(orderType);
//            order.setOrderId("" + tradeOrderV1);
//            order.setNumber(number);
//            orderMaps.put("" + tradeOrderV1, order);
        }
    }

    public void doOrder(int total, int number, String makerType, String orderType, double orderValue, double prefectValue) throws IOException, HttpException {
        int count = total / number - yingliMaps.size() - orderMaps.size();
        if (count > 0) {
            doOrder(count, number, orderType, orderValue, makerType, 1);
        }
    }


    public static void showLog(String log) {
        if (FConfig.getInstance().isAndroid()) {
            EventBus.getDefault().post(new LogEvent(log));
        } else {
            System.out.println(log);
        }
    }


    public TrickerEntity getLastEntity() {
        return lastEntity;
    }

    public void setLastEntity(TrickerEntity lastEntity) {
        this.lastEntity = lastEntity;
    }

    public TrickerEntity getLastJunxianEntity() {
        return lastJunxianEntity;
    }

    public void setLastJunxianEntity(TrickerEntity lastJunxianEntity) {
        this.lastJunxianEntity = lastJunxianEntity;
    }
}

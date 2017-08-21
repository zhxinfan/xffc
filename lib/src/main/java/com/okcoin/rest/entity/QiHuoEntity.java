package com.okcoin.rest.entity;

/**
 * Created by xinfan on 2017/8/21.
 */

public class QiHuoEntity {
    private int amount;//委托数量
    private String contract_name;//合约名称
    private long create_date;//委托时间
    private int deal_amount;//成交数量
    private double fee;//手续费
    private long order_id;//订单ID
    private double price;//订单价格
    private double price_avg;//平均价格
    private int status;//订单状态(0等待成交 1部分成交 2全部成交 -1撤单 4撤单处理中)
    private String symbol;//btc_usd:比特币,ltc_usd:莱特币
    private int type;// 订单类型 1：开多 2：开空 3：平多 4： 平空
    private double unit_amount;//合约面值

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getContract_name() {
        return contract_name;
    }

    public void setContract_name(String contract_name) {
        this.contract_name = contract_name;
    }

    public long getCreate_date() {
        return create_date;
    }

    public void setCreate_date(long create_date) {
        this.create_date = create_date;
    }

    public int getDeal_amount() {
        return deal_amount;
    }

    public void setDeal_amount(int deal_amount) {
        this.deal_amount = deal_amount;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(long order_id) {
        this.order_id = order_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice_avg() {
        return price_avg;
    }

    public void setPrice_avg(double price_avg) {
        this.price_avg = price_avg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getUnit_amount() {
        return unit_amount;
    }

    public void setUnit_amount(double unit_amount) {
        this.unit_amount = unit_amount;
    }
}

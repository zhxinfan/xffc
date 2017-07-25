package com.okcoin.rest.entity;

public class Order {
    private String orderId; // 订单id
    private String type;// 多单 还是空单
    private double perfecetValue;// 补价
    private double basePrice;// 底价
    private double number;//做的数量

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPerfecetValue() {
        return perfecetValue;
    }

    public void setPerfecetValue(double perfecetValue) {
        this.perfecetValue = perfecetValue;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public double getNumber() {
        return number;
    }

    public void setNumber(double number) {
        this.number = number;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

}

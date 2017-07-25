package com.okcoin.rest.entity;

import java.util.Comparator;

/**
 * Created by xinfan on 2017/7/19.
 */

public class TrickerEntity {

    public static class ChajiaComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {

            return new Double(((TrickerEntity) o2).getChajia()).compareTo(new Double(((TrickerEntity) o1).getChajia()));

        }
    }


    private double high;
    private double buy;
    private double last;
    private double low;
    private double sell;
    private double vol;
    private double junxian;
    private double chajia;

    private long time;
    private double open;
    private double close;

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getBuy() {
        return buy;
    }

    public void setBuy(double buy) {
        this.buy = buy;
    }

    public double getLast() {
        return last;
    }

    public void setLast(double last) {
        this.last = last;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getSell() {
        return sell;
    }

    public void setSell(double sell) {
        this.sell = sell;
    }

    public double getVol() {
        return vol;
    }

    public void setVol(double vol) {
        this.vol = vol;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getJunxian() {
        return junxian;
    }

    public void setJunxian(double junxian) {
        this.junxian = junxian;
    }

    public double getChajia() {
        return chajia;
    }

    public void setChajia(double chajia) {
        this.chajia = chajia;
    }
}

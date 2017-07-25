package com.okcoin.rest.entity.event;

/**
 * Created by xinfan on 2017/7/20.
 */

public class LogEvent {
    private String log;

    public LogEvent(String log) {
        this.log = log;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}

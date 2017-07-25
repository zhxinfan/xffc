package com.example.xinfan.x;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.okcoin.rest.StockClient;
import com.okcoin.rest.entity.TrickerEntity;
import com.okcoin.rest.entity.event.LogEvent;
import com.okcoin.rest.manager.TrickerManger;
import com.okcoin.rest.manager.XianHuoMarket;

import org.apache.http.HttpException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.okcoin.rest.StockClient.prefectxishu;

/**
 * Created by xinfan on 2017/7/20.
 */

public class MainActivity extends Activity {
    private OrderThread orderThread;
    private TextView tvInfo;
    private EditText etXishu, etJunxianCount;
    private CheckBox cbQidong;
    private boolean doXunhuan = false;
    TrickerManger trickerManger = new TrickerManger(new XianHuoMarket());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        tvInfo = (TextView) findViewById(R.id.main_info);
        etXishu = (EditText) findViewById(R.id.main_xishu);
        etXishu.setText("" + prefectxishu);

        etJunxianCount = (EditText) findViewById(R.id.main_junxiancount);
        etJunxianCount.setText("" + StockClient.junxianCount);

        cbQidong = (CheckBox) findViewById(R.id.main_qidong);
    }

    public class OrderThread extends Thread {
        @Override
        public void run() {
            while (doXunhuan) {
                prefectxishu = Double.valueOf(etXishu.getText().toString());
                String[] values = new String[3];
                try {
                    values = trickerManger.calJunxian(StockClient.makerType, Integer.valueOf(etJunxianCount.getText().toString()), prefectxishu);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (HttpException e) {
                    e.printStackTrace();
                }
                TrickerEntity lastEntity = trickerManger.getLastEntity();
                TrickerEntity lastJunxianEntity = trickerManger.getLastJunxianEntity();
                double orderValue = Double.valueOf(values[0]);
                double prefectValue = Double.valueOf(values[1]);
                String orderType = values[2];
                TrickerManger.showLog(etJunxianCount.getText().toString() + "均线" + "\n当前均线" + lastJunxianEntity.getJunxian() + " \n当前均线差价=" + (lastJunxianEntity.getJunxian() - lastEntity.getLast()) + " \n最新成交价=" + lastEntity.getLast() + "  \n推荐价格="
                        + orderValue + " \n盈利点1=" + (orderValue + prefectValue / 2) + " \n盈利点2="
                        + (orderValue - prefectValue / 2) + " \n系数=" + prefectxishu + (orderType.equals("buy") ? " \n做多" : " \n做空"));

                if (cbQidong.isChecked()) {
                    // --------------------------------开始查询老订单-----------------------------------
                    try {
                        trickerManger.selectDoOder(StockClient.makerType);
                        trickerManger.selectYingliOrder(StockClient.makerType);
                        trickerManger.doOrder(StockClient.total, StockClient.number, StockClient.makerType, orderType, orderValue, prefectValue);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (HttpException e) {
                        e.printStackTrace();
                    }

                }
                if (!cbQidong.isChecked())
                    break;
                try {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    TrickerManger.showLog(format.format(new Date(System.currentTimeMillis())) + "-----------");
                    Thread.sleep(StockClient.time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void btnClick(View view) {
        Button btn = (Button) view;
        if (btn.getText().toString().equals("开始")) {
            doXunhuan = true;
            if (orderThread == null) {
                orderThread = new OrderThread();
                orderThread.start();
            }
            btn.setText("结束");
        } else {
            doXunhuan = false;
            orderThread.interrupt();
            orderThread = null;
            btn.setText("开始");

        }

    }

    public void openOk(View view) {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.okcoin.trader");
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(LogEvent event) {
        tvInfo.setText(event.getLog() + "\n" + tvInfo.getText().toString());
    }

}

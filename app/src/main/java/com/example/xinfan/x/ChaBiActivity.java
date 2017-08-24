package com.example.xinfan.x;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.okcoin.rest.entity.TrickerEntity;
import com.okcoin.rest.entity.event.LogEvent;
import com.okcoin.rest.manager.FConfig;
import com.okcoin.rest.manager.MarketBase;
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
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import utils.StringUtils;

/**
 * Created by xinfan on 2017/7/20.
 */

public class ChaBiActivity extends Activity {
    @BindViews({R.id.chabi_money_type_1, R.id.chabi_money_type_2, R.id.chabi_money_type_3, R.id.chabi_money_type_4, R.id.chabi_money_type_5, R.id.chabi_money_type_6, R.id.chabi_money_type_7})
    List<RadioButton> radioButtonList;
    private OrderThread orderThread;
    @BindView(R.id.main_info)
    TextView tvInfo;
    @BindView(R.id.main_junxiancount)
    EditText etJunxianCount;
    @BindView(R.id.main_begin)
    Button btnBegin;
    private boolean doXunhuan = false;
    private MarketBase marketBase;
    private TrickerManger trickerManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_chabi);
        ButterKnife.bind(this);
        marketBase = new XianHuoMarket();
        trickerManger = new TrickerManger(marketBase);
        etJunxianCount.setText("" + FConfig.getInstance().getJunxianCount());
    }

    private String getLineType() {
        for (RadioButton radioButton : radioButtonList) {
            if (radioButton.isChecked()) {
                return radioButton.getText().toString();
            }
        }
        return null;
    }

    public class OrderThread extends Thread {
        @Override
        public void run() {
            while (doXunhuan) {
                List<TrickerEntity> btcList = null;
                List<TrickerEntity> ltcList = null;
                double newBtcPrice = 0;
                double newLtcPrice = 0;
                try {
//                    btcList = trickerManger.getTrickerEntityList("btc_usd", getLineType(), Integer.valueOf(etJunxianCount.getText().toString()));
//                    ltcList = trickerManger.getTrickerEntityList("ltc_usd", getLineType(), Integer.valueOf(etJunxianCount.getText().toString()));
                    btcList = trickerManger.getTrickerEntityList("btc_cny", getLineType(), Integer.valueOf(etJunxianCount.getText().toString()));
                    ltcList = trickerManger.getTrickerEntityList("ltc_cny", getLineType(), Integer.valueOf(etJunxianCount.getText().toString()));

                    newBtcPrice = btcList.get(btcList.size() - 1).getClose();
                    newLtcPrice = ltcList.get(ltcList.size() - 1).getClose();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (HttpException e) {
                    e.printStackTrace();
                }
                double low = 100000000;
                double high = 0.00000000000001;
                if (btcList != null && ltcList != null && btcList.size() == ltcList.size()) {
                    for (int i = 0; i < btcList.size(); i++) {
                        DateFormat format = new SimpleDateFormat("MM-dd HH:mm");
                        double number = ltcList.get(i).getClose() / btcList.get(i).getClose();
                        double highNumber = ltcList.get(i).getHigh() / btcList.get(i).getHigh();
                        double lowNumber = ltcList.get(i).getLow() / btcList.get(i).getLow();
                        high = Math.max(high, highNumber);
                        high = Math.max(high, lowNumber);

                        low = Math.min(low, highNumber);
                        low = Math.min(low, lowNumber);


                        TrickerManger.showLog(format.format(new Date(ltcList.get(i).getTime())) +
                                "-" + StringUtils.getBigDecimal(number) +
                                "-H " + StringUtils.getBigDecimal(highNumber) +
                                "-L " + StringUtils.getBigDecimal(lowNumber));
                    }
                }
                TrickerManger.showLog("----------H " + StringUtils.getBigDecimal(high) +
                        " L " + StringUtils.getBigDecimal(low) +
                        " BTC " + StringUtils.getBigDecimal0(newBtcPrice) +
                        " LTC " + StringUtils.getBigDecimal0(newLtcPrice));
                try {
                    Thread.sleep(FConfig.getInstance().getTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void stop() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                doXunhuan = false;
                orderThread.interrupt();
                orderThread = null;
                btnBegin.setText("开始");
            }
        });

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
            stop();

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

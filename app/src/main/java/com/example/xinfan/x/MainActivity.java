package com.example.xinfan.x;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.okcoin.rest.entity.TrickerEntity;
import com.okcoin.rest.entity.event.LogEvent;
import com.okcoin.rest.manager.FConfig;
import com.okcoin.rest.manager.MarketBase;
import com.okcoin.rest.manager.QiHuoMarket;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

import static com.okcoin.rest.manager.FConfig.MAKERTYPE_BTC;
import static com.okcoin.rest.manager.FConfig.MAKERTYPE_LTC;

/**
 * Created by xinfan on 2017/7/20.
 */

public class MainActivity extends Activity {
    private OrderThread orderThread;
    @BindView(R.id.main_info)
    TextView tvInfo;
    @BindView(R.id.main_xishu)
    EditText etXishu;
    @BindView(R.id.main_junxiancount)
    EditText etJunxianCount;
    @BindView(R.id.main_qidong)
    CheckBox cbQidong;
    @BindView(R.id.main_money_type_btc)
    RadioButton rbBtc;
    @BindView(R.id.main_begin)
    Button btnBegin;
    @BindView(R.id.main_jiange)
    EditText etJiange;
    private boolean doXunhuan = false;
    private MarketBase marketBase;
    private TrickerManger trickerManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        marketBase = new QiHuoMarket();
        trickerManger = new TrickerManger(marketBase);

        etXishu.setText("" + FConfig.getInstance().getPrefectxishu());
        etJunxianCount.setText("" + FConfig.getInstance().getJunxianCount());
        btcChecked();
    }

    @OnCheckedChanged(R.id.main_money_type_btc)
    public void btcChecked() {
        if (rbBtc.isChecked()) {
            FConfig.getInstance().setMakerType(MAKERTYPE_BTC);
        } else {
            FConfig.getInstance().setMakerType(MAKERTYPE_LTC);
        }
        FConfig.getInstance().setOrder_offset(marketBase.getOffsetValue());
        etJiange.setText("" + marketBase.getOffsetValue());
    }

    public class OrderThread extends Thread {
        @Override
        public void run() {
            double prefectxishu = Double.valueOf(etXishu.getText().toString());
            while (doXunhuan) {
                String[] values = new String[3];
                try {
                    values = trickerManger.calJunxian(FConfig.getInstance().getMakerType(), Integer.valueOf(etJunxianCount.getText().toString()), prefectxishu);
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
                TrickerManger.showLog(etJunxianCount.getText().toString() + "均线" + "\n当前均线" + lastJunxianEntity.getJunxian() +
                        " \n当前均线差价=" + (lastJunxianEntity.getJunxian() - lastEntity.getLast()) +
                        " \n最新成交价=" + lastEntity.getLast() + "  \n推荐价格=" + orderValue +
                        " \n盈利点1=" + (orderValue + prefectValue / 2) + " \n盈利点2="
                        + (orderValue - prefectValue / 2) + " \n系数=" + prefectxishu + (orderType.equals("buy") ? " \n做多" : " \n做空"));

                if (cbQidong.isChecked()) {
                    // --------------------------------开始查询老订单-----------------------------------
                    try {
                        trickerManger.selectDoOder(FConfig.getInstance().getMakerType());
                        trickerManger.selectYingliOrder(FConfig.getInstance().getMakerType());
                        trickerManger.doOrder(FConfig.getInstance().getTotal(), FConfig.getInstance().getNumber(), FConfig.getInstance().getMakerType(), orderType, orderValue, prefectValue);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (HttpException e) {
                        e.printStackTrace();
                    }

                }
                if (!cbQidong.isChecked()) {
                    MainActivity.this.stop();
                    break;
                }
                try {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    TrickerManger.showLog(format.format(new Date(System.currentTimeMillis())) + "-----------");
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

package com.example.xinfan.x;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.okcoin.rest.entity.TrickerEntity;
import com.okcoin.rest.entity.event.LogEvent;
import com.okcoin.rest.manager.FConfig;
import com.okcoin.rest.manager.MarketBase;
import com.okcoin.rest.manager.QiHuoMarket;
import com.okcoin.rest.manager.TrickerManger;

import org.apache.http.HttpException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

import static com.okcoin.rest.manager.FConfig.MAKERTYPE_LTC;

/**
 * Created by xinfan on 2017/7/20.
 */

public class MainActivity extends Activity {
    @BindView(R.id.main_info)
    TextView tvInfo;
    @BindView(R.id.main_begin)
    Button btnBegin;
    @BindView(R.id.main_jiange)
    EditText etJiange;
    @BindView(R.id.main_buy)
    RadioButton rbBuy;
    @BindView(R.id.main_price)
    TextView tvPrice;
    private MarketBase marketBase;
    private TrickerManger trickerManger;
    private static final int TIME = 2 * 1000;

    private Handler handler;

    double huilv = 0;
    int newVlaue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        marketBase = new QiHuoMarket();
        trickerManger = new TrickerManger(marketBase);
        FConfig.getInstance().setMakerType(MAKERTYPE_LTC);
        FConfig.getInstance().setNumber(1);

        handler = new Handler();
        handler.post(runnable);

        checkHuilv();
    }

    @OnCheckedChanged(R.id.main_buy)
    public void onRarioBtnClick() {
//        int temp = 0;
//        if (rbBuy.isChecked()) {
//            temp = newVlaue - 50;
//        } else {
//            temp = newVlaue + 50;
//        }
//        etJiange.setText("" + newVlaue);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final TrickerEntity trickerEntity = trickerManger.getTricker(FConfig.getInstance().getMakerType());
                    if (trickerEntity != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newVlaue = (int) (trickerEntity.getLast() * huilv);
                                TrickerManger.showLog("" + newVlaue);
                                tvPrice.setText("" + newVlaue);
                                //不轮询查价格
                                handler.postDelayed(runnable, TIME);
                            }
                        });
                    }
//                    trickerManger.getOrders();
                }
            }).start();

        }
    };

    private void checkHuilv() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String huilvStr = marketBase.exchangeRate();
                    JSONObject object = new JSONObject(huilvStr);
                    huilv = object.optDouble("rate");
                    //   TrickerManger.showLog("汇率:" + huilv);
                    FConfig.getInstance().setOrder_offset(1 / huilv);

                } catch (HttpException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void btnClick(View view) {
        Button btn = (Button) view;
        if (btn.getText().toString().equals("开始")) {
            doIt();
        }
    }

    private void doIt() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(etJiange.getText().toString())) {
                    return;
                }
                //输入的是人民币价格
                double value = Double.valueOf(etJiange.getText().toString());
                double newValue = value / huilv;
                // --------------------------------开始查询老订单-----------------------------------
                try {
                    trickerManger.doOrder(100, FConfig.getInstance().getNumber(), rbBuy.isChecked() ?
                            "buy" : "sell", newValue, FConfig.getInstance().getMakerType(), huilv);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (HttpException e) {
                    e.printStackTrace();
                }
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                TrickerManger.showLog(format.format(new Date(System.currentTimeMillis())) + "-----------");
            }
        }).start();

    }

    public void openOk(View view) {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.okinc.okex");
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(LogEvent event) {
        tvInfo.setText(event.getLog() + "\n" + tvInfo.getText().toString());
    }

}

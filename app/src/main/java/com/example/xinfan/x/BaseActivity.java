package com.example.xinfan.x;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by xinfan on 2017/8/7.
 */

public class BaseActivity extends Activity {

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }
}

package com.zhizulx.tt.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.zhizulx.tt.R;

import java.util.Calendar;

/**
 * Created by LL on 2016/9/1.
 */
public abstract class NoDoubleClickListener implements View.OnClickListener {
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    private Context ctx;
    public NoDoubleClickListener(Context ctx) {
        this.ctx = ctx;
    };

    @Override
    public void onClick(View v) {
        if (ctx != null) {
            Log.e("yuki", String.format("%s %s", ctx.getClass().getSimpleName(), ctx.getResources().getResourceEntryName(v.getId())));
            FileUtil.UserBehavior("%s %s", ctx.getClass().getSimpleName(), ctx.getResources().getResourceEntryName(v.getId()));
        }
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        } else {
            Toast.makeText(v.getContext(), v.getContext().getString(R.string.double_click), Toast.LENGTH_SHORT).show();
        }
    }

    public void onNoDoubleClick(View v) {

    }
}

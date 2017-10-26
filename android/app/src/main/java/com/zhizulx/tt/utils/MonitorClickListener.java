package com.zhizulx.tt.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;

/**
 * Created by LL on 2016/9/1.
 */
public abstract class MonitorClickListener implements View.OnClickListener {
    private Context ctx;
    public MonitorClickListener(Context ctx) {
        this.ctx = ctx;
    };

    @Override
    public void onClick(View v) {
        if (ctx != null && v.getId() != -1) {
            Log.e("yuki", String.format("%s %s", ctx.getClass().getSimpleName(), ctx.getResources().getResourceEntryName(v.getId())));
            FileUtil.UserBehavior("%s %s", ctx.getClass().getSimpleName(), ctx.getResources().getResourceEntryName(v.getId()));
        }
        onMonitorClick(v);
    }

    public void onMonitorClick(View v) {

    }
}

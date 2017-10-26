package com.zhizulx.tt.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;

/**
 * Created by LL on 2016/9/1.
 */
public class MonitorActivityBehavior {
    private Context ctx;
    public final static int START = 1;
    public final static int END = 0;
    public MonitorActivityBehavior(Context ctx) {
        this.ctx = ctx;
    };

    public void storeBehavior(int opt) {
        if (ctx != null) {
            String status = "Start";
            if (opt != 1) {
                status = "End";
            }
            FileUtil.UserBehavior("%s %s", ctx.getClass().getSimpleName(), status);
        }
    }

    public void storeBehavior(String content) {
        if (ctx != null) {
            FileUtil.UserBehavior("%s", content);
        }
    }
}

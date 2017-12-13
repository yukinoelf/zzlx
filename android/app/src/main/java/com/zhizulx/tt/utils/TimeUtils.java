package com.zhizulx.tt.utils;

import android.os.Handler;
import android.os.Message;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yuki on 2017/2/27.
 */

public class TimeUtils {
    private int time=60;

    private Timer timer;

    private Button btnSure;

    private String btnText;

    public TimeUtils(Button btnSure, String btnText) {
        super();
        this.btnSure = btnSure;
        this.btnText = btnText;
    }

    public void RunTimer(){
        timer=new Timer();
        TimerTask task=new TimerTask() {

            @Override
            public void run(){
                time--;
                Message msg=handler.obtainMessage();
                msg.what=1;
                handler.sendMessage(msg);
            }
        };
        timer.schedule(task, 100, 1000);
    }


    private Handler handler =new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    if(time>0){
                        btnSure.setEnabled(false);
                        btnSure.setText(time+"S");
                        btnSure.setTextSize(14);
                    }else{
                        timer.cancel();
                        btnSure.setText(btnText);
                        btnSure.setEnabled(true);
                        btnSure.setTextSize(14);
                    }
                    break;
                default:
                    break;
            }

        };
    };
}

package com.zhizulx.tt.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zhizulx.tt.R;
import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.imservice.support.IMServiceConnector;

public class Calculating extends FragmentActivity{
    private ImageView calculating;
    private IMService imService;
	private IMServiceConnector imServiceConnector = new IMServiceConnector(){
        @Override
        public void onIMServiceConnected() {
            imService = imServiceConnector.getIMService();
        }

        @Override
        public void onServiceDisconnected() {
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 在这个地方加可能会有问题吧
		imServiceConnector.connect(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.travel_activity_calculate);

        calculating = (ImageView)findViewById(R.id.calculating);
        Glide.with(this).load(R.drawable.calculating).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(calculating);
	}

	@Override
	protected void onDestroy() {
		imServiceConnector.disconnect(this);
        super.onDestroy();
	}
}

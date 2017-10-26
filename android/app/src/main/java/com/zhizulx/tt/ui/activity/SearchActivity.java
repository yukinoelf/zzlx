package com.zhizulx.tt.ui.activity;

import android.os.Bundle;

import com.zhizulx.tt.R;
import com.zhizulx.tt.imservice.manager.IMStackManager;
import com.zhizulx.tt.ui.base.TTBaseFragmentActivity;

public class SearchActivity extends   TTBaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        IMStackManager.getStackManager().pushActivity(this);
		setContentView(R.layout.tt_fragment_activity_search);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
        IMStackManager.getStackManager().popActivity(this);
		super.onDestroy();
	}

}

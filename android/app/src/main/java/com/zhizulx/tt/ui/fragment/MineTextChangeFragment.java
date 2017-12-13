package com.zhizulx.tt.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.zhizulx.tt.R;
import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.imservice.support.IMServiceConnector;
import com.zhizulx.tt.ui.base.TTBaseFragment;

/**
 * 设置页面
 */
public class MineTextChangeFragment extends TTBaseFragment{
	private View curView = null;
	private Intent intent;
	private EditText tvContent;
	private TextView tvLimit;
	private String title;
	private String content;
	private int limit;

	private IMServiceConnector imServiceConnector = new IMServiceConnector(){
        @Override
        public void onIMServiceConnected() {
            logger.d("config#onIMServiceConnected");
            IMService imService = imServiceConnector.getIMService();
            if (imService != null) {

            }
        }

        @Override
        public void onServiceDisconnected() {
        }
    };


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		imServiceConnector.connect(this.getActivity());
		if (null != curView) {
			((ViewGroup) curView.getParent()).removeView(curView);
			return curView;
		}
		curView = inflater.inflate(R.layout.travel_fragment_mine_text_change, topContentView);
		intent = getActivity().getIntent();
		title = intent.getStringExtra("title");
		content = intent.getStringExtra("content");
		limit = intent.getIntExtra("limit", 0);

		initRes();
        initBtn();
		return curView;
	}

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        imServiceConnector.disconnect(getActivity());
    }

	@Override
	public void onResume() {

		super.onResume();
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
	 * @Description 初始化资源
	 */
	private void initRes() {
		setTopTitle(title);
		setTopLeftButton(R.drawable.tt_top_back);
		topLeftContainerLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
                getActivity().finish();
			}
		});

        setTopRightButton(R.drawable.detail_disp_adjust_finish);
        topRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager().getBackStackEntryCount() == 0) {
                    intent.putExtra("content", tvContent.getText().toString());
                    if (title.equals(getString(R.string.change_name))) {
                        getActivity().setResult(11, intent);
                    } else {
                        getActivity().setResult(12, intent);
                    }
                    getActivity().finish();
                    return;
                }
                getFragmentManager().popBackStack();
            }
        });

        tvContent = (EditText)curView.findViewById(R.id.text_content);
        tvContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setLimit(tvContent.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tvLimit = (TextView)curView.findViewById(R.id.text_length_remain);
        tvContent.setText(content);
        setLimit(content.length());
	}

    private void setLimit(int length) {
        int value = 0;
        if (length <= limit) {
            value = limit - length;
        } else {
            String temp = tvContent.getText().toString().substring(0, limit);
            tvContent.setText(temp);
        }
        tvLimit.setText("剩余" + String.valueOf(value) + "个字");
        tvContent.setSelection(tvContent.getText().length());
    }

	@Override
	protected void initHandler() {
	}

    private void initBtn() {

    }
}

package com.zhizulx.tt.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.zhizulx.tt.R;
import com.zhizulx.tt.config.UrlConstant;
import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.imservice.support.IMServiceConnector;
import com.zhizulx.tt.ui.adapter.PictureUploadAdapter;
import com.zhizulx.tt.ui.base.TTBaseFragment;
import com.zhizulx.tt.ui.helper.PhotoHelper;
import com.zhizulx.tt.utils.FileUtil;
import com.zhizulx.tt.utils.ImageUtil;
import com.zhizulx.tt.utils.MoGuHttpClient;
import com.zhizulx.tt.utils.TravelUIHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 设置页面
 */
public class FeedbackFragment extends TTBaseFragment{
	private View curView = null;
    private IMService imService;
    private EditText feedbackContent;
	private RecyclerView rvPictureUpload;
    private Button send;
    private PictureUploadAdapter pictureUploadAdapter;
    private List<Uri> picUri = new ArrayList<>();
    private static final int PHOTO_REQUEST_CAREMA = 0;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 1;// 从相册中选择
    private Uri imagePhoto;
    private Uri feedback1;
    private Uri feedback2;
    private Uri feedback3;
    private TextView phote;
    private TextView gallary;
    private ImageView avatarCancel;
    private PopupWindow popupWindow;
    private static final int Cancel = 123;
    private int iAvatar = Cancel;
    private String feedbackUploadUrl;
    private boolean canClose = true;

    private IMServiceConnector imServiceConnector = new IMServiceConnector(){
        @Override
        public void onIMServiceConnected() {
            logger.d("config#onIMServiceConnected");
            imService = imServiceConnector.getIMService();
            if (imService != null) {
                feedback1 = Uri.parse("file://%s" + FileUtil.getAppPath() + File.separator + "feedback1.jpg");
                feedback2 = Uri.parse("file://%s" + FileUtil.getAppPath() + File.separator + "feedback2.jpg");
                feedback3 = Uri.parse("file://%s" + FileUtil.getAppPath() + File.separator + "feedback3.jpg");
                picUri.add(Uri.parse("no pic"));
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
		curView = inflater.inflate(R.layout.travel_fragment_feedback, topContentView);
        feedbackUploadUrl = UrlConstant.FEEDBACK_UPLOLAD_ADDRESS;
		initRes();
        initPictureUpload();
        if (!FileUtil.isFileExist("feedback1.jpg", "ZZLX")) {
            FileUtil.createFileInSDCard("feedback1.jpg", "ZZLX");
        }
        if (!FileUtil.isFileExist("feedback2.jpg", "ZZLX")) {
            FileUtil.createFileInSDCard("feedback2.jpg", "ZZLX");
        }
        if (!FileUtil.isFileExist("feedback3.jpg", "ZZLX")) {
            FileUtil.createFileInSDCard("feedback3.jpg", "ZZLX");
        }
        if (!FileUtil.isFileExist("feedback.txt", "ZZLX")) {
            FileUtil.createFileInSDCard("feedback.txt", "ZZLX");
        }
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
        getFocus();
	}

    private void getFocus() {
        getView().setFocusable(true);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    // 监听到返回按钮点击事件
                    if (canClose) {
                        getActivity().finish();
                    }

                    return true;// 未处理
                }
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = null;
        switch (requestCode) {
            case PHOTO_REQUEST_GALLERY:
                // 从相册返回的数据
                if (data != null) {
                    // 得到图片的全路径
                    uri = data.getData();
                }
                break;
            case PHOTO_REQUEST_CAREMA:
                // 从相机返回的数据
                if (hasSdcard() && resultCode == -1) {
                    uri = imagePhoto;
                }
                break;
        }
        if (uri != null) {
            uriProcess(uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
	 * @Description 初始化资源
	 */
	private void initRes() {
		setTopTitle(getString(R.string.feedback));
		setTopLeftButton(R.drawable.tt_top_back);
		topLeftContainerLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
                if (canClose) {
                    getActivity().finish();
                }
			}
		});
        feedbackContent = (EditText) curView.findViewById(R.id.feedback_content);
		rvPictureUpload = (RecyclerView) curView.findViewById(R.id.rv_feedback);
        rvPictureUpload.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                return false;
            }
        });
        send = (Button) curView.findViewById(R.id.feedback_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback();
            }
        });
	}

	@Override
	protected void initHandler() {
	}

	private void initPictureUpload() {
        rvPictureUpload.setHasFixedSize(true);
		LinearLayoutManager layoutManagerResult = new LinearLayoutManager(getActivity());
		layoutManagerResult.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvPictureUpload.setLayoutManager(layoutManagerResult);
		PictureUploadAdapter.OnRecyclerViewListener hotelRVListener = new PictureUploadAdapter.OnRecyclerViewListener() {
			@Override
			public void onItemClick(int position) {
                picUri.remove(position);
                if (picUri.size() == 2 && !picUri.get(1).equals(Uri.parse("no pic"))) {
                    picUri.add(Uri.parse("no pic"));
                }
                pictureUploadAdapter.notifyItemRemoved(position);
                pictureUploadAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAddClick(int position) {
                addFeedbackPic();
                switch (position) {
                    case 0:
                        imagePhoto = feedback1;
                        break;
                    case 1:
                        imagePhoto = feedback2;
                        break;
                    case 2:
                        imagePhoto = feedback3;
                        break;
                }
            }
        };
        pictureUploadAdapter = new PictureUploadAdapter(getActivity(), picUri);
        pictureUploadAdapter.setOnRecyclerViewListener(hotelRVListener);
        rvPictureUpload.setAdapter(pictureUploadAdapter);
	}

    private void addFeedbackPic() {
        View popupWindowView = getActivity().getLayoutInflater().inflate(R.layout.travel_popup_avatar, null);
        //内容，高度，宽度
        popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //动画效果
        popupWindow.setAnimationStyle(R.style.AnimationBottomFade);
        //菜单背景色
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        popupWindow.setBackgroundDrawable(dw);
        //显示位置
        popupWindow.showAtLocation(getActivity().getLayoutInflater().inflate(R.layout.travel_fragment_mine_info, null),
                Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        //设置背景半透明
        backgroundAlpha(0.5f);
        //关闭事件
        popupWindow.setOnDismissListener(new popupDismissListener());

        popupWindowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;
            }
        });

        phote = (TextView)popupWindowView.findViewById(R.id.mine_info_photo);
        gallary = (TextView)popupWindowView.findViewById(R.id.mine_info_gallary);
        avatarCancel = (ImageView)popupWindowView.findViewById(R.id.mine_info_avatar_cancel);

        View.OnClickListener popAvatar = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.mine_info_photo:
                        iAvatar = PHOTO_REQUEST_CAREMA;
                        avatorGet();
                        popupWindow.dismiss();
                        break;
                    case R.id.mine_info_gallary:
                        iAvatar = PHOTO_REQUEST_GALLERY;
                        avatorGet();
                        popupWindow.dismiss();
                        break;
                    case R.id.mine_info_avatar_cancel:
                        popupWindow.dismiss();
                        break;
                }
            }
        };
        phote.setOnClickListener(popAvatar);
        gallary.setOnClickListener(popAvatar);
        avatarCancel.setOnClickListener(popAvatar);
    }

    public void avatorGet() {
        switch(iAvatar) {
            case PHOTO_REQUEST_CAREMA:
                camera();
                break;
            case PHOTO_REQUEST_GALLERY:
                gallery();
                break;
            case Cancel:
                break;
        }
    }

    /* 从相册获取 */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /* 从相机获取 */
    public void camera() {
        // 激活相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePhoto);
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    private void uriProcess(Uri uri) {
        switch (picUri.size()) {
            case 1:
                picUri.remove(0);
                picUri.add(uri);
                picUri.add(Uri.parse("no pic"));
                break;
            case 2:
                picUri.remove(1);
                picUri.add(uri);
                picUri.add(Uri.parse("no pic"));
                break;
            case 3:
                picUri.remove(2);
                picUri.add(uri);
                break;
        }
        pictureUploadAdapter.notifyDataSetChanged();
    }

    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            //
            // TODO: http request.
            //
            try {
                FileWriter writer = new FileWriter(FileUtil.getCommentFile(), false);
                writer.write(feedbackContent.getText().toString());
                writer.close();
                byte[] content = FileUtil.getFileContent(FileUtil.getCommentFile());
                List<byte[]> picByteList = new ArrayList<>();
                for (Uri uri : picUri) {
                    if (uri.equals(Uri.parse("no pic"))) {
                        continue;
                    }
                    byte[] bytes = PhotoHelper.getBytes(ImageUtil.getBitmapFormUri(getActivity(), uri));
                    picByteList.add(bytes);
                }
                MoGuHttpClient httpClient = new MoGuHttpClient();
                if (feedbackUploadUrl != null && !feedbackUploadUrl.isEmpty()) {
                    String ret = httpClient.uploadFeedback(feedbackUploadUrl, content, picByteList, String.valueOf(imService.getLoginManager().getLoginId()));
                    canClose = true;
                    Message msg = new Message();
                    msg.what = 1;
                    Bundle data = new Bundle();
                    data.putString("ret", ret);
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            } catch (IOException e) {
                logger.e(e.getMessage());
            }
        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String val = "null";
            if (msg.what == 1) {
                Bundle data = msg.getData();
                val = data.getString("ret");
                if (val.equals("ok")) {
                    TravelUIHelper.showSuccessDialog(getActivity(), "反馈成功", new TravelUIHelper.dialogCallback() {
                        @Override
                        public void callback() {
                            getActivity().finish();
                        }
                    });
                }

            }
        }
    };

    private void sendFeedback() {
        if (feedbackContent.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "你的建议要大声说出来！", Toast.LENGTH_SHORT).show();
            return;
        }
        canClose = false;
        new Thread(runnable).start();
    }

    /* 判断sdcard是否被挂载 */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    class popupDismissListener implements PopupWindow.OnDismissListener{

        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }
}

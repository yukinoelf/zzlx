package com.zhizulx.tt.ui.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhizulx.tt.DB.entity.UserEntity;
import com.zhizulx.tt.DB.sp.SystemConfigSp;
import com.zhizulx.tt.R;
import com.zhizulx.tt.config.IntentConstant;
import com.zhizulx.tt.imservice.event.LoginEvent;
import com.zhizulx.tt.imservice.event.UserInfoEvent;
import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.imservice.service.LocationService;
import com.zhizulx.tt.imservice.support.IMServiceConnector;
import com.zhizulx.tt.utils.EquipmentHandler;
import com.zhizulx.tt.utils.FileUtil;
import com.zhizulx.tt.utils.ImageUtil;
import com.zhizulx.tt.utils.TravelUIHelper;

import java.io.File;

import de.greenrobot.event.EventBus;

public class HomePageActivity extends FragmentActivity {
    private ImageView mineIcon;
    private PopupWindow popupWindow;
    private RelativeLayout mine;
    private ImageView mineAvatar;
    private TextView collection;
    private TextView aboutUs;
    private TextView clearCache;
    private TextView feedback;
    private UserEntity userEntity;
    private TextView name;
    private TextView sex;
    private RelativeLayout topBar;

    private IMService imService;

    private IMServiceConnector imServiceConnector = new IMServiceConnector() {
        @Override
        public void onIMServiceConnected() {
            imService = imServiceConnector.getIMService();
            userEntity = imService.getLoginManager().getLoginInfo();
            EquipmentHandler equipmentHandler = EquipmentHandler.getInstance();
            equipmentHandler.init(HomePageActivity.this);
            if (equipmentHandler.hasEquipmentInfo() == false) {
                equipmentHandler.collection();
            }
            initPopupWindow();
        }

        @Override
        public void onServiceDisconnected() {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        imServiceConnector.connect(this);
        setContentView(R.layout.activity_homepage);
        initView();
        initButton();
        startLocation();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        imServiceConnector.disconnect(this);
        super.onDestroy();
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        mineIcon = (ImageView)findViewById(R.id.mine_icon);
        topBar = (RelativeLayout)findViewById(R.id.home_page_top_status);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) topBar.getLayoutParams();
        int topHeight = SystemConfigSp.instance().getIntConfig(SystemConfigSp.SysCfgDimension.TOP_BAR_HEIGHT);
        lp.setMargins(0, topHeight, 0, 0);
    }

    private void initButton() {
        View.OnClickListener homePageListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.mine_icon:
                        if (popupWindow == null) {
                            initPopupWindow();
                            //动画效果
                            popupWindow.setAnimationStyle(R.style.AnimationLeftFade);
                            //菜单背景色
                            ColorDrawable dw = new ColorDrawable(0xffffffff);
                            popupWindow.setBackgroundDrawable(dw);
                            //显示位置
                            popupWindow.showAtLocation(mineIcon, Gravity.LEFT, 0, 500);
                            //设置背景半透明
                            backgroundAlpha(0.5f);
                        } else {
                            if (popupWindow.isShowing()) {
                                return;
                            }
                            //动画效果
                            popupWindow.setAnimationStyle(R.style.AnimationLeftFade);
                            //菜单背景色
                            ColorDrawable dw = new ColorDrawable(0xffffffff);
                            popupWindow.setBackgroundDrawable(dw);
                            //显示位置
                            popupWindow.showAtLocation(mineIcon, Gravity.LEFT, 0, 500);
                            //设置背景半透明
                            backgroundAlpha(0.5f);
                        }
                        break;
                }
            }
        };
        mineIcon.setOnClickListener(homePageListener);
    }

    class popupDismissListener implements PopupWindow.OnDismissListener{
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }

    }

    protected void initPopupWindow(){
        View popupWindowView = getLayoutInflater().inflate(R.layout.travel_popup_mine, null);
        //内容，高度，宽度
        popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT, true);

        //关闭事件
        popupWindow.setOnDismissListener(new popupDismissListener());

        popupWindowView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*if( popupWindow!=null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                    popupWindow=null;
                }*/
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;
            }
        });

        mine = (RelativeLayout)popupWindowView.findViewById(R.id.mine_info);
        mineAvatar = (ImageView)popupWindowView.findViewById(R.id.mine_avatar);
        name = (TextView)popupWindowView.findViewById(R.id.mine_name);
        sex = (TextView)popupWindowView.findViewById(R.id.mine_sex);
        userEntity = imService.getLoginManager().getLoginInfo();
        if (userEntity != null) {
            ImageUtil.GlideRoundAvatar(HomePageActivity.this, userEntity.getAvatar(), mineAvatar);
            name.setText(userEntity.getMainName());
            String strSex = "保密";
            switch (userEntity.getGender()) {
                case 1:
                    strSex = "帅哥";
                    break;
                case 2:
                    strSex = "美女";
                    break;
            }
            sex.setText(strSex);
        }
        collection = (TextView)popupWindowView.findViewById(R.id.mine_collection);
        aboutUs = (TextView)popupWindowView.findViewById(R.id.mine_about_us);
        clearCache = (TextView)popupWindowView.findViewById(R.id.mine_clear_cache);
        feedback = (TextView)popupWindowView.findViewById(R.id.mine_feedback);

        View.OnClickListener popListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.mine_info:
                        Intent intentMine = new Intent(HomePageActivity.this, MineInfoActivity.class);
                        startActivity(intentMine);
                        popupWindow.dismiss();
                        break;
                    case R.id.mine_collection:
                        Intent intentMineCollection = new Intent(HomePageActivity.this, MineCollectionActivity.class);
                        startActivity(intentMineCollection);
                        popupWindow.dismiss();
                        break;
                    case R.id.mine_about_us:
                        Intent intentMineAboutUs = new Intent(HomePageActivity.this, MineAboutUsActivity.class);
                        startActivity(intentMineAboutUs);
                        popupWindow.dismiss();
                        break;
                    case R.id.mine_clear_cache:
                        ClearCache();
                        popupWindow.dismiss();
                        break;
                    case R.id.mine_feedback:
                        Intent intentMineFeedback = new Intent(HomePageActivity.this, FeedbackActivity.class);
                        startActivity(intentMineFeedback);
                        popupWindow.dismiss();
                        break;
                }
            }
        };
        mine.setOnClickListener(popListener);
        collection.setOnClickListener(popListener);
        aboutUs.setOnClickListener(popListener);
        clearCache.setOnClickListener(popListener);
        feedback.setOnClickListener(popListener);
    }

    private void ClearCache() {
        TravelUIHelper.dialogCallback callback = new TravelUIHelper.dialogCallback() {
            @Override
            public void callback() {
                ImageLoader.getInstance().clearMemoryCache();
                ImageLoader.getInstance().clearDiskCache();
                Glide.get(HomePageActivity.this).clearMemory();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FileUtil.deleteHistoryFiles(new File(FileUtil.getAppPath() + File.separator), System.currentTimeMillis());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.get(HomePageActivity.this).clearDiskCache();
                            }
                        }).start();
                        Toast toast = Toast.makeText(HomePageActivity.this,R.string.thumb_remove_finish,Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                },500);
            }
        };
        TravelUIHelper.showAlertDialog(HomePageActivity.this, getString(R.string.clear_cache_tip), callback);
    }

    public void onEventMainThread(UserInfoEvent event){
        switch (event){
            case USER_INFO_OK:
                break;
        }
    }

    public void onEventMainThread(LoginEvent event){
        switch (event){
            case LOGIN_OUT:
                handleOnLogout();
                break;
        }
    }

    private void handleOnLogout() {
        finish();
        jumpToLoginPage();

    }

    private void jumpToLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(IntentConstant.KEY_LOGIN_NOT_AUTO, true);
        startActivity(intent);
    }

    private void startLocation() {
        Intent intent = new Intent();
        intent.setClass(this, LocationService.class);
        startService(intent);
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }
}

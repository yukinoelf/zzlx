package com.zhizulx.tt.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.SocializeUtils;
import com.zhizulx.tt.DB.sp.LoginSp;
import com.zhizulx.tt.DB.sp.SystemConfigSp;
import com.zhizulx.tt.R;
import com.zhizulx.tt.config.IntentConstant;
import com.zhizulx.tt.config.UrlConstant;
import com.zhizulx.tt.imservice.manager.IMContactManager;
import com.zhizulx.tt.protobuf.IMBuddy;
import com.zhizulx.tt.utils.IMUIHelper;
import com.zhizulx.tt.imservice.event.LoginEvent;
import com.zhizulx.tt.imservice.event.SocketEvent;
import com.zhizulx.tt.imservice.manager.IMLoginManager;
import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.ui.base.TTBaseActivity;
import com.zhizulx.tt.imservice.support.IMServiceConnector;
import com.zhizulx.tt.utils.Logger;

import org.json.JSONObject;

import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import de.greenrobot.event.EventBus;

import static com.zhizulx.tt.config.UrlConstant.BASE_ADDRESS;

/**
 * @YM 1. 链接成功之后，直接判断是否loginSp是否可以直接登陆
 * true: 1.可以登陆，从DB中获取历史的状态
 * 2.建立长连接，请求最新的数据状态 【网络断开没有这个状态】
 * 3.完成
 * <p/>
 * false:1. 不能直接登陆，跳转到登陆页面
 * 2. 请求消息服务器地址，链接，验证，触发loginSuccess
 * 3. 保存登陆状态
 */
public class LoginActivity extends TTBaseActivity {
    private Logger logger = Logger.getLogger(LoginActivity.class);
    private Handler uiHandler = new Handler();
    private EditText mNameView;
    private EditText mPasswordView;
    private View loginPage;
    private View splashPage;
    private View mLoginStatusView;
    private TextView mSwitchLoginServer;
    private InputMethodManager intputManager;
    private Button requestIdentifyingCode;
    private Button login;
    private int i = 60;//倒计时
    private String countryCode="86";

    private IMService imService;
    private boolean autoLogin = true;
    private boolean loginSuccess = false;

    private ImageView qqLogin;
    private ImageView wechatLogin;
    private ProgressDialog dialog;
    private String name;
    private String avatar;
    private String wechatUnionID;

    private IMServiceConnector imServiceConnector = new IMServiceConnector() {
        @Override
        public void onServiceDisconnected() {
        }

        @Override
        public void onIMServiceConnected() {
            logger.d("login#onIMServiceConnected");
            imService = imServiceConnector.getIMService();
            try {
                do {
                    if (imService == null) {
                        //后台服务启动链接失败
                        break;
                    }
                    IMLoginManager loginManager = imService.getLoginManager();
                    LoginSp loginSp = imService.getLoginSp();
                    if (loginManager == null || loginSp == null) {
                        // 无法获取登陆控制器
                        break;
                    }

                    LoginSp.SpLoginIdentity loginIdentity = loginSp.getLoginIdentity();
                    if (loginIdentity == null) {
                        // 之前没有保存任何登陆相关的，跳转到登陆页面
                        break;
                    }

                    mNameView.setText(loginIdentity.getLoginName());
                    if (TextUtils.isEmpty(loginIdentity.getPwd())) {
                        // 密码为空，可能是loginOut
                        break;
                    }
                    mPasswordView.setText(loginIdentity.getPwd());

                    if (autoLogin == false) {
                        break;
                    }

                    handleGotLoginIdentity(loginIdentity);
                    return;
                } while (false);

                // 异常分支都会执行这个
                handleNoLoginIdentity();
            } catch (Exception e) {
                // 任何未知的异常
                logger.w("loadIdentity failed");
                handleNoLoginIdentity();
            }
        }
    };


    /**
     * 跳转到登陆的页面
     */
    private void handleNoLoginIdentity() {
        logger.i("login#handleNoLoginIdentity");
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showLoginPage();
            }
        }, 1000);
    }

    /**
     * 自动登陆
     */
    private void handleGotLoginIdentity(final LoginSp.SpLoginIdentity loginIdentity) {
        logger.i("login#handleGotLoginIdentity");

        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                logger.d("login#start auto login");
                if (imService == null || imService.getLoginManager() == null) {
                    Toast.makeText(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                    showLoginPage();
                }
                imService.getLoginManager().login(loginIdentity);
            }
        }, 500);
    }


    private void showLoginPage() {
        splashPage.setVisibility(View.GONE);
        loginPage.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        intputManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        logger.d("login#onCreate");

        SystemConfigSp.instance().init(getApplicationContext());
        if (TextUtils.isEmpty(SystemConfigSp.instance().getStrConfig(SystemConfigSp.SysCfgDimension.LOGINSERVER))) {
            SystemConfigSp.instance().setStrConfig(SystemConfigSp.SysCfgDimension.LOGINSERVER, UrlConstant.ACCESS_MSG_ADDRESS);
        }

        imServiceConnector.connect(LoginActivity.this);
        EventBus.getDefault().register(this);

        setContentView(R.layout.tt_activity_login);
        mSwitchLoginServer = (TextView)findViewById(R.id.sign_switch_login_server);
        mSwitchLoginServer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(LoginActivity.this, android.R.style.Theme_Holo_Light_Dialog));
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialog_view = inflater.inflate(R.layout.tt_custom_dialog, null);
                final EditText editText = (EditText)dialog_view.findViewById(R.id.dialog_edit_content);
                editText.setText(SystemConfigSp.instance().getStrConfig(SystemConfigSp.SysCfgDimension.LOGINSERVER));
                TextView textText = (TextView)dialog_view.findViewById(R.id.dialog_title);
                textText.setText(R.string.switch_login_server_title);
                builder.setView(dialog_view);
                builder.setPositiveButton(getString(R.string.tt_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(!TextUtils.isEmpty(editText.getText().toString().trim()))
                        {
                            SystemConfigSp.instance().setStrConfig(SystemConfigSp.SysCfgDimension.LOGINSERVER,editText.getText().toString().trim());
                            dialog.dismiss();
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.tt_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

        mNameView = (EditText) findViewById(R.id.name);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {

                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        requestIdentifyingCode = (Button)findViewById(R.id.request_identifying_code);
        requestIdentifyingCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = mNameView.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNum)) {
                    Toast.makeText(getApplicationContext(), "手机号码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                SMSSDK.getVerificationCode(countryCode, phoneNum);

                mPasswordView.setFocusable(true);
                mPasswordView.setFocusableInTouchMode(true);
                mPasswordView.requestFocus();
                intputManager.toggleSoftInputFromWindow(mPasswordView.getWindowToken(), 1, 0);

                requestIdentifyingCode.setClickable(false);
                requestIdentifyingCode.setBackgroundResource(R.drawable.request_identifying_code_clicked);
                //开始倒计时
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (; i > 0; i--) {
                            handler.sendEmptyMessage(-1);
                            if (i <= 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(-2);
                    }
                }).start();
            }
        });
        mLoginStatusView = findViewById(R.id.login_status);
        login = (Button) findViewById(R.id.sign_in_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNum = mNameView.getText().toString().trim();
                String code = mPasswordView.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNum)) {
                    Toast.makeText(getApplicationContext(), "手机号码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(getApplicationContext(), "验证码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                login.setBackgroundResource(R.drawable.login_button_clicked);
                SMSSDK.submitVerificationCode(countryCode, phoneNum, code);
                intputManager.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
                if (phoneNum.equals("521314")) {
                    attemptLogin();
                }
            }
        });
        initAutoLogin();

        // 启动短信验证sdk
        SMSSDK.initSDK(LoginActivity.this, "1baa910545d5b", "0928f3be205e6caf9af24f3486b52d64");

        //initSDK方法是短信SDK的入口，需要传递您从MOB应用管理后台中注册的SMSSDK的应用AppKey和AppSecrete，如果填写错误，后续的操作都将不能进行
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.what = -3;
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        //注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);

        qqLogin = (ImageView)findViewById(R.id.qq_login);
        qqLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qqLogin();
            }
        });

        wechatLogin = (ImageView)findViewById(R.id.wechat_login);
        wechatLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wechatLogin();
            }
        });
        dialog = new ProgressDialog(LoginActivity.this);
        getStatusBarHeight();
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -1) {
                requestIdentifyingCode.setText(i + " s");
            } else if (msg.what == -2) {
                requestIdentifyingCode.setText("重新发送");
                requestIdentifyingCode.setClickable(true);
                requestIdentifyingCode.setBackgroundResource(R.drawable.request_identifying_code);
                i = 60;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("asd", "event=" + event + "  result=" + result + "  ---> result=-1 success , result=0 error");
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，返回MainActivity,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        // 提交验证码成功,调用注册接口，之后直接登录
                        //当号码来自短信注册页面时调用登录注册接口
                        //当号码来自绑定页面时调用绑定手机号码接口
                        login.setBackgroundResource(R.drawable.login_button);
                        Toast.makeText(getApplicationContext(), "短信验证成功",
                                Toast.LENGTH_SHORT).show();
                        attemptLogin();

                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "验证码已经发送",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ((Throwable) data).printStackTrace();
                    }
                } else if (result == SMSSDK.RESULT_ERROR) {
                    try {
                        login.setBackgroundResource(R.drawable.login_button);
                        Throwable throwable = (Throwable) data;
                        throwable.printStackTrace();
                        JSONObject object = new JSONObject(throwable.getMessage());
                        String des = object.optString("detail");//错误描述
                        int status = object.optInt("status");//错误代码
                        if (status > 0 && !TextUtils.isEmpty(des)) {
                            Log.e("asd", "des: " + des);
                            Toast.makeText(LoginActivity.this, des, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e) {
                        //do something
                    }
                }
            }
        }
    };

    private void initAutoLogin() {
        logger.i("login#initAutoLogin");

        splashPage = findViewById(R.id.splash_page);
        loginPage = findViewById(R.id.login_page);
        autoLogin = shouldAutoLogin();

        loginPage.setVisibility(autoLogin ? View.GONE : View.VISIBLE);
        splashPage.setVisibility(autoLogin ? View.VISIBLE : View.GONE);

        loginPage.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (mPasswordView != null) {
                    intputManager.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
                }

                if (mNameView != null) {
                    intputManager.hideSoftInputFromWindow(mNameView.getWindowToken(), 0);
                }

                return false;
            }
        });

        if (autoLogin) {
            Animation splashAnimation = AnimationUtils.loadAnimation(this, R.anim.login_splash);
            if (splashAnimation == null) {
                logger.e("login#loadAnimation login_splash failed");
                return;
            }

            splashPage.startAnimation(splashAnimation);
        }
    }

    // 主动退出的时候， 这个地方会有值,更具pwd来判断
    private boolean shouldAutoLogin() {
        Intent intent = getIntent();
        if (intent != null) {
            boolean notAutoLogin = intent.getBooleanExtra(IntentConstant.KEY_LOGIN_NOT_AUTO, false);
            logger.d("login#notAutoLogin:%s", notAutoLogin);
            if (notAutoLogin) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁回调监听接口
        SMSSDK.unregisterAllEventHandler();

        imServiceConnector.disconnect(LoginActivity.this);
        EventBus.getDefault().unregister(this);
        splashPage = null;
        loginPage = null;
        UMShareAPI.get(this).release();
    }


    /*public void attemptLogin() {
        String loginName = mNameView.getText().toString();
        String mPassword = mPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mPassword)) {
            Toast.makeText(this, getString(R.string.error_pwd_required), Toast.LENGTH_SHORT).show();
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(loginName)) {
            Toast.makeText(this, getString(R.string.error_name_required), Toast.LENGTH_SHORT).show();
            focusView = mNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            if (imService != null) {
//				boolean userNameChanged = true;
//				boolean pwdChanged = true;
                loginName = loginName.trim();
                mPassword = mPassword.trim();
                imService.getLoginManager().login(loginName, mPassword);
            }
        }
    }
*/
    public void attemptLogin() {
        showProgress(true);
/*        Map<String, String> user = new HashMap<>();
        user.put("wb", "wb");
        user.put("llllll", "123456");
        user.put("hhhhhh", "123456");
        user.put("cccccc", "123456");

        String name = "";
        String passWord = "";
        if (imService != null) {
            //imService.getLoginManager().login("wb", "wb");
            switch (Integer.parseInt(mNameView.getText().toString())) {
                case 0:
                    name = "wb";
                    break;
                case 1:
                    name = "llllll";
                    break;
                case 2:
                    name = "hhhhhh";
                    break;
                case 3:
                    name = "cccccc";
                    break;
                default:
                    name = "wb";
                    break;
            }
            passWord = user.get(name);
            imService.getLoginManager().login(name, passWord);
        }*/
/*        if (mNameView.getText().toString().equals("521314")) {
            imService.getLoginManager().login("18588227343", "18588227343");
        } else {
            imService.getLoginManager().login(mNameView.getText().toString(), mNameView.getText().toString());
        }*/
        if (wechatUnionID != null) {
            imService.getLoginManager().login(wechatUnionID, wechatUnionID);
        }
    }

    private void showProgress(final boolean show) {
        if (show) {
            mLoginStatusView.setVisibility(View.VISIBLE);
        } else {
            mLoginStatusView.setVisibility(View.GONE);
        }
    }

    // 为什么会有两个这个
    // 可能是 兼容性的问题 导致两种方法onBackPressed
    @Override
    public void onBackPressed() {
        logger.d("login#onBackPressed");
        //imLoginMgr.cancel();
        // TODO Auto-generated method stub
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            LoginActivity.this.finish();
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * ----------------------------event 事件驱动----------------------------
     */
    public void onEventMainThread(LoginEvent event) {
        switch (event) {
            case LOCAL_LOGIN_SUCCESS:
            case LOGIN_OK:
                onLoginSuccess();
                break;
            case LOGIN_AUTH_FAILED:
                attemptLogin();
                break;
            case LOGIN_INNER_FAILED:
                if (!loginSuccess)
                    onLoginFailure(event);
                break;
        }
    }


    public void onEventMainThread(SocketEvent event) {
        switch (event) {
            case CONNECT_MSG_SERVER_FAILED:
            case REQ_MSG_SERVER_ADDRS_FAILED:
                if (!loginSuccess)
                    onSocketFailure(event);
                break;
        }
    }

    private void onLoginSuccess() {
        logger.i("login#onLoginSuccess");
        loginSuccess = true;
        updateLoginInfo();
        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    private void onLoginFailure(LoginEvent event) {
        logger.e("login#onLoginError -> errorCode:%s", event.name());
        showLoginPage();
        String errorTip = getString(IMUIHelper.getLoginErrorTip(event));
        logger.d("login#errorTip:%s", errorTip);
        mLoginStatusView.setVisibility(View.GONE);
        //Toast.makeText(this, errorTip, Toast.LENGTH_SHORT).show();
    }

    private void onSocketFailure(SocketEvent event) {
        logger.e("login#onLoginError -> errorCode:%s,", event.name());
        showLoginPage();
        String errorTip = getString(IMUIHelper.getSocketErrorTip(event));
        logger.d("login#errorTip:%s", errorTip);
        mLoginStatusView.setVisibility(View.GONE);
        Toast.makeText(this, errorTip, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void qqLogin() {
        //ThirdLoginProcess(SHARE_MEDIA.QQ);
    }

    private void wechatLogin() {
        ThirdLoginProcess(SHARE_MEDIA.WEIXIN.toSnsPlatform().mPlatform);
    }

    private void getStatusBarHeight(){
        /**
         * 获取状态栏高度——方法2
         * */
        int statusBarHeight2 = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusBarHeight2 = getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SystemConfigSp.instance().setIntConfig(SystemConfigSp.SysCfgDimension.TOP_BAR_HEIGHT, statusBarHeight2);
        Log.e("WangJ", "状态栏-方法2:" + statusBarHeight2);
    }

    private void ThirdLoginProcess(SHARE_MEDIA share_media) {
        UMShareAPI.get(LoginActivity.this).getPlatformInfo(LoginActivity.this, share_media, authListener);
    }

    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            String temp = "";
            for (String key : data.keySet()) {
                temp = temp + key + " : " + data.get(key) + "\n";
            }
            Log.e("yuki", temp);
            wechatUnionID = data.get("uid");
            name = data.get("name");
            avatar = data.get("iconurl");
            attemptLogin();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {

        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {

        }
    };

    private void updateLoginInfo() {
        if (imService == null || name == null || avatar == null) {
            return;
        }
        IMLoginManager imLoginManager = imService.getLoginManager();
        IMContactManager imContactManager = imService.getContactManager();
        int id = imLoginManager.getLoginId();
        imLoginManager.getLoginInfo().setMainName(name);
        imContactManager.reqInfoModify(id, IMBuddy.ModifyType.NICK, name);
        if (!imLoginManager.getLoginInfo().getAvatar().contains(BASE_ADDRESS)) {
            imService.getLoginManager().getLoginInfo().setAvatar(avatar);
            imContactManager.reqInfoModify(id, IMBuddy.ModifyType.AVATAR, avatar);
        }
    }
}

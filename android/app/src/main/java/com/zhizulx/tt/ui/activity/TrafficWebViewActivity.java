package com.zhizulx.tt.ui.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhizulx.tt.R;
import com.zhizulx.tt.config.IntentConstant;
import com.zhizulx.tt.ui.base.TTBaseFragmentActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class TrafficWebViewActivity extends TTBaseFragmentActivity{
    private WebView webView;
    private ImageView back;
    private TextView title;

    // https://m.flight.qunar.com/ncs/page/flightlist?depCity=重庆&arrCity=成都&goDate=2017-04-11&sort=&airLine=&from=
    // http://touch.train.qunar.com/trainList.html?startStation=杭州&endStation=西安&date=2017-04-11&searchType=stasta&bd_source=&filterTrainType=&filterTrainType=&filterTrainType=

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.traffic_web_view_activity);

        back = (ImageView) findViewById(R.id.traffic_web_view_back);
        title = (TextView) findViewById(R.id.traffic_web_view_title);
        title.setText(getIntent().getStringExtra(IntentConstant.NAME));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setDomStorageEnabled(true);
        // 设置支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
//启动缓存
        webView.getSettings().setAppCacheEnabled(true);
        //设置缓存模式
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        //加载网页
        webView.loadUrl(getIntent().getStringExtra(IntentConstant.WEBVIEW_URL));
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if( url.startsWith("http:") || url.startsWith("https:") ) {
                    return false;
                }
                return true;
            }
        });
    }
}

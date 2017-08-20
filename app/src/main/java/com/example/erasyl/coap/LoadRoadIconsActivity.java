package com.example.erasyl.coap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class LoadRoadIconsActivity extends Activity implements View.OnClickListener{

    private WebView mWebView;
    private String url = "http://java.coap.kz/coap_pdd.php?art=";
    private Button btnBackLoadRoad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_road);

        Intent intent = getIntent();
        String id = intent.getStringExtra("pddID");
        url = url + id;

        mWebView = (WebView) findViewById(R.id.wvLoadRoadIcons);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new HelloWebViewClient());

        btnBackLoadRoad = (Button) findViewById(R.id.btnBackLoadRoad);
        btnBackLoadRoad.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnBackLoadRoad) {
            finish();
        }
    }


    private class HelloWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }
}
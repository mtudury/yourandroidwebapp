package fr.coding.yourandroidwebapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import fr.coding.tools.AutoAuthSslWebView;
import fr.coding.yourandroidwebapp.settings.WebApp;
import fr.coding.yourandroidwebapp.settings.WebAppSettings;


public class WebMainActivity extends Activity {

    private WebView mWebView;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_main);

        url = "http://toutestquantique.fr/en/";

        mWebView = (WebView) findViewById(R.id.activity_main_webview);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        AutoAuthSslWebView wvc = new AutoAuthSslWebView();
        mWebView.setWebViewClient(wvc);


        String webappid = getIntent().getStringExtra("webappid");
        if ((webappid != null) && (!webappid.isEmpty())) {
            WebApp wa = WebAppSettings.getWebAppById(this, webappid);
            if (wa != null) {
                url = wa.url;
            }
        }

        mWebView.postDelayed(new Runnable() {
            @Override
            public void run() {

                LoadWebView();
            }
        }, 5);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    protected void LoadWebView() {
        mWebView.loadUrl(url);
    }

}

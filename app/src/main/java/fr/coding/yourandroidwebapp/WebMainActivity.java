package fr.coding.yourandroidwebapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebMainActivity extends Activity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_main);

        mWebView = (WebView) findViewById(R.id.activity_main_webview);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient());
        //final Context context = getApplicationContext();

        mWebView.postDelayed(new Runnable() {
            @Override
            public void run() {

                LoadWebView();
            }
        }, 100);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    protected void LoadWebView() {
        mWebView.loadUrl("http://toutestquantique.fr/en/");
    }

}

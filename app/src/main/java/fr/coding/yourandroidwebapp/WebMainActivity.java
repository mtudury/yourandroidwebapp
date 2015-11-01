package fr.coding.yourandroidwebapp;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import fr.coding.tools.AutoAuthSslWebView;
import fr.coding.tools.gdrive.GoogleDriveApiActivity;
import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsCallback;
import fr.coding.yourandroidwebapp.settings.WebApp;
import fr.coding.yourandroidwebapp.settings.AppSettingsManager;


public class WebMainActivity extends GoogleDriveApiActivity implements AppSettingsCallback {

    private WebView mWebView;

    private String url;

    private String webAppId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_main);

        url = "http://toutestquantique.fr/en/";
        webAppId = getIntent().getStringExtra("webappid");

        mWebView = (WebView) findViewById(R.id.activity_main_webview);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        AutoAuthSslWebView wvc = new AutoAuthSslWebView();
        mWebView.setWebViewClient(wvc);

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);

        AppSettingsManager settingsManager = new AppSettingsManager();
        settingsManager.LoadSettings(this.getGoogleApiClient(), this, this);
    }

    @Override
    public void onAppSettingsReady(AppSettings settings) {

        if ((webAppId != null) && (!webAppId.isEmpty())) {
            WebApp wa = settings.getWebAppById(webAppId);
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

package fr.coding.yourandroidwebapp;

import android.app.Activity;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import fr.coding.tools.AutoAuthSslWebView;
import fr.coding.tools.gdrive.GoogleDriveApiActivity;
import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsCallback;
import fr.coding.yourandroidwebapp.settings.WebApp;
import fr.coding.yourandroidwebapp.settings.AppSettingsManager;


public class WebMainActivity extends Activity {

    private WebView mWebView;

    private String url;

    private String webAppId;

    private boolean needLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_main);

        final Activity webactivity = this;

        url = "http://toutestquantique.fr/en/";
        webAppId = getIntent().getStringExtra("webappid");

        mWebView = (WebView) findViewById(R.id.activity_main_webview);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                webactivity.setProgress(progress * 1000);
            }
        });

        // Force links and redirects to open in the WebView instead of in a browser
        AutoAuthSslWebView wvc = new AutoAuthSslWebView();
        mWebView.setWebViewClient(wvc);


        AppSettingsManager settingsManager = new AppSettingsManager(this);
        AppSettings settings = settingsManager.LoadSettingsLocally();

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

                needLoad = false;
            }
        }, 5);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //todo: add wifi/mobile test here
    }

    protected void LoadWebView() {
        mWebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
            finish();
        }
    }

}

package fr.coding.yourandroidwebapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

import fr.coding.tools.AutoAuthSslWebView;
import fr.coding.tools.Callback;
import fr.coding.tools.model.HostAuth;
import fr.coding.tools.model.SslByPass;
import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsActivityHelper;
import fr.coding.yourandroidwebapp.settings.AppSettingsCallback;
import fr.coding.yourandroidwebapp.settings.WebApp;
import fr.coding.yourandroidwebapp.settings.AppSettingsManager;


public class WebMainActivity extends Activity {

    private WebView mWebView;

    private String url;

    private String webAppId;

    private boolean needLoad = true;

    private AppSettingsManager settingsManager;

    private static final String TAG = "WebMainActivity";

    protected AppSettingsActivityHelper coreActivity = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_main);

        final Activity webActivity = this;

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
                webActivity.setProgress(progress * 1000);
            }
        });

        // Force links and redirects to open in the WebView instead of in a browser
        AutoAuthSslWebView wvc = new AutoAuthSslWebView();
        mWebView.setWebViewClient(wvc);


        settingsManager = new AppSettingsManager(this);
        AppSettings settings = settingsManager.LoadSettingsLocally();

        wvc.sslUnknownManager = new Callback<SslByPass>() {
            @Override
            public void onCallback(final SslByPass arg) {
                if (coreActivity == null) {
                    coreActivity = new AppSettingsActivityHelper(webActivity, new AppSettingsCallback() {
                        @Override
                        public void onAppSettingsReady(AppSettings settings) {
                            boolean found = false;
                            for(SslByPass ssl : settings.SslByPasses) {
                                if ((ssl.Host.equals(arg.Host))
                                &&(ssl.CName.equals(arg.CName))
                                &&(ssl.ValidNotAfter == arg.ValidNotAfter))
                                    found = true;
                            }
                            if (!found) {
                                settings.SslByPasses.add(arg);
                                coreActivity.Save(settings);
                            }

                            mWebView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(webActivity, SSLSettingListActivity.class);
                                    startActivity(intent);
                                }
                            }, 2000);
                        }
                    }, TAG);
                    coreActivity.onResume();
                }
                else
                {
                    coreActivity.getSettings();
                }
            }
        };

        if ((webAppId != null) && (!webAppId.isEmpty())) {
            WebApp wa = settings.getWebAppById(webAppId);
            wvc.setCompleteByPass(wa.allCertsByPass);
            if (wa.allowedSSlActivated) {
                wvc.setSSLAllowed(settings.SslByPasses);
            }
            if (wa.autoAuth) {
                //wvc.setAllowedHosts(settings.Auths);
            }

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
        if (coreActivity != null)
            coreActivity.onResume();
    }

    /**
     * Handles resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (coreActivity != null)
            coreActivity.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Called when activity gets invisible. Connection to Drive service needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onPause() {
        if (coreActivity != null)
            coreActivity.onPause();
        super.onPause();
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

package fr.coding.yourandroidwebapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUriExposedException;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.net.URLConnection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import fr.coding.tools.AutoAuthSslWebView;
import fr.coding.tools.Callback;
import fr.coding.tools.CallbackResult;
import fr.coding.tools.Perms;
import fr.coding.tools.gdrive.GoogleDriveCoreActivity;
import fr.coding.tools.model.HostAuth;
import fr.coding.tools.model.SslByPass;
import fr.coding.tools.networks.NetworkChangeEvent;
import fr.coding.tools.networks.NetworkChangeReceiver;
import fr.coding.tools.networks.Wifi;
import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsActivityHelper;
import fr.coding.yourandroidwebapp.settings.AppSettingsCallback;
import fr.coding.yourandroidwebapp.settings.WebApp;
import fr.coding.yourandroidwebapp.settings.AppSettingsManager;


public class WebMainActivity extends Activity implements NetworkChangeEvent {

    private WebView mWebView;
    private AutoAuthSslWebView wvc;

    private String url;

    private String webAppId;

    private boolean needLoad = true;
    private boolean lastContextAlternate;
    private boolean lastContextAlternateNotConnect;
    private AppSettings settings;

    private AppSettingsManager settingsManager;

    private static final String TAG = "WebMainActivity";

    protected AppSettingsActivityHelper coreActivity = null;

    protected NetworkChangeReceiver networkChangeReceiver = null;

    private WebApp wa;

    private HostAuth hostAuth;

    private boolean showprogressbar;

    protected static final int QUERY_PERMS_READSD = GoogleDriveCoreActivity.NEXT_AVAILABLE_REQUEST_CODE + 100;
    protected static final int QUERY_PERMS_GPS = QUERY_PERMS_READSD + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        if (AppSettingsManager.KeepTheScreenOn(this))
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_main);

        final WebMainActivity webActivity = this;

        url = "file:///android_asset/default.html";
        webAppId = getIntent().getStringExtra("webappid");

        mWebView = (WebView) findViewById(R.id.activity_main_webview);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAppCachePath(getCacheDir().getPath());
        webSettings.setAppCacheEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        wvc = new AutoAuthSslWebView();
        mWebView.setWebViewClient(wvc);

        showprogressbar = AppSettingsManager.ShowProgressBar(this);

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                if (showprogressbar) {
                    ProgressBar pb = ((ProgressBar) webActivity.findViewById(R.id.viewprogress));
                    if (pb != null) {
                        int visibility = View.GONE;
                        if (progress < 100)
                            visibility = View.VISIBLE;
                        pb.setProgress(progress);
                        pb.setVisibility(visibility);
                    }
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                // Always grant permission since the app itself requires location
                // permission and the user has therefore already granted it
                if (settings.Advanced.allowGeoloc) {
                    if (!Perms.checkGPSPermissions(webActivity)) {
                        Perms.requestGPSPermissions(webActivity, QUERY_PERMS_GPS);
                    }
                }

                callback.invoke(origin, settings.Advanced.allowGeoloc, false);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (AppSettingsManager.IsRemoteDebuggingActivated(this))
                WebView.setWebContentsDebuggingEnabled(true);
        }

        settingsManager = new AppSettingsManager(this);
        settings = settingsManager.LoadSettingsLocally();

        if (settings.Advanced.disableMediasRequireUserGesture){
            mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }

        if ((settings.Advanced.userAgent != null)&&(!settings.Advanced.userAgent.isEmpty())) {
            mWebView.getSettings().setUserAgentString(settings.Advanced.userAgent);
        }

        if (settings.Advanced.disableMediasRequireUserGesture){
            mWebView.getSettings().setGeolocationEnabled(settings.Advanced.disableMediasRequireUserGesture);
        }

        mWebView.setDownloadListener((String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) -> {
                Intent downloadviewer = new Intent(Intent.ACTION_VIEW);
                downloadviewer.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri uri = Uri.parse(url);

                if (TextUtils.isEmpty(mimetype)) {
                    mimetype = URLConnection.guessContentTypeFromName(url);
                }
                if (TextUtils.isEmpty(mimetype)) {
                    downloadviewer.setData(uri);
                } else {
                    downloadviewer.setDataAndType(uri, mimetype);
                }

                try {
                    if (!settings.Advanced.forceDownloadViewerChooser) {
                        startActivity(downloadviewer);
                    } else {
                        Intent downloadviewerchooser = Intent.createChooser(downloadviewer, getResources().getString(R.string.download_chooser_title));
                        downloadviewerchooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(downloadviewerchooser);
                    }
                } catch(RuntimeException fue) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (fue instanceof FileUriExposedException) {
                            Uri contentUri = FileProvider.getUriForFile(this, "fr.coding.yourandroidwebapp.fileProvider", new File(Uri.parse(url).getPath()));
                            downloadviewer.setData(contentUri);
                            if (!settings.Advanced.forceDownloadViewerChooser) {
                                startActivity(downloadviewer);
                            } else {
                                Intent downloadviewerchooser = Intent.createChooser(downloadviewer, getResources().getString(R.string.download_chooser_title));
                                downloadviewerchooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(downloadviewerchooser);
                            }
                        } else {
                            Toast.makeText(this, fue.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, fue.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        wvc.sslUnknownManager = new Callback<SslByPass>() {
            @Override
            public void onCallback(final SslByPass arg) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                webActivity.SaveAcceptedSSlChoice(arg);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                needLoad = true;
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(webActivity);
                builder.setMessage("Allow this SSl Cert : " + arg.CName + ", " + arg.Host).setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        };

        wvc.AuthAsked = new CallbackResult<HostAuth, HostAuth>() {
            @Override
            public HostAuth onCallback(final HostAuth arg) {
                if (webActivity.hostAuth != null) {
                    HostAuth auth = webActivity.hostAuth;
                    webActivity.hostAuth = null;
                    return auth;
                }


                final Dialog login = new Dialog(webActivity);
                login.setContentView(R.layout.login_dialog);
                login.setTitle("Login to " + arg.Host);

                Button btnLogin = (Button) login.findViewById(R.id.btnLogin);
                Button btnCancel = (Button) login.findViewById(R.id.btnCancel);

                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arg.Login = ((EditText) login.findViewById(R.id.txtUsername)).getText().toString();
                        arg.Password = ((EditText) login.findViewById(R.id.txtPassword)).getText().toString();
                        webActivity.hostAuth = arg;
                        login.dismiss();
                        LoadWebView();

                        Toast.makeText(webActivity,
                                "Try Login", Toast.LENGTH_LONG).show();

                        if (((CheckBox) login.findViewById(R.id.savePassword)).isChecked()) {
                            webActivity.SaveAcceptedHostAuth(arg);
                        }
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        login.dismiss();
                    }
                });

                login.show();

                return null;
            }
        };

        long autoRefreshDelay = AppSettingsManager.AutoRefreshRate(this)*1000*60;
        if (autoRefreshDelay>0) {
            Timer timer = new Timer();
            class AutoRefresh extends TimerTask {

                @Override
                public void run() {
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            LoadWebView();
                        }});
                }

            }
            AutoRefresh ar = new AutoRefresh();
            timer.schedule(ar, autoRefreshDelay, autoRefreshDelay);
        }

        if ((webAppId != null) && (webAppId.equals( "CLEAR_CACHE"))) {
            mWebView.clearCache(true);
            Toast.makeText(this, "Cache Cleared", Toast.LENGTH_LONG).show();
            finish();
        } else {
            LoadWebViewSettings(settings);
        }

        // create receiver, will be registered in onResume
        networkChangeReceiver = new NetworkChangeReceiver();
        networkChangeReceiver.eventReceiver = this;
    }

    protected boolean isAlternateContext(WebApp webapp) {
        if (TextUtils.isEmpty(webapp.alternateSSIDs))
            return false;
        if (TextUtils.isEmpty(webapp.alternateUrl))
            return false;


        if (!Perms.checkGPSPermissions(this)) {
            Toast.makeText(this, "From latest android version I need Location Permission to get Wifi SSID", Toast.LENGTH_LONG).show();
            Perms.requestGPSPermissions(this, QUERY_PERMS_GPS);
        }

        return Wifi.isOnlineAndWifi(this) && Wifi.isInSSIDList(this, webapp.alternateSSIDs);
    }

    protected boolean isNotConnectedContext(WebApp webapp) {
        if (TextUtils.isEmpty(webapp.alternateUrlNotConnected))
            return false;

        return !Wifi.isOnline(this);
    }

    protected void LoadWebViewSettings(AppSettings settings) {
        if (!TextUtils.isEmpty(webAppId)) {
            wa = settings.getWebAppById(webAppId);
            if (wa != null) {
                wvc.setCompleteByPass(wa.allCertsByPass);
                if (wa.allowedSSlActivated) {
                    wvc.setSSLAllowed(settings.SslByPasses);
                }
                if (wa.autoAuth) {
                    wvc.setAllowedHosts(settings.HostAuths);
                }
                mWebView.getSettings().setCacheMode(wa.cacheMode);
                if (wa.pinchZoomMode > WebApp.PinchZoomMode_None) {
                    mWebView.getSettings().setBuiltInZoomControls(true);
                    mWebView.getSettings().setDisplayZoomControls(wa.pinchZoomMode == WebApp.PinchZoomMode_WithControls);
                } else
                {
                    mWebView.getSettings().setBuiltInZoomControls(false);
                }
            } else {
                Toast.makeText(this, "This WebAppId does not exist (no more?)", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void reloadIfNeeded() {
        if (wa != null) {
            boolean newContext = isAlternateContext(wa);
            boolean newContext2 = isNotConnectedContext(wa);
            if ((newContext != lastContextAlternate)||(newContext2 != lastContextAlternateNotConnect)||(needLoad)) {
                lastContextAlternate = newContext;
                lastContextAlternateNotConnect = newContext2;
                if (!TextUtils.isEmpty(wa.url))
                    url = wa.url;
                if (lastContextAlternate) {
                    url = wa.alternateUrl;
                }
                if (lastContextAlternateNotConnect) {
                    url = wa.alternateUrlNotConnected;
                }


                // check local uri and ask for rights if needed
                if (!TextUtils.isEmpty(url)) {
                    Uri parsedUri = Uri.parse(url);
                    if (parsedUri.getScheme().equalsIgnoreCase("file")||parsedUri.getScheme().equalsIgnoreCase("content")) {
                        if (!Perms.checkReadSDPermission(this)) {
                            Perms.requestReadSDPermission(this, QUERY_PERMS_READSD);
                        }
                    }
                }

                LoadWebView();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (coreActivity != null)
            coreActivity.onResume();
        if (mWebView != null) {
            mWebView.onResume();
            mWebView.resumeTimers();
        }
        reloadIfNeeded();

        // refresh local settings (used as cache when using gdrive) once by week
        if (AppSettingsManager.IsSettingsInGdrive(this)&&(AppSettingsManager.GetLastUpdatedFromGDrive(this).getTime() < new Date().getTime()-(7*24*60*60*1000))) {
            UpdateLocalConfig();
        }

        IntentFilter connectivityChange = new IntentFilter();
        connectivityChange.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityChange.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        connectivityChange.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        this.registerReceiver(networkChangeReceiver, connectivityChange);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LoadWebView();
        }
    }
    /**
     * Called when activity gets invisible. Connection to Drive service needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onPause() {
        if (coreActivity != null)
            coreActivity.onPause();
        if (mWebView != null) {
            mWebView.onPause();
            mWebView.pauseTimers();
        }
        unregisterReceiver(networkChangeReceiver);
        super.onPause();
    }

    protected void LoadWebView() {
        mWebView.loadUrl(url);
        needLoad = false;
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

    protected void SaveAcceptedSSlChoice(final SslByPass arg) {
        final WebMainActivity webActivity = this;
        arg.activated = true;

        AppSettingsCallback SaveSSLCallback = new AppSettingsCallback() {
            @Override
            public void onAppSettingsReady(AppSettings settings) {
                boolean found = false;
                for (SslByPass ssl : settings.SslByPasses) {
                    if ((ssl.Host.equals(arg.Host))
                            && (ssl.CName.equals(arg.CName))
                            && (ssl.ValidNotAfter == arg.ValidNotAfter)) {
                        found = true;
                        ssl.activated = true;
                    }
                }
                if (!found) {
                    settings.SslByPasses.add(arg);
                }
                settings.getWebAppById(webAppId).allowedSSlActivated = true;
                coreActivity.Save(settings);

                LoadWebViewSettings(settings);
            }
        };

        if (coreActivity == null) {
            coreActivity = new AppSettingsActivityHelper(this, SaveSSLCallback, TAG);
            coreActivity.onResume();
        } else {
            coreActivity.getSettings(SaveSSLCallback);
        }
    }

    protected void SaveAcceptedHostAuth(final HostAuth arg) {
        final WebMainActivity webActivity = this;
        arg.activated = true;

        AppSettingsCallback SaveAcceptedHostAuth = new AppSettingsCallback() {
            @Override
            public void onAppSettingsReady(AppSettings settings) {
                boolean found = false;
                for (HostAuth hostAuth : settings.HostAuths) {
                    if (hostAuth.Host.equals(arg.Host)) {
                        found = true;
                        hostAuth.Login = arg.Login;
                        hostAuth.Password = arg.Password;
                    }
                }
                if (!found) {
                    settings.HostAuths.add(arg);
                }
                settings.getWebAppById(webAppId).autoAuth = true;
                coreActivity.Save(settings);
            }
        };

        if (coreActivity == null) {
            coreActivity = new AppSettingsActivityHelper(webActivity, SaveAcceptedHostAuth, TAG);
            coreActivity.onResume();
        } else {
            coreActivity.getSettings(SaveAcceptedHostAuth);
        }
    }

    protected void UpdateLocalConfig() {
        final WebMainActivity webActivity = this;

        AppSettingsCallback DoNothingCallback = new AppSettingsCallback() {
            @Override
            public void onAppSettingsReady(AppSettings settings) {
            }
        };

        if (coreActivity == null) {
            coreActivity = new AppSettingsActivityHelper(webActivity, DoNothingCallback, TAG);
            coreActivity.onResume();
        } else {
            coreActivity.getSettings(DoNothingCallback);
        }
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus&&AppSettingsManager.ImmersiveFullscreenMode(this)) {
            mWebView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    @Override
    public void networkChangeEvent(Intent event) {
        reloadIfNeeded();
    }
}

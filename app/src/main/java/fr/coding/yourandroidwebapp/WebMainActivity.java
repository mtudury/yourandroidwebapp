package fr.coding.yourandroidwebapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import fr.coding.tools.AutoAuthSslWebView;
import fr.coding.tools.Callback;
import fr.coding.tools.CallbackResult;
import fr.coding.tools.model.HostAuth;
import fr.coding.tools.model.SslByPass;
import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsActivityHelper;
import fr.coding.yourandroidwebapp.settings.AppSettingsCallback;
import fr.coding.yourandroidwebapp.settings.WebApp;
import fr.coding.yourandroidwebapp.settings.AppSettingsManager;


public class WebMainActivity extends Activity {

    private WebView mWebView;
    private AutoAuthSslWebView wvc;

    private String url;

    private String webAppId;

    private boolean needLoad = true;

    private AppSettingsManager settingsManager;

    private static final String TAG = "WebMainActivity";

    protected AppSettingsActivityHelper coreActivity = null;

    private WebApp wa;

    private HostAuth hostAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_main);

        final WebMainActivity webActivity = this;

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
        wvc = new AutoAuthSslWebView();
        mWebView.setWebViewClient(wvc);


        settingsManager = new AppSettingsManager(this);
        AppSettings settings = settingsManager.LoadSettingsLocally();

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

        if (webAppId.equals( "CLEAR_CACHE")) {
            mWebView.clearCache(true);
            Toast.makeText(this, "Cache Cleared", Toast.LENGTH_LONG).show();
            finish();
        } else {
            LoadWebViewSettings(settings);
        }
    }

    protected void LoadWebViewSettings(AppSettings settings) {
        if ((webAppId != null) && (!webAppId.isEmpty())) {
            wa = settings.getWebAppById(webAppId);
            if (wa != null) {
                wvc.setCompleteByPass(wa.allCertsByPass);
                if (wa.allowedSSlActivated) {
                    wvc.setSSLAllowed(settings.SslByPasses);
                }
                if (wa.autoAuth) {
                    wvc.setAllowedHosts(settings.HostAuths);
                }
                url = wa.url;
            } else {
                Toast.makeText(this, "This WebAppId does not exist", Toast.LENGTH_LONG).show();
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
        if (mWebView != null) {
            mWebView.onResume();
            mWebView.resumeTimers();
        }
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
        if (mWebView != null) {
            mWebView.onPause();
            mWebView.pauseTimers();
        }
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

    protected void SaveAcceptedSSlChoice(final SslByPass arg) {
        final WebMainActivity webActivity = this;
        arg.activated = true;

        if (coreActivity == null) {
            coreActivity = new AppSettingsActivityHelper(this, new AppSettingsCallback() {
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
            }, TAG);
            coreActivity.onResume();
        } else {
            coreActivity.getSettings();
        }
    }

    protected void SaveAcceptedHostAuth(final HostAuth arg) {
        final WebMainActivity webActivity = this;
        arg.activated = true;

        if (coreActivity == null) {
            coreActivity = new AppSettingsActivityHelper(webActivity, new AppSettingsCallback() {
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
            }, TAG);
            coreActivity.onResume();
        } else {
            coreActivity.getSettings();
        }
    }

}

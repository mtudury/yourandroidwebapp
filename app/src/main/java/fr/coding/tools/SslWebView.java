package fr.coding.tools;

import android.net.http.SslError;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Matthieu on 03/10/2015.
 */
public class SslWebView extends WebViewClient {

    protected String sslAllowedCName;
    public void setSSLAllowedCName(String cname) {
        sslAllowedCName = cname;
    }

    public String getSSLAllowedCName() {
        return sslAllowedCName;
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        if (error.getCertificate().getIssuedBy().getCName().equals(sslAllowedCName) && (error.getPrimaryError() == SslError.SSL_UNTRUSTED)&& (!error.hasError(SslError.SSL_IDMISMATCH))&& (!error.hasError(SslError.SSL_EXPIRED)))
            handler.proceed(); // Ignore SSL certificate errors
    }
}

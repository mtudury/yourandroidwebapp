package fr.coding.tools;

import android.net.http.SslError;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.List;

import fr.coding.tools.model.HostAuth;
import fr.coding.tools.model.SslByPass;

/**
 * Created by Matthieu on 03/10/2015.
 *
 * This webview ignore self signed Certificates if provided CNAME is equal to configured ONE or complete bypass is defined.
 * !!! Warning !!! checking CNAME, will reduce the risk of man in the middle attack, but this is not really security.
 *
 * This Webview should be considered insecure.
 * Use at your own risks
 *
 */
public class SslWebView extends ErrorHandledWebView {

    // if using this
    protected boolean completeByPass = false;
    public void setCompleteByPass(boolean bypass) {
        completeByPass = bypass;
    }

    public boolean getCompleteByPass() {
        return completeByPass;
    }


    protected List<SslByPass> sslAllowed;
    public void setSSLAllowed(List<SslByPass> ssl) {
        sslAllowed = ssl;
    }

    public List<SslByPass> getSSLAllowed() {
        return sslAllowed;
    }


    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

        // totally insecure
        if (completeByPass) {
            handler.proceed();
            return;
        }
        if (sslAllowed != null) {
            for (SslByPass sbp :
                    sslAllowed) {
                // a little bit less insecure
                if (error.getCertificate().getIssuedBy().getCName().equals(sbp.CName)
                        && (error.getCertificate().hashCode() == sbp.hashCode)
                        && (error.getPrimaryError() == SslError.SSL_UNTRUSTED)
                        && (!error.hasError(SslError.SSL_IDMISMATCH)) && (!error.hasError(SslError.SSL_EXPIRED))) {
                    handler.proceed(); // Ignore SSL certificate errors
                    return;
                }
            }
        }

        // Not Handled print error
        Toast.makeText(view.getContext(), "SSL not accepted CNAME : "+ error.getCertificate().getIssuedBy().getCName() + ", hashcode : "+error.getCertificate().hashCode(), Toast.LENGTH_LONG).show();

    }
}

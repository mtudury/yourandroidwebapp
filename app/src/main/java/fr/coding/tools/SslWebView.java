package fr.coding.tools;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

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

    public Callback<SslByPass> sslUnknownManager;

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

        try {
            String host = new URL(error.getUrl()).getHost().toLowerCase();

            // totally insecure
            if (completeByPass) {
                handler.proceed();
                return;
            }
            if (sslAllowed != null) {
                for (SslByPass sbp :
                        sslAllowed) {
                    // a little bit less insecure, but still insecure

                    if (error.getCertificate().getIssuedBy().getCName().equals(sbp.CName)
                            && (host.equals(sbp.Host))
                            && (error.getCertificate().getValidNotAfterDate().getTime() == sbp.ValidNotAfter)
                            && (error.getPrimaryError() == SslError.SSL_UNTRUSTED)
                            && (!error.hasError(SslError.SSL_IDMISMATCH)) && (!error.hasError(SslError.SSL_EXPIRED))) {
                        if (sbp.activated)
                            handler.proceed(); // Ignore SSL certificate errorse
                        else
                            Toast.makeText(view.getContext(), "SSL Certificate " + error.getCertificate().getIssuedBy().getCName() + " not allowed", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }

            if (!error.hasError(SslError.SSL_IDMISMATCH) && (!error.hasError(SslError.SSL_EXPIRED))) {
                // Not Handled print error
                Toast.makeText(view.getContext(), "SSL not accepted CNAME : " + error.getCertificate().getIssuedBy().getCName() + ", hashcode : " + error.getCertificate().hashCode(), Toast.LENGTH_LONG).show();

                if (sslUnknownManager != null) {
                    SslByPass byPass = new SslByPass();
                    byPass.Host = host;
                    byPass.CName = error.getCertificate().getIssuedBy().getCName();
                    byPass.ValidNotAfter = error.getCertificate().getValidNotAfterDate().getTime();
                    byPass.dtCreated = new Date();
                    sslUnknownManager.onCallback(byPass);
                }
            } else {
                Toast.makeText(view.getContext(), "SSL Cert : " + error.getCertificate().getIssuedBy().getCName() + " not accepted, SSL error : " + error.toString(), Toast.LENGTH_LONG).show();
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }
}

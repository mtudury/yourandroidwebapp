package fr.coding.tools;

import android.webkit.HttpAuthHandler;
import android.webkit.WebView;

import java.util.Date;
import java.util.List;
import fr.coding.tools.model.HostAuth;
import fr.coding.tools.model.SslByPass;

/**
 * Created by Matthieu on 03/10/2015.
 */
public class AutoAuthSslWebView extends SslWebView {

    public CallbackResult<HostAuth, HostAuth> AuthAsked;

    protected List<HostAuth> allowedHosts;
    public void setAllowedHosts(List<HostAuth> hosts) {
        allowedHosts = hosts;
    }

    public List<HostAuth> getAllowedHosts() {
        return allowedHosts;
    }

    public long AutoTestedDate = 0;

    @Override
    public void onReceivedHttpAuthRequest(WebView webView, HttpAuthHandler handler, String host, String realm) {
        if ((allowedHosts != null)&&(AutoTestedDate < (new Date().getTime()-(15000)))) {
            for (HostAuth ha :
                    allowedHosts) {
                if (host.equals(ha.Host)) {
                    AutoTestedDate = new Date().getTime();
                    handler.proceed(ha.Login, ha.Password);
                    return;
                }
            }
        }
        if (AuthAsked != null) {
            HostAuth hostAuth = new HostAuth();
            hostAuth.Host = host;
            HostAuth ret = AuthAsked.onCallback(hostAuth);
            if (ret != null) {
                handler.proceed(ret.Login, ret.Password);
                return;
            }
        }
        super.onReceivedHttpAuthRequest(webView, handler, host, realm);
    }
}

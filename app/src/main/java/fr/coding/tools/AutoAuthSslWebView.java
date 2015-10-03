package fr.coding.tools;

import android.webkit.HttpAuthHandler;
import android.webkit.WebView;
import java.util.List;
import fr.coding.tools.model.HostAuth;

/**
 * Created by Matthieu on 03/10/2015.
 */
public class AutoAuthSslWebView extends SslWebView {

    protected List<HostAuth> allowedHosts;
    public void setAllowedHosts(List<HostAuth> hosts) {
        allowedHosts = hosts;
    }

    public List<HostAuth> getAllowedHosts() {
        return allowedHosts;
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView webView, HttpAuthHandler handler, String host, String realm) {
        for (HostAuth ha :
                allowedHosts) {
            if (host.equals(ha.Host)) {
                handler.proceed(ha.Login, ha.Password);
                return;
            }
        }
        super.onReceivedHttpAuthRequest(webView, handler, host, realm);
    }
}

package fr.coding.tools;

import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by Matthieu on 03/10/2015.
 */
public class ErrorHandledWebView extends WebViewClient {
    private boolean showErrors = true;

    public boolean isShowErrors() {
        return showErrors;
    }

    public void setShowErrors(boolean showErrors) {
        this.showErrors = showErrors;
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
        super.onReceivedError(view, req, rerr);
        if (isShowErrors()) {
            Toast.makeText(view.getContext(), "Error in WebApp : " + rerr.getDescription() + ", Url : " + req.getUrl() + ", code : " + rerr.getErrorCode(), Toast.LENGTH_LONG).show();
        }
    }


}

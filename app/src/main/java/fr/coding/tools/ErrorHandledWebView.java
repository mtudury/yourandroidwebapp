package fr.coding.tools;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by Matthieu on 03/10/2015.
 */
public class ErrorHandledWebView extends WebViewClient {

    @Override
    public void onReceivedError(WebView view, int errorCode,
                                String description, String failingUrl) {
        Toast.makeText(view.getContext(), "Error in WebApp : "+description+", Url : "+failingUrl + ", code : "+errorCode, Toast.LENGTH_LONG).show();
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

}

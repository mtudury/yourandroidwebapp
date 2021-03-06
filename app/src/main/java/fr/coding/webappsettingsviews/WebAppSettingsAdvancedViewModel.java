package fr.coding.webappsettingsviews;

import androidx.lifecycle.ViewModel;
import fr.coding.yourandroidwebapp.settings.WebApp;

public class WebAppSettingsAdvancedViewModel extends ViewModel {
    private WebApp wa = null;

    public WebApp getWebApp() {
        return wa;
    }

    public boolean setWebApp(WebApp webApp) {
        if (wa == null) {
            wa = webApp;
            return true;
        }
        return  false;
    }
}

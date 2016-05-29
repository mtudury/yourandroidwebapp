package fr.coding.yourandroidwebapp.settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.coding.tools.model.HostAuth;
import fr.coding.tools.model.SslByPass;

/**
 * Created by Matthieu on 31/10/2015.
 */
public class AppSettings {


    public List<WebApp> WebApps;
    public List<SslByPass> SslByPasses;
    public List<HostAuth> HostAuths;

    public AdvancedAppSettings Advanced;

    public AppSettings()
    {
        WebApps = new ArrayList<>();
        SslByPasses = new ArrayList<>();
        HostAuths = new ArrayList<>();
    }

    public WebApp getWebAppById(String id) {
        for (WebApp webApp :
                WebApps) {
            if (webApp.id.equals(id))
                return webApp;
        }
        return null;
    }


    public WebApp getWebAppByName(String name) {
        for (WebApp webApp :
                WebApps) {
            if (webApp.name.equals(name))
                return webApp;
        }
        return null;
    }

    public static AppSettings JSONobjToAppSettings(JSONObject jsonobj) throws JSONException {
        AppSettings appSettings = new AppSettings();
        JSONArray arr = jsonobj.getJSONArray("WebApps");
        for (int i = 0; i < arr.length(); i++) {
            appSettings.WebApps.add(WebApp.JSONobjToWebApp(arr.getJSONObject(i)));
        }
        if (jsonobj.has("SslByPasses")) {
            arr = jsonobj.getJSONArray("SslByPasses");
            for (int i = 0; i < arr.length(); i++) {
                appSettings.SslByPasses.add(SslByPass.JSONobjToSslByPass(arr.getJSONObject(i)));
            }
        }
        if (jsonobj.has("HostAuths")) {
            arr = jsonobj.getJSONArray("HostAuths");
            for (int i = 0; i < arr.length(); i++) {
                appSettings.HostAuths.add(HostAuth.JSONobjToSslByPass(arr.getJSONObject(i)));
            }
        }
        if (jsonobj.has("advanced"))
        {
            appSettings.Advanced = AdvancedAppSettings.JSONobjToWebApp(jsonobj.getJSONObject("advanced"));
        }

        return appSettings;
    }

    public JSONObject AppSettingsToJSONobj() throws JSONException {
        JSONObject jsonobj = new JSONObject();
        JSONArray webapps = new JSONArray();
        for(WebApp wa : WebApps) {
            webapps.put(wa.toJSONobj());
        }
        jsonobj.put("WebApps", webapps);

        JSONArray sslByPasses = new JSONArray();
        for(SslByPass ssl : SslByPasses) {
            sslByPasses.put(ssl.toJSONobj());
        }
        jsonobj.put("SslByPasses", sslByPasses);

        JSONArray hostAuths = new JSONArray();
        for(HostAuth hostAuth : HostAuths) {
            hostAuths.put(hostAuth.toJSONobj());
        }
        jsonobj.put("HostAuths", hostAuths);

        if (Advanced != null) {
            jsonobj.put("advanced", Advanced.toJSONobj());
        }

        return jsonobj;
    }

}

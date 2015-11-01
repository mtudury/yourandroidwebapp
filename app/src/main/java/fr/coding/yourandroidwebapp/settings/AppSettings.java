package fr.coding.yourandroidwebapp.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import fr.coding.tools.RetrieveHttpFile;
import fr.coding.tools.gdrive.GoogleDriveApiActivity;
import fr.coding.yourandroidwebapp.R;

/**
 * Created by Matthieu on 31/10/2015.
 */
public class AppSettings {

    public List<WebApp> WebApps;

    public AppSettings()
    {
        WebApps = new ArrayList<>();
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
        return appSettings;
    }

    public JSONObject AppSettingsToJSONobj() throws JSONException {
        JSONObject jsonobj = new JSONObject();
        JSONArray webapps = new JSONArray();
        for(WebApp wa : WebApps) {
            webapps.put(wa.WebAppToJSONobj());
        }
        jsonobj.put("WebApps", webapps);
        return jsonobj;
    }

}

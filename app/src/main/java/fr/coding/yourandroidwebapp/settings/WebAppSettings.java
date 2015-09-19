package fr.coding.yourandroidwebapp.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthieu on 13/09/2015.
 */
public class WebAppSettings {
    public static final String PREFS = "WebApps";

    public static List<WebApp> getWebApps(Activity activity)
    {
        List<WebApp> webapps = new ArrayList<WebApp>();
        SharedPreferences prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int count = prefs.getInt("count",0);
        if (count > 0) {
            for(Integer i = 0; i < count; i++) {
                try {
                    JSONObject jsonobj = new JSONObject(prefs.getString("webapp"+i.toString(), "{}"));
                    WebApp webapp = JSONobjToWebApp(jsonobj);
                    webapps.add(webapp);
                } catch (JSONException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(activity).setTitle("ErrorLoadingSettings").setMessage(e.toString()).setNeutralButton("Close", null).show();
                }
            }
        }


        return webapps;
    }

    private static WebApp JSONobjToWebApp(JSONObject jsonobj) throws JSONException {
        WebApp webapp = new WebApp();
        webapp.id = jsonobj.getString("id");
        webapp.name = jsonobj.getString("name");
        webapp.iconUrl = jsonobj.getString("iconUrl");
        webapp.url = jsonobj.getString("url");
        return webapp;
    }

    private static JSONObject WebAppToJSONobj(WebApp webapp) throws JSONException {
        JSONObject jsonobj = new JSONObject();
        jsonobj.put("id", webapp.id);
        jsonobj.put("name", webapp.name);
        jsonobj.put("iconUrl", webapp.iconUrl);
        jsonobj.put("url", webapp.url);
        return jsonobj;
    }

    public static void InsertOrUpdate(Activity activity, WebApp webApp) {
        try {
            boolean added = false;
            int count = 0;
            SharedPreferences prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            if ((webApp.id==null)||(webApp.id.length()==0)) {
                count = prefs.getInt("count",0);
                webApp.id = Integer.toString(count);
                added = true;
                count++;
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("webapp" + webApp.id, WebAppToJSONobj(webApp).toString());
            if (added) {
                editor.putInt("count", count);
            }
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
            new AlertDialog.Builder(activity).setTitle("ErrorLoadingSettings").setMessage(e.toString()).setNeutralButton("Close", null).show();
        }
    }

}

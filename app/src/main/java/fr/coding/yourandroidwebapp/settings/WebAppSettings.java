package fr.coding.yourandroidwebapp.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.coding.yourandroidwebapp.R;
import fr.coding.yourandroidwebapp.WebMainActivity;

/**
 * Created by Matthieu on 13/09/2015.
 */
public class WebAppSettings {
    public static final String PREFS = "WebApps";

    public static List<WebApp> getWebApps(Activity activity)
    {
        List<WebApp> webapps = new ArrayList<>();
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
        if (jsonobj.has("iconUrl"))
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

    public static WebApp getWebAppById(Activity activity, String id) {
        List<WebApp> webApps = getWebApps(activity);
        for (WebApp webApp:
             webApps) {
            if (webApp.id.equals(id))
                return webApp;
        }
        return null;
    }


    public static WebApp getWebAppByName(Activity activity, String name) {
        List<WebApp> webApps = getWebApps(activity);
        for (WebApp webApp:
                webApps) {
            if (webApp.name.equals(name))
                return webApp;
        }
        return null;
    }

    public static void LauncherShortcut(Context appContext, WebApp app){

        Intent shortcutIntent = new Intent(appContext, fr.coding.yourandroidwebapp.WebMainActivity.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shortcutIntent.setAction("android.intent.action.WEBMAIN");
        shortcutIntent.putExtra("webappid", app.id);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, app.name);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(appContext, R.mipmap.ic_launcher));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        // requires android permission :
        // <uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
        appContext.sendBroadcast(addIntent);
    }
}

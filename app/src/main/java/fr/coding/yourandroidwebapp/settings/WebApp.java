package fr.coding.yourandroidwebapp.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import fr.coding.tools.RetrieveHttpFile;
import fr.coding.yourandroidwebapp.R;

/**
 * Created by Matthieu on 13/09/2015.
 */
public class WebApp {
    public String id;
    public String name;
    public String url;
    public String iconUrl;

    public String toString() {
        return name;
    }


    public static WebApp JSONobjToWebApp(JSONObject jsonobj) throws JSONException {
        WebApp webapp = new WebApp();
        webapp.id = jsonobj.getString("id");
        webapp.name = jsonobj.getString("name");
        if (jsonobj.has("iconUrl"))
            webapp.iconUrl = jsonobj.getString("iconUrl");
        webapp.url = jsonobj.getString("url");
        return webapp;
    }

    public JSONObject WebAppToJSONobj() throws JSONException {
        JSONObject jsonobj = new JSONObject();
        jsonobj.put("id", id);
        jsonobj.put("name", name);
        jsonobj.put("iconUrl", iconUrl);
        jsonobj.put("url", url);
        return jsonobj;
    }

    public void LauncherShortcut(Context appContext) {
        WebApp app = this;
        Intent shortcutIntent = new Intent(appContext, fr.coding.yourandroidwebapp.WebMainActivity.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shortcutIntent.setAction("android.intent.action.WEBMAIN");
        shortcutIntent.putExtra("webappid", app.id);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, app.name);

        if ((app.iconUrl != null) && (!app.iconUrl.isEmpty())) {
            Bitmap theBitmap = null;
            try {
                byte[] img = new RetrieveHttpFile().execute(app.iconUrl).get();
                theBitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            } catch (ExecutionException ee) {
                ee.printStackTrace();
            } catch (InterruptedException ee) {
                ee.printStackTrace();
            }
            if (theBitmap == null) {
                Toast.makeText(appContext, "Error loading icon, Url : " + app.iconUrl, Toast.LENGTH_LONG).show();
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(appContext, R.mipmap.ic_launcher));
            } else {
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(theBitmap, 256, 256, true);
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, scaledBitmap);
            }
        } else {
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(appContext, R.mipmap.ic_launcher));
        }
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        // requires android permission :
        // <uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
        appContext.sendBroadcast(addIntent);
    }
}

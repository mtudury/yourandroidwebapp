package fr.coding.yourandroidwebapp.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.webkit.WebSettings;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import fr.coding.tools.DiskCacheRetrieveHttpFile;
import fr.coding.yourandroidwebapp.R;
import fr.coding.yourandroidwebapp.WebMainActivity;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Created by Matthieu on 13/09/2015.
 */
public class WebApp {
// constantes
    public static final int PinchZoomMode_None = 0;
    public static final int PinchZoomMode_NoControls = 1;
    public static final int PinchZoomMode_WithControls = 2;

// members

    public String id;
    public String name;
    public String url;
    public String iconUrl;

    // alternate url
    public String alternateUrl;
    public String alternateSSIDs;
    public String alternateUrlNotConnected;

    // ssl
    public boolean allCertsByPass;
    public boolean allowedSSlActivated;
    // auth
    public boolean autoAuth;

    // connection
    public boolean reloadOnConnectionChange;

    // cache mode
    public int cacheMode;

    // pinchzoom mode
    public int pinchZoomMode;


    // default values
    public WebApp() {
        id = UUID.randomUUID().toString();
        allowedSSlActivated = true;
        autoAuth = true;
        reloadOnConnectionChange = false;
    }


    public String toString() {
        return name;
    }

    public WebApp Duplicate() {
        WebApp dup = new WebApp();
        dup.id = UUID.randomUUID().toString();

        dup.name = name + " Copy";
        dup.url = url;
        dup.iconUrl = iconUrl;

        dup.alternateUrl = alternateUrl;
        dup.alternateSSIDs = alternateSSIDs;
        dup.alternateUrlNotConnected = alternateUrlNotConnected;
        dup.allCertsByPass = allCertsByPass;
        dup.allowedSSlActivated = allowedSSlActivated;

        dup.autoAuth = autoAuth;
        dup.reloadOnConnectionChange = reloadOnConnectionChange;
        dup.cacheMode = cacheMode;
        dup.pinchZoomMode = pinchZoomMode;

        return dup;
    }


    public static WebApp JSONobjToWebApp(JSONObject jsonobj) throws JSONException {
        WebApp webapp = new WebApp();
        webapp.id = jsonobj.getString("id");
        webapp.name = jsonobj.getString("name");
        webapp.url = jsonobj.getString("url");

        if (jsonobj.has("iconUrl"))
            webapp.iconUrl = jsonobj.getString("iconUrl");

        if (jsonobj.has("alternateUrl"))
            webapp.alternateUrl = jsonobj.getString("alternateUrl");
        if (jsonobj.has("alternateUrlNotConnected"))
            webapp.alternateUrlNotConnected = jsonobj.getString("alternateUrlNotConnected");

        if (jsonobj.has("alternateSSIDs"))
            webapp.alternateSSIDs = jsonobj.getString("alternateSSIDs");


        if (jsonobj.has("allCertsByPass"))
            webapp.allCertsByPass = jsonobj.getBoolean("allCertsByPass");
        if (jsonobj.has("allowedSSlActivated"))
            webapp.allowedSSlActivated = jsonobj.getBoolean("allowedSSlActivated");

        if (jsonobj.has("autoAuth"))
            webapp.autoAuth = jsonobj.getBoolean("autoAuth");
        if (jsonobj.has("reloadOnConnectionChange"))
            webapp.reloadOnConnectionChange = jsonobj.getBoolean("reloadOnConnectionChange");

        webapp.cacheMode = WebSettings.LOAD_DEFAULT;
        if (jsonobj.has("cacheMode"))
            webapp.cacheMode = jsonobj.getInt("cacheMode");

        webapp.pinchZoomMode = PinchZoomMode_None;
        if (jsonobj.has("pinchZoomMode"))
            webapp.pinchZoomMode = jsonobj.getInt("pinchZoomMode");

        return webapp;
    }

    public JSONObject toJSONobj() throws JSONException {
        JSONObject jsonobj = new JSONObject();
        jsonobj.put("id", id);
        jsonobj.put("name", name);
        jsonobj.put("url", url);
        jsonobj.put("iconUrl", iconUrl);

        jsonobj.put("alternateUrl", alternateUrl);
        jsonobj.put("alternateUrlNotConnected", alternateUrlNotConnected);

        jsonobj.put("alternateSSIDs", alternateSSIDs);

        jsonobj.put("allCertsByPass", allCertsByPass);
        jsonobj.put("allowedSSlActivated", allowedSSlActivated);
        jsonobj.put("autoAuth", autoAuth);
        jsonobj.put("reloadOnConnectionChange", reloadOnConnectionChange);

        jsonobj.put("cacheMode", cacheMode);
        jsonobj.put("pinchZoomMode", pinchZoomMode);
        return jsonobj;
    }

    public void LauncherShortcut(Context appContext) {
        WebApp app = this;

        Bitmap theBitmap = null;
        if ((app.iconUrl != null) && (!app.iconUrl.isEmpty())) {
            Uri icon = Uri.parse(app.iconUrl);
            try {
                if (icon.getScheme().equalsIgnoreCase("file")||icon.getScheme().equalsIgnoreCase("content")) {
                    theBitmap = BitmapFactory.decodeFile(icon.getPath());
                } else {
                    byte[] img = new DiskCacheRetrieveHttpFile(appContext).execute(app.iconUrl).get();
                    if (img != null)
                        theBitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                }
            } catch (ExecutionException ee) {
                ee.printStackTrace();
            } catch (InterruptedException ee) {
                ee.printStackTrace();
            }
            if (theBitmap == null) {
                Toast.makeText(appContext, "Error loading icon, Url : " + app.iconUrl, Toast.LENGTH_LONG).show();
            } else {
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(theBitmap, 256, 256, true);
                theBitmap = scaledBitmap;
            }
        }

        Intent shortcutIntent = new Intent(appContext, WebMainActivity.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shortcutIntent.setAction("android.intent.action.WEBMAIN");
        shortcutIntent.putExtra("webappid", app.id);

        if (SDK_INT >= 26) {
            ShortcutManager scm = (ShortcutManager)appContext.getSystemService(Context.SHORTCUT_SERVICE);
            ShortcutInfo.Builder scib = new ShortcutInfo.Builder(appContext, app.id)
                    .setShortLabel(app.name)
                    .setIntent(shortcutIntent);
            if (theBitmap != null) {
                scib.setIcon(Icon.createWithBitmap(theBitmap));
            } else {
                scib.setIcon(Icon.createWithBitmap(BitmapFactory.decodeResource(appContext.getResources(), R.mipmap.ic_launcher)));
            }

            scm.requestPinShortcut(scib.build(), null);
        } else {


            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, app.name);
            if (theBitmap == null) {
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(appContext, R.mipmap.ic_launcher));
            } else {
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, theBitmap);
            }

            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            // requires android permission :
            // <uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
            appContext.sendBroadcast(addIntent);
        }
    }


    public void StartWebApp(Context ctx) {
        Intent detailIntent = new Intent(ctx, WebMainActivity.class);
        detailIntent.putExtra("webappid", id);
        ctx.startActivity(detailIntent);
    }
}

package fr.coding.yourandroidwebapp.websync;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.preference.PreferenceManager;
import fr.coding.yourandroidwebapp.settings.AppSettingsManager;

import static fr.coding.yourandroidwebapp.settings.AppSettingsManager.SYNC_AUTODOWNLOAD_ENABLE;
import static fr.coding.yourandroidwebapp.settings.AppSettingsManager.SYNC_DOWNLOADHEADERKEY;
import static fr.coding.yourandroidwebapp.settings.AppSettingsManager.SYNC_DOWNLOADHEADERVALUE;
import static fr.coding.yourandroidwebapp.settings.AppSettingsManager.SYNC_DOWNLOAD_URL;
import static fr.coding.yourandroidwebapp.settings.AppSettingsManager.SYNC_UPLOADHEADERKEY;
import static fr.coding.yourandroidwebapp.settings.AppSettingsManager.SYNC_UPLOADHEADERVALUE;
import static fr.coding.yourandroidwebapp.settings.AppSettingsManager.SYNC_UPLOAD_HTTPMETHOD;
import static fr.coding.yourandroidwebapp.settings.AppSettingsManager.SYNC_UPLOAD_URL;

public class SyncSettings {
    private String sync_autodownload_enable;

    private String sync_download_url;
    private String sync_downloadheaderkey;
    private String sync_downloadheadervalue;

    private String sync_upload_url;
    private String sync_uploadheaderkey;
    private String sync_uploadheadervalue;
    private String sync_upload_httpmethod;


    public static SyncSettings JSONobjToWebApp(JSONObject jsonobj) throws JSONException {
        SyncSettings syncSettings = new SyncSettings();
        if (jsonobj.has("sync_autodownload_enable"))
            syncSettings.sync_autodownload_enable = jsonobj.getString("sync_autodownload_enable");

        if (jsonobj.has("sync_download_url"))
            syncSettings.sync_download_url = jsonobj.getString("sync_download_url");
        if (jsonobj.has("sync_downloadheaderkey"))
            syncSettings.sync_downloadheaderkey = jsonobj.getString("sync_downloadheaderkey");
        if (jsonobj.has("sync_downloadheadervalue"))
            syncSettings.sync_downloadheadervalue = jsonobj.getString("sync_downloadheadervalue");

        if (jsonobj.has("sync_upload_url"))
            syncSettings.sync_upload_url = jsonobj.getString("sync_upload_url");
        if (jsonobj.has("sync_uploadheaderkey"))
            syncSettings.sync_uploadheaderkey = jsonobj.getString("sync_uploadheaderkey");
        if (jsonobj.has("sync_uploadheadervalue"))
            syncSettings.sync_uploadheadervalue = jsonobj.getString("sync_uploadheadervalue");
        if (jsonobj.has("sync_upload_httpmethod"))
            syncSettings.sync_upload_httpmethod = jsonobj.getString("sync_upload_httpmethod");

        return syncSettings;
    }

    public JSONObject toJSONobj() throws JSONException {
        JSONObject jsonobj = new JSONObject();
        if ((sync_autodownload_enable != null)&&(!sync_autodownload_enable.isEmpty()))
            jsonobj.put("sync_autodownload_enable", sync_autodownload_enable);

        if ((sync_download_url != null)&&(!sync_download_url.isEmpty()))
            jsonobj.put("sync_download_url", sync_download_url);
        if ((sync_downloadheaderkey != null)&&(!sync_downloadheaderkey.isEmpty()))
            jsonobj.put("sync_downloadheaderkey", sync_downloadheaderkey);
        if ((sync_downloadheadervalue != null)&&(!sync_downloadheadervalue.isEmpty()))
            jsonobj.put("sync_downloadheadervalue", sync_downloadheadervalue);


        if ((sync_upload_url != null)&&(!sync_upload_url.isEmpty()))
            jsonobj.put("sync_upload_url", sync_upload_url);
        if ((sync_uploadheaderkey != null)&&(!sync_uploadheaderkey.isEmpty()))
            jsonobj.put("sync_uploadheaderkey", sync_uploadheaderkey);
        if ((sync_uploadheadervalue != null)&&(!sync_uploadheadervalue.isEmpty()))
            jsonobj.put("sync_uploadheadervalue", sync_uploadheadervalue);
        if ((sync_upload_httpmethod != null)&&(!sync_upload_httpmethod.isEmpty()))
            jsonobj.put("sync_upload_httpmethod", sync_upload_httpmethod);
        return jsonobj;
    }

    public void FillFromSettings(Context context) {
        if (!AppSettingsManager.getSyncSettingDownloadExclude(context)) {
            sync_autodownload_enable = AppSettingsManager.getSyncAutoDownload(context) ? "1" : "0";

            sync_download_url = AppSettingsManager.getSettingDownloadUrl(context);
            sync_downloadheaderkey = AppSettingsManager.getSettingDownloadHeaderKey(context);
            sync_downloadheadervalue = AppSettingsManager.getSettingUploadHeaderValue(context);
        }

        if (!AppSettingsManager.getSyncSettingUploadExclude(context)) {
            sync_upload_url = AppSettingsManager.getSettingUploadUrl(context);
            sync_uploadheaderkey = AppSettingsManager.getSettingUploadHeaderKey(context);
            sync_uploadheadervalue = AppSettingsManager.getSettingUploadHeaderValue(context);
            sync_upload_httpmethod = AppSettingsManager.getSettingUploadMethod(context);
        }
    }

    public void setPrefs(Context context) {
        boolean excludedownload = AppSettingsManager.getSyncSettingDownloadExclude(context);
        boolean excludeupload = AppSettingsManager.getSyncSettingUploadExclude(context);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        if (!excludedownload) {
            editor.putBoolean(SYNC_AUTODOWNLOAD_ENABLE, sync_autodownload_enable == "1");
            editor.putString(SYNC_DOWNLOAD_URL, sync_download_url);
            editor.putString(SYNC_DOWNLOADHEADERKEY, sync_downloadheaderkey);
            editor.putString(SYNC_DOWNLOADHEADERVALUE, sync_downloadheadervalue);
        }

        if (!excludeupload) {
            editor.putString(SYNC_UPLOAD_URL, sync_upload_url);
            editor.putString(SYNC_UPLOADHEADERKEY, sync_uploadheaderkey);
            editor.putString(SYNC_UPLOADHEADERVALUE, sync_uploadheadervalue);
            editor.putString(SYNC_UPLOAD_HTTPMETHOD, sync_upload_httpmethod);
        }

        editor.commit();
    }
}

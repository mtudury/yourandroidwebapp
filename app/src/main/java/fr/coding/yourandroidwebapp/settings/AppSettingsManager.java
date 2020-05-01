package fr.coding.yourandroidwebapp.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import androidx.preference.PreferenceManager;
import fr.coding.yourandroidwebapp.R;


/**
 * Created by Matthieu on 13/09/2015.
 */
public class AppSettingsManager {

    public static final String PREFSFILE = "yourandroidwebappconfig.json";

    private static  final String RemoteDebugging = "webview_debug_mode";
    private static  final String KeepScreenOn = "webview_keepscreen_on";
    private static final String FullScreenMode = "webview_fullscreen_mode";
    private static final String ProgressBar = "webview_progress";
    public static final String AutoRefresh = "webview_refreshevery";
    private static final String PREFS_WEBSYNCLASTUPDATED = "google_drive_last_updated";
    public static final String SYNC_AUTODOWNLOAD_ENABLE = "sync_autodownload_enable";
    public static final String SYNC_DOWNLOAD_URL = "sync_download_url";
    public static final String SYNC_DOWNLOADHEADERKEY = "sync_downloadheaderkey";
    public static final String SYNC_DOWNLOADHEADERVALUE = "sync_downloadheadervalue";
    public static final String SYNC_UPLOADHEADERKEY = "sync_uploadheaderkey";
    public static final String SYNC_UPLOADHEADERVALUE = "sync_uploadheadervalue";
    public static final String SYNC_UPLOAD_HTTPMETHOD = "sync_upload_httpmethod";
    public static final String SYNC_UPLOAD_URL = "sync_upload_url";

    private static void SaveExternaly(String path, String jsonval) throws IOException {
        FileOutputStream fileout = new FileOutputStream (new File(path));
        OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
        outputWriter.write(jsonval);
        outputWriter.close();
    }
    private static void SaveLocally(Context context, String jsonval) throws IOException {
        // add-write text into file
        FileOutputStream fileout = context.openFileOutput(PREFSFILE, Context.MODE_PRIVATE);
        OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
        outputWriter.write(jsonval);
        outputWriter.close();
    }

    public static String SaveSettingsLocally(Context context, AppSettings apps) {
        String jsonval = null;

        try {
            jsonval = apps.AppSettingsToJSONobj().toString();
            SaveLocally(context, jsonval);
        } catch (IOException|JSONException e) {
            e.printStackTrace();
            new AlertDialog.Builder(context).setTitle("ErrorSavingSettings").setMessage(e.toString()).setNeutralButton("Close", null).show();
        }
        return jsonval;
    }

    public static String ExportSettingsToExternalStorage(Context context, AppSettings apps, Uri uri) {
        String jsonval = null;
        try {
            jsonval = apps.AppSettingsToJSONobj().toString();

            OutputStream output = context.getContentResolver().openOutputStream(uri);

            output.write(jsonval.getBytes());
            output.flush();
            output.close();

        } catch (IOException|JSONException e) {
            e.printStackTrace();
            new AlertDialog.Builder(context).setTitle("ErrorSavingSettings").setMessage(e.toString()).setNeutralButton("Close", null).show();
        }
        return jsonval;
    }

    public static AppSettings LoadSettingsFromExternalStorage(Context context, Uri uri) {
        String jsonval = null;
        try {

            InputStream input = context.getContentResolver().openInputStream(uri);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            input.close();
            buffer.flush();
            jsonval = new String(buffer.toByteArray(), StandardCharsets.UTF_8);

            return AppSettings.JSONobjToAppSettings(new JSONObject(jsonval));

        } catch (IOException|JSONException e) {
            e.printStackTrace();
            new AlertDialog.Builder(context).setTitle("ErrorSavingSettings").setMessage(e.toString()).setNeutralButton("Close", null).show();
        }
        return null;
    }

    private static AppSettings LoadExternaly(String path) throws IOException, JSONException {
        FileInputStream fileIn=new FileInputStream (new File(path));
        BufferedReader InputRead= new BufferedReader(new InputStreamReader(fileIn));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = InputRead.readLine()) != null) {
            builder.append(line);
        }
        return AppSettings.JSONobjToAppSettings(new JSONObject(builder.toString()));
    }

    private static AppSettings LoadLocally(Context activity) throws IOException, JSONException {
        FileInputStream fileIn=activity.openFileInput(PREFSFILE);
        BufferedReader InputRead= new BufferedReader(new InputStreamReader(fileIn));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = InputRead.readLine()) != null) {
            builder.append(line);
        }
        return AppSettings.JSONobjToAppSettings(new JSONObject(builder.toString()));
    }

    public static AppSettings LoadSettingsLocally(Context activity) {
        try {
            return LoadLocally(activity);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException|JSONException e)
        {
            e.printStackTrace();
            new AlertDialog.Builder(activity).setTitle("ErrorSavingSettings").setMessage(e.toString()).setNeutralButton("Close", null).show();
        }
        return new AppSettings();
    }

    public AppSettings LoadSettingsFromExternalStorage(Context context, String path) {
        try {
            return LoadExternaly(path);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException|JSONException e)
        {
            e.printStackTrace();
            new AlertDialog.Builder(context).setTitle("ErrorSavingSettings").setMessage(e.toString()).setNeutralButton("Close", null).show();
        }
        return new AppSettings();
    }

    public static boolean IsRemoteDebuggingActivated(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return prefs.getBoolean(RemoteDebugging, false);
    }

    public static boolean ImmersiveFullscreenMode(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return prefs.getBoolean(FullScreenMode, false);
    }

    public static boolean KeepTheScreenOn(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return prefs.getBoolean(KeepScreenOn, false);
    }

    public static boolean ShowProgressBar(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return prefs.getBoolean(ProgressBar, false);
    }

    public static Long GetLastUpdatedFromWebSync(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return prefs.getLong(PREFS_WEBSYNCLASTUPDATED, 0);
    }

    public static void SetLastUpdatedFromWebSync(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        Date datedef = new Date();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PREFS_WEBSYNCLASTUPDATED, datedef.getTime());
        editor.commit();
    }

    public static long AutoRefreshRate(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        return Long.parseLong(prefs.getString(AutoRefresh, "-1"));
    }


    public static boolean getSyncAutoDownload(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(SYNC_AUTODOWNLOAD_ENABLE, false);
    }

    public static String getSettingDownloadUrl(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String setting = prefs.getString(SYNC_DOWNLOAD_URL, null);
        if (setting == null)
            return null;
        if (context.getString(R.string.syncsettings_default_url).contentEquals(setting))
            return null;
        return setting;
    }

    public static String getSettingUploadUrl(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String setting = prefs.getString(SYNC_UPLOAD_URL, null);
        if (setting == null)
            return null;
        if (context.getString(R.string.syncsettings_default_url).contentEquals(setting))
            return null;
        return setting;
    }


    public static String getSettingDownloadHeaderKey(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(SYNC_DOWNLOADHEADERKEY, null);
    }

    public static String getSettingDownloadHeaderValue(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String setting = prefs.getString(SYNC_DOWNLOADHEADERVALUE, null);
        if (setting == null)
            return null;
        if (context.getString(R.string.syncsettings_default_headervalue).contentEquals(setting))
            return null;
        return setting;
    }

    public static String getSettingUploadHeaderKey(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(SYNC_UPLOADHEADERKEY, null);
    }

    public static String getSettingUploadHeaderValue(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String setting = prefs.getString(SYNC_UPLOADHEADERVALUE, null);
        if (setting == null)
            return null;
        if (context.getString(R.string.syncsettings_default_headervalue).contentEquals(setting))
            return null;
        return setting;
    }


    public static String getSettingUploadMethod(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(SYNC_UPLOAD_HTTPMETHOD, null);
    }

    public static boolean getSyncSettingDownloadExclude(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("sync_upload_downloadsettings_exclude", false);
    }

    public static boolean getSyncSettingUploadExclude(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("sync_upload_uploadsettings_exclude", false);
    }
}

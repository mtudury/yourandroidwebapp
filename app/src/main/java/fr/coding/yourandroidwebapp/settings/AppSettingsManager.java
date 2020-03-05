package fr.coding.yourandroidwebapp.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.Date;


/**
 * Created by Matthieu on 13/09/2015.
 */
public class AppSettingsManager {

    public static final String PREFSFILE = "yourandroidwebappconfig.json";

    public static final String PREFS = "fr.coding.yourandroidwebapp_preferences";

    public static final String PREFS_CUSTOMDRIVEID = "google_drive_customid";
    public static final String PREFS_USECUSTOMDRIVEID = "google_drive_use_custom";
    public static final String PREFS_CUSTOMDRIVEIDDESC = "google_drive_customiddesc";
    private static final String UseGDrive = "google_drive_usage";
    private static  final String RemoteDebugging = "webview_debug_mode";
    private static  final String KeepScreenOn = "webview_keepscreen_on";
    private static final String FullScreenMode = "webview_fullscreen_mode";
    private static final String ProgressBar = "webview_progress";
    public static final String AutoRefresh = "webview_refreshevery";
    private static final String PREFS_GDRIVELASTUPDATED = "google_drive_last_updated";

    private Activity activity;
    private String jsonval;

    public AppSettingsManager(Activity act) {
        activity = act;
    }

    private void SaveExternaly(String path) throws IOException {
        FileOutputStream fileout = new FileOutputStream (new File(path));
        OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
        outputWriter.write(jsonval);
        outputWriter.close();
    }
    private void SaveLocally() throws IOException {
        // add-write text into file
        FileOutputStream fileout = activity.openFileOutput(PREFSFILE, Context.MODE_PRIVATE);
        OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
        outputWriter.write(jsonval);
        outputWriter.close();
    }

    public String SaveSettingsLocally(AppSettings apps) {
        try {
            jsonval = apps.AppSettingsToJSONobj().toString();
            SaveLocally();
        } catch (IOException|JSONException e) {
            e.printStackTrace();
            new AlertDialog.Builder(activity).setTitle("ErrorSavingSettings").setMessage(e.toString()).setNeutralButton("Close", null).show();
        }
        return jsonval;
    }

    public String ExportSettingsToExternalStorage(AppSettings apps, String path) {
        try {
            jsonval = apps.AppSettingsToJSONobj().toString();
            SaveExternaly(path);
        } catch (IOException|JSONException e) {
            e.printStackTrace();
            new AlertDialog.Builder(activity).setTitle("ErrorSavingSettings").setMessage(e.toString()).setNeutralButton("Close", null).show();
        }
        return jsonval;
    }

    private AppSettings LoadExternaly(String path) throws IOException, JSONException {
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

    public AppSettings LoadSettingsFromExternalStorage(String path) {
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
            new AlertDialog.Builder(activity).setTitle("ErrorSavingSettings").setMessage(e.toString()).setNeutralButton("Close", null).show();
        }
        return new AppSettings();
    }

    public static boolean IsRemoteDebuggingActivated(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(RemoteDebugging, false);
    }

    public static boolean ImmersiveFullscreenMode(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(FullScreenMode, false);
    }

    public static boolean KeepTheScreenOn(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(KeepScreenOn, false);
    }

    public static boolean ShowProgressBar(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(ProgressBar, false);
    }

    public static Date GetLastUpdatedFromGDrive(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        Date datedef = new Date();
        return new Date(prefs.getLong(PREFS_GDRIVELASTUPDATED, datedef.getTime()));
    }

    public static long AutoRefreshRate(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return Long.parseLong(prefs.getString(AutoRefresh, "-1"));
    }
}

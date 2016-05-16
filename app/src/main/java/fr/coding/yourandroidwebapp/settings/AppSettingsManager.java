package fr.coding.yourandroidwebapp.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.android.gms.common.api.*;
import com.google.android.gms.drive.*;
import com.google.android.gms.drive.DriveApi.*;
import com.google.android.gms.drive.query.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

import fr.coding.tools.*;
import fr.coding.tools.gdrive.GoogleDriveCreateFile;
import fr.coding.tools.gdrive.GoogleDriveReadFile;
import fr.coding.tools.gdrive.GoogleDriveUpdateFile;
import fr.coding.yourandroidwebapp.R;

/**
 * Created by Matthieu on 13/09/2015.
 */
public class AppSettingsManager {

    public static final String PREFSFILE = "appconfig.json";

    public static final String PREFS = "fr.coding.yourandroidwebapp_preferences";

    public static final String PREFS_CUSTOMDRIVEID = "google_drive_customid";
    public static final String PREFS_USECUSTOMDRIVEID = "google_drive_use_custom";
    public static final String PREFS_CUSTOMDRIVEIDDESC = "google_drive_customiddesc";
    private static final String UseGDrive = "google_drive_usage";

    private GoogleApiClient googleApiClient;

    private AppSettingsCallback getResultHandler;

    private Callback<String> saveResultHandler;

    private Activity activity;

    private String jsonval;

    public AppSettingsManager(Activity act) {
        activity = act;
    }

    public void LoadSettings(GoogleApiClient apiClient, AppSettingsCallback handler) {
        googleApiClient = apiClient;
        getResultHandler = handler;

        Drive.DriveApi.getAppFolder(googleApiClient)
                .queryChildren(googleApiClient, new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, PREFSFILE))
                        .build())
                .setResultCallback(new ResultCallback<MetadataBufferResult>() {
                    @Override
                    public void onResult(MetadataBufferResult metadataBufferResult) {
                        QueryResultsCallback(metadataBufferResult);
                    }
                });
    }

    public void QueryResultsCallback(MetadataBufferResult result) {
        if (result.getMetadataBuffer().getCount() > 0) {
            DriveFile df = result.getMetadataBuffer().get(0).getDriveId().asDriveFile();
            new GoogleDriveReadFile(googleApiClient, activity).GetDriveFileContent(df, new Callback<String>() {
                @Override
                public void onCallback(String restxt) {
                    AppSettings res = null;
                    try {
                        res = AppSettings.JSONobjToAppSettings(new JSONObject(restxt));
                        jsonval = restxt;
                        SaveLocally();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(activity).setTitle("ErrorLoadingSettings").setMessage(e.toString()).setNeutralButton("Close", null).show();
                        if (res == null)
                            res = new AppSettings();
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (res == null)
                            res = new AppSettings();
                    }


                    getResultHandler.onAppSettingsReady(res);
                }
            });
        } else {
            AppSettings res = new AppSettings();
            try {
                res = LoadLocally();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (JSONException|IOException e) {
                e.printStackTrace();
                new AlertDialog.Builder(activity).setTitle("ErrorLoadingSettings").setMessage(e.toString()).setNeutralButton("Close", null).show();
            }
            getResultHandler.onAppSettingsReady(res);
        }
    }

    public void Save(AppSettings apps, GoogleApiClient apiClient, Callback<String> saveHandler) {
        googleApiClient = apiClient;
        saveResultHandler = saveHandler;
        try {
            jsonval = apps.AppSettingsToJSONobj().toString();
            SaveLocally();

            if ((apiClient != null)&&(IsSettingsInGdrive(activity))) {
                Drive.DriveApi.getAppFolder(googleApiClient)
                        .queryChildren(googleApiClient, new Query.Builder()
                                .addFilter(Filters.eq(SearchableField.TITLE, PREFSFILE))
                                .build())
                        .setResultCallback(new ResultCallback<MetadataBufferResult>() {
                            @Override
                            public void onResult(MetadataBufferResult metadataBufferResult) {
                                QueryResultsCallbackSave(metadataBufferResult);
                            }
                        });
            }
        } catch (IOException|JSONException e) {
            e.printStackTrace();
            new AlertDialog.Builder(activity).setTitle("ErrorSavingSettings").setMessage(e.toString()).setNeutralButton("Close", null).show();
        }
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

    public String ExportSettingsLocally(AppSettings apps, String path) {
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

    private AppSettings LoadLocally() throws IOException, JSONException {
        FileInputStream fileIn=activity.openFileInput(PREFSFILE);
        BufferedReader InputRead= new BufferedReader(new InputStreamReader(fileIn));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = InputRead.readLine()) != null) {
            builder.append(line);
        }
        return AppSettings.JSONobjToAppSettings(new JSONObject(builder.toString()));
    }

    public AppSettings LoadSettingsLocally() {
        try {
            return LoadLocally();
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

    public AppSettings ImportSettingsLocally(String path) {
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

    public void QueryResultsCallbackSave(MetadataBufferResult result) {

        if (result.getMetadataBuffer().getCount() > 0) {
            // update
            DriveFile df = result.getMetadataBuffer().get(0).getDriveId().asDriveFile();
            new GoogleDriveUpdateFile(googleApiClient, activity).SetDriveFileContent(df, jsonval, successSavedCallBack);
        } else {
            // create file
            new GoogleDriveCreateFile(googleApiClient, activity).CreateFile(PREFSFILE, "application/json", successSavedCallBack);
        }
    }

    final private Callback<String> successSavedCallBack = new Callback<String>() {
        @Override
        public void onCallback(String restxt) {
            if (saveResultHandler != null) {
                saveResultHandler.onCallback(restxt);
            }
            else {
                Toast.makeText(activity, R.string.webapp_saved_toast, Toast.LENGTH_SHORT).show();
            }
        }
    };


    public static boolean IsSettingsInGdrive(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(UseGDrive, false);
    }
//
//    public static void SetUseOrNotGDrive(Activity activity, boolean useGDrive) {
//        SharedPreferences prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putBoolean(UseGDrive, useGDrive);
//        editor.apply();
//    }

}

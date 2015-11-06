package fr.coding.yourandroidwebapp.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.*;
import com.google.android.gms.drive.*;
import com.google.android.gms.drive.DriveApi.*;
import com.google.android.gms.drive.DriveFolder.*;
import com.google.android.gms.drive.query.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

import fr.coding.tools.*;
import fr.coding.tools.gdrive.GoogleDriveBaseTools;
import fr.coding.tools.gdrive.GoogleDriveCreateFile;
import fr.coding.tools.gdrive.GoogleDriveReadFile;
import fr.coding.tools.gdrive.GoogleDriveUpdateFile;
import fr.coding.yourandroidwebapp.R;

/**
 * Created by Matthieu on 13/09/2015.
 */
public class AppSettingsManager {

    public static final String PREFSFILE = "appconfig.json";

    private static final String PREFS = "general_settings";
    private static final String UseGDrive = "google_drive_usage";

    private GoogleApiClient googleApiClient;

    private AppSettingsCallback resulthandler;

    private Activity activity;

    private String jsonval;

    public void LoadSettings(GoogleApiClient apiClient, AppSettingsCallback handler, Activity act) {
        googleApiClient = apiClient;
        resulthandler = handler;
        activity = act;

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
                    AppSettings res;
                    try {
                        res = AppSettings.JSONobjToAppSettings(new JSONObject(restxt));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(activity).setTitle("ErrorLoadingSettings").setMessage(e.toString()).setNeutralButton("Close", null).show();
                        res = new AppSettings();
                    }


                    resulthandler.onAppSettingsReady(res);
                }
            });
        } else {
            resulthandler.onAppSettingsReady(new AppSettings());
        }
    }

    public void Save(Activity act, AppSettings apps, GoogleApiClient apiClient) {
        googleApiClient = apiClient;
        activity = act;
        try {
            jsonval = apps.AppSettingsToJSONobj().toString();
            SaveLocally();

            if ((apiClient != null)&&(IsSettingsInGdrive(act))) {
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

    private void SaveLocally() throws IOException {
        // add-write text into file
        FileOutputStream fileout = activity.openFileOutput(PREFSFILE, Context.MODE_PRIVATE);
        OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
        outputWriter.write(jsonval);
        outputWriter.close();
    }


    private AppSettings LoadLocally() throws IOException, JSONException {
        // add-write text into file
        FileInputStream fileIn=activity.openFileInput(PREFSFILE);
        BufferedReader InputRead= new BufferedReader(new InputStreamReader(fileIn));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = InputRead.readLine()) != null) {
            builder.append(line);
        }
        return AppSettings.JSONobjToAppSettings(new JSONObject(builder.toString()));
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
            Toast.makeText(activity, R.string.webapp_saved_toast, Toast.LENGTH_SHORT).show();
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

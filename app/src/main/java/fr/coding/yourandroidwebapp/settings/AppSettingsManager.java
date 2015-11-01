package fr.coding.yourandroidwebapp.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.*;
import com.google.android.gms.drive.*;
import com.google.android.gms.drive.DriveApi.*;
import com.google.android.gms.drive.DriveFolder.*;
import com.google.android.gms.drive.query.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

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
        } catch (JSONException e) {
            e.printStackTrace();
            new AlertDialog.Builder(activity).setTitle("ErrorLoadingSettings").setMessage(e.toString()).setNeutralButton("Close", null).show();
        }
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


}

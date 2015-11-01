package fr.coding.yourandroidwebapp.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.concurrent.ExecutionException;

import fr.coding.tools.*;
import fr.coding.tools.gdrive.GoogleDriveTools;
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
            GoogleDriveTools gdrivetools = new GoogleDriveTools(googleApiClient, activity);
            gdrivetools.GetDriveFileContent(df, new Callback<String>() {
                @Override
                public void onCallback(String restxt) {
                    AppSettings res = null;
                    try {
                        res = AppSettings.JSONobjToAppSettings(new JSONObject(restxt));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(activity).setTitle("ErrorLoadingSettings").setMessage(e.toString()).setNeutralButton("Close", null).show();
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
            GoogleDriveTools gdriveTools = new GoogleDriveTools(googleApiClient, activity);
            gdriveTools.SetDriveFileContent(df, jsonval, new Callback<String>() {
                @Override
                public void onCallback(String restxt) {
                    Toast.makeText(activity, R.string.webapp_saved_toast, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // create file
            Drive.DriveApi.newDriveContents(googleApiClient)
                    .setResultCallback(driveContentsCallback);
        }
    }

    final private ResultCallback<DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveContentsResult>() {
                @Override
                public void onResult(DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        new AlertDialog.Builder(activity).setTitle("Error Saving Settings").setMessage(result.getStatus().getStatusMessage()).setNeutralButton("Close", null).show();
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();

                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {
                            // write content to DriveContents
                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer = new OutputStreamWriter(outputStream);
                            try {
                                writer.write(jsonval);
                                writer.close();
                            } catch (IOException e) {
                                Log.e("AppSettingsManager", e.getMessage());
                            }

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(PREFSFILE)
                                    .setMimeType("application/json")
                                    .build();

                            // create a file on root folder
                            Drive.DriveApi.getAppFolder(googleApiClient)
                                    .createFile(googleApiClient, changeSet, driveContents)
                                    .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };

    final private ResultCallback<DriveFileResult> fileCallback = new
            ResultCallback<DriveFileResult>() {
                @Override
                public void onResult(DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        new AlertDialog.Builder(activity).setTitle("Error while trying to create the file").setMessage(result.getStatus().getStatusMessage()).setNeutralButton("Close", null).show();
                        return;
                    }
                    Toast.makeText(activity, R.string.webapp_saved_toast, Toast.LENGTH_SHORT).show();
                }
            };


}

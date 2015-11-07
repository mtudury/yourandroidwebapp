package fr.coding.tools.gdrive;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import fr.coding.tools.Callback;
import fr.coding.yourandroidwebapp.R;

/**
 * Created by Matthieu on 01/11/2015.
 */
public class GoogleDriveCreateFile extends GoogleDriveBaseTools {

    private Callback<String> writeCallback;

    private String writeContents;

    private MetadataChangeSet changeSet;


    public GoogleDriveCreateFile(GoogleApiClient gApiClient, Activity activity) {
        super(gApiClient, activity, "GoogleDriveCreateFile");
    }

    public void CreateFile(String fileName, String mimetype, Callback<String> callback) {
        CreateFile(new MetadataChangeSet.Builder()
                .setTitle(fileName)
                .setMimeType(mimetype)
                .build(), callback);
    }

    public void CreateFile(MetadataChangeSet cs, Callback<String> callback) {
        writeCallback = callback;
        changeSet = cs;

        Drive.DriveApi.newDriveContents(googleApiClient).setResultCallback(driveContentsCallback);

    }

    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
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
                                writer.write(writeContents);
                                writer.close();
                            } catch (IOException e) {
                                Log.e("AppSettingsManager", e.getMessage());
                            }



                            // create a file on root folder
                            Drive.DriveApi.getAppFolder(googleApiClient)
                                    .createFile(googleApiClient, changeSet, driveContents)
                                    .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        new AlertDialog.Builder(activity).setTitle("Error while trying to create the file").setMessage(result.getStatus().getStatusMessage()).setNeutralButton("Close", null).show();
                        return;
                    }
                    Toast.makeText(activity, R.string.webapp_saved_toast, Toast.LENGTH_SHORT).show();
                }
            };

}

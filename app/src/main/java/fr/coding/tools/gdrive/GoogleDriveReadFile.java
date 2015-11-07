package fr.coding.tools.gdrive;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import fr.coding.tools.Callback;

/**
 * Created by Matthieu on 01/11/2015.
 */
public class GoogleDriveReadFile extends GoogleDriveBaseTools {

    private Callback<String> readCallback;

    public GoogleDriveReadFile(GoogleApiClient gApiClient, Activity activity) {
        super(gApiClient, activity, "GoogleDriveReadFile");
    }

    /**
     * Load a text file content from drivefile
     *
     * @param df DriveFile
     */
    public void GetDriveFileContent(DriveFile df, Callback<String> readcallback) {
        readCallback = readcallback;
        df.open(googleApiClient, DriveFile.MODE_READ_ONLY, null).setResultCallback(driveContentsCallback);
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
                            String contents = null;

                            // write content to DriveContents
                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(driveContents.getInputStream()));
                            StringBuilder builder = new StringBuilder();
                            String line;
                            try {
                                while ((line = reader.readLine()) != null) {
                                    builder.append(line);
                                }
                                contents = builder.toString();
                            } catch (IOException e) {
                                Log.e(TAG, "IOException while reading from the stream", e);
                            }

                            driveContents.discard(googleApiClient);

                            final String resultcontents = contents;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    readCallback.onCallback(resultcontents);
                                }
                            });
                        }
                    }.start();
                }
            };
}

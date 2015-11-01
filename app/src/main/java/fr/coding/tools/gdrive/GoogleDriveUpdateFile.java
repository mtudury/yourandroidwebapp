package fr.coding.tools.gdrive;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import fr.coding.tools.Callback;

/**
 * Created by Matthieu on 01/11/2015.
 */
public class GoogleDriveUpdateFile extends GoogleDriveBaseTools {

    private Callback<String> writeCallback;

    private String writeContents;


    public GoogleDriveUpdateFile(GoogleApiClient gApiClient, Activity activity) {
        super(gApiClient, activity);
    }


    public void SetDriveFileContent(DriveFile df, String contents, Callback<String> writecallback) {
        writeCallback = writecallback;
        writeContents = contents;
        df.open(googleApiClient, DriveFile.MODE_WRITE_ONLY, null).setResultCallback(writedriveContentsCallback);
    }

    final private ResultCallback<DriveApi.DriveContentsResult> writedriveContentsCallback = new
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
                            try {
                                OutputStream outputStream = driveContents.getOutputStream();
                                Writer writer = new OutputStreamWriter(outputStream);
                                writer.write(writeContents);
                                writer.close();
                                driveContents.commit(googleApiClient, null).await();
                                if (writeCallback != null) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            writeCallback.onCallback(writeContents);
                                        }
                                    });
                                }

                            } catch (IOException e) {
                                Log.e(TAG, "IOException while reading from the stream", e);
                            }
                        }
                    }.start();
                }
            };



}

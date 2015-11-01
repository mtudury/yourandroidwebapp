package fr.coding.tools.gdrive;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.*;

import java.io.*;

import fr.coding.tools.Callback;

/**
 * Created by Matthieu on 31/10/2015.
 */
public class GoogleDriveTools {

    private static final String TAG = "GoogleDriveTools";

    private GoogleApiClient googleApiClient;

    private Activity activity;

    private Callback<String> readCallback;

    private Callback<String> writeCallback;

    private String writeContents;

    public GoogleDriveTools(GoogleApiClient gApiClient, Activity activity) {
        googleApiClient = gApiClient;
        this.activity = activity;
    }

    /**
     * Load a text file content from drivefile
     *
     * @param df DriveFile
     * @return String : content of the file
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

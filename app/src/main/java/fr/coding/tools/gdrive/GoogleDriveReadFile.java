package fr.coding.tools.gdrive;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import fr.coding.tools.Callback;

/**
 * Created by Matthieu on 01/11/2015.
 */
public class GoogleDriveReadFile extends GoogleDriveBaseTools {

    private Callback<String> readCallback;

    public GoogleDriveReadFile(GoogleSignInClient gApiClient, Activity activity) {
        super(gApiClient, activity, "GoogleDriveReadFile");
    }

    /**
     * Load a text file content from drivefile
     *
     * @param df DriveFile
     */
    public void GetDriveFileContent(DriveFile df, Callback<String> readcallback) {
        GoogleSignInAccount gsa = GoogleSignIn.getLastSignedInAccount(activity);
        if (gsa != null) {
            readCallback = readcallback;
            DriveResourceClient drc = Drive.getDriveResourceClient(activity, gsa);
            Task<DriveContents> openTask =
                    drc.openFile(df, DriveFile.MODE_READ_ONLY);

            openTask.addOnSuccessListener(driveContents -> {
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

                                drc.discardContents(driveContents);

                                final String resultcontents = contents;
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        readCallback.onCallback(resultcontents);
                                    }
                                });
                            }
                        }.start();
                    }).addOnFailureListener(failure -> {
                new AlertDialog.Builder(activity).setTitle("Error Saving Settings").setMessage(failure.getLocalizedMessage()).setNeutralButton("Close", null).show();
            });
        } else {
            new AlertDialog.Builder(activity).setTitle("NotConnected").setMessage("Go to google drive settings to link to an account").setNeutralButton("Close", null).show();
        }
    }
}

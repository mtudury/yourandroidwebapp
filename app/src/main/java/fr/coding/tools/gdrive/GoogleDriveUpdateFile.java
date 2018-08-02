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


    public GoogleDriveUpdateFile(GoogleSignInClient gApiClient, Activity activity) {
        super(gApiClient, activity, "GoogleDriveUpdateFile");
    }


    public void SetDriveFileContent(DriveFile df, String contents, Callback<String> writecallback) {
        writeCallback = writecallback;
        writeContents = contents;

        GoogleSignInAccount gsa = GoogleSignIn.getLastSignedInAccount(activity);
        if (gsa != null) {
            DriveResourceClient drc = Drive.getDriveResourceClient(activity, GoogleSignIn.getLastSignedInAccount(activity));

            Task<DriveContents> openTask =
                    drc.openFile(df, DriveFile.MODE_WRITE_ONLY);

            openTask.addOnSuccessListener(driveContents -> {
                // Perform I/O off the UI thread.
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer = new OutputStreamWriter(outputStream);
                            writer.write(writeContents);
                            writer.close();
                            drc.commitContents(driveContents, null);
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
            }).addOnFailureListener(failure -> {
                new AlertDialog.Builder(activity).setTitle("Error Saving Settings").setMessage(failure.getLocalizedMessage()).setNeutralButton("Close", null).show();
            });

        } else {
            new AlertDialog.Builder(activity).setTitle("NotConnected").setMessage("Go to google drive settings to link to an account").setNeutralButton("Close", null).show();
        }
    }

}

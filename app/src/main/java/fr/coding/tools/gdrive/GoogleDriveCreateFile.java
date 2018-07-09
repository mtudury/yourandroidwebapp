package fr.coding.tools.gdrive;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Task;

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


    public GoogleDriveCreateFile(GoogleSignInClient gApiClient, Activity activity) {
        super(gApiClient, activity, "GoogleDriveCreateFile");
    }

    public void CreateFile(String fileName, String content, String mimetype, Callback<String> callback) {
        writeContents = content;
        CreateFile(new MetadataChangeSet.Builder()
                .setTitle(fileName)
                .setMimeType(mimetype)
                .build(), callback);
    }

    public void CreateFile(MetadataChangeSet cs, Callback<String> callback) {
        writeCallback = callback;
        changeSet = cs;

        DriveResourceClient drc = Drive.getDriveResourceClient(activity, GoogleSignIn.getLastSignedInAccount(activity));
        Task<DriveContents> tdc = drc.createContents();
        tdc.addOnSuccessListener(ldriveContents -> {
            final DriveContents driveContents = ldriveContents;

            // Perform I/O off the UI thread.
            new Thread() {
                @Override
                public void run() {
                    // write content to DriveContents
                    OutputStream outputStream = driveContents.getOutputStream();
                    if (outputStream != null) {
                        Writer writer = new OutputStreamWriter(outputStream);
                        try {
                            writer.write(writeContents);
                            writer.close();
                        } catch (IOException e) {
                            Log.e("AppSettingsManager", e.getMessage());
                        }


                        // create a file on root folder
                        drc.createFile(drc.getAppFolder(), changeSet, driveContents);
                    }
                }
            }.start();

        }).addOnFailureListener( failure -> {
            new AlertDialog.Builder(activity).setTitle("Error Saving Settings").setMessage(failure.getLocalizedMessage()).setNeutralButton("Close", null).show();
        });

    }

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

package fr.coding.tools.gdrive;

import android.app.Activity;
import android.app.AlertDialog;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by Matthieu on 01/11/2015.
 */
public class GoogleDriveFindFile extends GoogleDriveBaseTools {

    public GoogleDriveFindFile(GoogleSignInClient gApiClient, Activity activity) {
        super(gApiClient, activity, "GoogleDriveFindFile");
    }

    public void FindFileAppFolder(String filename, OnSuccessListener<MetadataBuffer> callback) {
        GoogleSignInAccount gsa = GoogleSignIn.getLastSignedInAccount(activity);
        if (gsa != null) {
            DriveResourceClient drc = Drive.getDriveResourceClient(activity, gsa);
            drc.getRootFolder().addOnSuccessListener(
                    result -> {
                        FindFile(result,
                                new Query.Builder()
                                        .addFilter(Filters.eq(SearchableField.TITLE, filename))
                                        .build(),
                                callback);

                    }
            ).addOnFailureListener(failure -> {
                new AlertDialog.Builder(activity).setTitle("Error retry later").setMessage(failure.getLocalizedMessage()).setNeutralButton("Close", null).show();
            });
        } else {
            new AlertDialog.Builder(activity).setTitle("NotConnected").setMessage("Go to google drive settings to link to an account").setNeutralButton("Close", null).show();
        }
    }

    public void FindFileRootFolder(String filename, OnSuccessListener<MetadataBuffer> callback) {
        GoogleSignInAccount gsa = GoogleSignIn.getLastSignedInAccount(activity);
        if (gsa != null) {
            DriveResourceClient drc = Drive.getDriveResourceClient(activity, gsa);
            drc.query(
                    new Query.Builder()
                            .addFilter(Filters.eq(SearchableField.TITLE, filename))
                            .build()).addOnSuccessListener(callback).addOnFailureListener(failure -> {
                new AlertDialog.Builder(activity).setTitle("Error retry later").setMessage(failure.getLocalizedMessage()).setNeutralButton("Close", null).show();
            });
        } else {
            new AlertDialog.Builder(activity).setTitle("NotConnected").setMessage("Go to google drive settings to link to an account").setNeutralButton("Close", null).show();
        }
    }

    public void FindFile(DriveFolder folder, String filename, OnSuccessListener<MetadataBuffer> callback) {
        FindFile(folder,
                new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, filename))
                        .build(),
                callback);
    }

    public void FindFile(DriveFolder folder, Query query, OnSuccessListener<MetadataBuffer> callback) {
        GoogleSignInAccount gsa = GoogleSignIn.getLastSignedInAccount(activity);
        if (gsa != null) {
            DriveResourceClient drc = Drive.getDriveResourceClient(activity, gsa);
            drc.queryChildren(folder, query).addOnSuccessListener(callback).addOnFailureListener(failure -> {
                new AlertDialog.Builder(activity).setTitle("Error retry later").setMessage(failure.getLocalizedMessage()).setNeutralButton("Close", null).show();
            });
        } else {
            new AlertDialog.Builder(activity).setTitle("NotConnected").setMessage("Go to google drive settings to link to an account").setNeutralButton("Close", null).show();
        }

    }
}

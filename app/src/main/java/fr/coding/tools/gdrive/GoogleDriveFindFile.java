package fr.coding.tools.gdrive;

import android.app.Activity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

/**
 * Created by Matthieu on 01/11/2015.
 */
public class GoogleDriveFindFile extends GoogleDriveBaseTools {

    public GoogleDriveFindFile(GoogleApiClient gApiClient, Activity activity) {
        super(gApiClient, activity);
    }

    public void FindFileAppFolder(String filename, ResultCallback<DriveApi.MetadataBufferResult> callback) {
        FindFile(Drive.DriveApi.getAppFolder(googleApiClient),
                new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, filename))
                        .build(),
                callback);
    }

    public void FindFileRootFolder(String filename, ResultCallback<DriveApi.MetadataBufferResult> callback) {
        FindFile(Drive.DriveApi.getRootFolder(googleApiClient),
                new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, filename))
                        .build(),
                callback);
    }

    public void FindFile(DriveFolder folder, String filename, ResultCallback<DriveApi.MetadataBufferResult> callback) {
        FindFile(folder,
                new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, filename))
                        .build(),
                callback);
    }

    public void FindFile(DriveFolder folder, Query query, ResultCallback<DriveApi.MetadataBufferResult> callback) {
        folder.queryChildren(googleApiClient, query)
                .setResultCallback(callback);
    }
}

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
public class GoogleDriveBaseTools {

    protected String TAG;

    protected GoogleApiClient googleApiClient;

    protected Activity activity;

    public GoogleDriveBaseTools(GoogleApiClient gApiClient, Activity activity, String LogTAG) {
        googleApiClient = gApiClient;
        this.activity = activity;
        this.TAG = LogTAG;
    }

    public GoogleApiClient getGoogleApiClient() { return googleApiClient; }
}

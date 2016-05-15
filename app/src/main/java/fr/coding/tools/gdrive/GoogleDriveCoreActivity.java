package fr.coding.tools.gdrive;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.plus.Plus;

/**
 * Created by Matthieu on 07/11/2015.
 *
 * base is from https://raw.githubusercontent.com/googledrive/android-demos/master/app/src/main/java/com/google/android/gms/drive/sample/demo/BaseDemoActivity.java
 */
public class GoogleDriveCoreActivity extends GoogleDriveBaseTools implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Next available request code.
     */
    protected static final int NEXT_AVAILABLE_REQUEST_CODE = 2;

    /**
     * InAuth */
    private boolean mIsInAuth;

    public GoogleDriveCoreActivity(Activity activity, String TAG) {
        super(null, activity, TAG);
    }

    public void onResume() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .addApi(Drive.API)
                    .addApi(Plus.API)
                    .addScope(new Scope(Scopes.PROFILE))
                    .addScope(new Scope(Scopes.EMAIL))
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        googleApiClient.connect();
    }

    /**
     * Handles resolution callbacks.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (googleApiClient != null) {
            if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == activity.RESULT_OK) {
                googleApiClient.connect();
            }
        }
    }

    public void onPause() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    /**
     * Called when {@code googleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        if (activity instanceof GoogleApiClient.ConnectionCallbacks)
            ((GoogleApiClient.ConnectionCallbacks)activity).onConnected(connectionHint);
        else
            Log.i(TAG, "GoogleApiClient connected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!mIsInAuth) {
            if (!result.hasResolution()) {
                // show the localized error dialog.
                GoogleApiAvailability.getInstance().getErrorDialog(activity, result.getErrorCode(), 0).show();
                return;
            }
            try {
                mIsInAuth = true;
                result.startResolutionForResult(activity, REQUEST_CODE_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Exception while starting resolution activity", e);
            }
        } else {
            GoogleApiAvailability.getInstance().getErrorDialog(activity, result.getErrorCode(), 0).show();
        }
    }


    /**
     * Called when {@code googleApiClient} is disconnected.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    /**
     * Shows a toast message.
     */
    public void showMessage(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }
}

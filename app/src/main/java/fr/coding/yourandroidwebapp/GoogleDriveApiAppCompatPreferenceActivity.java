package fr.coding.yourandroidwebapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import fr.coding.tools.gdrive.GoogleDriveCoreActivity;
import fr.coding.yourandroidwebapp.AppCompatPreferenceActivity;

/**
 * Created by Matthieu on 31/10/2015.
 *
 * Java doesn't support inheritence from generics <T>, would be usefull there, i need to duplicate code
 */
public abstract class GoogleDriveApiAppCompatPreferenceActivity extends AppCompatPreferenceActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "GoogleDriveApiAppCompatPreferenceActivity";

    protected static final int NEXT_AVAILABLE_REQUEST_CODE = GoogleDriveCoreActivity.NEXT_AVAILABLE_REQUEST_CODE;

    protected GoogleDriveCoreActivity coreActivity;

    protected boolean autoConnect = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coreActivity = new GoogleDriveCoreActivity(this, TAG);
    }

    /**
     * Called when activity gets visible. A connection to Drive services need to
     * be initiated as soon as the activity is visible. Registers
     * {@code ConnectionCallbacks} and {@code OnConnectionFailedListener} on the
     * activities itself.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (autoConnect)
            coreActivity.onResume();
    }

    /*
    Do not connect automatically on startup, but once needed,
    Connect and enter back in normal cycle (autoconnect) :
    suspend when going to another activity, restore connection...
     */
    protected void manualConnect() {
        autoConnect = true;
        coreActivity.onResume();
    }

    protected  void Connect() {
        coreActivity.connect();
    }

    /**
     * Handles resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        coreActivity.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Called when activity gets invisible. Connection to Drive service needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onPause() {
        coreActivity.onPause();
        super.onPause();
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        Log.i(TAG, "GoogleApiClient connected");
    }

    /**
     * Called when {@code mGoogleApiClient} is disconnected.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution is
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        coreActivity.onConnectionFailed(result);
    }



    /**
     * Getter for the {@code GoogleApiClient}.
     */
    public GoogleSignInClient getGoogleApiClient() {
        return coreActivity.getGoogleApiClient();
    }


}
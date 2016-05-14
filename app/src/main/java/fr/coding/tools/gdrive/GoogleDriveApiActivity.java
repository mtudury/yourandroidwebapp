package fr.coding.tools.gdrive;


import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.io.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.*;
import com.google.android.gms.drive.DriveApi.*;
import com.google.android.gms.drive.query.*;

/**
 * Created by Matthieu on 31/10/2015.
 *
 * base is from https://raw.githubusercontent.com/googledrive/android-demos/master/app/src/main/java/com/google/android/gms/drive/sample/demo/BaseDemoActivity.java
 *
 * Java doesn't support inheritence from generics <T>, would be usefull there, i need to duplicate code between Activity Types
 */
public abstract class GoogleDriveApiActivity extends Activity  {

    private static final String TAG = "GoogleDriveApiActivity";

    protected GoogleDriveCoreActivity coreActivity;

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
        coreActivity.onResume();
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
     * Getter for the {@code GoogleApiClient}.
     */
    public GoogleApiClient getGoogleApiClient() {
        return coreActivity.getGoogleApiClient();
    }


}
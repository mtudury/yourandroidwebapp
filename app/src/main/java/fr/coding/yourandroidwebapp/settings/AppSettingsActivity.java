package fr.coding.yourandroidwebapp.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;

import fr.coding.tools.gdrive.GoogleDriveCoreActivity;

/**
 * Created by Matthieu on 07/11/2015.
 */
public class AppSettingsActivity extends AppCompatActivity
        implements AppSettingsCallback {

    private static final String TAG = "GoogleDriveApiActivity";

    protected AppSettingsActivityHelper coreActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coreActivity = new AppSettingsActivityHelper(this, this, TAG);
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

    public void onAppSettingsReady(AppSettings settings) {

    }

    public AppSettings getAppSettings() {
        return coreActivity.getAppSettings();
    }

    public AppSettingsManager getSettingManager() {
        return coreActivity.getSettingManager();
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

    public void SaveSettings(AppSettings settings) {
        coreActivity.Save(settings);
    }

}

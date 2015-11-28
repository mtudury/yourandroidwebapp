package fr.coding.yourandroidwebapp.settings;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import fr.coding.tools.Callback;
import fr.coding.tools.gdrive.GoogleDriveCoreActivity;

/**
 * Created by Matthieu on 07/11/2015.
 */
public class AppSettingsActivityHelper extends GoogleDriveCoreActivity implements AppSettingsCallback {

    private boolean UseGDrive;

    protected AppSettings appSettings;

    protected AppSettingsManager appSettingsManager;

    protected AppSettingsCallback appSettingsCallback;

    public AppSettingsActivityHelper(Activity activity, AppSettingsCallback appSettingsCallback, String TAG) {
        super(activity, TAG);
        UseGDrive = AppSettingsManager.IsSettingsInGdrive(activity);
        appSettingsManager = new AppSettingsManager(activity);
        this.appSettingsCallback = appSettingsCallback;
    }

    @Override
    public void onResume() {
        if (UseGDrive) {
            super.onResume();
        } else {
            appSettings = appSettingsManager.LoadSettingsLocally();
            appSettingsCallback.onAppSettingsReady(appSettings);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        appSettingsManager.LoadSettings(googleApiClient, this);
    }

    public void getSettings() {
        appSettingsManager.LoadSettings(googleApiClient, this);
    }

    public AppSettings getAppSettings() {
        return appSettings;
    }

    public AppSettingsManager getSettingManager() {
        return appSettingsManager;
    }

    public void Save(AppSettings settings, Callback<String> saveHandler) {
        this.appSettings = settings;
        if (UseGDrive)
            appSettingsManager.Save(settings, getGoogleApiClient(), saveHandler);
        else {
            String res = appSettingsManager.SaveSettingsLocally(settings);
            if (saveHandler != null)
                saveHandler.onCallback(res);
        }
    }

    public void Save(AppSettings settings) {
        Save(settings, null);
    }

    @Override
    public void onAppSettingsReady(AppSettings settings) {
        appSettings = settings;
        appSettingsCallback.onAppSettingsReady(appSettings);
    }
}

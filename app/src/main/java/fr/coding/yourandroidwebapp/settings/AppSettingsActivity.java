package fr.coding.yourandroidwebapp.settings;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Matthieu on 07/11/2015.
 */
public class AppSettingsActivity extends AppCompatActivity {

    public AppSettings getAppSettings() {
        return AppSettingsManager.LoadSettingsLocally(this);
    }

    public void SaveSettings(AppSettings settings) {
        AppSettingsManager setmgr = new AppSettingsManager(this);
        setmgr.SaveSettingsLocally(settings);
    }

}

package fr.coding.yourandroidwebapp.settings;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import fr.coding.yourandroidwebapp.R;

public class AdvancedSettingsActivity extends AppSettingsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.pref_advancedsettings, rootKey);
        }

        public void updateShownSettings() {
            final AppSettingsActivity activity = (AppSettingsActivity)getActivity();
            final AppSettings sett = activity.getAppSettings();
            final AdvancedAppSettings advSettings = sett.Advanced;

            CheckBoxPreference setplaybackrg = (CheckBoxPreference)findPreference("webview_disable_playback_require_gesture");
            if (setplaybackrg != null) {
                setplaybackrg.setChecked(advSettings.disableMediasRequireUserGesture);
                setplaybackrg.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        advSettings.disableMediasRequireUserGesture = (boolean)newValue;

                        activity.SaveSettings(sett);
                        return true;
                    }

                });
            }

            CheckBoxPreference forceDownloadViewerChooser = (CheckBoxPreference)findPreference("webview_force_downloadviewer_chooser");
            if (forceDownloadViewerChooser != null) {
                forceDownloadViewerChooser.setChecked(advSettings.forceDownloadViewerChooser);
                forceDownloadViewerChooser.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        advSettings.forceDownloadViewerChooser = (boolean)newValue;

                        activity.SaveSettings(sett);
                        return true;
                    }

                });
            }

            CheckBoxPreference allowgeolocPref = (CheckBoxPreference)findPreference("webview_allow_geoloc");
            if (allowgeolocPref != null) {
                allowgeolocPref.setChecked(advSettings.allowGeoloc);
                allowgeolocPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        advSettings.allowGeoloc = (boolean)newValue;

                        activity.SaveSettings(sett);
                        return true;
                    }

                });
            }

            EditTextPreference prefuseragent = (EditTextPreference)findPreference("webview_user_agent");
            if (prefuseragent != null) {
                prefuseragent.setText(advSettings.userAgent);
                prefuseragent.setSummary(advSettings.userAgent);
                prefuseragent.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        EditTextPreference preference1 = (EditTextPreference)preference;
                        advSettings.userAgent = (String)newValue;
                        preference1.setSummary(advSettings.userAgent);

                        activity.SaveSettings(sett);
                        return true;
                    }

                });
            }
        }
    }
}
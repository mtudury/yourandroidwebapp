package fr.coding.yourandroidwebapp;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.*;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fr.coding.tools.Callback;
import fr.coding.tools.filedialog.SimpleFileDialog;
import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsManager;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class GoogleDriveSettingsActivity extends GoogleDriveApiAppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    private static final int REQUEST_CODE_OPENER = 1;


    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        addPreferencesFromResource(R.xml.pref_googledrivesettings);

/*        EditTextPreference pref = (EditTextPreference)findPreference("google_drive_path_custom");
        if (pref != null) {
            bindPreferenceSummaryToValue(pref);
        }*/

        Preference pref2 = findPreference("google_drive_path_custom");
        if (pref2 != null) {
            pref2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if ((getGoogleApiClient() != null)&&(getGoogleApiClient().isConnected())) {
                        IntentSender intentSender = Drive.DriveApi
                                .newOpenFileActivityBuilder()
                                .setMimeType(new String[]{DriveFolder.MIME_TYPE})
                                .build(getGoogleApiClient());
                        try {
                            startIntentSenderForResult(
                                    intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            //Log.w(TAG, "Unable to send intent", e);
                        }
                    }
                    else
                    {
                        Toast.makeText(preference.getContext(), "Currently connecting, retry in seconds", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }


            });
        }

        Preference pref3 = findPreference("google_drive_account");
        if (pref3 != null) {
            pref3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if ((getGoogleApiClient() != null)&&(getGoogleApiClient().isConnected())) {
                        Plus.AccountApi.clearDefaultAccount(getGoogleApiClient());
                        return true;
                    }
                    else
                    {
                        Toast.makeText(preference.getContext(), "Currently connecting, retry in seconds", Toast.LENGTH_LONG).show();
                    }
                    return false;
                }


            });
        }

        Preference pref4 = findPreference("local_export");
        if (pref4 != null) {
            final GoogleDriveSettingsActivity ctx = this;
            pref4.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SimpleFileDialog FolderChooseDialog =  new SimpleFileDialog(ctx, "FileSave",
                            new SimpleFileDialog.SimpleFileDialogListener()
                            {
                                @Override
                                public void onChosenDir(String chosenDir)
                                {
                                    // The code in this function will be executed when the dialog OK button is pushed
                                    AppSettingsManager manager = new AppSettingsManager(ctx);
                                    manager.ExportSettingsLocally(manager.LoadSettingsLocally(), chosenDir);
                                    Toast.makeText(ctx, R.string.webapp_imported_toast, Toast.LENGTH_SHORT).show();
                                }
                            });

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    FolderChooseDialog.Default_File_Name = "appsettings_backup_"+ format.format(new Date())+".json";
                    FolderChooseDialog.chooseFile_or_Dir();
                    return false;
                }


            });
        }

        Preference preflocalimport = findPreference("local_import");
        if (preflocalimport != null) {
            final GoogleDriveSettingsActivity ctx = this;
            preflocalimport.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SimpleFileDialog FolderChooseDialog =  new SimpleFileDialog(ctx, "FileOpen",
                            new SimpleFileDialog.SimpleFileDialogListener()
                            {
                                @Override
                                public void onChosenDir(String chosenDir)
                                {
                                    // The code in this function will be executed when the dialog OK button is pushed
                                    AppSettingsManager manager = new AppSettingsManager(ctx);
                                    AppSettings settings = manager.ImportSettingsLocally(chosenDir);
                                    manager.Save(settings, getGoogleApiClient(), new Callback<String>() {
                                        @Override
                                        public void onCallback(String arg) {
                                            Toast.makeText(ctx, R.string.webapp_imported_toast, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                    FolderChooseDialog.chooseFile_or_Dir();
                    return false;
                }


            });
        }
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    localdriveId = driveId;
                    //showMessage("Selected folder's ID: " + driveId);
                }

                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private DriveId localdriveId;

    //Toast.makeText(this, Drive.DriveApi.getFolder(getGoogleApiClient(), driveId).toString(), Toast.LENGTH_LONG).show();
    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        //todo renseigner le google account dans la pref : google_drive_account
        Preference pref = findPreference("google_drive_account");
        if (pref != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(getGoogleApiClient());
            if (currentPerson!=null) {
                String DisplayName = currentPerson.getDisplayName();
                pref.setSummary(DisplayName + " (" + Plus.AccountApi.getAccountName(getGoogleApiClient()) + ")");
            }
            else {
                pref.setSummary(Plus.AccountApi.getAccountName(getGoogleApiClient()));
            }
        }

        if (localdriveId != null) {
            final Context act = this;
            Drive.DriveApi.getFolder(getGoogleApiClient(), localdriveId).getMetadata(getGoogleApiClient()).setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
                @Override
                public void onResult(DriveResource.MetadataResult mdRslt) {
                    if (mdRslt != null && mdRslt.getStatus().isSuccess()) {
                        Toast.makeText(act, mdRslt.getMetadata().getTitle(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }
    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        //loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_googledrivesettings);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), GoogleDriveSettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), GoogleDriveSettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), GoogleDriveSettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}

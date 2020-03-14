package fr.coding.yourandroidwebapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import fr.coding.tools.Perms;
import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsManager;

public class SettingsActivity extends AppCompatActivity {

    protected static final int QUERY_EXPORT = 100;
    protected static final int REQUEST_CODE_EXPORT = QUERY_EXPORT + 1;

    protected static final int QUERY_IMPORT = REQUEST_CODE_EXPORT + 1;
    protected static final int REQUEST_CODE_IMPORT = QUERY_IMPORT + 1;


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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_EXPORT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportToSD();
                } else {
                    Toast.makeText(this, "SD write permission denied, Trying without rights", Toast.LENGTH_SHORT).show();
                    exportToSD();
                }
                break;
            case REQUEST_CODE_IMPORT:
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    importFromSD();
                } else {
                    Toast.makeText(this, "SD read permission denied, aborting", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == QUERY_EXPORT && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();

            //just as an example, I am writing a String to the Uri I received from the user:
            AppSettingsManager.ExportSettingsToExternalStorage(this, AppSettingsManager.LoadSettingsLocally(this), uri);
            Snackbar.make(findViewById(android.R.id.content), "Settings exported", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }


        if(requestCode == QUERY_IMPORT && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();

            //just as an example, I am writing a String to the Uri I received from the user:
            AppSettings settings = AppSettingsManager.LoadSettingsFromExternalStorage (this, uri);
            AppSettingsManager.SaveSettingsLocally(this, settings);
            Snackbar.make(findViewById(android.R.id.content), "Settings imported", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private boolean exportToSD() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, "appsettings_backup_" + format.format(new Date()) + ".json");
        startActivityForResult(intent, QUERY_EXPORT);

        return true;

    }

    private boolean importFromSD() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        startActivityForResult(intent, QUERY_IMPORT);

        return true;

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference pref4 = findPreference("local_export");
            if (pref4 != null) {
                final SettingsActivity ctx = (SettingsActivity)getActivity();
                pref4.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if (Perms.checkWriteSDPermission(ctx)) {
                            return ctx.exportToSD();
                        } else {
                            Perms.requestWriteSDPermission(ctx, REQUEST_CODE_EXPORT);
                            return false;
                        }

                    }


                });
            }

            Preference preflocalimport = findPreference("local_import");
            if (preflocalimport != null) {
                final SettingsActivity ctx = (SettingsActivity)getActivity();
                preflocalimport.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if (Perms.checkReadSDPermission(ctx)) {
                            return ctx.importFromSD();
                        } else {
                            Perms.requestReadSDPermission(ctx, REQUEST_CODE_IMPORT);
                            return false;
                        }
                    }


                });
            }
        }


    }
}
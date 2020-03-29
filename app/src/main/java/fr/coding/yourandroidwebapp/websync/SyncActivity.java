package fr.coding.yourandroidwebapp.websync;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import fr.coding.yourandroidwebapp.R;
import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SyncActivity extends AppCompatActivity {

    public static Dialog progress;

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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.sync_settings, rootKey);

            Preference pref_downnow = findPreference("sync_download_now");
            if (pref_downnow != null) {
                pref_downnow.setOnPreferenceClickListener(preference -> {
                    showProgressDialog(getContext());
                    download(getContext());
                    return true;
                });
            }

            Preference pref_upnow = findPreference("sync_upload_now");
            if (pref_upnow != null) {
                pref_upnow.setOnPreferenceClickListener(preference -> {
                    showProgressDialog(getContext());
                    upload();
                    return true;
                });
            }

        }


        private boolean upload() {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    String url = AppSettingsManager.getSettingUploadUrl(getContext());

                    if (url == null) {
                        Snackbar.make(getView(), "ERROR: set at least an URL", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        hideProgressDialog();
                        return;
                    }

                    Request.Builder requestbuilder = new Request.Builder()
                            .url(url)
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json");

                    String headkey = AppSettingsManager.getSettingUploadHeaderKey(getContext());
                    String headval = AppSettingsManager.getSettingUploadHeaderValue(getContext());
                    if ((headkey != null) && (headval != null) && (!headkey.isEmpty()) && (!headval.isEmpty())) {
                        requestbuilder.addHeader(headkey, headval);
                    }

                    AppSettings settings = AppSettingsManager.LoadSettingsLocally(getContext());

                    RequestBody body = null;
                    try {
                        body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), settings.AppSettingsToJSONobj().toString());
                    } catch (JSONException err) {
                        Snackbar.make(getView(), "ERROR: getting settings", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        hideProgressDialog();
                        return;
                    }

                    Request req = null;
                    if (AppSettingsManager.getSettingUploadMethod(getContext()).contentEquals("PUT")) {
                        req = requestbuilder.put(body).build();
                    } else {
                        req = requestbuilder.post(body).build();
                    }

                    OkHttpClient client = new OkHttpClient.Builder().build();
                    Response response = null;

                    try {
                        response = client.newCall(req).execute();
                    } catch (IOException err) {
                        Snackbar.make(getView(), "ERROR: Uploading" + err.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        hideProgressDialog();
                        return;
                    }

                    if (response.isSuccessful()) {

                        Snackbar.make(getView(), "Settings uploaded", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(getView(), "Error uploading : HTTP "+response.code()+ " "+response.message(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    }
                    hideProgressDialog();
                }
            });
            thread.start();
            return true;
        }
    }


    public static void download(Context context) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                String url = AppSettingsManager.getSettingDownloadUrl(context);

                if (url == null) {
                    View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
                    Snackbar.make(rootView, "ERROR: set at least an URL", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    hideProgressDialog();
                    return;
                }

                Request.Builder requestbuilder = new Request.Builder()
                        .url(url)
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .get();

                String headkey = AppSettingsManager.getSettingDownloadHeaderKey(context);
                String headval = AppSettingsManager.getSettingDownloadHeaderValue(context);
                if ((headkey != null) && (headval != null) && (!headkey.isEmpty()) && (!headval.isEmpty())) {
                    requestbuilder.addHeader(headkey, headval);
                }


                Request req = requestbuilder.build();

                OkHttpClient client = new OkHttpClient.Builder().build();
                Response response = null;

                try {
                    response = client.newCall(req).execute();
                } catch (IOException err) {
                    View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
                    Snackbar.make(rootView, "ERROR: Downloading" + err.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    hideProgressDialog();
                    return;
                }

                View rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
                if (response.isSuccessful()) {

                    RequestBody body = null;
                    try {
                        AppSettings settings = AppSettings.JSONobjToAppSettings(new JSONObject(response.body().string()));
                        AppSettingsManager.SaveSettingsLocally(context, settings);
                    } catch (JSONException err) {
                        Snackbar.make(rootView, "ERROR: parsing downloaded settings", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        hideProgressDialog();
                        return;
                    } catch (IOException err) {
                        Snackbar.make(rootView, "ERROR: parsing downloaded settings", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        hideProgressDialog();
                        return;

                    }


                    Snackbar.make(rootView, "Settings downloaded", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(rootView, "Error downloading : HTTP "+response.code()+ " "+response.message(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
                hideProgressDialog();
            }
        });
        thread.start();
    }


    public static void showProgressDialog(Context ctx) {
        hideProgressDialog();

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setView(R.layout.progress);
        SyncActivity.progress = builder.create();
        SyncActivity.progress.show();
    }


    public static void hideProgressDialog() {
        if (SyncActivity.progress != null) {
            SyncActivity.progress.dismiss();
            SyncActivity.progress = null;
        }
    }

}
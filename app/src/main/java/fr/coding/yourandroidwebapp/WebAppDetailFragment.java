package fr.coding.yourandroidwebapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.UUID;

import fr.coding.tools.Callback;
import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsActivity;
import fr.coding.yourandroidwebapp.settings.WebApp;

/**
 * A fragment representing a single WebApp detail screen.
 * This fragment is either contained in a {@link WebAppListActivity}
 * in two-pane mode (on tablets) or a {@link WebAppDetailActivity}
 * on handsets.
 */
public class WebAppDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";


    public String item_id;

    /**
     * The item.
     */
    private WebApp mItem;

    /*
      Settings
     */
    private AppSettingsActivity activity;
    public AppSettings settings;

    private View rootView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WebAppDetailFragment() {
    }

    public void onSettingsReceived(AppSettings appSettings) {
        settings = appSettings;

        if (item_id != null) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            if (settings != null)
                mItem = settings.getWebAppById(item_id);

            if (mItem == null) {
                mItem = new WebApp();
                mItem.id = UUID.randomUUID().toString();
                mItem.allowedSSlActivated = true;
                mItem.autoAuth = true;
                mItem.reloadOnConnectionChange = false;
                settings.WebApps.add(mItem);
            }

            // Show the dummy content as text in a TextView.
            if ((mItem != null) && (rootView != null)) {
                fillItem();

                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    String name = mItem.name;
                    if ((name == null) || (name == ""))
                        name = "+new";
                    appBarLayout.setTitle(name);

                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((AppSettingsActivity) getActivity());
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            item_id = getArguments().getString(ARG_ITEM_ID);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_webapp_detail, container, false);


        if (activity != null) {
            if (activity instanceof WebAppListActivity) {
                Button button = (Button) rootView.findViewById(R.id.webapp_save_button);
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveWebApp(v);
                    }
                });
            } else {
                ((WebAppDetailActivity) activity).fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveWebApp(v);
                    }
                });
            }
        }


        Button buttonCreateShortcut = (Button) rootView.findViewById(R.id.webapp_create_shortcut);
        buttonCreateShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createShortcut(v);
            }
        });

        Button buttonLaunchWebApp = (Button) rootView.findViewById(R.id.webapp_launch_webapp);
        buttonLaunchWebApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View rootView = getView();
                getItem(rootView);

                activity.SaveSettings(settings, new Callback<String>() {
                    @Override
                    public void onCallback(String res) {
                        Intent detailIntent = new Intent(activity, WebMainActivity.class);
                        detailIntent.putExtra("webappid", mItem.id);
                        startActivity(detailIntent);
                    }
                });
            }
        });

        Button buttonDelete = (Button) rootView.findViewById(R.id.webapp_delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = v.getContext();

                new AlertDialog.Builder(ctx)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(ctx.getString(R.string.dialog_title_delete))
                        .setMessage(ctx.getString(R.string.dialog_message_delete_webapp))
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                settings.WebApps.remove(mItem);
                                activity.SaveSettings(settings, new Callback<String>() {
                                    @Override
                                    public void onCallback(String res) {
                                        Intent intent = new Intent(v.getContext(),SettingsActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                });
                            }

                        })
                        .show();
            }
        });

        Button buttonDuplicate = (Button) rootView.findViewById(R.id.webapp_duplicate);
        buttonDuplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = v.getContext();

                new AlertDialog.Builder(ctx)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(ctx.getString(R.string.dialog_title_duplicate))
                        .setMessage(ctx.getString(R.string.dialog_message_duplicate_webapp))
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                settings.WebApps.add(mItem.Duplicate());
                                activity.SaveSettings(settings, new Callback<String>() {
                                    @Override
                                    public void onCallback(String res) {
                                        activity.finish();
                                        startActivity(activity.getIntent());
                                    }
                                });
                            }

                        })
                        .show();
            }
        });


        if (settings != null) {
            onSettingsReceived(settings);
        }

        // cachemode
        Spinner cachemode = (Spinner) rootView.findViewById(R.id.webapp_cache_mode);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(),
                R.array.cache_mode, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cachemode.setAdapter(adapter);

        // pinchzoommode
        Spinner pinchzoommode = (Spinner) rootView.findViewById(R.id.webapp_pinchzoom_mode);
        ArrayAdapter<CharSequence> pzadapter = ArrayAdapter.createFromResource(rootView.getContext(),
                R.array.pinchzoom_mode, android.R.layout.simple_spinner_item);
        pzadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pinchzoommode.setAdapter(pzadapter);

        return rootView;
    }


    public void saveWebApp(View view) {
        View rootView = view.getRootView();
        getItem(rootView);

        activity.SaveSettings(settings);
    }

    private void getItem(View rootView) {
        // base fields
        mItem.name = ((EditText) rootView.findViewById(R.id.webapp_name)).getText().toString();
        mItem.url = ((EditText) rootView.findViewById(R.id.webapp_url)).getText().toString();
        mItem.iconUrl = ((EditText) rootView.findViewById(R.id.webapp_iconurl)).getText().toString();

        // alternate fields
        mItem.alternateUrl = ((EditText) rootView.findViewById(R.id.webapp_alternateurl)).getText().toString();
        mItem.alternateSSIDs = ((EditText) rootView.findViewById(R.id.webapp_alternateurlssid)).getText().toString();

        mItem.alternateUrlNotConnected = ((EditText) rootView.findViewById(R.id.webapp_alternateurloffline)).getText().toString();

        // insecure fields
        mItem.allCertsByPass = ((CheckBox) rootView.findViewById(R.id.webapp_sslall_activated)).isChecked();
        mItem.allowedSSlActivated = ((CheckBox) rootView.findViewById(R.id.webapp_ssl_activated)).isChecked();
        mItem.autoAuth = ((CheckBox) rootView.findViewById(R.id.webapp_autoauth)).isChecked();

        mItem.reloadOnConnectionChange = ((CheckBox) rootView.findViewById(R.id.webapp_reload_connection_change)).isChecked();

        // cache mode
        mItem.cacheMode = SpinnerToCacheMode((Spinner) rootView.findViewById(R.id.webapp_cache_mode));

        // pinchZoomMode
        mItem.pinchZoomMode = ((Spinner) rootView.findViewById(R.id.webapp_pinchzoom_mode)).getSelectedItemPosition();
    }

    private int SpinnerToCacheMode(Spinner spinner) {
        int result = 0;
        switch (spinner.getSelectedItemPosition()) {
            case 0:
                result = WebSettings.LOAD_DEFAULT;
                break;
            case 1:
                result = WebSettings.LOAD_NO_CACHE;
                break;
            case 2:
                result = WebSettings.LOAD_CACHE_ELSE_NETWORK;
                break;
            case 3:
                result = WebSettings.LOAD_CACHE_ONLY;
                break;
        }
        return result;
    }


    private int CacheModeToSpinner(int cacheMode) {
        int result = 0;
        switch (cacheMode) {
            case WebSettings.LOAD_DEFAULT:
                result = 0;
                break;
            case WebSettings.LOAD_NO_CACHE:
                result = 1;
                break;
            case WebSettings.LOAD_CACHE_ELSE_NETWORK:
                result = 2;
                break;
            case WebSettings.LOAD_CACHE_ONLY:
                result = 3;
                break;
        }
        return result;
    }


    private void fillItem() {
        // base fields
        ((EditText) rootView.findViewById(R.id.webapp_name)).setText(mItem.name);
        ((EditText) rootView.findViewById(R.id.webapp_url)).setText(mItem.url);
        ((EditText) rootView.findViewById(R.id.webapp_iconurl)).setText(mItem.iconUrl);

        // alternate fields
        ((EditText) rootView.findViewById(R.id.webapp_alternateurl)).setText(mItem.alternateUrl);
        ((EditText) rootView.findViewById(R.id.webapp_alternateurlssid)).setText(mItem.alternateSSIDs);

        ((EditText) rootView.findViewById(R.id.webapp_alternateurloffline)).setText(mItem.alternateUrlNotConnected);

        // insecure fields
        ((CheckBox) rootView.findViewById(R.id.webapp_sslall_activated)).setChecked(mItem.allCertsByPass);
        ((CheckBox) rootView.findViewById(R.id.webapp_ssl_activated)).setChecked(mItem.allowedSSlActivated);
        ((CheckBox) rootView.findViewById(R.id.webapp_autoauth)).setChecked(mItem.autoAuth);

        ((CheckBox) rootView.findViewById(R.id.webapp_reload_connection_change)).setChecked(mItem.reloadOnConnectionChange);

        // cache mode
        ((Spinner) rootView.findViewById(R.id.webapp_cache_mode)).setSelection(CacheModeToSpinner(mItem.cacheMode));

        // pinchZoomMode
        ((Spinner) rootView.findViewById(R.id.webapp_pinchzoom_mode)).setSelection(mItem.pinchZoomMode);
    }

    public void createShortcut(View view) {
        View rootView = view.getRootView();
        getItem(rootView);

        Toast.makeText(getActivity(), R.string.webapp_shortcutcreating_toast, Toast.LENGTH_SHORT).show();

        activity.SaveSettings(settings);
        mItem.LauncherShortcut(getActivity().getApplicationContext());
        Toast.makeText(getActivity(), R.string.webapp_shortcutcreated_toast, Toast.LENGTH_SHORT).show();
    }

}

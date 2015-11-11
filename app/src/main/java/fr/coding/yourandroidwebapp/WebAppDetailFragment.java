package fr.coding.yourandroidwebapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.UUID;

import fr.coding.tools.gdrive.GoogleDriveApiAppCompatActivity;
import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsActivity;
import fr.coding.yourandroidwebapp.settings.AppSettingsManager;
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

    /**
     * The item.
     */
    private WebApp mItem;

    /*
      Settings
     */
    private AppSettingsActivity activity;
    private AppSettings settings;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WebAppDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((AppSettingsActivity) getActivity());
        settings = activity.getAppSettings();

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            if (settings != null)
                mItem = settings.getWebAppById(getArguments().getString(ARG_ITEM_ID));

            if (mItem == null) {
                mItem = new WebApp();
                mItem.id = UUID.randomUUID().toString();
                mItem.allowedSSlActivated = true;
                mItem.autoAuth = true;
                settings.WebApps.add(mItem);
            }

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                String name = mItem.name;
                if ((name == null)||(name == ""))
                    name = "+new";
                appBarLayout.setTitle(name);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_webapp_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {

            ((EditText)rootView.findViewById(R.id.webapp_name)).setText(mItem.name);
            ((EditText)rootView.findViewById(R.id.webapp_url)).setText(mItem.url);
            ((EditText)rootView.findViewById(R.id.webapp_iconurl)).setText(mItem.iconUrl);
            ((CheckBox)rootView.findViewById(R.id.webapp_sslall_activated)).setChecked(mItem.allCertsByPass);
            ((CheckBox)rootView.findViewById(R.id.webapp_ssl_activated)).setChecked(mItem.allowedSSlActivated);
            ((CheckBox)rootView.findViewById(R.id.webapp_autoauth)).setChecked(mItem.autoAuth);


        }
        if (getActivity() instanceof WebAppListActivity)
        {
            Button button = (Button) rootView.findViewById(R.id.webapp_save_button);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveWebApp(v);
                }
            });
        } else {
            ((WebAppDetailActivity)getActivity()).fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveWebApp(v);
                }
            });
        }


        Button button = (Button) rootView.findViewById(R.id.webapp_create_shortcut);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createShortcut(v);
            }
        });

        return rootView;
    }


    public void saveWebApp(View view) {
        View rootView = view.getRootView();
        getItem(rootView);

        activity.SaveSettings(settings);
    }

    private void getItem(View rootView) {
        mItem.name = ((EditText)rootView.findViewById(R.id.webapp_name)).getText().toString();
        mItem.url = ((EditText)rootView.findViewById(R.id.webapp_url)).getText().toString();
        mItem.iconUrl = ((EditText)rootView.findViewById(R.id.webapp_iconurl)).getText().toString();
        mItem.allCertsByPass = ((CheckBox)rootView.findViewById(R.id.webapp_sslall_activated)).isChecked();
        mItem.allowedSSlActivated = ((CheckBox)rootView.findViewById(R.id.webapp_ssl_activated)).isChecked();
        mItem.autoAuth = ((CheckBox)rootView.findViewById(R.id.webapp_autoauth)).isChecked();
    }

    public void createShortcut(View view) {
        View rootView = view.getRootView();
        getItem(rootView);

        activity.SaveSettings(settings);
        mItem.LauncherShortcut(getActivity().getApplicationContext());
        Toast.makeText(getActivity(), R.string.webapp_shortcutcreated_toast, Toast.LENGTH_SHORT).show();
    }
}

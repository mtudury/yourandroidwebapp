package fr.coding.yourandroidwebapp;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import fr.coding.tools.model.SslByPass;
import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsActivity;

/**
 * A fragment representing a single SSLSetting detail screen.
 * This fragment is either contained in a {@link SSLSettingListActivity}
 * in two-pane mode (on tablets) or a {@link SSLSettingDetailActivity}
 * on handsets.
 */
public class SSLSettingDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";


    public int item_id;
    /**
     * The dummy content this fragment is presenting.
     */
    private SslByPass mItem;

    private AppSettingsActivity activity;
    private AppSettings settings;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SSLSettingDetailFragment() {
    }

    public void onSettingsReceived(AppSettings appSettings) {
        settings = activity.getAppSettings();

        // Load the dummy content specified by the fragment
        // arguments. In a real-world scenario, use a Loader
        // to load content from a content provider.
        mItem = settings.SslByPasses.get(item_id);

        if (mItem != null) {
            View rootView = getView();
            ((TextView) rootView.findViewById(R.id.sslsetting_detail)).setText("Cname : " + mItem.CName + "\nHost : " + mItem.Host);
            ((CheckBox) rootView.findViewById(R.id.sslsetting_detail_activated)).setChecked(mItem.activated);

            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.CName);
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((AppSettingsActivity) getActivity());
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            item_id = getArguments().getInt(ARG_ITEM_ID);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sslsetting_detail, container, false);


        if (getActivity() instanceof SSLSettingListActivity) {
            Button button = (Button) rootView.findViewById(R.id.sslsetting_save_button);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveSslSetting(v);
                }
            });
        } else {
            ((SSLSettingDetailActivity) getActivity()).fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveSslSetting(v);
                }
            });
        }
        return rootView;
    }

    public void saveSslSetting(View view) {
        View rootView = view.getRootView();
        getItem(rootView);

        activity.SaveSettings(settings);
    }

    private void getItem(View rootView) {
        mItem.activated = ((CheckBox) rootView.findViewById(R.id.sslsetting_detail_activated)).isChecked();
    }
}

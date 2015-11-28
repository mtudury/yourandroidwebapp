package fr.coding.yourandroidwebapp;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.coding.tools.model.HostAuth;
import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsActivity;

/**
 * A fragment representing a single HostAuth detail screen.
 * This fragment is either contained in a {@link HostAuthSettingsListActivity}
 * in two-pane mode (on tablets) or a {@link HostAuthSettingsDetailActivity}
 * on handsets.
 */
public class HostAuthSettingsDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    public int item_id;
    /**
     * The dummy content this fragment is presenting.
     */
    private HostAuth mItem;
    private AppSettingsActivity activity;
    private AppSettings settings;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HostAuthSettingsDetailFragment() {
    }

    public void onSettingsReceived(AppSettings appSettings) {
        settings = activity.getAppSettings();

        mItem = settings.HostAuths.get(getArguments().getInt(ARG_ITEM_ID));
        if (mItem != null) {
            ((TextView) getView().findViewById(R.id.hostauth_detail)).setText("Host: "+mItem.Host+"\nLogin: "+mItem.Login);
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.Host);
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
        View rootView = inflater.inflate(R.layout.fragment_hostauth_detail, container, false);

        // Show the dummy content as text in a TextView.


        return rootView;
    }
}

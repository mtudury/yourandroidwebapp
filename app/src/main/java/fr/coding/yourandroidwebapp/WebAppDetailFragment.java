package fr.coding.yourandroidwebapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fr.coding.yourandroidwebapp.dummy.DummyContent;
import fr.coding.yourandroidwebapp.settings.WebApp;
import fr.coding.yourandroidwebapp.settings.WebAppSettings;

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
     * The dummy content this fragment is presenting.
     */
    private WebApp mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WebAppDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = WebAppSettings.getWebAppById(getActivity(), getArguments().getString(ARG_ITEM_ID));
            if (mItem == null)
                mItem = new WebApp();
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

        }

        Button button = (Button) rootView.findViewById(R.id.webapp_save_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWebApp(v);
            }
        });


        button = (Button) rootView.findViewById(R.id.webapp_create_shortcut);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createShortcut(v);
            }
        });

        button = (Button) rootView.findViewById(R.id.webapp_ssl_settings);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sslSettings(v);
            }
        });

        return rootView;
    }


    public void saveWebApp(View view) {
        View rootView = view.getRootView();
        getItem(rootView);

        WebAppSettings.InsertOrUpdate(getActivity(),mItem);
        Toast.makeText(getActivity(), R.string.webapp_saved_toast, Toast.LENGTH_SHORT).show();
    }

    private void getItem(View rootView) {
        mItem.name = ((EditText)rootView.findViewById(R.id.webapp_name)).getText().toString();
        mItem.url = ((EditText)rootView.findViewById(R.id.webapp_url)).getText().toString();
        mItem.iconUrl = ((EditText)rootView.findViewById(R.id.webapp_iconurl)).getText().toString();
    }

    public void createShortcut(View view) {
        View rootView = view.getRootView();
        getItem(rootView);

        WebAppSettings.InsertOrUpdate(getActivity(),mItem);
        WebAppSettings.LauncherShortcut(getActivity().getApplicationContext(), mItem);
        Toast.makeText(getActivity(), R.string.webapp_shortcutcreated_toast, Toast.LENGTH_SHORT).show();
    }

    public void sslSettings(View view) {
        SslSettingsActivityFragment fragment = new SslSettingsActivityFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(this)
                .add(R.id.webapp_detail_container, fragment)
                .commit();
    }

}

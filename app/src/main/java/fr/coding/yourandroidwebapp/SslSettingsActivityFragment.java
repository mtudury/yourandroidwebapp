package fr.coding.yourandroidwebapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class SslSettingsActivityFragment extends Fragment {

    public SslSettingsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (getActivity() instanceof WebAppListActivity) {
            ((WebAppListActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ab.setTitle(ab.getTitle() + " - SSL Settings");
        return inflater.inflate(R.layout.fragment_ssl_settings, container, false);
    }
}

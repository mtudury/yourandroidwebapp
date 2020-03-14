package fr.coding.yourandroidwebapp;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import fr.coding.tools.model.HostAuth;
import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsActivity;
import fr.coding.yourandroidwebapp.settings.AppSettingsManager;
import fr.coding.yourandroidwebapp.ui.main.HostAuthRecyclerViewAdapter;


public class HostAuthActivity  extends AppSettingsActivity implements HostAuthRecyclerViewAdapter.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostauth);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    protected  void onResume() {
        super.onResume();
        AppSettings settings = AppSettingsManager.LoadSettingsLocally(this);

        List<HostAuth> webapps = new ArrayList<>(settings.HostAuths);
        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setAdapter(new HostAuthRecyclerViewAdapter(webapps, this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onListFragmentInteraction(HostAuth item) {

    }

    public void DeleteHostAuth(String id) {
        AppSettings settings = getAppSettings();
        settings.DeleteHostAuthById(id);
        SaveSettings(settings);
        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setAdapter(new HostAuthRecyclerViewAdapter(settings.HostAuths, this));
    }
}

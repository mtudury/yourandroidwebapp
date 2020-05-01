package fr.coding.yourandroidwebapp;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.snackbar.Snackbar;

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

        if (webapps.size() == 0) {
            Snackbar.make(findViewById(android.R.id.content), "This list is empty until you save an authentication while browsing a website", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void onListFragmentInteraction(HostAuth item) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void DeleteHostAuth(String id) {
        AppSettings settings = getAppSettings();
        settings.DeleteHostAuthById(id);
        SaveSettings(settings);
        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setAdapter(new HostAuthRecyclerViewAdapter(settings.HostAuths, this));
    }
}

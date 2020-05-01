package fr.coding.yourandroidwebapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import fr.coding.tools.model.HostAuth;
import fr.coding.tools.model.SslByPass;
import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsActivity;
import fr.coding.yourandroidwebapp.settings.AppSettingsManager;
import fr.coding.yourandroidwebapp.ui.main.CertsRecyclerViewAdapter;
import fr.coding.yourandroidwebapp.ui.main.HostAuthRecyclerViewAdapter;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class CertsActivity  extends AppSettingsActivity implements CertsRecyclerViewAdapter.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certs);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    protected  void onResume() {
        super.onResume();
        AppSettings settings = AppSettingsManager.LoadSettingsLocally(this);

        List<SslByPass> certs = new ArrayList<>(settings.SslByPasses);
        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setAdapter(new CertsRecyclerViewAdapter(certs, this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        if (certs.size() == 0) {
            Snackbar.make(findViewById(android.R.id.content), "This list is empty until you accept a certificate while browsing a website", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

    @Override
    public void onListFragmentInteraction(SslByPass item) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void DeleteCert(String id) {
        AppSettings settings = getAppSettings();
        settings.DeleteCertById(id);
        SaveSettings(settings);
        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setAdapter(new CertsRecyclerViewAdapter(settings.SslByPasses, this));
    }
}

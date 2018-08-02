package fr.coding.yourandroidwebapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsActivity;
import fr.coding.yourandroidwebapp.settings.AppSettingsManager;
import fr.coding.yourandroidwebapp.settings.WebApp;

public class StartupActivity extends AppCompatActivity implements MyWebAppRecyclerViewAdapter.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent = new Intent(view.getContext(), SettingsActivity.class);
                startActivity(detailIntent);
            }
        });


    }


    @Override
    protected  void onResume() {
        super.onResume();
        AppSettingsManager settingsManager = new AppSettingsManager(this);
        AppSettings settings = settingsManager.LoadSettingsLocally();

        List<WebApp> webapps = new ArrayList<>(settings.WebApps);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setAdapter(new MyWebAppRecyclerViewAdapter(webapps, this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onListFragmentInteraction(WebApp item) {
        Intent detailIntent = new Intent(this, WebMainActivity.class);
        detailIntent.putExtra("webappid", item.id);
        startActivity(detailIntent);
    }


}

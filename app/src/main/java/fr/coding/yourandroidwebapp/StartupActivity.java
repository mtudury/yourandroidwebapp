package fr.coding.yourandroidwebapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import fr.coding.yourandroidwebapp.settings.AppSettings;
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
                /*Intent detailIntent = new Intent(view.getContext(), SettingsActivity.class);
                startActivity(detailIntent);*/
            }
        });


    }


    @Override
    protected  void onResume() {
        super.onResume();
        AppSettings settings = AppSettingsManager.LoadSettingsLocally(this);

        List<WebApp> webapps = new ArrayList<>(settings.WebApps);
        RecyclerView recyclerView = findViewById(R.id.list);
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

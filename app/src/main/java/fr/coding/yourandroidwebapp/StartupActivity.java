package fr.coding.yourandroidwebapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.TypedValue;
import android.view.View;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsManager;
import fr.coding.yourandroidwebapp.settings.WebApp;
import fr.coding.yourandroidwebapp.ui.main.WebAppRecyclerViewAdapter;

public class StartupActivity extends AppCompatActivity implements WebAppRecyclerViewAdapter.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((View view) -> {
            Intent detailIntent = new Intent(view.getContext(), SettingsActivity.class);
            startActivity(detailIntent);
        });

        FloatingActionButton createfab = findViewById(R.id.createfab);
        createfab.setOnClickListener(view -> {
            Intent detailIntent = new Intent(view.getContext(), WebAppDetail.class);
            startActivity(detailIntent);
        });

        FloatingActionButton shortcutfab = findViewById(R.id.shortcutfab);
        shortcutfab.setOnClickListener(view -> {
            Intent shortcutIntent = new Intent(this, StartupActivity.class);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            shortcutIntent.setAction("android.intent.category.LAUNCHER");


            ShortcutManager scm = (ShortcutManager)this.getSystemService(Context.SHORTCUT_SERVICE);
            ShortcutInfo.Builder scib = new ShortcutInfo.Builder(this, "yourandroidwebapp_startup")
                    .setShortLabel(getString(R.string.title_statup))
                    .setIntent(shortcutIntent);
            scib.setIcon(Icon.createWithResource(this, R.drawable.ic_format_list_bulleted_black_24dp));

            scm.requestPinShortcut(scib.build(), null);
        });

        FloatingActionButton editfab = findViewById(R.id.editfab);
        editfab.setOnClickListener(view -> {
            createfab.setVisibility(View.VISIBLE);
            editfab.setVisibility(View.GONE);
            shortcutfab.setVisibility(View.VISIBLE);


            updateRVA();

            Snackbar.make(findViewById(android.R.id.content), "Edit Mode", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        });
    }

    protected void setBottomMargin() {
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                80,
                r.getDisplayMetrics()
        );

        NestedScrollView webappcontainer = findViewById(R.id.webapp_list_container);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)webappcontainer.getLayoutParams();
        params.setMargins(0, 0, 0, px);
        webappcontainer.setLayoutParams(params);
    }


    protected void updateRVA() {
        FloatingActionButton createfab = findViewById(R.id.createfab);
        FloatingActionButton editfab = findViewById(R.id.editfab);
        AppSettings settings = AppSettingsManager.LoadSettingsLocally(this);

        if (settings.WebApps.size() == 0) {
            createfab.setVisibility(View.VISIBLE);
            editfab.setVisibility(View.GONE);
        }

        List<WebApp> webapps = new ArrayList<>(settings.WebApps);
        RecyclerView recyclerView = findViewById(R.id.list);
        WebAppRecyclerViewAdapter rva = new WebAppRecyclerViewAdapter(webapps, this);
        if (createfab.getVisibility() == View.VISIBLE) {
            rva.setVisible();
            setBottomMargin();
        }

        recyclerView.setAdapter(rva);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    protected  void onResume() {
        super.onResume();
        updateRVA();
    }

    @Override
    public void onListFragmentInteraction(WebApp item) {
        Intent detailIntent = new Intent(this, WebMainActivity.class);
        detailIntent.putExtra("webappid", item.id);
        startActivity(detailIntent);
    }


}

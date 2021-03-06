package fr.coding.yourandroidwebapp;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.UUID;

import fr.coding.webappsettingsviews.WebAppSettingsAdvancedFragment;
import fr.coding.webappsettingsviews.WebAppSettingsGeneralFragment;
import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsActivity;
import fr.coding.yourandroidwebapp.settings.AppSettingsManager;
import fr.coding.yourandroidwebapp.settings.WebApp;
import fr.coding.yourandroidwebapp.ui.main.SectionsPagerAdapter;

public class WebAppDetail extends AppSettingsActivity {

    WebApp wa = new WebApp();
    SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_app_detail);
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        String webAppId = getIntent().getStringExtra("webappid");
        if (webAppId != null) {
            wa = getAppSettings().getWebAppById(webAppId);
        }
        sectionsPagerAdapter.setWebApp(wa);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SaveWebApp();

                Snackbar.make(view, "WebApp Saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void SaveWebApp() {
        ((WebAppSettingsGeneralFragment)sectionsPagerAdapter.getItem(0)).fillWebAppfromView(wa);
        ((WebAppSettingsAdvancedFragment)sectionsPagerAdapter.getItem(1)).fillWebAppfromView(wa);

        AppSettings settings = getAppSettings();
        settings.UpsertWebApp(wa);
        SaveSettings(settings);
    }

    public void LaunchWebApp() {
        SaveWebApp();

        wa.StartWebApp(this);
    }

    public void DeleteWebApp() {
        AppSettings settings = getAppSettings();
        settings.DeleteWebAppById(wa.id);
        SaveSettings(settings);
    }

    public void CreateShortcutWebApp() {
        SaveWebApp();

        wa.LauncherShortcut(this);
    }

    public void DuplicateWebApp() {
        SaveWebApp();

        AppSettings settings = getAppSettings();
        settings.UpsertWebApp(wa.Duplicate());
        SaveSettings(settings);
    }
}
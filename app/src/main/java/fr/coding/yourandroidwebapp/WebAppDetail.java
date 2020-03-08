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
import fr.coding.yourandroidwebapp.settings.AppSettingsManager;
import fr.coding.yourandroidwebapp.settings.WebApp;
import fr.coding.yourandroidwebapp.ui.main.SectionsPagerAdapter;

public class WebAppDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_app_detail);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        String webAppId = getIntent().getStringExtra("webappid");
        if (webAppId != null) {
            AppSettings settings = AppSettingsManager.LoadSettingsLocally(this);
            sectionsPagerAdapter.setWebApp(settings.getWebAppById(webAppId));
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebApp wa = new WebApp();
                ((WebAppSettingsGeneralFragment)sectionsPagerAdapter.getItem(0)).fillWebAppfromView(wa);
                ((WebAppSettingsAdvancedFragment)sectionsPagerAdapter.getItem(1)).fillWebAppfromView(wa);

                AppSettings settings = AppSettingsManager.LoadSettingsLocally(getApplicationContext());
                settings.UpsertWebApp(wa);
                AppSettingsManager manager = new AppSettingsManager(getApplicationContext());
                manager.SaveSettingsLocally(settings);


                Snackbar.make(view, "WebApp Saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
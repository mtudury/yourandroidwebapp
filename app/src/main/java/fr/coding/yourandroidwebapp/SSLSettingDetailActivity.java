package fr.coding.yourandroidwebapp;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;

import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsActivity;

/**
 * An activity representing a single SSLSetting detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link SSLSettingListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link SSLSettingDetailFragment}.
 */
public class SSLSettingDetailActivity extends AppSettingsActivity implements AppSettingsCallback {

    private Bundle savedInstanceState;

    public FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;

        setContentView(R.layout.activity_sslsetting_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(SSLSettingDetailFragment.ARG_ITEM_ID,
                    getIntent().getIntExtra(SSLSettingDetailFragment.ARG_ITEM_ID, 0));
            SSLSettingDetailFragment fragment = new SSLSettingDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.sslsetting_detail_container, fragment)
                    .commit();
        }

    }

    @Override
    public void onAppSettingsReady(AppSettings settings) {
        SSLSettingDetailFragment frag = (SSLSettingDetailFragment)getSupportFragmentManager().findFragmentById(R.id.sslsetting_detail_container);
        if (frag != null) {
            frag.onSettingsReceived(settings);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, SSLSettingListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

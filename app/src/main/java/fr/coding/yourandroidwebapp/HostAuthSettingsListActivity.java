package fr.coding.yourandroidwebapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.widget.FrameLayout;

import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsActivity;

/**
 * An activity representing a list of HostAuths. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link HostAuthSettingsDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link HostAuthSettingsListFragment} and the item details
 * (if present) is a {@link HostAuthSettingsDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link HostAuthSettingsListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class HostAuthSettingsListActivity extends AppSettingsActivity
        implements HostAuthSettingsListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private boolean allreadyInit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostauth_app_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onAppSettingsReady(AppSettings settings) {
        if (!allreadyInit) {
            FrameLayout flayout = (FrameLayout) findViewById(R.id.frameLayout);
            flayout.addView(getLayoutInflater().inflate(R.layout.activity_hostauth_list, null));

            if (findViewById(R.id.hostauth_detail_container) != null) {
                // The detail container view will be present only in the
                // large-screen layouts (res/values-large and
                // res/values-sw600dp). If this view is present, then the
                // activity should be in two-pane mode.
                mTwoPane = true;

                // In two-pane mode, list items should be given the
                // 'activated' state when touched.
                ((HostAuthSettingsListFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.hostauth_list))
                        .setActivateOnItemClick(true);
            }
            allreadyInit = true;
        }
        // TODO: If exposing deep links into your app, handle intents here.
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            supportNavigateUpTo(getIntent());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method from {@link HostAuthSettingsListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(int id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(HostAuthSettingsDetailFragment.ARG_ITEM_ID, id);
            HostAuthSettingsDetailFragment fragment = new HostAuthSettingsDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.hostauth_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, HostAuthSettingsDetailActivity.class);
            detailIntent.putExtra(HostAuthSettingsDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}

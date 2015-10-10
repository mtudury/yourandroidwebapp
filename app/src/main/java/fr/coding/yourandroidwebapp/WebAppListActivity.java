package fr.coding.yourandroidwebapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.List;

import fr.coding.yourandroidwebapp.settings.WebApp;
import fr.coding.yourandroidwebapp.settings.WebAppSettings;


/**
 * An activity representing a list of WebApps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link WebAppDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link WebAppListFragment} and the item details
 * (if present) is a {@link WebAppDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link WebAppListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class WebAppListActivity extends AppCompatActivity
        implements WebAppListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    public boolean startCreate;

    private String lastId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webapp_list);

        if (findViewById(R.id.webapp_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((WebAppListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.webapp_list))
                    .setActivateOnItemClick(true);
        }
        setTitle(R.string.pref_title_manage_webapps_settings);

        // TODO: If exposing deep links into your app, handle intents here.
        String intentparam = this.getIntent().getDataString();
        if (intentparam != null) {
            startCreate = true;


            ((WebAppListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.webapp_list)).setNewItemClicked();
            onItemSelected("new");
        }
        else {
            startCreate = false;
        }

    }

    /**
     * Callback method from {@link WebAppListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        lastId = id;
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(WebAppDetailFragment.ARG_ITEM_ID, id);
            WebAppDetailFragment fragment = new WebAppDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.webapp_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, WebAppDetailActivity.class);
            detailIntent.putExtra(WebAppDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
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
            if (mTwoPane) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                onItemSelected(lastId);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

package fr.coding.yourandroidwebapp;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * An activity representing a single WebApp detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link WebAppListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link WebAppDetailFragment}.
 */
public class WebAppDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webapp_detail);

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
            arguments.putString(WebAppDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(WebAppDetailFragment.ARG_ITEM_ID));
            WebAppDetailFragment fragment = new WebAppDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.webapp_detail_container, fragment)
                    .commit();
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
            if (getSupportFragmentManager().getFragments().get(0) instanceof WebAppDetailFragment) {
                navigateUpTo(new Intent(this, WebAppListActivity.class));
               return true;
            } else {
                Bundle arguments = new Bundle();
                arguments.putString(WebAppDetailFragment.ARG_ITEM_ID,
                        getIntent().getStringExtra(WebAppDetailFragment.ARG_ITEM_ID));
                WebAppDetailFragment fragment = new WebAppDetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.webapp_detail_container, fragment)
                        .commit();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}

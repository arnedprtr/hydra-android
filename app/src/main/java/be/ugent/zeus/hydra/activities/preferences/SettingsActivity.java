package be.ugent.zeus.hydra.activities.preferences;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.AppCompatPreferenceActivity;
import be.ugent.zeus.hydra.fragments.preferences.*;
import be.ugent.zeus.hydra.notifications.NotificationScheduler;

import java.util.List;

/**
 * Display settings.
 *
 * @author Niko Strijbol
 * @author Rien Maertens
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications. Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || RestoPreferenceFragment.class.getName().equals(fragmentName)
                || HomeFragment.class.getName().equals(fragmentName)
                || MinervaFragment.class.getName().equals(fragmentName)
                || UrgentFragment.class.getName().equals(fragmentName)
                || SkoFragment.class.getName().equals(fragmentName)
                || AboutFragment.class.getName().equals(fragmentName);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //This recreates the main activity instead of resuming. Why?
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onIsMultiPane() {
        return this.getResources().getConfiguration().isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE);
    }

    public void testNotification(View view){
        new NotificationScheduler(this).testNotification();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HydraApplication app = (HydraApplication) getApplication();
        app.sendScreenName("Settings overview");
    }
}
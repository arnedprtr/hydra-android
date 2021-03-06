package be.ugent.zeus.hydra;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.jakewharton.threetenabp.AndroidThreeTen;

/**
 * The Hydra application.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class HydraApplication extends Application {

    private Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            if (BuildConfig.DEBUG) {
                // disable google analytics while debugging
                analytics.setDryRun(true);
            }

            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(R.xml.global_tracker);
        }
        return tracker;
    }

    /**
     * Send a screen name to the analytics.
     *
     * @param screenName The screen name to send.
     */
    public void sendScreenName(String screenName) {
        Tracker t = getDefaultTracker();
        t.setScreenName(screenName);
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }

    /**
     * Get the application from an activity. The application is cast to this class.
     *
     * @param activity The activity.
     *
     * @return The application.
     */
    public static HydraApplication getApplication(@NonNull Activity activity) {
        return (HydraApplication) activity.getApplication();
    }
}
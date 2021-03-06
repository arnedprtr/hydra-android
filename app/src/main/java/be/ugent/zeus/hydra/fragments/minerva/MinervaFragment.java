package be.ugent.zeus.hydra.fragments.minerva;

import android.accounts.*;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.fragments.common.LoaderFragment;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDao;
import be.ugent.zeus.hydra.minerva.auth.AccountUtils;
import be.ugent.zeus.hydra.minerva.auth.MinervaConfig;
import be.ugent.zeus.hydra.minerva.course.CourseDao;
import be.ugent.zeus.hydra.minerva.course.CourseDaoLoader;
import be.ugent.zeus.hydra.minerva.sync.SyncAdapter;
import be.ugent.zeus.hydra.minerva.sync.SyncBroadcast;
import be.ugent.zeus.hydra.minerva.sync.SyncUtils;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.recyclerview.adapters.minerva.CourseAdapter;

import java.io.IOException;
import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Displays Minerva items.
 *
 * @author silox
 * @author Niko Strijbol
 */
public class MinervaFragment extends LoaderFragment<List<Course>> {

    private static final String TAG = "MinervaFragment";

    private RecyclerView recyclerView;
    private View authWrapper;
    private Snackbar syncBar;

    private CourseAdapter adapter;
    private AccountManager manager;
    private CourseDao courseDao;

    /**
     * Do not automatically start the loaders, we do it by hand.
     */
    public MinervaFragment() {
        autoStart = false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_minerva, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hideProgressBar();

        this.manager = AccountManager.get(getContext());
        this.courseDao = new CourseDao(getContext());

        Button authorize = $(view, R.id.authorize);
        authorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maybeLaunchAuthorization();
            }
        });

        authWrapper = $(view, R.id.auth_wrapper);

        recyclerView = $(view, R.id.recycler_view);
        adapter = new CourseAdapter();

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    private boolean isLoggedIn() {
        return AccountUtils.hasAccount(getContext());
    }

    private void maybeLaunchAuthorization() {
        if (!isLoggedIn()) {
            manager.addAccount(MinervaConfig.ACCOUNT_TYPE, MinervaConfig.DEFAULT_SCOPE, null, null, getActivity(), new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                    try {
                        Bundle result = accountManagerFuture.getResult();
                        Log.d(TAG, "Account " + result.getString(AccountManager.KEY_ACCOUNT_NAME) + " was created.");
                        onAccountAdded();
                    } catch (OperationCanceledException e) {
                        Toast.makeText(getContext().getApplicationContext(), "Je gaf geen toestemming om je account te gebruiken.", Toast.LENGTH_LONG).show();
                    } catch (IOException | AuthenticatorException e) {
                        Log.w(TAG, "Account not added.", e);
                    }
                }
            }, null);
        }
    }

    private void onAccountAdded() {
        //Get an account
        Account account = AccountUtils.getAccount(getContext());

        //Enable sync
        SyncUtils.enableSync(getContext(), account);

        //Request first sync
        Log.d(TAG, "Requesting first sync...");
        Bundle bundle = new Bundle();
        bundle.putBoolean(SyncAdapter.ARG_FIRST_SYNC, true);
        requestSync(account, bundle);
    }

    private void requestSync(Account account, Bundle bundle) {
        //Request first sync
        Log.d(TAG, "Requesting sync...");
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, MinervaConfig.ACCOUNT_AUTHORITY, bundle);
    }

    private void manualSync() {
        //Get an account
        adapter.clear();
        Account account = AccountUtils.getAccount(getContext());
        Bundle bundle = new Bundle();
        requestSync(account, bundle);
    }

    /**
     * Thanks to the constructor the loaders are not started in the parent function. We start them here
     * manually.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        maybeLoadData();
    }

    /**
     * Check if we can load data and if yes, do it.
     */
    private void maybeLoadData() {
        //If we are logged in, we can start loading the data.
        if(isLoggedIn()) {
            authWrapper.setVisibility(View.GONE);
            showProgressBar();
            loaderHandler.startLoader();
        }
    }

    /**
     * This must be called when data is received that has no errors.
     *
     * @param data The data.
     */
    @Override
    public void receiveData(@NonNull List<Course> data) {
        adapter.setItems(data);
        recyclerView.setVisibility(View.VISIBLE);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(isLoggedIn()) {
            inflater.inflate(R.menu.menu_minerva, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_logout) {
            signOut();
            return true;
        } else if (item.getItemId() == R.id.action_sync) {
            manualSync();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sign out and hide the data.
     */
    private void signOut() {
        //Sign out first, and then remove all data.
        Account a = AccountUtils.getAccount(getContext());
        ContentResolver.cancelSync(a, MinervaConfig.ACCOUNT_AUTHORITY);
        Toast.makeText(getContext(), "Logging out...", Toast.LENGTH_SHORT).show();
        manager.removeAccount(a, new AccountManagerCallback<Boolean>() {
            @Override
            public void run(AccountManagerFuture<Boolean> accountManagerFuture) {
                //Delete items
                adapter.clear();
                //Hide list
                recyclerView.setVisibility(View.GONE);
                //Hide progress
                hideProgressBar();
                //Show login prompt
                authWrapper.setVisibility(View.VISIBLE);
                //Destroy loaders
                loaderHandler.destroyLoader();
                //Delete database
                clearDatabase();
                //Reload options
                getActivity().invalidateOptionsMenu();
            }
        }, null);
    }

    private void clearDatabase() {
        courseDao.deleteAll();
        AnnouncementDao announcementDao = new AnnouncementDao(getContext());
        announcementDao.deleteAll();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        manager.registerReceiver(syncReceiver, SyncBroadcast.getBroadcastFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        manager.unregisterReceiver(syncReceiver);
    }

    //This will only be called if manually set to send broadcasts.
    private BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            assert getView() != null;
            switch (intent.getAction()) {
                case SyncBroadcast.SYNC_START:
                    Log.d(TAG, "Start!");
                    ensureSyncStatus("Vakken ophalen...");
                    return;
                case SyncBroadcast.SYNC_DONE:
                    Log.d(TAG, "Done!");
                    ensureSyncStatus("Klaar");
                    syncBar.dismiss();
                    syncBar = null;
                    recyclerView.setVisibility(View.VISIBLE);
                    loaderHandler.restartLoader();
                    return;
                case SyncBroadcast.SYNC_ERROR:
                    Log.d(TAG, "Error");
                    ensureSyncStatus(getString(R.string.failure));
                    syncBar.setDuration(Snackbar.LENGTH_LONG);
                    return;
                case SyncBroadcast.SYNC_PROGRESS_WHATS_NEW:
                    Log.d(TAG, "Progress");
                    if(progressBar.isIndeterminate()) {
                        progressBar.setIndeterminate(false);

                    }
                    int current = intent.getIntExtra(SyncBroadcast.ARG_SYNC_PROGRESS_CURRENT, 0);
                    int total = intent.getIntExtra(SyncBroadcast.ARG_SYNC_PROGRESS_TOTAL, 0);
                    progressBar.setMax(total);
                    progressBar.setProgress(current);
                    ensureSyncStatus("Vak " + current + " van " + total + " ophalen...");
            }
        }
    };

    /**
     * Ensure the sync status is enabled, and set the the given text on the snackbar.
     *
     * @param text To display on the snackbar.
     */
    private void ensureSyncStatus(String text) {
        assert getView() != null;
        if(syncBar == null) {
            authWrapper.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            showProgressBar();
            syncBar = Snackbar.make(getView(), text, Snackbar.LENGTH_INDEFINITE);
            syncBar.show();
        } else {
            syncBar.setText(text);
        }
    }

    @Override
    public Loader<ThrowableEither<List<Course>>> getLoader() {
        return new CourseDaoLoader(getContext(), courseDao);
    }
}
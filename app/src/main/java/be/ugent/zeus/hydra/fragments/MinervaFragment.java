package be.ugent.zeus.hydra.fragments;

import android.accounts.*;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.auth.AccountUtils;
import be.ugent.zeus.hydra.auth.EndpointConfiguration;
import be.ugent.zeus.hydra.cache.file.FileCache;
import be.ugent.zeus.hydra.fragments.common.LoaderFragment;
import be.ugent.zeus.hydra.models.minerva.Courses;
import be.ugent.zeus.hydra.recyclerview.adapters.minerva.CourseAnnouncementAdapter;
import be.ugent.zeus.hydra.requests.minerva.CoursesMinervaRequest;
import be.ugent.zeus.hydra.requests.minerva.WhatsNewRequest;
import be.ugent.zeus.hydra.utils.recycler.DividerItemDecoration;

import java.io.IOException;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Displays Minerva items.
 *
 * @author silox
 * @author Niko Strijbol
 */
public class MinervaFragment extends LoaderFragment<Courses> {

    private static final String TAG = "MinervaFragment";

    private static final int AUTH_REQUEST = 1;

    private RecyclerView recyclerView;
    private View authWrapper;

    private CourseAnnouncementAdapter adapter;
    private AccountManager manager;

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

        Button authorize = $(view, R.id.authorize);
        authorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maybeLaunchAuthorization();
            }
        });

        authWrapper = $(view, R.id.auth_wrapper);

        recyclerView = $(view, R.id.recycler_view);
        adapter = new CourseAnnouncementAdapter((HydraApplication) getActivity().getApplication());

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private boolean isLoggedIn() {
        return AccountUtils.hasAccount(getContext());
    }

    private void maybeLaunchAuthorization() {
        if (!isLoggedIn()) {
            manager.addAccount(EndpointConfiguration.ACCOUNT_TYPE, EndpointConfiguration.DEFAULT_SCOPE, null, null, getActivity(), new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                    try {
                        Bundle result = accountManagerFuture.getResult();
                        Log.d(TAG, "Account " + result.getString(AccountManager.KEY_ACCOUNT_NAME) + " was created.");
                        maybeLoadData();
                    } catch (OperationCanceledException e) {
                        Toast.makeText(getContext().getApplicationContext(), "Je gaf geen toestemming om je account te gebruiken.", Toast.LENGTH_LONG).show();
                    } catch (IOException | AuthenticatorException e) {
                        Log.w(TAG, "Account not added.", e);
                    }
                }
            }, null);
        }
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
            startLoader();
        }
    }

    /**
     * This must be called when data is received that has no errors.
     *
     * @param data The data.
     */
    @Override
    public void receiveData(@NonNull Courses data) {
        adapter.setItems(data.getCourses());
        recyclerView.setVisibility(View.VISIBLE);
        getActivity().invalidateOptionsMenu();
    }

    /**
     * @return The request that will be executed.
     */
    @Override
    public CoursesMinervaRequest getRequest() {
        return new CoursesMinervaRequest(getContext(), getActivity());
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
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sign out and hide the data.
     */
    private void signOut() {
        //Sign out first, and then remove all data.
        Account a = AccountUtils.getAccount(getContext());
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
                destroyLoader();
                //Delete cache
                clearCache();
                //Reload options
                getActivity().invalidateOptionsMenu();
            }
        }, null);
    }

    /**
     * Clear the cache of the existing requests.
     */
    private void clearCache() {
        //Delete list of courses
        FileCache.deleteStartingWith(CoursesMinervaRequest.BASE_KEY, getContext().getApplicationContext());
        //Delete all courses
        FileCache.deleteStartingWith(WhatsNewRequest.BASE_KEY, getContext().getApplicationContext());
    }
}
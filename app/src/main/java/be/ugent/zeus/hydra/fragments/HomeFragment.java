package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.loader.LoaderCallback;
import be.ugent.zeus.hydra.loader.ThrowableEither;
import be.ugent.zeus.hydra.loader.cache.Request;
import be.ugent.zeus.hydra.models.HomeCard;
import be.ugent.zeus.hydra.models.association.AssociationActivities;
import be.ugent.zeus.hydra.models.association.AssociationActivity;
import be.ugent.zeus.hydra.models.resto.RestoOverview;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.models.specialevent.SpecialEventWrapper;
import be.ugent.zeus.hydra.requests.AssociationActivitiesRequest;
import be.ugent.zeus.hydra.requests.RestoMenuOverviewRequest;
import be.ugent.zeus.hydra.requests.SpecialEventRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static be.ugent.zeus.hydra.common.ViewUtils.$;

/**
 * @author Niko Strijbol
 * @author silox
 */
public class HomeFragment extends Fragment {

    private static final int MENU_LOADER = 1;
    private static final int ACTIVITY_LOADER = 2;
    private static final int SPECIAL_LOADER = 3;

    private MenuCallback menuCallback;
    private ActivityCallback activityCallback;
    private SpecialEventCallback specialEventCallback;


    private HomeCardAdapter adapter;
    private View layout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.homefragment_view, container, false);

        RecyclerView recyclerView = $(layout, R.id.home_cards_view);
        swipeRefreshLayout = $(layout, R.id.swipeRefreshLayout);
        progressBar = $(layout, R.id.progress_bar);

        adapter = new HomeCardAdapter();
        assert recyclerView != null;
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                restartLoaders();
            }
        });

        startLoaders();

        return layout;
    }

    /**
     * Start the loaders.
     */
    private void startLoaders() {
        menuCallback = new MenuCallback();
        activityCallback = new ActivityCallback();
        specialEventCallback = new SpecialEventCallback();

        getLoaderManager().initLoader(MENU_LOADER, null, menuCallback);
        getLoaderManager().initLoader(ACTIVITY_LOADER, null, activityCallback);
        getLoaderManager().initLoader(SPECIAL_LOADER, null, specialEventCallback);
    }

    /**
     * Restart the loaders
     */
    private void restartLoaders() {
        getLoaderManager().restartLoader(MENU_LOADER, null, menuCallback);
        getLoaderManager().restartLoader(ACTIVITY_LOADER, null, activityCallback);
        getLoaderManager().restartLoader(SPECIAL_LOADER, null, specialEventCallback);
    }

    /**
     * When one of the loaders is complete.
     */
    private void loadComplete() {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Show a snack bar.
     *
     * @param field The failing field.
     */
    private void showFailureSnackbar(String field) {
        Snackbar.make(layout, "Oeps! Kon " + field + " niet ophalen.", Snackbar.LENGTH_LONG)
                .setAction("Opnieuw proberen", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restartLoaders();
                    }
                })
                .show();
    }

    private class MenuCallback extends LoaderCallback<RestoOverview> {

        /**
         * This must be called when data is received that has no errors.
         *
         * @param data The data.
         */
        @Override
        public void receiveData(@NonNull RestoOverview data) {
            ArrayList<HomeCard> list = new ArrayList<>();
            list.addAll(data);
            adapter.updateCardItems(list, HomeCardAdapter.HomeType.RESTO);
            loadComplete();
        }

        /**
         * This must be called when an error occurred.
         *
         * @param error The exception.
         */
        @Override
        public void receiveError(@NonNull Throwable error) {
            showFailureSnackbar("restomenu");
            loadComplete();
        }

        /**
         * @return The request that will be executed.
         */
        @Override
        public Request<RestoOverview> getRequest() {
            return new RestoMenuOverviewRequest();
        }

        @Override
        public Loader<ThrowableEither<RestoOverview>> onCreateLoader(int id, Bundle args) {
            return super.onCreateLoader(getContext());
        }
    }

    private class ActivityCallback extends LoaderCallback<AssociationActivities> {

        /**
         * This must be called when data is received that has no errors.
         *
         * @param data The data.
         */
        @Override
        public void receiveData(@NonNull AssociationActivities data) {
            List<AssociationActivity> filteredAssociationActivities = AssociationActivities.getPreferredActivities(data, getContext());
            Date date = new Date();
            List<HomeCard> list = new ArrayList<>();
            for (AssociationActivity activity: filteredAssociationActivities) {
                if(activity.getPriority() > 0 && activity.end.after(date)) {
                    list.add(activity);
                }
            }
            adapter.updateCardItems(list, HomeCardAdapter.HomeType.ACTIVITY);
            loadComplete();
        }

        /**
         * This must be called when an error occurred.
         *
         * @param error The exception.
         */
        @Override
        public void receiveError(@NonNull Throwable error) {
            showFailureSnackbar("activiteiten");
            loadComplete();
        }

        /**
         * @return The request that will be executed.
         */
        @Override
        public Request<AssociationActivities> getRequest() {
            return new AssociationActivitiesRequest();
        }

        @Override
        public Loader<ThrowableEither<AssociationActivities>> onCreateLoader(int id, Bundle args) {
            return super.onCreateLoader(getContext());
        }
    }

    private class SpecialEventCallback extends LoaderCallback<SpecialEventWrapper> {

        /**
         * This must be called when data is received that has no errors.
         *
         * @param data The data.
         */
        @Override
        public void receiveData(@NonNull SpecialEventWrapper data) {
            List<HomeCard> list = new ArrayList<>();
            boolean development_enabled = true;
            for (SpecialEvent event: data.getSpecialEvents()) {
                if ((event.getStart().before(new Date()) && event.getEnd().after(new Date())) || (development_enabled && event.isDevelopment())) {
                    list.add(event);
                }
            }

            adapter.updateCardItems(list, HomeCardAdapter.HomeType.SPECIALEVENT);
            loadComplete();
        }

        /**
         * This must be called when an error occurred.
         *
         * @param error The exception.
         */
        @Override
        public void receiveError(@NonNull Throwable error) {
            showFailureSnackbar("speciale activiteiten");
            loadComplete();
        }

        /**
         * @return The request that will be executed.
         */
        @Override
        public Request<SpecialEventWrapper> getRequest() {
            return new SpecialEventRequest();
        }

        @Override
        public Loader<ThrowableEither<SpecialEventWrapper>> onCreateLoader(int id, Bundle args) {
            return super.onCreateLoader(getContext());
        }
    }
}
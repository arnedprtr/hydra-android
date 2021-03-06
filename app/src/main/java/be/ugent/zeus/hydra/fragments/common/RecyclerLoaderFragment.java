package be.ugent.zeus.hydra.fragments.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.recyclerview.adapters.common.Adapter;

import java.io.Serializable;
import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * A fragment that uses a {@link android.support.v7.widget.RecyclerView} to display the data.
 *
 * This is used in this app mainly as the tabs in the home activity.
 *
 * @author Niko Strijbol
 */
public abstract class RecyclerLoaderFragment<E, D extends Serializable & List<E>, A extends RecyclerView.Adapter<?> & Adapter<E>> extends CachedLoaderFragment<D> {

    protected RecyclerView recyclerView;
    protected A adapter;
    protected boolean hasFixedSize = true;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = setUpRecyclerView(view);
    }

    protected RecyclerView setUpRecyclerView(View view) {
        recyclerView = $(view, R.id.recycler_view);
        recyclerView.setHasFixedSize(hasFixedSize);
        adapter = getAdapter();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(getLayoutManager());

        return recyclerView;
    }

    /**
     * @return The layout manager.
     */
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this.getActivity());
    }

    /**
     * @return The adapter to use.
     */
    protected abstract A getAdapter();

    /**
     * This must be called when data is received that has no errors.
     *
     * @param data The data.
     */
    @Override
    public void receiveData(@NonNull D data) {
        adapter.setItems(data);
    }
}
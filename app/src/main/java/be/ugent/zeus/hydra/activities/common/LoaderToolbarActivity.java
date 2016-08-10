package be.ugent.zeus.hydra.activities.common;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.loader.CachedAsyncTaskLoader;
import be.ugent.zeus.hydra.loader.ErrorLoaderCallback;
import be.ugent.zeus.hydra.loader.ThrowableEither;

import java.io.Serializable;

/**
 * Activity that uses the {@link be.ugent.zeus.hydra.loader.CachedAsyncTaskLoader}.
 *
 * @author Niko Strijbol
 */
public abstract class LoaderToolbarActivity<D extends Serializable> extends ToolbarActivity implements ErrorLoaderCallback<D, D> {

    private static final String TAG = "LoaderToolbarActivity";

    // ID of the loader.
    private static final int LOADER = 0;
    protected boolean shouldRenew = false;
    //The progress bar. Is used if not null.
    protected ProgressBar progressBar;

    /**
     * {@inheritDoc}
     *
     * Also sets the progress bar.
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        progressBar = $(R.id.progress_bar);

    }

    /**
     * Hide the progress bar.
     */
    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.  The application
     * should at this point remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<ThrowableEither<D>> loader) {
        loader.reset();
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<ThrowableEither<D>> loader, ThrowableEither<D> data) {
        if(data.hasError()) {
            receiveError(data.getError());
        } else {
            receiveData(data.getData());
        }
        hideProgressBar();
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<ThrowableEither<D>> onCreateLoader(int id, Bundle args) {
        return new CachedAsyncTaskLoader<>(getRequest(), getApplicationContext(), shouldRenew);
    }

    /**
     * Start the loader.
     */
    protected void startLoader() {
        // Start the data loader.
        getSupportLoaderManager().initLoader(LOADER, null, this);
    }

    /**
     * Restart the loader.
     */
    protected void restartLoader() {
        // Start the data loader.
        getSupportLoaderManager().restartLoader(LOADER, null, this);
    }

    /**
     * @return The main view of this activity. Currently this is used for snack bars, but that may change.
     */
    protected abstract View getView();

    /**
     * When the request has failed.
     */
    public void receiveError(@NonNull Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(getView(), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refresh();
                    }
                })
                .show();
        hideProgressBar();
    }

    /**
     * Launch a new request.
     */
    protected void refresh() {
        Toast.makeText(getApplicationContext(), R.string.begin_refresh, Toast.LENGTH_SHORT).show();
        shouldRenew = true;
        restartLoader();
        shouldRenew = false;
    }
}
package be.ugent.zeus.hydra.activities.common;

import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.utils.ViewUtils;

/**
 * An activity where there is no action bar, and instead a {@link android.support.v7.widget.Toolbar} is used.
 *
 * This class is designed to work with the XML file "toolbar.xml". Should you want another toolbar to be used,
 * please update the ID of the toolbar.
 *
 * This activity uses the NoActionBar theme.
 *
 * If you use the {@link #setContentView(int)} method, the action bar is set for you. If you do not use this method,
 * you must call {@link #setUpActionBar()} manually, after the view has been loaded, as that methods looks up the
 * action bar using it's ID.
 *
 * @author Niko Strijbol
 */
public abstract class ToolbarActivity extends HydraActivity {

    /**
     * The ID of the toolbar in the XML file. Change this if you want to use a different one.
     */
    protected int toolbarId = R.id.toolbar;

    private boolean hasParent = true;

    /**
     * When using this method, the action bar is automatically set up for you.
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setUpActionBar();
    }

    /**
     * Set the toolbar as action bar, and set it up to have an up button, if
     */
    protected void setUpActionBar() {
        Toolbar toolbar = $(toolbarId);
        setSupportActionBar(toolbar);

        //Set the up button.
        if (hasParent) {
            getToolBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * A non-null version of {@link #getSupportActionBar()}.
     *
     * @return The action bar.
     */
    @NonNull
    protected ActionBar getToolBar() {
        assert getSupportActionBar() != null;
        return getSupportActionBar();
    }

    /**
     * Replace an icon with given ID by the same icon but in the correct colour.
     *
     * @param menu The menu.
     * @param ids The ids of the icon.
     */
    protected void tintToolbarIcons(Menu menu, int... ids) {

        int color = ViewUtils.getColor(getToolBar().getThemedContext(), android.R.attr.textColorPrimary);

        for (int id: ids) {
            Drawable drawable = DrawableCompat.wrap(menu.findItem(id).getIcon());
            DrawableCompat.setTint(drawable, color);
            menu.findItem(id).setIcon(drawable);
        }
    }

    /**
     * Set if the activity has a parent or not.
     */
    protected void hasParent(boolean hasParent) {
        this.hasParent = hasParent;
    }
}
package be.ugent.zeus.hydra.fragments.resto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.views.MenuTable;

/**
 * Displays the resto menu for one day.
 */
public class MenuFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this fragment.
     */
    private static final String ARG_DATA_MENU = "resto_menu";

    /**
     * The fragment argument representing the section number for this fragment.
     */
    private RestoMenu data;

    public MenuFragment() {}

    public static MenuFragment newInstance(RestoMenu menu) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DATA_MENU, menu);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = getArguments().getParcelable(ARG_DATA_MENU);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MenuTable table = (MenuTable) inflater.inflate(R.layout.fragment_menu, container, false);
        table.setMenu(data);
        return table;
    }

    public void setData(RestoMenu data) {
        this.data = data;
    }

    public RestoMenu getData() {
        return data;
    }
}
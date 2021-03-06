package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.view.View;
import android.widget.Toast;

import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;

/**
 * Created by feliciaan on 29/06/16.
 */
public class MinervaLoginViewHolder extends DataViewHolder<HomeCard> {

    private static final String TAG = "LoginViewHolder";

    public MinervaLoginViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void populate(HomeCard card) {
        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Not done yet.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
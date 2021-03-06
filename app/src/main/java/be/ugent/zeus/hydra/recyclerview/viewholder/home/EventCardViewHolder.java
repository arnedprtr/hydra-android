package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.content.Intent;
import android.os.Parcelable;
import android.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.EventDetailActivity;
import be.ugent.zeus.hydra.models.association.Event;
import be.ugent.zeus.hydra.models.cards.EventCard;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import com.squareup.picasso.Picasso;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by feliciaan on 06/04/16.
 */
public class EventCardViewHolder extends HideableViewHolder {

    private TextView start;
    private TextView title;
    private TextView association;
    private ImageView imageView;

    public EventCardViewHolder(View v, HomeCardAdapter adapter) {
        super(v, adapter);
        title = $(v, R.id.name);
        association = $(v, R.id.association);
        start = $(v, R.id.starttime);
        imageView = $(v, R.id.imageView);
    }

    @Override
    public void populate(final HomeCard card) {

        final Event event = card.<EventCard>checkCard(HomeCard.CardType.ACTIVITY).getEvent();

        title.setText(event.getTitle());
        association.setText(event.getLocation());
        start.setText(DateUtils.relativeDateTimeString(event.getStart(), itemView.getContext(), false));
        String description = itemView.getResources().getString(R.string.home_card_description);
        toolbar.setTitle(String.format(description, event.getAssociation().getDisplayName()));

        Picasso.with(itemView.getContext()).load(event.getAssociation().getImageLink()).fit().centerInside().into(imageView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), EventDetailActivity.class);
                intent.putExtra(EventDetailActivity.PARCEL_EVENT, (Parcelable) event);
                itemView.getContext().startActivity(intent);
            }
        });

        toolbar.setMenu(R.menu.now_toolbar_association_event);
        toolbar.setOnClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_hide:
                        adapter.disableCardType(card.getCardType());
                        return true;
                    case R.id.menu_hide_association:
                        adapter.disableAssociation(event.getAssociation());
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}
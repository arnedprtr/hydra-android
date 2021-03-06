package be.ugent.zeus.hydra.recyclerview.viewholder;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.EventDetailActivity;
import be.ugent.zeus.hydra.models.association.Event;
import org.threeten.bp.format.DateTimeFormatter;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * View holder for an event in the event tab.
 *
 * @author Niko Strijbol
 */
public class EventViewHolder extends DataViewHolder<Event> {

    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private TextView start;
    private TextView title;
    private TextView association;

    public EventViewHolder(View v) {
        super(v);
        title = $(v, R.id.name);
        association = $(v, R.id.association);
        start = $(v, R.id.starttime);
    }

    public void populate(final Event event) {
        title.setText(event.getTitle());
        association.setText(event.getAssociation().getDisplayName());
        start.setText(event.getLocalStart().format(HOUR_FORMATTER));
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), EventDetailActivity.class);
                intent.putExtra(EventDetailActivity.PARCEL_EVENT, (Parcelable) event);
                itemView.getContext().startActivity(intent);
            }
        });
    }
}

package be.ugent.zeus.hydra.recyclerviewholder.home;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.models.association.AssociationNewsItem;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.NewsItemCard;

/**
 * Created by feliciaan on 18/06/16.
 */
public class NewsItemViewHolder extends AbstractViewHolder{
    private TextView info;
    private TextView title;
    private TextView summary;
    private ImageView star;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault());
    private boolean big;
    private LinearLayout head;

    public NewsItemViewHolder(View v) {
        super(v);
        title = (TextView) v.findViewById(R.id.name);
        summary = (TextView) v.findViewById(R.id.summary);
        info = (TextView) v.findViewById(R.id.info);
        star = (ImageView) v.findViewById(R.id.star);
        head = (LinearLayout) v.findViewById(R.id.head);
    }


    public void populate(HomeCard card) {
        if (card.getCardType() != HomeCardAdapter.HomeType.NEWSITEM) {
            return; // TODO: generate error
        }

        NewsItemCard newsItemCard = (NewsItemCard) card;
        final AssociationNewsItem newsItem = newsItemCard.getNewsItem();

        title.setText(newsItem.title);

        info.setText(dateFormatter.format(newsItem.date) + " by "+ "TODO");//// TODO: 07/04/2016 after merge do getName
        if(!newsItem.highlighted){
            star.setVisibility(View.INVISIBLE);
        }else{
            star.setVisibility(View.VISIBLE);
        }

        big = false;
        summary.setText("");
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 07/04/2016
                if(big) {
                    summary.setText("");
                }else{
                    summary.setText(Html.fromHtml(newsItem.content));
                }
                big=!big;
            }
        });
    }
}

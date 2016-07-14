package be.ugent.zeus.hydra.recyclerview.viewholder;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.SchamperArticleActivity;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.utils.DateUtils;
import com.squareup.picasso.Picasso;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * View holder for the schamper fragment.
 */
public class SchamperViewHolder extends RecyclerView.ViewHolder implements DataViewHolder<Article> {
    private TextView title;
    private TextView date;
    private TextView author;
    private ImageView image;

    public SchamperViewHolder(View itemView) {
        super(itemView);

        title = $(itemView, R.id.title);
        date = $(itemView, R.id.date);
        author = $(itemView, R.id.author);
        image = $(itemView, R.id.card_image);
    }

    public void populateData(final Article article) {

        title.setText(article.getTitle());
        date.setText(DateUtils.relativeDateString(article.getPubDate(), itemView.getContext()));
        author.setText(article.getAuthor());

        Picasso.with(this.itemView.getContext()).load(article.getLargeImage()).into(image);

        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), SchamperArticleActivity.class);
                intent.putExtra("article", (Parcelable) article);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) itemView.getContext(), image, "hero");
                itemView.getContext().startActivity(intent, options.toBundle());
            }
        });
    }
}
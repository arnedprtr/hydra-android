package be.ugent.zeus.hydra.recyclerview.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.UiThread;
import android.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.preferences.AssociationSelectPrefActivity;
import be.ugent.zeus.hydra.fragments.home.HomeFeedFragment;
import be.ugent.zeus.hydra.models.association.Association;
import be.ugent.zeus.hydra.models.cards.EventCard;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import be.ugent.zeus.hydra.recyclerview.viewholder.home.*;

import java.util.*;

import static be.ugent.zeus.hydra.models.cards.HomeCard.CardType.*;

/**
 * Adapter for {@link HomeFeedFragment}.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class HomeCardAdapter extends RecyclerView.Adapter<DataViewHolder<HomeCard>> {

    private final List<HomeCard> cardItems;
    private final SharedPreferences preferences;

    public HomeCardAdapter(Context activity) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        cardItems = new ArrayList<>();
    }

    /**
     * Remove all items of a given type and re-add the given list. This method is not thread safe and must be run on
     * the UI thread.
     *
     * @param cardList List with object implementing the card protocol
     * @param type The type of the cards
     */
    @UiThread
    public void updateCardItems(List<HomeCard> cardList, @HomeCard.CardType int type) {

        //Remove the current cards.
        removeCardType(type);

        cardItems.addAll(cardList);

        Collections.sort(cardItems, new Comparator<HomeCard>() {
            @Override
            public int compare(HomeCard lhs, HomeCard rhs) {
                return -(lhs.getPriority() - rhs.getPriority());
            }
        });

        notifyDataSetChanged();
    }

    /**
     * Remove all cards of a certain card type. This method is not thread safe and must be run on the UI thread.
     *
     * @param type The type of card to remove.
     */
    @UiThread
    private void removeCardType(@HomeCard.CardType int type) {
        Iterator<HomeCard> it = cardItems.iterator();
        while (it.hasNext()) { // Why no filter :(
            HomeCard c = it.next();
            if (c.getCardType() == type) {
                notifyItemRemoved(cardItems.indexOf(c));
                it.remove();
            }
        }
    }

    @Override
    public DataViewHolder<HomeCard> onCreateViewHolder(ViewGroup parent, @HomeCard.CardType int viewType) {
        switch (viewType) {
            case RESTO:
                return new RestoCardViewHolder(getViewForLayout(R.layout.home_card_resto, parent), this);
            case ACTIVITY:
                return new EventCardViewHolder(getViewForLayout(R.layout.home_card_event, parent), this);
            case SPECIAL_EVENT:
                return new SpecialEventCardViewHolder(getViewForLayout(R.layout.home_card_special, parent));
            case SCHAMPER:
                return new SchamperViewHolder(getViewForLayout(R.layout.home_card_schamper, parent), this);
            case NEWS_ITEM:
                return new NewsItemViewHolder(getViewForLayout(R.layout.home_card_news_item, parent), this);
            case MINERVA_LOGIN:
                return new MinervaLoginViewHolder(getViewForLayout(R.layout.home_minerva_login_card, parent));
            case MINERVA_ANNOUNCEMENT:
                return new MinervaAnnouncementViewHolder(getViewForLayout(R.layout.home_minerva_announcement_card, parent), this);
            case MINERVA_AGENDA:
                return new MinervaAgendaViewHolder(getViewForLayout(R.layout.home_minerva_agenda_card, parent), this);
        }
        return null;
    }

    /**
     * Disable a type of card. This method is not thread safe and must be run on the UI thread.
     *
     * @param type The type of card to disable.
     */
    public void disableCardType(@HomeCard.CardType int type) {
        Set<String> disabled = preferences.getStringSet(HomeFeedFragment.PREF_DISABLED_CARDS, Collections.<String>emptySet());
        Set<String> newDisabled = new HashSet<>(disabled);
        newDisabled.add(String.valueOf(type));
        preferences.edit().putStringSet(HomeFeedFragment.PREF_DISABLED_CARDS, newDisabled).apply();
        removeCardType(type);
    }

    public void disableAssociation(Association association) {
        Set<String> disabled = preferences.getStringSet(AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING, new HashSet<String>());
        disabled.add(association.getInternalName());
        preferences.edit().putStringSet(AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING, disabled).apply();

        //Why no filter :(
        Iterator<HomeCard> it = cardItems.iterator();
        while (it.hasNext()) { // Why no filter :(
            HomeCard c = it.next();
            if (c.getCardType() == ACTIVITY) {
                EventCard card = c.checkCard(ACTIVITY);
                if(card.getEvent().getAssociation().getInternalName().equals(association.getInternalName())) {
                    notifyItemRemoved(cardItems.indexOf(c));
                    it.remove();
                }
            }
        }

    }

    private View getViewForLayout(int rLayout, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(rLayout, parent, false);
    }

    @Override
    public void onBindViewHolder(DataViewHolder<HomeCard> holder, int position) {
        HomeCard object = cardItems.get(position);
        holder.populate(object);
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    @Override
    @HomeCard.CardType
    public int getItemViewType(int position) {
        return cardItems.get(position).getCardType();
    }

    /**
     * Helper method that returns a listener that hides a given card type in this adapter. This will only work with the
     * default menu in {@link be.ugent.zeus.hydra.views.NowToolbar}.
     *
     * @param type The type of card to hide.
     * @return A listener that will hide the given card type in this adapter.
     */
    public PopupMenu.OnMenuItemClickListener listener(@HomeCard.CardType final int type) {
        return new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menu_hide) {
                    disableCardType(type);
                    return true;
                }
                return false;
            }
        };
    }
}
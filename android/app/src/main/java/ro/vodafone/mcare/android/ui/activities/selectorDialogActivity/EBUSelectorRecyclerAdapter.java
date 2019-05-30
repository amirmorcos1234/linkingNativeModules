package ro.vodafone.mcare.android.ui.activities.selectorDialogActivity;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.RealmList;
import io.realm.annotations.PrimaryKey;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Ban;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
import ro.vodafone.mcare.android.ui.utils.PhoneNumberUtils;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Bogdan Marica on 10/25/2017.
 */
class EBUSelectorRecyclerAdapter extends RecyclerView.Adapter {

    private static final int BAN_TYPE = 0;
    private static final int SUBSCRIBER_TYPE = 1;
    private static final int ONLY_BAN_TYPE = 2;
    boolean isOnlyBanSelector = false;
    private List<AdapterBan> banList = new ArrayList<>();
    private List<AdapterSubscriber> subscriberList = new ArrayList<>();
    private boolean isBanSelector;
    private EBUSelectorDialogActivity activity;
    private boolean canSelectItem = true;

    EBUSelectorRecyclerAdapter(EBUSelectorDialogActivity activity, List<Ban> banList) {
        canSelectItem = true;
        this.activity = activity;
        this.banList = getAdapterBanList(banList);
        this.isBanSelector = true;
        isOnlyBanSelector = false;
    }

    EBUSelectorRecyclerAdapter(EBUSelectorDialogActivity activity, List<Ban> banList, boolean isOnlyBanSelector) {
        canSelectItem = true;
        this.activity = activity;
        this.banList = getAdapterBanList(banList);
        this.isBanSelector = true;
        this.isOnlyBanSelector = isOnlyBanSelector;
    }

    EBUSelectorRecyclerAdapter(EBUSelectorDialogActivity activity, List<Subscriber> subscriberList, boolean isBanSelector, boolean isSearching) {
        this.activity = activity;
        canSelectItem = true;
        this.subscriberList = getUiSubscriberList(subscriberList, isSearching);
        this.isBanSelector = isBanSelector;
        if (!isSearching)
            updateSearchViewVisibility(subscriberList.size());
    }

    private List<AdapterSubscriber> getUiSubscriberList(List<Subscriber> subscriberList, boolean isSearching){
        List<AdapterSubscriber> uiSubscriberList = getAdapterSubscribersList(subscriberList,isSearching);
        if((uiSubscriberList.size() < 10 || isSearching)){
            return uiSubscriberList;
        }else if(uiSubscriberList.size()>=10){
            return uiSubscriberList.subList(0,10);
        }else{
            return uiSubscriberList;
        }
    }
    private List<AdapterSubscriber> getAdapterSubscribersList(List<Subscriber> subscriberList, boolean isSearching) {
        List<AdapterSubscriber> list = new ArrayList<>();

        for (Subscriber s : subscriberList) {
            AdapterSubscriber as = new AdapterSubscriber();
            as.setAlias(s.getAlias());
            as.setAvatarUrl(s.getAvatarUrl());
            as.setMsisdn(s.getMsisdn());
            as.setResourceType(s.getResourceType());
            as.setSid(s.getSid());
            as.setSelected(isPreviousSelected(s));
            as.setIsGeoNumber(s.isGeoNumber());
            list.add(as);
           /* if (list.size() < 10)
                list.add(as);
            else if (isSearching)
               */
        }

        Collections.sort(list, new Comparator<AdapterSubscriber>() {
            @Override
            public int compare(AdapterSubscriber as1, AdapterSubscriber as2) {
                if(as1.isSelected && !as2.isSelected){
                    return -1;
                }else if(as2.isSelected && !as1.isSelected){
                    return 1;
                }else{
                    return 0;
                }
            }
        });

        return list;
    }

    private List<AdapterBan> getAdapterBanList(List<Ban> bl) {
        List<AdapterBan> list = new ArrayList<>();

        for (Ban b : bl) {
            AdapterBan ab = new AdapterBan();
            ab.setNumber(b.getNumber());
            ab.setSubscriberList(b.getSubscriberList());
            ab.setSelected(isPreviousBanSelected(b));
            list.add(ab);
        }

        return list;
    }

    @Override
    public int getItemViewType(int position) {
        if (isBanSelector)
            if (!isOnlyBanSelector)
                return BAN_TYPE;
            else
                return ONLY_BAN_TYPE;
        else
            return SUBSCRIBER_TYPE;
    }

    boolean getIsBan() {
        return isBanSelector;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case BAN_TYPE:
                View banItemView = View.inflate(activity, R.layout.ebu_ban_selector_ban_item, null);
                return new EBUSelectorRecyclerAdapter.BanViewHolder(banItemView);
            case SUBSCRIBER_TYPE:
                View subscriberItemView = View.inflate(activity, R.layout.ebu_ban_selector_subscriber_item, null);
                return new EBUSelectorRecyclerAdapter.SubscriberViewHolder(subscriberItemView);
            case ONLY_BAN_TYPE:
                View onlyBanItemView = View.inflate(activity, R.layout.ebu_ban_selector_subscriber_item, null);
                return new EBUSelectorRecyclerAdapter.SubscriberViewHolder(onlyBanItemView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder uncastHolder, final int position) {

        if (uncastHolder instanceof EBUSelectorRecyclerAdapter.BanViewHolder) {
            final EBUSelectorRecyclerAdapter.BanViewHolder storeHolder = (EBUSelectorRecyclerAdapter.BanViewHolder) uncastHolder;

            TextView ban = storeHolder.ban;
            ban.setText(banList.get(position).getNumber());
            storeHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isBanSelector = false;
                    loadActivityViewChanges(position);
                    notifyDataSetChanged();
                }
            });

        } else if (uncastHolder instanceof EBUSelectorRecyclerAdapter.SubscriberViewHolder) {
            if (!isOnlyBanSelector) {
                final EBUSelectorRecyclerAdapter.SubscriberViewHolder storeHolder = (EBUSelectorRecyclerAdapter.SubscriberViewHolder) uncastHolder;

                TextView subscriberText = storeHolder.subscriber;
                final ImageView circle = storeHolder.circle;
                AdapterSubscriber subscriber = subscriberList.get(position);

                DynamicColorImageView iconImage = storeHolder.icon;
                setSubscriberIconImage(subscriber, iconImage);

                if (subscriber.isSelected())
                    circle.setImageResource(R.drawable.selector_checked_image);
                else
                    circle.setImageResource(R.drawable.selector_unchecked_image);

//                circle.setImageResource(subscriber.getMsisdn().equals(UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getMsisdn())
//                        ? R.drawable.selector_checked_image
//                        : R.drawable.selector_unchecked_image);

                AdapterSubscriber currentSubscriber = subscriberList.get(position);
                String formatedMsisdn = currentSubscriber.isGeoNumber() ? currentSubscriber.getMsisdn() : PhoneNumberUtils.checkNumberMsisdnFormat(currentSubscriber.getMsisdn());
                subscriberText.setText(formatedMsisdn);

                storeHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (canSelectItem) {
                            canSelectItem = false;
                            for (AdapterSubscriber subscriber : subscriberList) {
                                subscriber.setSelected(false);
                            }
                            subscriberList.get(position).setSelected(true);
                            notifyDataSetChanged();
                            activity.updateSelectedBanInController(activity.selectedBan.getNumber());
                            activity.setSelectedSubscriber(getSubscriber(subscriberList.get(position)));
                        }
                    }
                });
            } else {
                final EBUSelectorRecyclerAdapter.SubscriberViewHolder storeHolder = (EBUSelectorRecyclerAdapter.SubscriberViewHolder) uncastHolder;

                TextView subscriberText = storeHolder.subscriber;
                final ImageView circle = storeHolder.circle;
                AdapterBan currentBan = banList.get(position);

//                if (currentBan.isSelected())
//                    circle.setImageResource(R.drawable.selector_checked_image);
//                else
//                    circle.setImageResource(R.drawable.selector_unchecked_image);

                circle.setImageResource(currentBan.getNumber().equals(UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan())
                        ? R.drawable.selector_checked_image
                        : R.drawable.selector_unchecked_image);

                subscriberText.setText(banList.get(position).getNumber());
                storeHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (canSelectItem) {
                            canSelectItem = false;
                            for (AdapterBan ab : banList) {
                                ab.setSelected(false);
                            }
                            banList.get(position).setSelected(true);
                            notifyDataSetChanged();
                            activity.setOnlySelectedBan(getBan(banList.get(position)));
                            activity.callProfileHierarchyOnBanChangeOrClose(false);
                        }
                    }
                });
            }
        }
    }

    private Subscriber getSubscriber(AdapterSubscriber as) {
        Subscriber s = new Subscriber();
        s.setAlias(as.getAlias());
        s.setAvatarUrl(as.getAvatarUrl());
        s.setMsisdn(as.getMsisdn());
        s.setResourceType(as.getResourceType());
        s.setSid(as.getSid());

        return s;
    }

    private Ban getBan(AdapterBan ab) {
        Ban s = new Ban();
        s.setNumber(ab.getNumber());
        s.setSubscriberList(ab.getSubscriberList());

        return s;
    }

    private void loadActivityViewChanges(int position) {
        activity.displayBackButton();
        activity.setPageTitle(banList.get(position).getNumber());
        activity.banNumber = banList.get(position).getNumber();

        if (!activity.haveSingleMsisdn)
            activity.callApi19(banList.get(position).getNumber());
        else {
            activity.setSelectedBan(getBan(banList.get(position)));
            List<Subscriber> list = new ArrayList<>();
            list.add(UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber());
            activity.ebu_selector_recyclerView.setAdapter(new EBUSelectorRecyclerAdapter(activity, list, false, false));

        }
    }

    private void updateSearchViewVisibility(int subscriberListSize) {
        activity.hideSearchView();
        if (subscriberListSize > 10) {
            activity.displaySearchIcon(true);
            activity.showSearchView();
        } else {
            activity.displaySearchIcon(false);
            activity.hideSearchView();
        }


    }

    private boolean isPreviousSelected(Subscriber subscriber) {
        try {
            return subscriber.getMsisdn().equals(UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getMsisdn())
                    && subscriber.getSid().equals(UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid());
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isPreviousBanSelected(Ban ban) {
        try {
            return UserSelectedMsisdnBanController.getInstance().getSelectedBan() != null && ban.getNumber().equals(UserSelectedMsisdnBanController.getInstance().getSelectedBan().getNumber());
        } catch (Exception e) {
            return false;
        }
    }

    private void setSubscriberIconImage(AdapterSubscriber subscriber, DynamicColorImageView iconImage){
        if(subscriber.isGeoNumber())
            iconImage.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.country_or_international_black_bg));
        else
            iconImage.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.phone_icon));

    }

    @Override
    public int getItemCount() {
        return isBanSelector ? banList.size() : subscriberList.size();
    }

    private class BanViewHolder extends RecyclerView.ViewHolder {
        TextView ban;
        ImageView arrow;

        BanViewHolder(View itemView) {
            super(itemView);
            ban = (TextView) itemView.findViewById(R.id.ebu_selector_ban_name);
            arrow = (ImageView) itemView.findViewById(R.id.ban_item_arrow);
        }
    }

    private class SubscriberViewHolder extends RecyclerView.ViewHolder {
        TextView subscriber;
        ImageView circle;
        DynamicColorImageView icon;

        SubscriberViewHolder(View itemView) {
            super(itemView);
            subscriber = (TextView) itemView.findViewById(R.id.ebu_selector_subscriber_name);
            circle = (ImageView) itemView.findViewById(R.id.ban_item_arrow);
            icon = itemView.findViewById(R.id.ban_item_drawable);
        }
    }

    private class AdapterSubscriber {
        boolean isSelected;
        boolean isGeoNumber;
        @SerializedName("avatarUrl")
        String avatarUrl;

        @SerializedName("alias")
        String alias;

        @SerializedName("msisdn")
        String msisdn;

        @SerializedName("sid")
        String sid;

        @SerializedName("resourceType")
        String resourceType;

        AdapterSubscriber() {
        }


        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getMsisdn() {
            return msisdn;
        }

        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

        public String getSid() {
            return sid;
        }

        public void setSid(String sid) {
            this.sid = sid;
        }

        String getResourceType() {
            return resourceType;
        }

        void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        public void setIsGeoNumber(boolean isGeoNumber){
            this.isGeoNumber = isGeoNumber;
        }

        public boolean isGeoNumber(){
            return isGeoNumber;
        }
    }

    private class AdapterBan {
        boolean isSelected;
        @PrimaryKey
        @SerializedName("number")
        String number;
        @SerializedName("subscriberList")
        RealmList<Subscriber> subscriberList;

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public RealmList<Subscriber> getSubscriberList() {
            return subscriberList;
        }

        public void setSubscriberList(RealmList<Subscriber> subscriberList) {
            this.subscriberList = subscriberList;
        }

    }
}

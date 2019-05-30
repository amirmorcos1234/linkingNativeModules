package ro.vodafone.mcare.android.card.costControl.currentBill;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.bumptech.glide.Glide;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.adapters.ExpandableListAdapter;
import ro.vodafone.mcare.android.client.model.billing.CurrentBillAdapterModel;
import ro.vodafone.mcare.android.client.model.costControl.AdditionalCost;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.BillingOverviewLabels;
import ro.vodafone.mcare.android.interfaces.expandable.ExpandableCostControlInterfaceCard;
import ro.vodafone.mcare.android.ui.fragments.callDetails.Category;
import ro.vodafone.mcare.android.ui.utils.DrawableUtils;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.ExpandableAdapterBackedLinearLayout;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Bivol Pavel on 13.04.2017.
 */
public class CurrentBillCard extends VodafoneAbstractCard implements ExpandableCostControlInterfaceCard {

    @BindView(R.id.button_line)
    View buttonLine;

    @BindView(R.id.users_icon)
    CircleImageView userIcon;

    @BindView(R.id.msisdn)
    VodafoneTextView msisdn;

    @BindView(R.id.arrow)
    ImageView arrow;

    @BindView(R.id.expand_section)
    RelativeLayout expandSection;

    @BindView(R.id.expandable_list_view)
    ExpandableAdapterBackedLinearLayout expandableListView;

    private String currentMsisdn;
    private boolean isDisplayedExpandSection = false;

    ExpandableListAdapter listAdapter;
    List<ExpandableListItemModel> listDataHeader;
    HashMap<ExpandableListItemModel, List<ExpandableListItemModel>> listDataChild;

    private CurrentBillAdapterModel adapterModel;

    public CurrentBillCard(Context context) {
        super(context);
        init(null);
    }

    public CurrentBillCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @Override
    protected int setContent() {
        return R.layout.card_current_bill;
    }

    private void init(AttributeSet attrs) {
        ButterKnife.bind(this);
        initClickListner();
        setNoAditionalCostStyle();
        setCardMargins();
    }

    private void setCardMargins() {
        setCardMargins((int) getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal),
                (int) getResources().getDimensionPixelSize(R.dimen.default_margin_card_vertical),
                (int) getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal),
                (int) getResources().getDimensionPixelSize(R.dimen.default_margin_card_vertical));
    }

    private void initClickListner() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadingView == null || (loadingView != null && !loadingView.isVisible())) {
                    if (errorView != null && errorView.getVisibility() == VISIBLE) {
                        CurrentBillCardController.getInstance().requestData(CurrentBillCard.this, currentMsisdn);
                    } else if (isDataAlreadySetted()) {
                        arrow.setRotation(0);
                        DrawableUtils.rotateArrow(arrow, !isDisplayedExpandSection);
                        changeExpandSectionVisibility();
                    } else {
                        CurrentBillCardController.getInstance().requestData(CurrentBillCard.this, currentMsisdn);
                    }
                }
            }
        });
    }

    private void changeExpandSectionVisibility() {
        if (!isDisplayedExpandSection) {
            expandContent();
        } else {
            expandSection.setVisibility(GONE);
        }
        isDisplayedExpandSection = !isDisplayedExpandSection;
        adapterModel.setExpanded(isDisplayedExpandSection);
    }

    private void expandContent() {
        expandSection.setVisibility(VISIBLE);
        expandableListView.expandGroup(0);
    }

    private void initUIElements() {

        Subscriber subscriber = (Subscriber) RealmManager.getRealmObjectAfterStringFieldThatContains(Subscriber.class, Subscriber.MSISDN_KEY, currentMsisdn);
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);

        if (subscriber != null && subscriber.getAvatarUrl() != null && !subscriber.getAvatarUrl().isEmpty()) {
            setAvatar(subscriber.getAvatarUrl());
        } else if (profile != null && profile.getHomeMsisdn() != null &&
                currentMsisdn != null && profile.getHomeMsisdn().contains(currentMsisdn)) {
            setAvatar(profile.getAvatarUrl());
        } else {
            userIcon.setPadding(ScreenMeasure.dpToPx(10), ScreenMeasure.dpToPx(10), ScreenMeasure.dpToPx(10), ScreenMeasure.dpToPx(10));
            userIcon.setColorFilter(Color.parseColor("#ffffff"));
        }
        arrow.setColorFilter(Color.parseColor("#E60000"));
    }

    private void setAvatar(String avatar) {
        Glide.with(getContext())
                .load(avatar)
                .placeholder(R.drawable.phone_icon)
                .error(R.drawable.phone_icon)
                .into(userIcon);
    }

    public void onDataLoaded(AdditionalCost additionalCost) {
        adapterModel.setAdditionalCost(additionalCost);
        buildCard();
    }

    private void buildCard() {
        hideLoading();
        hideError();
        if (adapterModel.getAdditionalCost() != null) {
            AdditionalCost additionalCost = adapterModel.getAdditionalCost();
            setupExpandableListView();
            if (additionalCost.getTotalCost() != null && additionalCost.getTotalCost() > 0) {
                setAditionalCostStyle();
            } else {
                setNoAditionalCostStyle();
            }
        /*    arrow.setRotation(0);
            DrawableUtils.rotateArrow(arrow, true);
            changeExpandSectionVisibility();*/
        } else {
            showError(true);
        }
    }

    public void setupView() {
        if (isDataAlreadySetted()) {
            setupExpandableListView();
            AdditionalCost additionalCost = adapterModel.getAdditionalCost();
            isDisplayedExpandSection = adapterModel.isExpanded();
            if (isDisplayedExpandSection) {
                setupExpandableListView();
                arrow.setRotation(180);
                expandContent();
            } else {
                arrow.setRotation(0);
                expandSection.setVisibility(GONE);
            }
            if (additionalCost.getTotalCost() != null && additionalCost.getTotalCost() > 0) {
                setAditionalCostStyle();
            } else {
                setNoAditionalCostStyle();
            }
        }else{
            CurrentBillCardController.getInstance().requestData(CurrentBillCard.this, currentMsisdn);
        }
    }

    boolean isDataAlreadySetted() {
        return adapterModel != null && adapterModel.getAdditionalCost() != null;
    }

    private void setNoAditionalCostStyle() {
        buttonLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_chart_top_color));
    }

    private void setAditionalCostStyle() {
        buttonLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple));
    }

    private void setupExpandableListView() {
        if (!isDataAlreadySetted() || listAdapter == null) {
            prepareListData();
        }
        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild, true);
        expandableListView.setAdapter(listAdapter);
        expandableListView.setOnChildClickListener(childClickListener);
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<ExpandableListItemModel>();
        listDataChild = new HashMap<ExpandableListItemModel, List<ExpandableListItemModel>>();

        List<ExpandableListItemModel> items = new ArrayList<ExpandableListItemModel>();
        AdditionalCost additionalCost = adapterModel.getAdditionalCost();

        listDataHeader.add(new ExpandableListItemModel("Suplimentar", additionalCost.getTotalCost(), BillingOverviewLabels.getBilling_overview_euro_unit(), false));

        items.add(new ExpandableListItemModel("Voce", additionalCost.getVoiceCost(), BillingOverviewLabels.getBilling_overview_euro_unit(), true));
        items.add(new ExpandableListItemModel("Date", additionalCost.getDataCost(), BillingOverviewLabels.getBilling_overview_euro_unit(), true));
        items.add(new ExpandableListItemModel("SMS", additionalCost.getSmsCost(), BillingOverviewLabels.getBilling_overview_euro_unit(), true));
        items.add(new ExpandableListItemModel("Altele", additionalCost.getOtherCost(), BillingOverviewLabels.getBilling_overview_euro_unit(), true));

        listDataChild.put(listDataHeader.get(0), items);
    }


    ExpandableListView.OnChildClickListener childClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

            ExpandableListItemModel child = (ExpandableListItemModel) listAdapter.getChild(i, i1);
            switch (child.get_itemName()) {
                case "Voce":
                    navigateToCallDetails(Category.VOCE);
                    break;
                case "Date":
                    navigateToCallDetails(Category.DATE);
                    break;
                case "SMS":
                    navigateToCallDetails(Category.SMS);
                    break;
                case "Altele":
                    navigateToCallDetails(Category.OTHER);
                    break;
            }
            return false;
        }
    };

    private void navigateToCallDetails(Category category) {
        Log.d(TAG, "navigateToCallDetails");
        IntentActionName i = IntentActionName.CALL_DETAILS_UNBILLED;

        try {
            Class c = IntentActionName.CALL_DETAILS_UNBILLED.getClass();
            Method method = c.getMethod("setCategory", Category.class);
            method.invoke(i, category);
            UserSelectedMsisdnBanController.getInstance().setSelectedMsisdn(currentMsisdn);
            new NavigationAction(getContext()).finishCurrent(true).startAction(i);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public VodafoneAbstractCard showError(boolean hideContent) {
        super.showError(hideContent);
        return this;
    }

    public void setCurrentBillAdapterModel(CurrentBillAdapterModel currentBillAdapterModel) {
        this.adapterModel = currentBillAdapterModel;
        this.currentMsisdn = currentBillAdapterModel.getMsisdn();
        initUIElements();
        setupView();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (adapterModel != null && adapterModel.isExpanded()) {
            D.w(String.valueOf(adapterModel.hashCode()));
            arrow.setRotation(180);
            initUIElements();
            setupView();
        }
    }

}

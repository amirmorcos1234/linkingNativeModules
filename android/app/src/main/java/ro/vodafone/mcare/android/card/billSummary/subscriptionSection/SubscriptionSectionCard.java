package ro.vodafone.mcare.android.card.billSummary.subscriptionSection;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.bumptech.glide.Glide;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.card.costControl.currentBill.ExpandableListItemModel;
import ro.vodafone.mcare.android.client.adapters.ExpandableListAdapter;
import ro.vodafone.mcare.android.client.model.realm.billSummary.BillSummaryItem;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.system.BillingOverviewLabels;
import ro.vodafone.mcare.android.ui.activities.BillingOverviewActivity;
import ro.vodafone.mcare.android.ui.fragments.billingOverview.InstructionalMessageFragment;
import ro.vodafone.mcare.android.ui.utils.DrawableUtils;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;
import ro.vodafone.mcare.android.ui.views.ExpandableAdapterBackedLinearLayout;
import ro.vodafone.mcare.android.ui.views.specialgroupview.CustomWidgetLoadingLayout;
import ro.vodafone.mcare.android.ui.views.specialgroupview.ViewGroupParamsEnum;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Bivol Pavel on 18.04.2017.
 */
public class SubscriptionSectionCard extends VodafoneAbstractCard {

    @BindView(R.id.card_container)
    LinearLayout cardContainer;

    @BindView(R.id.user_image)
    CircleImageView userImage;

    @BindView(R.id.subscriber_full_name)
    VodafoneTextView subscriberName;

    @BindView(R.id.msisdn)
    VodafoneTextView msisdn;

    @BindView(R.id.cost)
    VodafoneTextView cost;

    @BindView(R.id.aditional_cost)
    VodafoneTextView aditionalCost;

    @BindView(R.id.arrow)
    ImageView arrow;

    @BindView(R.id.expand_section)
    RelativeLayout billSummaryDataSection;

    @BindView(R.id.expandable_list_view)
    ExpandableAdapterBackedLinearLayout expandableListView;

    @BindView(R.id.error_container)
    RelativeLayout errorContainer;

    private boolean isDisplayedExpandSection = false;

    private ExpandableListAdapter listAdapter;

    private BillSummaryItem billSummaryItem;

    private static final String TEMPLATE = "template";

    private CardErrorLayout billSummaryErrorView;
    private CustomWidgetLoadingLayout billSummaryLoadingView;

    private List<ExpandableListItemModel> listDataHeader;
    private HashMap<ExpandableListItemModel, List<ExpandableListItemModel>> listDataChild;
    private Long billClosedDate;
    private boolean olderThanLast3Months;

    public SubscriptionSectionCard(Context context) {
        super(context);
        init(null);
    }

    public SubscriptionSectionCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @Override
    protected int setContent() {
        return R.layout.card_subscription_section;
    }

    private void init(AttributeSet attrs) {
        ButterKnife.bind(this);

        initClickListner();
        setCardMargins();
    }

    private void setCardMargins() {
        setCardMargins((int) getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal),
                (int) getResources().getDimensionPixelSize(R.dimen.default_margin_card_vertical),
                (int) getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal),
                (int) getResources().getDimensionPixelSize(R.dimen.default_margin_card_vertical));
    }

    public void buildHeaderSection(BillSummaryItem billSummaryItem, Long billClosedDate, boolean olderThanLast3Months) {
        this.billSummaryItem = billSummaryItem;
        this.billClosedDate = billClosedDate;
        this.olderThanLast3Months = olderThanLast3Months;
        this.listDataHeader = billSummaryItem.getListDataHeader();
        this.listDataChild = billSummaryItem.getListDataChild();
        this.isDisplayedExpandSection = billSummaryItem.isExpanded();
        if (this.billSummaryItem != null) {
            setAttributes();
        } else {
            showError(true);
        }

        initUIElements();

        if (isDisplayedExpandSection) {
            displayBillSummarySection();
            arrow.setRotation(180);
        } else {
            arrow.setRotation(0);
            hideBillSummarySection();
        }
    }

    public void displayBillSummaryData(List<ExpandableListItemModel> listDataHeader, HashMap<ExpandableListItemModel, List<ExpandableListItemModel>> listDataChild) {

        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
        billSummaryItem.setListDataChild(listDataChild);
        billSummaryItem.setListDataHeader(listDataHeader);

        hideBillSummaryLoading();
        if (listDataHeader.size() != 0) {
            setupExpandableListView();
        } else {
            showBillSummaryError();
        }
    }

    private void setupExpandableListView() {
        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild, false);
        expandableListView.setOnGroupClickListener(groupClickListner);
        expandableListView.setAdapter(listAdapter);
        //expandableListView.setDividerDrawable();
        expandableListView.setVisibility(VISIBLE);
    }

    ExpandableListView.OnGroupClickListener groupClickListner = new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {

            try {
                ExpandableListItemModel header = (ExpandableListItemModel) listAdapter.getGroup(i);

                String callDetailsSelectedTab;
                if(header.get_itemName().equalsIgnoreCase("suplimentar")) {
                    callDetailsSelectedTab = "1";
                } else {
                    callDetailsSelectedTab = "0";
                }
                Log.d(TAG, header.get_itemName());
                if (header.is_isClickable()) {
                    if ((!isOlderThanLast3Months())) {
                        IntentActionName intentActionName = IntentActionName.CALL_DETAILS_BILLED;
                        try {
                            Class c = IntentActionName.CALL_DETAILS_BILLED.getClass();
                            Method method;
                            method = c.getMethod("setLastBillClosedDate", long.class);
                            method.invoke(intentActionName, billClosedDate);
                            String lastBillDate = billClosedDate != null ? String.valueOf(billClosedDate) : "";
                            Log.d(TAG, lastBillDate);
                            String redirectParams = lastBillDate + ":" + callDetailsSelectedTab;
                            IntentActionName.CALL_DETAILS_BILLED.setOneUsageSerializedData(redirectParams);
                            //Set Selected Ban
                            if (billSummaryItem != null) {
                                Subscriber subscriber = getBillPhoneNumberSubscriber(billSummaryItem.getPhoneNumber());
                                if (haveMoreThanOneSubscriber()) {
                                    if (isBillNumberInSubscriberList(subscriber)) {
                                        UserSelectedMsisdnBanController.getInstance().setSelectedSubscriber(subscriber);
                                    } else {
                                        //TODO show error
                                        ((BillingOverviewActivity) view.getContext()).atachFragment(InstructionalMessageFragment.newInstance(BillingOverviewLabels.getBillingOverviewExpiredBillMessage()));
                                        return false;
                                    }
                                }
                            }
                            new NavigationAction(getContext()).finishCurrent(true).startAction(intentActionName);
                        } catch
                                (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else if (SubscriptionSectionController.getInstance().allowRedirect()) {
                        ((BillingOverviewActivity) view.getContext()).atachFragment(InstructionalMessageFragment.newInstance(BillingOverviewLabels.getBillingOverviewExpiredBillMessage()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        private boolean isBillNumberInSubscriberList(Subscriber subscriber) {
            return  subscriber != null;
        }
    };

    private boolean haveMoreThanOneSubscriber() {
        return RealmManager.getAllRealmObject(Subscriber.class).size() > 1;
    }

    private Subscriber getBillPhoneNumberSubscriber(String phoneNumber) {
        return (Subscriber) RealmManager.getAllRealmObject(Subscriber.class).where().contains(Subscriber.MSISDN_KEY, phoneNumber).findFirst();
    }

    private boolean isOlderThanLast3Months() {
        return olderThanLast3Months;
    }

    private void setAttributes() {

        msisdn.setText(billSummaryItem.getPhoneNumber());

        if (billSummaryItem.getAdditionalCost() != null && billSummaryItem.getAdditionalCost() > 0) {
            setAditionalCostStyle();
        } else {
            setNoAditionalCostStyle();
        }
    }

    private void setNoAditionalCostStyle() {
        cardContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_chart_top_color));
        cost.setText(getTotalBillValueFormated(billSummaryItem.getTotalAmount(), true, false));
    }

    private void setAditionalCostStyle() {
        cardContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple));
        aditionalCost.setVisibility(VISIBLE);
        aditionalCost.setText(getAditionalCostFormated(billSummaryItem.getAdditionalCost()));
        cost.setText(getTotalBillValueFormated(billSummaryItem.getTotalAmount(), true, true));
    }

    private SpannableStringBuilder getTotalBillValueFormated(double totalBillValue, boolean boldStyleAplied, boolean purpleStyleAplied) {

        String finalText = BillingOverviewLabels.getBilling_overview_total_bill_value();
        String costWithUnit = String.valueOf(NumbersUtils.twoDigitsAfterDecimal(totalBillValue)) + " " + BillingOverviewLabels.getBilling_overview_ron_unit();
        finalText = finalText.replace(TEMPLATE, costWithUnit);
        SpannableStringBuilder sb = new SpannableStringBuilder(finalText);

        try {
            if (boldStyleAplied) {
                StyleSpan boldStyleSpan = new StyleSpan(android.graphics.Typeface.BOLD);
                sb.setSpan(boldStyleSpan, 0, costWithUnit.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }

            if (purpleStyleAplied) {
                ForegroundColorSpan textColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.purple));
                sb.setSpan(textColorSpan, 0, costWithUnit.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

    private SpannableStringBuilder getAditionalCostFormated(double aditionalCost) {

        String finalText = BillingOverviewLabels.getBilling_overview_aditional_cost();
        String costWithUnit = String.valueOf(NumbersUtils.twoDigitsAfterDecimal(aditionalCost)) + " " + BillingOverviewLabels.getBilling_overview_ron_unit();
        finalText = finalText.replace(TEMPLATE, costWithUnit);
        SpannableStringBuilder sb = new SpannableStringBuilder(finalText);
        try {
            StyleSpan boldStyleSpan = new StyleSpan(android.graphics.Typeface.BOLD);
            sb.setSpan(boldStyleSpan, 8, 8 + costWithUnit.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

    private void initUIElements() {
        Log.d(TAG, "initUIElements: ");
        if (billSummaryItem != null) {
            String mMsisdn = billSummaryItem.getPhoneNumber();
            Log.d(TAG, "initUIElements: mMsisdn " + mMsisdn);

            Subscriber subscriber = (Subscriber) RealmManager.getRealmObjectAfterStringFieldThatContains(Subscriber.class, Subscriber.MSISDN_KEY, formatMsisdn(mMsisdn));
            Log.d(TAG, "initUIElements: subscriber " + subscriber);
            Log.d(TAG, "initUIElements: subscribermsis " + formatMsisdn(mMsisdn));

            if (subscriber != null && subscriber.getAvatarUrl() != null && !subscriber.getAvatarUrl().isEmpty()) {
                Glide.with(getContext())
                        .load(subscriber.getAvatarUrl())
                        .placeholder(R.drawable.phone_icon)
                        //RoundedImageView are known to have issues with TransitionDrawable use .dontAnimate() to fix the issue.
                        .dontAnimate()
                        .error(R.drawable.phone_icon)
                        .into(userImage);
            } else {
                userImage.setPadding(ScreenMeasure.dpToPx(10), ScreenMeasure.dpToPx(10), ScreenMeasure.dpToPx(10), ScreenMeasure.dpToPx(10));
                userImage.setColorFilter(Color.parseColor("#ffffff"));
            }
        } else {
            userImage.setPadding(ScreenMeasure.dpToPx(10), ScreenMeasure.dpToPx(10), ScreenMeasure.dpToPx(10), ScreenMeasure.dpToPx(10));
            userImage.setColorFilter(Color.parseColor("#ffffff"));
        }


        arrow.setColorFilter(Color.parseColor("#E60000"));
    }

    private void initClickListner() {

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadingView == null || !loadingView.isVisible()) {
                    if (billSummaryErrorView != null && billSummaryErrorView.getVisibility() == VISIBLE) {
                        SubscriptionSectionController.getInstance().requestBillSummaryDetails(SubscriptionSectionCard.this, billSummaryItem, billClosedDate);
                    } else {
                        arrow.setRotation(0);
                        DrawableUtils.rotateArrow(arrow, !isDisplayedExpandSection);
                        changeBillSummarySectionVisibility();
                    }
                }
            }
        });
    }

    private void changeBillSummarySectionVisibility() {
        if (!isDisplayedExpandSection) {
            displayBillSummarySection();
        } else {
            hideBillSummarySection();
        }
        isDisplayedExpandSection = !isDisplayedExpandSection;
        billSummaryItem.setExpanded(isDisplayedExpandSection);
    }


    private void displayBillSummarySection() {
        if (listDataHeader != null && listDataChild != null) {
            setupExpandableListView();
        } else {
            SubscriptionSectionController.getInstance().requestBillSummaryDetails(this, billSummaryItem, billClosedDate);
        }

        billSummaryDataSection.setVisibility(VISIBLE);
    }

    private void hideBillSummarySection() {
        billSummaryDataSection.setVisibility(GONE);
    }

    public VodafoneAbstractCard showBillSummaryError() {
        hideBillSummaryLoading();
        expandableListView.setVisibility(GONE);
        billSummaryErrorView = new CardErrorLayout(getContext());
        billSummaryErrorView.setText(BillingOverviewLabels.getErrorInformationCanNotBeDisplayed());
        billSummaryDataSection.addView(billSummaryErrorView);
        return this;
    }

    public VodafoneAbstractCard showBillSummaryLoading() {
        hideBillSummaryError();
        expandableListView.setVisibility(GONE);
        billSummaryLoadingView = new CustomWidgetLoadingLayout(getContext()).build(
                billSummaryDataSection,
                Color.RED,
                ViewGroupParamsEnum.relative_center);
        billSummaryLoadingView.show();
        return this;
    }

    public void hideBillSummaryError() {
        if (billSummaryErrorView != null && billSummaryErrorView.getVisibility() == VISIBLE) {
            billSummaryDataSection.removeView(billSummaryErrorView);
            billSummaryErrorView = null;
        }
    }

    public void hideBillSummaryLoading() {
        if (billSummaryLoadingView != null && billSummaryLoadingView.isVisible()) {
            billSummaryDataSection.removeView(billSummaryLoadingView);
            billSummaryLoadingView.hide();
        }
    }

    private String formatMsisdn(String msisdn) {
        String newMsisdn = msisdn;
        if (msisdn.startsWith("4")) {
            newMsisdn = msisdn.substring(1);
        }
        return newMsisdn;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (billSummaryItem != null && billSummaryItem.isExpanded()) {
            arrow.setRotation(180);
        }
    }
}

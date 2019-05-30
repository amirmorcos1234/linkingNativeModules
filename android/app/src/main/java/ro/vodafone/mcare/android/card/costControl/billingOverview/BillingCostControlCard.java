package ro.vodafone.mcare.android.card.costControl.billingOverview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.card.costControl.currentBill.ExpandableListItemModel;
import ro.vodafone.mcare.android.client.model.costControl.AdditionalCost;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.realm.system.BillingOverviewLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.PrivateUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.ui.activities.BillingOverviewActivity;
import ro.vodafone.mcare.android.ui.fragments.billingOverview.CostControlCurrentBillFragment;
import ro.vodafone.mcare.android.ui.fragments.callDetails.Category;
import ro.vodafone.mcare.android.ui.utils.DrawableUtils;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.utils.navigation.ProfileUtils;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Bivol Pavel on 07.04.2017.
 */

public class BillingCostControlCard extends VodafoneAbstractCard {

    @BindView(R.id.card_container)
    View container;

    @BindView(R.id.bill_cycle_date)
    VodafoneTextView billCycleDate;

    @BindView(R.id.cost)
    VodafoneTextView cost;

    @BindView(R.id.card_image)
    ImageView arrow;

    @BindView(R.id.expand_section)
    LinearLayout expandSection;

    ExpandableContent expandableContent;

    private boolean isDisplayedExpandSection = true;

    public BillingCostControlCard(Context context) {
        super(context);
        init(null);
    }

    public BillingCostControlCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BillingCostControlCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void setupController() {
        BillingCostControlCardController.getInstance().setup(this);
    }

    @Override
    protected int setContent() {
        return R.layout.card_billing_cost_control;
    }

    private void init(AttributeSet attrs) {

        ButterKnife.bind(this);
        setupController();
        displayArrow();
        setNoAditionalCostStyle();
        setClickListner();
        setCardMargins();
        getCostControlFromRealm();
    }

    private void setCardMargins() {
        setCardMargins((int) getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal),
                (int) getResources().getDimensionPixelSize(R.dimen.default_margin_card_vertical),
                (int) getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal),
                (int) getResources().getDimensionPixelSize(R.dimen.default_margin_card_vertical));
    }

    private void displayArrow() {
        User user = VodafoneController.getInstance().getUser();
        if (user instanceof ResCorp
                || user instanceof PrivateUser) {
            arrow.setVisibility(VISIBLE);
        } else if (user instanceof EbuMigrated) {
            arrow.setVisibility(VISIBLE);
            arrow.setRotation(270);
            invalidate();
        }
    }

    private void getCostControlFromRealm() {
        BillingCostControlCardController.getInstance().requestData();
    }

    public BillingCostControlCard buildCard(AdditionalCost additionalCost) {
        hideLoading();
        hideError();

        if (errorView != null) {
            errorView.setVisibility(GONE);
        }

        String textWithSelectedNumberIfNeeded = BillingCostControlCardController.getInstance().shouldDisplayCurrentNumber() ?
                BillingOverviewLabels.getBilling_overview_costcontrol_for_number() + " " + UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getNumberWithout4PrefixOneMsisdn() : "";
        String headerText = BillingOverviewLabels.getBilling_overview_last_bill_cycle_date() +
                " " + ProfileUtils.getLastBillCycleDate() + " " +
                textWithSelectedNumberIfNeeded;
        billCycleDate.setText(headerText);

        if (additionalCost != null && additionalCost.getTotalCost() != null && additionalCost.getTotalCost() > 0) {
            setAditionalCostStyle();
            cost.setText(NumbersUtils.twoDigitsAfterDecimal(additionalCost.getTotalCost()) + " " + BillingOverviewLabels.getBilling_overview_euro_unit());
        } else {
            setNoAditionalCostStyle();
            cost.setText("0.00 €");
        }
        if (VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
            expandSection.setVisibility(VISIBLE);
            setupDetails(additionalCost);
        }

        return this;
    }

    /*
    * Displayed blue if there is no additional cost of bill. Cost control = 0€
    */
    private void setNoAditionalCostStyle() {
        container.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_chart_top_color));
        cost.setTextColor(ContextCompat.getColor(getContext(), R.color.blue_chart_top_color));
    }

    /*
    * Displayed mauve if there is additional cost of bill. Cost control > 0€
    */
    private void setAditionalCostStyle() {
        container.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.purple));
        cost.setTextColor(ContextCompat.getColor(getContext(), R.color.purple));
    }


    public BillingCostControlCard setClickListner() {

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadingView == null || !loadingView.isVisible()) {
                    Log.d(TAG, "onClick");
                    if (errorView != null && errorView.getVisibility() == VISIBLE) {
                        BillingCostControlCardController.getInstance().requestData();
                    } else {
                        User user = VodafoneController.getInstance().getUser();
                        if (user instanceof ResCorp
                                || user instanceof PrivateUser) {
                            ((BillingOverviewActivity) getContext()).atachFragment(new CostControlCurrentBillFragment());
                        } else if (user instanceof EbuMigrated) {
                            ebuClickAction();
                        }
                    }
                }
            }
        });

        return this;
    }

    private void ebuClickAction() {
        isDisplayedExpandSection = !isDisplayedExpandSection;
        DrawableUtils.rotateArrow(arrow, !isDisplayedExpandSection);
        changeExpandSectionVisibility();
    }

    private void changeExpandSectionVisibility() {
        if (isDisplayedExpandSection) {
            expandSection.setVisibility(VISIBLE);
        } else {
            expandSection.setVisibility(GONE);
        }
    }

    public BillingCostControlCard showError(String errorMessage) {
        super.showError(true);
        if (errorMessage != null) {
            errorView.setText(errorMessage);
        }
        return this;
    }

    public BillingCostControlCard showLoading() {
        super.showLoading(true);
        return this;
    }


    public void setupDetails(AdditionalCost additionalCost) {
        ArrayList<ExpandableListItemModel> items = new ArrayList<>();
        ExpandableListItemModel groupModel = new ExpandableListItemModel("Suplimentar", additionalCost.getTotalCost(), BillingOverviewLabels.getBilling_overview_euro_unit(), false);

        items.add(new ExpandableListItemModel("Voce", additionalCost.getVoiceCost(), BillingOverviewLabels.getBilling_overview_euro_unit(), false));
        items.add(new ExpandableListItemModel("Date", additionalCost.getDataCost(), BillingOverviewLabels.getBilling_overview_euro_unit(), false));
        items.add(new ExpandableListItemModel("SMS", additionalCost.getSmsCost(), BillingOverviewLabels.getBilling_overview_euro_unit(), false));
        items.add(new ExpandableListItemModel("Altele", additionalCost.getOtherCost(), BillingOverviewLabels.getBilling_overview_euro_unit(), false));

        inflateExpandableGroup(groupModel, items);
    }

    private void inflateExpandableGroup(ExpandableListItemModel groupModel, ArrayList<ExpandableListItemModel> items) {
        View viewExpandController = inflateItemModel(groupModel, true);
        final List<View> detailViews = new ArrayList<>();
        for (ExpandableListItemModel itemModel : items) {
            detailViews.add(inflateItemModel(itemModel, false));
        }

        expandableContent = new ExpandableContent(expandSection, detailViews, viewExpandController);
        expandableContent.inflateContent();
    }

    private View inflateItemModel(final ExpandableListItemModel itemModel, boolean expandable) {
        LayoutInflater inflater = (LayoutInflater) this.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.expandable_list_item, null, false);

        VodafoneTextView itemName = convertView.findViewById(R.id.item_name);
        VodafoneTextView cost = convertView.findViewById(R.id.cost);
        ImageView itemArrow = convertView.findViewById(R.id.item_arrow);
        if (expandable) {
            itemArrow.setVisibility(View.VISIBLE);
            itemArrow.setImageResource(R.drawable.arrow);
            itemArrow.setRotation(180);
        } else {
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getContext() != null){
                        switch (itemModel.get_itemName()) {
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
                    }

                }

                private void navigateToCallDetails(Category category) {
                    Log.d(TAG, "navigateToCallDetails");
                    IntentActionName i = IntentActionName.CALL_DETAILS_UNBILLED;

                    try {
                        Class c = IntentActionName.CALL_DETAILS_UNBILLED.getClass();
                        Method method;
                        method = c.getMethod("setCategory", Category.class);
                        method.invoke(i, category);
                        new NavigationAction(getContext()).finishCurrent(true).startAction(i);

                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        itemName.setText(itemModel.get_itemName());
        cost.setText(NumbersUtils.twoDigitsAfterDecimal(itemModel.get_itemParameter()) + " " + itemModel.get_unit());
        setCostAndNameColor(itemModel, cost, itemName);
        return convertView;
    }


    private void setCostAndNameColor(ExpandableListItemModel expandableListItemModel, VodafoneTextView cost, VodafoneTextView groupName) {
        if (expandableListItemModel.get_itemName().equals("Suplimentar")) {
            groupName.setFont(VodafoneTextView.TextStyle.VODAFONE_RGBD);
        } else {
            groupName.setFont(VodafoneTextView.TextStyle.VODAFONE_LT);
        }

        if (expandableListItemModel.get_itemParameter() != null && expandableListItemModel.get_itemParameter() > 0) {
            cost.setTextColor(ContextCompat.getColor(getContext(), R.color.purple));
        }
    }

    class ExpandableContent {

        final ViewGroup parent;
        final List<View> childs;
        final View viewController;

        boolean isExpanded = true;

        public ExpandableContent(ViewGroup parent, List<View> childs, View viewController) {
            this.parent = parent;
            this.childs = childs;
            this.viewController = viewController;
            viewController.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleExpandedSection();
                }
            });

        }


        public void expandGroup() {
            for (View view : ExpandableContent.this.childs) {
                view.setVisibility(VISIBLE);
            }
        }

        public void retractGroup() {
            for (View view : ExpandableContent.this.childs) {
                view.setVisibility(GONE);
            }
        }

        public void toggleExpandedSection() {
            isExpanded = !isExpanded;
            if (isExpanded) {
                expandGroup();
            } else {
                retractGroup();
            }
            ImageView arrowView = viewController.findViewById(R.id.item_arrow);
            DrawableUtils.rotateArrow(arrowView, !isExpanded);
        }

        public void inflateContent() {
            if (parent != null) {
                parent.addView(viewController);
                for (View view :
                        childs) {
                    parent.addView(view);
                }
            }
        }
    }
}

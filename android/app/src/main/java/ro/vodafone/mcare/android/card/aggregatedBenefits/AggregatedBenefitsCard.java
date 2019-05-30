package ro.vodafone.mcare.android.card.aggregatedBenefits;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.amountUnit.AmountUnitModel;
import ro.vodafone.mcare.android.client.model.costControl.AmountTypeIdEnum;
import ro.vodafone.mcare.android.client.model.costControl.AmountUnitEnum;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;
import ro.vodafone.mcare.android.ui.utils.AmountUnitUtils;
import ro.vodafone.mcare.android.ui.utils.DrawableUtils;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;
import ro.vodafone.mcare.android.ui.views.specialgroupview.CustomWidgetLoadingLayout;
import ro.vodafone.mcare.android.ui.views.specialgroupview.ViewGroupParamsEnum;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Bivol Pavel on 04.04.2017.
 */

public class AggregatedBenefitsCard extends VodafoneAbstractCard {

    private Context mContext;

    AggregatedBenefitsCardController aggregatedBenefitsCardController;

    ImageView arrow;
    RelativeLayout expandButton;
    boolean shouldRotateUp = true;
    private List<BalanceShowAndNotShown> aggregatedBenefitsList;
    private LinearLayout aggregatedBenefitsListView;
    private LinearLayout expandedLayout;
    private CardErrorLayout cardErrorView;

    public AggregatedBenefitsCard(Context context, AggregatedBenefitsCardController aggregatedBenefitsCardController) {
        super(context);
        this.mContext = context;
        this.aggregatedBenefitsCardController = aggregatedBenefitsCardController;
        init(null);
    }

    public AggregatedBenefitsCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    public AggregatedBenefitsCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }

    @Override
    protected int setContent() {
        return R.layout.card_summary_of_benefits;
    }

    private void init(AttributeSet attrs) {
        initExpandButton();
        setupController();
        aggregatedBenefitsCardController.requestData();
    }

    public AggregatedBenefitsCard setOnErrorClickListner() {
        cardErrorView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                aggregatedBenefitsCardController.requestData();
            }
        });
        return this;
    }



    private void initExpandButton() {
        expandButton = (RelativeLayout) findViewById(R.id.expand_button);
        expandedLayout = (LinearLayout) findViewById(R.id.expanded_layout);
        expandedLayout.setVisibility(GONE);

        arrow = (ImageView) findViewById(R.id.arrow);
        arrow.setColorFilter(Color.parseColor("#E60000"));

        expandButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawableUtils.rotateArrow(arrow, shouldRotateUp);
                shouldRotateUp = !shouldRotateUp;
                if (expandedLayout.getVisibility() == GONE) {
                    expandedLayout.setVisibility(VISIBLE);
                } else if (expandedLayout.getVisibility() == VISIBLE) {
                    expandedLayout.setVisibility(GONE);
                }
            }
        });
    }

    private void setupController() {
        aggregatedBenefitsCardController = new AggregatedBenefitsCardController(this);
    }

    public AggregatedBenefitsCardController getController() {
        return aggregatedBenefitsCardController;
    }

    public void showCardError(String errorMessage) {
        hideCardLoading();
        if (cardErrorView == null) {
            cardErrorView = new CardErrorLayout(getContext());
            cardErrorView.setMargins(0, 16, 0, 16);
            expandedLayout.addView(cardErrorView);
            setOnErrorClickListner();
        } else {
            cardErrorView.setVisibility(VISIBLE);
        }

        if (errorMessage != null) {
            cardErrorView.setText(errorMessage);
        }
    }

    public void hideCardError() {
        if (cardErrorView != null) {
            cardErrorView.setVisibility(GONE);
        }
    }

    public void showCardLoading() {
        hideCardError();
        hideError();
        if (loadingView == null) {
            loadingView = new CustomWidgetLoadingLayout(mContext).build(
                    expandedLayout,
                    Color.RED,
                    ViewGroupParamsEnum.card_params);
        }
        loadingView.show();
    }

    public void hideCardLoading() {
        if (loadingView != null) {
            loadingView.hide();
        }

    }

    public void buildPrepaidCard(List<BalanceShowAndNotShown> list) {
        this.aggregatedBenefitsList = list;
        setupListView();
        hideCardLoading();
    }

    private void setupListView() {
        Log.d(TAG, "setupListView: ");
        aggregatedBenefitsListView = (LinearLayout) findViewById(R.id.aggregated_benefits_list_view);
        aggregatedBenefitsListView.removeAllViews();

        for (BalanceShowAndNotShown balance : aggregatedBenefitsList) {
            aggregatedBenefitsListView.addView(makeBenefitView(aggregatedBenefitsListView, balance));
        }
    }

    private View makeBenefitView(ViewGroup group, BalanceShowAndNotShown balance) {
        final View view = ((LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.aggregated_benefit_list_item, group, false);
        VodafoneTextView benefitName = (VodafoneTextView) view.findViewById(R.id.benefit_name);
        VodafoneTextView benefitAmount = (VodafoneTextView) view.findViewById(R.id.benefit_amount);
        ImageView benefitIcon = (ImageView) view.findViewById(R.id.benefit_icon);
        VodafoneTextView amountUnit = (VodafoneTextView) view.findViewById(R.id.amount_unit);


        benefitName.setText(balance.getNameRO());

        if (balance.getAmountUnit() == AmountUnitEnum.kb || balance.getAmountUnit() == AmountUnitEnum.mb || balance.getAmountUnit() == AmountUnitEnum.gb) {

            benefitAmount.setText(NumbersUtils.roundUP(toMb(balance.getAmountUnit(), Double.valueOf(balance.getRemainingAmount()))));

        }else{
            AmountUnitModel amountUnitModel = AmountUnitUtils.getAmountUnitObject(balance, Double.valueOf(balance.getRemainingAmount()));
            if (amountUnitModel.getAmount() != null) {
                benefitAmount.setText(String.valueOf(Math.round(amountUnitModel.getAmount())));
            }
            else
            {
                String unlimitedBalance = balance.getNameRO().toLowerCase();
                if(unlimitedBalance.contains("nelimitat"))
                {
                    benefitAmount.setText("Nelimitat");
                }
            }

            //amountUnit.setText(amountUnitModel.getUnit());
        }

        setRowImage(benefitIcon, balance.getAmountTypeId());

        return view;
    }


    private Double toMb(AmountUnitEnum amountUnitEnum, Double value){
        Double result = Double.valueOf(0);

        if(value != null){
            switch (amountUnitEnum)
            {
                case kb:
                    result = value/1024.0;
                    break;
                case mb:
                    result = value;
                    break;
                case gb:
                    result = value*1024.0;
                    break;
            }
        }

        return result;
    }

    private void setRowImage(ImageView imageView, AmountTypeIdEnum category) {
        if (category != null) {
            switch (category) {
                case data:
                    imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.data_sharing));
                    break;
                case voice:
                    imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.minutes));
                    break;
                case sms:
                    imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.sms));
                    break;
                case vas:
                    imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.sync_48));
                    break;
                case cvt:
                    imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.community_or_foundation_48));
                    break;
            }
        } else {
            //Set default image for other .
            imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.minutes));
        }
    }
}

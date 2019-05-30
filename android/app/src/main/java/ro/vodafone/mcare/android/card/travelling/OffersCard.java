package ro.vodafone.mcare.android.card.travelling;

import android.content.Context;
import android.util.AttributeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.travellingAboard.PrePaidOffersList;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by OsMattar on 14-Jan-19.
 */

public class OffersCard extends VodafoneAbstractCard {
    @BindView(R.id.offer_name)
    VodafoneTextView offerNameTextView;
    @BindView(R.id.cost)
    VodafoneTextView offerCostTextView;
    @BindView(R.id.offer_description)
    VodafoneTextView offerDescription;
    @BindView(R.id.add_extraoptions_button)
    VodafoneButton extraOptions;
    private PrePaidOffersList prePaidOffersList;

    public OffersCard(Context context) {
        super(context);
    }

    public OffersCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OffersCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OffersCard build() {
        ButterKnife.bind(this);
        setupOfferCardLabels();
        setupCardMargins();
        return this;
    }

    private void setupCardMargins() {
        int horizontalMargin = getResources().getDimensionPixelSize(R.dimen.card_horizontal_padding);
        int verticalMargin = getResources().getDimensionPixelSize(R.dimen.default_margin_vertical);
        setCardMargins(horizontalMargin, 0, horizontalMargin, verticalMargin);
    }

    public PrePaidOffersList getPrePaidOffersList() {
        return prePaidOffersList;
    }

    public OffersCard setPrePaidOffersList(PrePaidOffersList prePaidOffersList) {
        this.prePaidOffersList = prePaidOffersList;
        return this;
    }

    private void setupOfferCardLabels() {
        offerNameTextView.setText(prePaidOffersList.getOfferName());
        offerCostTextView.setText("Cost: " + String.valueOf(prePaidOffersList.getOfferPrice()) + " €");
        offerDescription.setText(prePaidOffersList.getOfferShortDescription());
        offerDescription.setTextSize(16);
    }

    @OnClick(R.id.add_extraoptions_button)
    public void buttonClick() {
        NavigationAction navAction = new NavigationAction(getContext(), IntentActionName.OFFERS_BEO_DETAILS)
                .setOneUsageSerializedData(String.valueOf(prePaidOffersList.getOfferId()))
                .setExtraParameter("fromRoaming");
        navAction.startAction();
    }

    @Override
    protected int setContent() {
        return R.layout.prepaid_roaming_extra_option_card;
    }
}

package ro.vodafone.mcare.android.card.additionalPromos;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hookedonplay.decoviewlib.DecoView;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.amountUnit.AmountUnitModel;
import ro.vodafone.mcare.android.client.model.card.CardButtonModel;
import ro.vodafone.mcare.android.client.model.card.CardHeaderModel;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.BillingOffer;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.Promo;
import ro.vodafone.mcare.android.ui.activities.yourServices.YourServicesActivity;
import ro.vodafone.mcare.android.ui.fragments.yourServices.details.OptionsDetailsFragment;
import ro.vodafone.mcare.android.ui.utils.AmountUnitUtils;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.CardButton;
import ro.vodafone.mcare.android.ui.views.CardHeader;
import ro.vodafone.mcare.android.ui.views.arcView.ArcView;
import ro.vodafone.mcare.android.ui.views.arcView.ArcViewModel;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

import static ro.vodafone.mcare.android.ui.utils.AmountUnitUtils.GB_DATA_UNIT;

/**
 * Created by Bivol Pavel on 18.10.2017.
 */

public class AdditionalPromosCard extends VodafoneAbstractCard{

    private AdditionalPromosViewGroup viewGroup;
    private List<BalanceShowAndNotShown> balanceShowAndNotShownList;
    private LinearLayout activeOffersListView;

    private Promo promo;
    private BillingOffer billingOffer;

    public AdditionalPromosCard(Context context) {
        super(context);
        init(null);
    }

    public AdditionalPromosCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public Promo getPromo() {
        return promo;
    }

    @Override
    protected int setContent() {
        return R.layout.card_additional_promos;
    }

    private void init(AttributeSet attrs) {
        setCardPaddingsInPx((int)(getResources().getDimension(R.dimen.default_padding_horizontal)),
                0, (int)(getResources().getDimension(R.dimen.default_padding_horizontal)), 0);
    }

    public AdditionalPromosCard buildCard(Promo promo, BillingOffer billingOffer) {
        this.promo = promo;
        this.billingOffer = billingOffer;

        activeOffersListView = (LinearLayout) findViewById(R.id.additional_promos_list_view);
        activeOffersListView.removeAllViews();

        if(promo != null){
            String promoPrice = null;
            if(promo.getPromoPrice() != null)
                promoPrice = NumbersUtils
                        .truncateDecimal(promo.getPromoPrice(),2) + " € pe lună";
            atachHeader(null, promo.getPromoName(), promoPrice,
                    promo.getPromoCategory(), null);

            if(balanceShowAndNotShownList != null && !balanceShowAndNotShownList.isEmpty()
                    && promo.getBoList() != null){
                for(BalanceShowAndNotShown balanceShowAndNotShown : balanceShowAndNotShownList){
                    for(BillingOffer bo : promo.getBoList()){
                        if(balanceShowAndNotShown.getId() == bo.getBoId()){
                            makeOfferView(activeOffersListView, balanceShowAndNotShown);
                        }
                    }
                }
            }
        }else if(billingOffer != null){
            String offerPrice = null;
            if(billingOffer.getBoPrice() != null){
                offerPrice = NumbersUtils.truncateDecimal(
                        Float.valueOf(billingOffer.getBoPrice()),2) + " € pe lună";
            }
            atachHeader(null, billingOffer.getBoName(), offerPrice,
                    null, null);

            if(balanceShowAndNotShownList != null && !balanceShowAndNotShownList.isEmpty()){
                for(BalanceShowAndNotShown balanceShowAndNotShown : balanceShowAndNotShownList){
                    if(balanceShowAndNotShown.getId() == billingOffer.getBoId()){
                        makeOfferView(activeOffersListView, balanceShowAndNotShown);
                    }
                }
            }
        }

        atachButton();
        hideLoading();

        return this;
    }

    private void setBoldTypeFace(SpannableString spannableString, int start, int end){
        spannableString.setSpan(new StyleSpan(Typeface.BOLD),start,end,0);
    }

    private int getLastBoldPosition(String label, String lastBoldSequence){
        return label.indexOf(lastBoldSequence) + 1;
    }

    private void atachHeader(String headerIcon, String headerTitle, String headerSubTitle, String headerCategory, String headerExtraParameter) {
        addHeader(new CardHeader(getContext())
                .buildHeader(new CardHeaderModel(headerIcon, headerTitle, makeOfferPriceBoldText(headerSubTitle),
                        headerCategory, null, headerExtraParameter)));
    }

    private void atachButton() {

        addButton(new CardButton(getContext())
                .buildButton(new CardButtonModel("Detalii opțiune", new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(promo != null){
                            ((YourServicesActivity) getContext()).attachFragment(new OptionsDetailsFragment()
                                    .setArgsOnBundle(Promo.class.getCanonicalName(), promo));

                        } else if (billingOffer != null){
                            ((YourServicesActivity) getContext()).attachFragment(new OptionsDetailsFragment()
                                    .setArgsOnBundle(BillingOffer.class.getCanonicalName(), billingOffer));
                        }
                    }
                }, null)));
    }

    private View makeOfferView(ViewGroup group, BalanceShowAndNotShown balanceShowAndNotShown) {
        final View view = ((LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_active_offers, group, false);

        //binding views
        DecoView decoView = (DecoView) view.findViewById(R.id.dynamicArcView);
        VodafoneTextView offerName = (VodafoneTextView) view.findViewById(R.id.offer_name);
        VodafoneTextView offerPrice = (VodafoneTextView) view.findViewById(R.id.offer_price);

        //Set text for total amount
        AmountUnitModel amountUnitModel = AmountUnitUtils.getAmountUnitObject(balanceShowAndNotShown, balanceShowAndNotShown.getTotalAmount());
        if(amountUnitModel.getUnit().equals(GB_DATA_UNIT)){
            offerName.setText(NumbersUtils.truncateDecimal(amountUnitModel.getAmount(), 2) + " " + amountUnitModel.getUnit());
        } else {
            offerName.setText(NumbersUtils.truncateDecimal(amountUnitModel.getAmount(),0) + " " + amountUnitModel.getUnit());
        }

        //offerName.setText(balanceShowAndNotShown.getNameRO());

        //Set text for remaining amount
        AmountUnitModel priceAmountUnitModel = AmountUnitUtils.getAmountUnitObject(balanceShowAndNotShown, balanceShowAndNotShown.getRemainingAmount());
        if(priceAmountUnitModel.getUnit().equals(GB_DATA_UNIT)){
            offerPrice.setText(NumbersUtils.truncateDecimal(priceAmountUnitModel.getAmount(),2) + " " + priceAmountUnitModel.getUnit());
        } else {
            offerPrice.setText(NumbersUtils.truncateDecimal(priceAmountUnitModel.getAmount(),0) + " " + priceAmountUnitModel.getUnit());
        }

        //Generate arc view
        if(balanceShowAndNotShown.getTotalAmount() != null && balanceShowAndNotShown.getRemainingAmount() != null)
        ArcView.atachTracksToArcView(decoView, new ArcViewModel(0, balanceShowAndNotShown.getTotalAmount().floatValue(),
                balanceShowAndNotShown.getRemainingAmount().floatValue(), 3f, 4f, Color.parseColor("#ebebeb"), Color.parseColor("#00afca")));


        return view;
    }

    private SpannableString makeOfferPriceBoldText(String offerPrice){

        SpannableString spannableString = null;

        if(offerPrice != null){
            spannableString = new SpannableString(offerPrice);
            setBoldTypeFace(spannableString, 0, getLastBoldPosition(spannableString.toString(), "€"));
        }

        return spannableString;
    }

    public AdditionalPromosCard setViewGroup(AdditionalPromosViewGroup viewGroup){
        this.viewGroup = viewGroup;
        return this;
    }

    public AdditionalPromosCard setBalanceShowAndNotShownList(CostControl costControl) {
        if(costControl != null && costControl.getCurrentExtraoptions() != null){
            this.balanceShowAndNotShownList = costControl.getCurrentExtraoptions().getExtendedBalanceList();
        }

        return this;
    }
}

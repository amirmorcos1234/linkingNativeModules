package ro.vodafone.mcare.android.card.activeOptions;

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

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.amountUnit.AmountUnitModel;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.BalanceInfo;
import ro.vodafone.mcare.android.client.model.card.CardButtonModel;
import ro.vodafone.mcare.android.client.model.card.CardHeaderModel;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.BillingOffer;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.Promo;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.interfaces.ActivityFragmentInterface;
import ro.vodafone.mcare.android.ui.fragments.yourServices.details.OptionsDetailsFragment;
import ro.vodafone.mcare.android.ui.utils.AmountUnitUtils;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.CardButton;
import ro.vodafone.mcare.android.ui.views.CardHeader;
import ro.vodafone.mcare.android.ui.views.arcView.ArcView;
import ro.vodafone.mcare.android.ui.views.arcView.ArcViewModel;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

import static ro.vodafone.mcare.android.ui.utils.AmountUnitUtils.GB_DATA_UNIT;
import static ro.vodafone.mcare.android.ui.utils.AmountUnitUtils.UNLIMITED;

/**
 * Created by user on 22.03.2017.
 */
public class ActiveOptionsCard extends VodafoneAbstractCard {

    private ActiveOptionsViewGroup viewGroup;
    private LinearLayout activeOffersListView;
    private String offerPrice;

    private ActiveOfferPostpaid activeOfferPostpaid;
    private Promo promo;
    private BillingOffer billingOffer;
    private ActiveOffer activeOffer;

    private List<BalanceShowAndNotShown> balanceShowAndNotShownList;

    public Promo getPromo() {
        return promo;
    }

    public ActiveOptionsCard(Context context) {
        super(context);
        init(null);
    }

    public ActiveOptionsCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ActiveOptionsCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setCardPaddingsInPx((int)(getResources().getDimension(R.dimen.default_padding_horizontal)), 0, (int)(getResources().getDimension(R.dimen.default_padding_horizontal)), 0);
        //setOnErrorClickListner();
    }

    public ActiveOptionsCard setOnErrorClickListner() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadingView == null || !loadingView.isVisible()) {
                    if (errorView != null && errorView.getVisibility() == VISIBLE) {
                        viewGroup.getController().requestData();
                    }
                }
            }
        });
        return this;
    }

    public ActiveOptionsCard setViewGroup(ActiveOptionsViewGroup viewGroup){
        this.viewGroup = viewGroup;
        return this;
    }

    public ActiveOptionsCard buildCBUCard(ActiveOfferPostpaid activeOfferPostpaid, List<BalanceShowAndNotShown> balanceShowAndNotShownList) {

        this.activeOfferPostpaid = activeOfferPostpaid;
        this.balanceShowAndNotShownList = balanceShowAndNotShownList;

        if (activeOfferPostpaid.getOfferPrice() != null) {
            offerPrice = String.valueOf(NumbersUtils.truncateDecimal(Float.valueOf(String.valueOf(activeOfferPostpaid.getOfferPrice())), 2));
            atachHeader(activeOfferPostpaid.getOfferImage(), activeOfferPostpaid.getOfferName(), String.format(getResources().getString(R.string.your_services_options_amount_unit), offerPrice), activeOfferPostpaid.getOfferCategory(), null);
        } else {
            atachHeader(activeOfferPostpaid.getOfferImage(), activeOfferPostpaid.getOfferName(), null, activeOfferPostpaid.getOfferCategory(), null);
        }

        setupListView();
        atachButton();
        hideLoading();

        return this;
    }

    public ActiveOptionsCard buildEBUCard(Promo promo, BillingOffer billingOffer,
                                          List<BalanceShowAndNotShown> balanceShowAndNotShownList) {

        this.promo = promo;
        this.billingOffer = billingOffer;
        this.balanceShowAndNotShownList = balanceShowAndNotShownList;

        if(promo != null){
            if(promo.getPromoPrice() != null){
                offerPrice = String.valueOf(NumbersUtils.truncateDecimal(promo.getPromoPrice(),2));
                atachHeader(null, promo.getPromoName(), offerPrice + " € pe lună", promo.getPromoCategory(), null);
            } else {
                atachHeader(null, promo.getPromoName(), null, promo.getPromoCategory(), null);
            }
        }else if(billingOffer != null){
            if(billingOffer.getBoPrice() != null){
                offerPrice = String.valueOf(NumbersUtils.truncateDecimal(Float.valueOf(billingOffer.getBoPrice()),2));
                atachHeader(null, billingOffer.getBoName(), offerPrice + " € pe lună", null, null);
            } else {
                atachHeader(null, billingOffer.getBoName(), null, null, null);
            }
        }

        setupListView();
        atachButton();
        hideLoading();

        return this;
    }

    public ActiveOptionsCard buildPrepaidCard(ActiveOffer activeOffer) {
        this.activeOffer = activeOffer;
        atachHeader(activeOffer.getOfferImage(), activeOffer.getOfferName(), null, null, null);
        setupListView();
        atachButton();
        hideLoading();

        return this;
    }

    private void atachHeader(String headerIcon, String headerTitle, String headerSubTitle, String headerCategory, String headerExtraParameter) {

        addHeader(new CardHeader(getContext())
                .buildHeader(new CardHeaderModel(headerIcon, headerTitle, makeOfferPriceBoldText(headerSubTitle),
                        headerCategory, null, null)));
    }


    private SpannableString makeOfferPriceBoldText(String offerPrice){

        SpannableString spannableString = null;

        if(offerPrice != null){
            spannableString = new SpannableString(offerPrice);
            setBoldTypeFace(spannableString, 0, getLastBoldPosition(spannableString.toString(), "€"));
        }

        return spannableString;
    }

    private void setBoldTypeFace(SpannableString spannableString, int start, int end){
        spannableString.setSpan(new StyleSpan(Typeface.BOLD),start,end,0);
    }

    private int getLastBoldPosition(String label, String lastBoldSequence){
        return label.indexOf(lastBoldSequence) + 1;
    }

    private void atachButton() {
        if(!(getContext() instanceof ActivityFragmentInterface))
            return;
        addButton(new CardButton(getContext())
                .buildButton(new CardButtonModel("Detalii opțiune", new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        User user = VodafoneController.getInstance().getUser();
                        if (user instanceof PrepaidUser){
                            ((ActivityFragmentInterface) getContext()).attachFragment(new OptionsDetailsFragment()
                                    .setArgsOnBundle(OfferRowInterface.class.getCanonicalName(),activeOffer));
                        }else if (user instanceof CBUUser){
                            ((ActivityFragmentInterface) getContext()).attachFragment(new OptionsDetailsFragment()
                                    .setArgsOnBundle(OfferRowInterface.class.getCanonicalName(),activeOfferPostpaid));
                        }else if (user instanceof EbuMigrated){
                            if(promo != null){
                                ((ActivityFragmentInterface) getContext()).attachFragment(new OptionsDetailsFragment()
                                        .setArgsOnBundle(promo.getClass().getCanonicalName(),promo));
                            }else if(billingOffer != null){
                                ((ActivityFragmentInterface) getContext()).attachFragment(new OptionsDetailsFragment()
                                        .setArgsOnBundle(billingOffer.getClass().getCanonicalName(),billingOffer));
                            }
                        }
                    }
                }, null)));
    }

    private void setupListView() {
        activeOffersListView = (LinearLayout) findViewById(R.id.active_options_list_view);
        activeOffersListView.removeAllViews();

        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
            if(activeOffer != null){
                List<BalanceInfo> balanceInfos = activeOffer.getBalances();
                for(BalanceInfo balanceInfo : balanceInfos){
                    activeOffersListView.addView(makePrepaidOfferView(activeOffersListView, balanceInfo));
                }
            }
        }else if(VodafoneController.getInstance().getUser() instanceof CBUUser){
            if (balanceShowAndNotShownList != null) {
                for (BalanceShowAndNotShown balance : balanceShowAndNotShownList) {
                    if(matchIds(activeOfferPostpaid.getOfferId(), balance.getId())
                            && matchIds(activeOfferPostpaid.getOfferInstanceId(), balance.getInstanceId())){
                        activeOffersListView.addView(makePostpaidOfferView(activeOffersListView, balance, activeOfferPostpaid.getEndDate()));
                        viewGroup.getController().removeOfferFromBalanceList(balance);
                        break;
                    }
                }
            }
        }else if(VodafoneController.getInstance().getUser() instanceof EbuMigrated){
            if (balanceShowAndNotShownList != null) {

                if(promo != null && promo.getBoList() != null){
                    for(BillingOffer billingOffer : promo.getBoList()){
                        for (BalanceShowAndNotShown balance : balanceShowAndNotShownList) {
                            if(matchIds(billingOffer.getBoId(), balance.getId())
                                    && matchIds(billingOffer.getBoInstanceId(), balance.getInstanceId())){
                                activeOffersListView.addView(makePostpaidOfferView(activeOffersListView, balance, billingOffer.getDeactivationDate()));
                                viewGroup.getController().removeOfferFromBalanceList(balance);
                                break;
                            }
                        }
                    }
                }else if(billingOffer != null){
                    for (BalanceShowAndNotShown balance : balanceShowAndNotShownList) {
                        if(matchIds(billingOffer.getBoId(), balance.getId())
                                && matchIds(billingOffer.getBoInstanceId(), balance.getInstanceId())){
                            activeOffersListView.addView(makePostpaidOfferView(activeOffersListView, balance, billingOffer.getDeactivationDate()));
                            viewGroup.getController().removeOfferFromBalanceList(balance);
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean matchIds(Long firstId, Long secondId){
        return firstId != null && secondId != null && firstId.equals(secondId);
    }

    private View makePrepaidOfferView(ViewGroup group, BalanceInfo balanceInfo) {
        final View view = ((LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_active_offers, group, false);
        DecoView decoView = (DecoView) view.findViewById(R.id.dynamicArcView);
        VodafoneTextView offerName = (VodafoneTextView) view.findViewById(R.id.offer_name);
        VodafoneTextView offerPrice = (VodafoneTextView) view.findViewById(R.id.offer_price);

        offerName.setText(balanceInfo.getBalanceDescription());

        if(!isExpired(activeOffer.getEndDate())){

            AmountUnitModel amountUnitModel = AmountUnitUtils.getAmountUnitObject(balanceInfo.getBalanceType(), balanceInfo.getBalanceUnit(), balanceInfo.getBalanceRemaining());

            if(balanceInfo.getBalanceUnit() == BalanceInfo.BalanceUnitEnum.kb || balanceInfo.getBalanceUnit() == BalanceInfo.BalanceUnitEnum.mb || balanceInfo.getBalanceUnit() == BalanceInfo.BalanceUnitEnum.gb){
                offerPrice.setText(NumbersUtils.truncateDecimal(toMb(balanceInfo.getBalanceUnit(), balanceInfo.getBalanceRemaining()), 0) + "MB");
            } else if (balanceInfo.getBalanceUnit() == BalanceInfo.BalanceUnitEnum.sec){
                offerPrice.setText(NumbersUtils.truncateDecimal(amountUnitModel.getAmount()!= null?amountUnitModel.getAmount():Double.valueOf(0), 0) + " " + amountUnitModel.getUnit());
            } else {
                offerPrice.setText(NumbersUtils.truncateDecimal(amountUnitModel.getAmount()!= null?amountUnitModel.getAmount():Double.valueOf(0),2) + " " + amountUnitModel.getUnit());
            }

            Float totalBalance = new Float(balanceInfo.getBalanceTotal());
            Float remainingBalance = new Float(balanceInfo.getBalanceRemaining());
            if(remainingBalance > totalBalance){
                remainingBalance = totalBalance;
            }

            ArcView.atachTracksToArcView(decoView, new ArcViewModel(0, totalBalance,
                    remainingBalance, 3f, 4f, Color.parseColor("#ebebeb"), Color.parseColor("#00afca")));
        }

        return view;
    }

    private View makePostpaidOfferView(ViewGroup group, BalanceShowAndNotShown balanceShowAndNotShown, Long endDate) {
        final View view = ((LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_active_offers, group, false);

        //binding views
        DecoView decoView = (DecoView) view.findViewById(R.id.dynamicArcView);
        VodafoneTextView offerName = (VodafoneTextView) view.findViewById(R.id.offer_name);
        VodafoneTextView offerPrice = (VodafoneTextView) view.findViewById(R.id.offer_price);

        //Set text for total amount
        AmountUnitModel amountUnitModel = AmountUnitUtils.getAmountUnitObject(balanceShowAndNotShown, balanceShowAndNotShown.getTotalAmount());
        if(amountUnitModel.getUnit().equals(UNLIMITED)){
            offerName.setText(balanceShowAndNotShown.getNameRO());
        }else if(amountUnitModel.getUnit().equals(GB_DATA_UNIT)){
            offerName.setText(NumbersUtils.truncateDecimal(amountUnitModel.getAmount(), 2) + " " + amountUnitModel.getUnit());
        } else {
            offerName.setText(NumbersUtils.truncateDecimal(amountUnitModel.getAmount(), 0) + " " + amountUnitModel.getUnit());
        }

        //offerName.setText(balanceShowAndNotShown.getNameRO());

        // TODO: 16.01.2018 Please clarify if should be displayed gauge if offer is expired!
       // if(!isExpired(endDate)){
            //Set text for remaining amount
            AmountUnitModel priceAmountUnitModel = AmountUnitUtils.getAmountUnitObject(balanceShowAndNotShown, balanceShowAndNotShown.getRemainingAmount());
            if(amountUnitModel.getUnit().equals(UNLIMITED)){
                offerPrice.setText(amountUnitModel.getUnit());
            }else if(priceAmountUnitModel.getUnit().equals(GB_DATA_UNIT)){
                offerPrice.setText(NumbersUtils.truncateDecimal(priceAmountUnitModel.getAmount(), 2) + " " + priceAmountUnitModel.getUnit());
            } else {
                offerPrice.setText(NumbersUtils.truncateDecimal(priceAmountUnitModel.getAmount(), 0) + " " + priceAmountUnitModel.getUnit());
            }

            Float totalBalance = balanceShowAndNotShown.getTotalAmount()!=null?balanceShowAndNotShown.getTotalAmount().floatValue():null;
            Float remainingBalance = balanceShowAndNotShown.getRemainingAmount()!=null?balanceShowAndNotShown.getRemainingAmount().floatValue():null;
            if(totalBalance!=null && remainingBalance!=null){
                if(remainingBalance > totalBalance){
                    remainingBalance = totalBalance;
                }
                //Generate arc view
                ArcView.atachTracksToArcView(decoView, new ArcViewModel(0, totalBalance,
                        remainingBalance, 3f, 4f, Color.parseColor("#ebebeb"), Color.parseColor("#00afca")));
            }else{
                if(amountUnitModel.getUnit().equals(UNLIMITED)){
                    ArcView.atachTracksToArcView(decoView, new ArcViewModel(0, 1,
                            1, 3f, 4f, Color.parseColor("#ebebeb"), Color.parseColor("#00afca")));
                }
            }
       // }


        return view;
    }

    private Double toMb(BalanceInfo.BalanceUnitEnum amountUnitEnum, Double value){
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


    private boolean isExpired(Long endDate){
        Calendar cSchedStartCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        long gmtTime = cSchedStartCal.getTime().getTime();
        long timezoneAlteredTime = gmtTime + TimeZone.getTimeZone("Europe/Bucharest").getRawOffset();
        if(endDate!=null){
            return  endDate < timezoneAlteredTime;
        }
        return false;
    }

    @Override
    protected int setContent() {
        return R.layout.card_active_options;
    }

    public VodafoneAbstractCard showError(boolean hideContent, String errorMessage, boolean isClicableError) {
        super.showError(hideContent, errorMessage);
        errorView.setMargins(0,16,0,16);
        if(isClicableError){
            setOnErrorClickListner();
        }
        return this;
    }
}

package ro.vodafone.mcare.android.card.pricePlan;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hookedonplay.decoviewlib.DecoView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.amountUnit.AmountUnitModel;
import ro.vodafone.mcare.android.client.model.card.CardButtonModel;
import ro.vodafone.mcare.android.client.model.card.CardHeaderModel;
import ro.vodafone.mcare.android.client.model.costControl.AdditionalCost;
import ro.vodafone.mcare.android.client.model.costControl.AmountTypeIdEnum;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.offers.ActiveOffersPostpaidSuccess;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.ActiveOffersPostpaidEbuSuccess;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.BillingOffer;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.Promo;
import ro.vodafone.mcare.android.client.model.realm.system.ServicesLabels;
import ro.vodafone.mcare.android.ui.activities.yourServices.YourServicesActivity;
import ro.vodafone.mcare.android.ui.fragments.yourServices.YourServicesTariffsFragment;
import ro.vodafone.mcare.android.ui.fragments.yourServices.details.ContractDetailsFragment;
import ro.vodafone.mcare.android.ui.utils.AmountUnitUtils;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.CardButton;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;
import ro.vodafone.mcare.android.ui.views.CardHeader;
import ro.vodafone.mcare.android.ui.views.arcView.ArcView;
import ro.vodafone.mcare.android.ui.views.arcView.ArcViewModel;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

import static ro.vodafone.mcare.android.ui.utils.AmountUnitUtils.GB_DATA_UNIT;
import static ro.vodafone.mcare.android.ui.utils.AmountUnitUtils.KB_DATA_UNIT;
import static ro.vodafone.mcare.android.ui.utils.AmountUnitUtils.MB_DATA_UNIT;

/**
 * Created by Bivol Pavel on 20.03.2017.
 */
public class PricePlanCard extends VodafoneAbstractCard {

    public static String TAG = "PricePlanCard";

    public static final String TARIFFS_BUTTON_TITLE = "Tarife abonament";
    public static final String CONTRACT_DETAILS_BUTTON_TITLE = "Detalii contract";

    PricePlanCardController pricePlanCardController;
    private LinearLayout pricePlanListView;
    private ActiveOfferPostpaid pricePlan;
    private ActiveOffersPostpaidEbuSuccess activeOffersPostpaidEbuSuccess;
    private AdditionalCost additionalCost;
    private Promo promo;
    private List<BalanceShowAndNotShown> pricePlanList;
    private List<BalanceShowAndNotShown> matchedList;

    public PricePlanCard(Context context, PricePlanCardController pricePlanCardController) {
        super(context);
        this.pricePlanCardController = pricePlanCardController;
        init(null);
    }

    public PricePlanCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PricePlanCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setCardPaddingsInPx((int) (getResources().getDimension(R.dimen.default_padding_horizontal)), 0, (int) (getResources().getDimension(R.dimen.default_padding_horizontal)), 0);

        //setupController();
        setOnErrorClickListner();
    }

    private void setOnErrorClickListner() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loadingView != null) {
                }
                if (loadingView == null || !loadingView.isVisible()) {
                    if (errorView != null && errorView.getVisibility() == VISIBLE) {
                        pricePlanCardController.requestData();
                    }
                }
            }
        });
    }

    @Override
    public void reinit(){
        super.reinit();

        setCardPaddingsInPx((int) (getResources().getDimension(R.dimen.default_padding_horizontal)), 0,
                (int) (getResources().getDimension(R.dimen.default_padding_horizontal)), 0);
    }

    public void buildCBUPricePlanCard(ActiveOffersPostpaidSuccess offersPostpaidSuccess, List<BalanceShowAndNotShown> pricePlanList,
                                      AdditionalCost additionalCost) {

        this.pricePlanList = pricePlanList;
        this.pricePlan = offersPostpaidSuccess.getPricePlan();
        this.additionalCost = additionalCost;

        reinit();

        if (pricePlanList != null) {
            setupListView(removeOffersWithOldChunkModel(checkOffersToBeDisplayed(offersPostpaidSuccess, pricePlanList)));
        }

        attachPricePlanHeader();
        atachButton(CONTRACT_DETAILS_BUTTON_TITLE);
        hideLoading();
        hideError();
    }

    public void buildEBUPricePlanCard(ActiveOffersPostpaidEbuSuccess activeOffersPostpaidEbuSuccess,
                                      List<BalanceShowAndNotShown> pricePlanList, AdditionalCost additionalCost) {

        this.activeOffersPostpaidEbuSuccess = activeOffersPostpaidEbuSuccess;
        this.pricePlanList = pricePlanList;
        this.additionalCost = additionalCost;

        hideError();
        reinit();

        if(activeOffersPostpaidEbuSuccess != null){
            this.promo = activeOffersPostpaidEbuSuccess.getPricePlan();

            if (pricePlanList != null && promo != null) {
                setupListView(removeOffersWithOldChunkModel(matchList(activeOffersPostpaidEbuSuccess)));
            }

            if(promo != null){

                if(activeOffersPostpaidEbuSuccess.getTariffsList() != null &&
                        !activeOffersPostpaidEbuSuccess.getTariffsList().isEmpty()){
                    atachButton(TARIFFS_BUTTON_TITLE);
                }
                atachButton(CONTRACT_DETAILS_BUTTON_TITLE);
                attachPromoHeader();
            }

        }else{

            if (pricePlanList != null) {
                setupListView(pricePlanList);
            }
            attachEmptyCardHeader();
        }

        hideLoading();

    }

    private List<BalanceShowAndNotShown> checkOffersToBeDisplayed(ActiveOffersPostpaidSuccess offersPostpaidSuccess, List<BalanceShowAndNotShown> balanceShowAndNotShowns){
            List<BalanceShowAndNotShown> balances = new ArrayList<>();
            List<ActiveOfferPostpaid> activeOfferPostpaids = offersPostpaidSuccess.getActiveOffersList();
            balances.addAll(balanceShowAndNotShowns);

            for(BalanceShowAndNotShown balance : balanceShowAndNotShowns){
                for (ActiveOfferPostpaid activeOfferPostpaid : activeOfferPostpaids){
                    if(matchIds(balance.getId(), activeOfferPostpaid.getOfferId())
                            && matchIds(balance.getInstanceId() ,activeOfferPostpaid.getOfferInstanceId()) ){
                        balances.remove(balance);
                    }
                }
            }
            return balances;
    }

    private List<BalanceShowAndNotShown> matchList(ActiveOffersPostpaidEbuSuccess activeOffersPostpaidEbuSuccess){
        List<BalanceShowAndNotShown> balanceShowAndNotShowns = new ArrayList<>();
        balanceShowAndNotShowns.addAll(pricePlanList);
        List<BillingOffer> totalBillingOffers = new ArrayList<>();

        if(activeOffersPostpaidEbuSuccess.getActiveOffers() != null){
            if(activeOffersPostpaidEbuSuccess.getActiveOffers().getPromoList() != null){
                for(Promo promo : activeOffersPostpaidEbuSuccess.getActiveOffers().getPromoList()){
                    if(promo.getBoList() != null)
                         totalBillingOffers.addAll(promo.getBoList());
                }
            }
            if(activeOffersPostpaidEbuSuccess.getActiveOffers().getBoList() != null)
                totalBillingOffers.addAll(activeOffersPostpaidEbuSuccess.getActiveOffers().getBoList());
        }
        for(BillingOffer billingOffer : totalBillingOffers){
            for (BalanceShowAndNotShown balance : pricePlanList){
                if(matchIds(balance.getId(), billingOffer.getBoId()) && matchIds(balance.getInstanceId(), billingOffer.getBoInstanceId())){
                    balanceShowAndNotShowns.remove(balance);
                }
            }
        }
        return balanceShowAndNotShowns;
    }

    private boolean matchIds(Long firstId, Long secondId){
        return firstId != null && secondId != null && firstId.equals(secondId);
    }

    private List<BalanceShowAndNotShown> removeOffersWithOldChunkModel(List<BalanceShowAndNotShown> balanceShowAndNotShowns){

        Iterator<BalanceShowAndNotShown> i = balanceShowAndNotShowns.iterator();
        while (i.hasNext()) {
            BalanceShowAndNotShown balanceShowAndNotShown = i.next();
            if(isChunkOffer(balanceShowAndNotShown) && !isNewChunkModel(balanceShowAndNotShown)){
                i.remove();
            }
        }

        return balanceShowAndNotShowns;
    }

    private void attachPricePlanHeader() {

        CardHeader cardHeader = new CardHeader(getContext())
                .buildHeader(new CardHeaderModel(null, pricePlan.getOfferName(),
                        makeOfferPriceBoldText(NumbersUtils
                                .truncateDecimal(pricePlan.getOfferPrice(),2) + " € pe lună (TVA inclus)"),
                        pricePlan.getOfferCategory(), null, null));
        addHeader(cardHeader);
    }

    private void attachPromoHeader(){

        SpannableString spannablePromoPrice = null;

        if(promo.getPromoPrice() != null){
            spannablePromoPrice = makeOfferPriceBoldText(NumbersUtils
                    .truncateDecimal(promo.getPromoPrice(),2) + " € pe lună");
        }

        CardHeader cardHeader = new CardHeader(getContext())
                .buildHeader(new CardHeaderModel(null, promo.getPromoName(), spannablePromoPrice,
                        null, null, null));
        addHeader(cardHeader);
    }

    private void attachEmptyCardHeader(){

        CardHeader cardHeader = new CardHeader(getContext())
                .buildHeader(new CardHeaderModel(null, "", null,
                        null, null, null))
                .setHeaderTitleVisibility(View.VISIBLE);
        addHeader(cardHeader);
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

    private void atachButton(final String text) {
        final CardButton cardButton = new CardButton(getContext())
                .buildButton(new CardButtonModel(text, new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(pricePlan != null){
                            ((YourServicesActivity) getContext())
                                    .attachFragment(new ContractDetailsFragment().setArgsOnBundle(
                                            ActiveOfferPostpaid.class.getCanonicalName(), pricePlan));
                        }else if(promo != null){
                            switch (text){
                                case TARIFFS_BUTTON_TITLE:
                                    ((YourServicesActivity) getContext())
                                            .attachFragment(new YourServicesTariffsFragment()
                                                    .setArgsOnBundle(ActiveOffersPostpaidEbuSuccess.class.getCanonicalName(),
                                                    activeOffersPostpaidEbuSuccess));
                                    break;
                                case CONTRACT_DETAILS_BUTTON_TITLE:
                                    ((YourServicesActivity) getContext())
                                            .attachFragment(new ContractDetailsFragment()
                                                    .setArgsOnBundle(Promo.class.getCanonicalName(),
                                                            promo));
                                    break;
                            }
                        }
                    }
                }, null));
        addButton(cardButton);
    }

    private void setupListView(List<BalanceShowAndNotShown> balanceShowAndNotShowns) {
        Log.d(TAG, "setupListView");
        try {
            pricePlanListView = (LinearLayout) findViewById(R.id.active_offers_list_view);
            if(pricePlanListView==null){
                return;
            }
            pricePlanListView.removeAllViews();

            if(balanceShowAndNotShowns != null && !balanceShowAndNotShowns.isEmpty()){
                for (BalanceShowAndNotShown balanceShowAndNotShown : balanceShowAndNotShowns) {
                    pricePlanListView.addView(makePostpaidOfferView(pricePlanListView, balanceShowAndNotShown));
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private View makePostpaidOfferView(ViewGroup group, BalanceShowAndNotShown balanceShowAndNotShown) {
        final View view = ((LayoutInflater) this.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.list_item_active_offers, group, false);

        AmountUnitModel amountUnitModel = null;
        DecoView decoView = view.findViewById(R.id.dynamicArcView);
        VodafoneTextView offerName = view.findViewById(R.id.offer_name);
        VodafoneTextView offerPrice = view.findViewById(R.id.offer_price);
        VodafoneTextView benefitDescription = view.findViewById(R.id.benefit_description);
        VodafoneTextView offerSubName = view.findViewById(R.id.offer_sub_name);

        if(balanceShowAndNotShown.getRemainingAmount() != null){
            amountUnitModel = AmountUnitUtils.getAmountUnitObject(balanceShowAndNotShown,
                    balanceShowAndNotShown.getRemainingAmount());
        }else if (isFUPOffer(balanceShowAndNotShown)){
            amountUnitModel = AmountUnitUtils.getAmountUnitObject(balanceShowAndNotShown,
                    balanceShowAndNotShown.getRemainingAmountFUP());
        }

        if(isFUPOffer(balanceShowAndNotShown)){
            offerName.setText(balanceShowAndNotShown.getNameRO());
            benefitDescription.setText(ServicesLabels.getService_fup_informational_message());
            benefitDescription.setVisibility(VISIBLE);
        }else {
            if(balanceShowAndNotShown.getAmountTypeId() != null
                    && balanceShowAndNotShown.getAmountTypeId().equals(AmountTypeIdEnum.chunk)){
                offerName.setText(String.format(ServicesLabels.getService_chunk_offer_name(),balanceShowAndNotShown.getNameRO()));
                if(balanceShowAndNotShown.getGroupSize() > 1) {
                    offerSubName.setText(String.format(ServicesLabels.getService_chunk_offer_description(), balanceShowAndNotShown.getGroupSize()));
                } else {
                    offerSubName.setText(String.format(ServicesLabels.getServiceSingleChunkOfferDescription(), balanceShowAndNotShown.getGroupSize()));
                }
                offerSubName.setVisibility(VISIBLE);
            } else {
                offerName.setText(balanceShowAndNotShown.getNameRO());
            }
        }

        if(amountUnitModel == null){
            offerPrice.setText(ServicesLabels.getServices_unlimited_label());
            ArcView.atachTracksToArcView(decoView, new ArcViewModel(0, 1,
                    1, 3f, 4f, Color.parseColor("#ebebeb"),
                    Color.parseColor("#00afca")));
        }else{
            if(balanceShowAndNotShown.getAmountTypeId() != null && balanceShowAndNotShown.getAmountTypeId().equals(AmountTypeIdEnum.data) ||
                    balanceShowAndNotShown.getAmountTypeId().equals(AmountTypeIdEnum.chunk)){
                if(amountUnitModel.getUnit().equals(GB_DATA_UNIT)){
                    offerPrice.setText(NumbersUtils.truncateDecimal(amountUnitModel.getAmount(),2) + " " + amountUnitModel.getUnit());
                } else {
                    offerPrice.setText(NumbersUtils.truncateDecimal(toMb(amountUnitModel.getUnit(),
                            Double.valueOf(amountUnitModel.getAmount())),0) + " " + MB_DATA_UNIT);
                }
            }else{
                offerPrice.setText(NumbersUtils.truncateDecimal(amountUnitModel.getAmount(), 0) + " " + amountUnitModel.getUnit());
            }

            if(isFUPOffer(balanceShowAndNotShown)){
                ArcView.atachTracksToArcView(decoView, new ArcViewModel(0, new Float(balanceShowAndNotShown.getTotalAmountFUP()),
                        new Float(balanceShowAndNotShown.getRemainingAmountFUP()), 3f, 4f,
                        Color.parseColor("#ebebeb"), Color.parseColor("#00afca")));
            }else{
                if(balanceShowAndNotShown.getTotalAmount()!=null && balanceShowAndNotShown.getRemainingAmount()!=null){
                    ArcView.atachTracksToArcView(decoView, new ArcViewModel(0, balanceShowAndNotShown.getTotalAmount().floatValue(),
                            balanceShowAndNotShown.getRemainingAmount().floatValue(), 3f, 4f,
                            Color.parseColor("#ebebeb"), Color.parseColor("#00afca")));
                }
            }
        }

        return view;
    }

    //If totalAmountFUP is not null then this is FUP offer
    private boolean isFUPOffer(BalanceShowAndNotShown balanceShowAndNotShown){
        return balanceShowAndNotShown.getTotalAmountFUP() != null;
    }

    private boolean isChunkOffer(BalanceShowAndNotShown balanceShowAndNotShown){
        return balanceShowAndNotShown.getAmountTypeId() != null
                && balanceShowAndNotShown.getAmountTypeId().equals(AmountTypeIdEnum.chunk);
    }

    private boolean isNewChunkModel(BalanceShowAndNotShown balanceShowAndNotShown){
        return balanceShowAndNotShown.getChunkmodel() != null
                && (balanceShowAndNotShown.getChunkmodel() == 0 || balanceShowAndNotShown.getChunkmodel() == 1);
    }

    @Override
    protected int setContent() {
        return R.layout.card_price_plan;
    }

    @Override
    public VodafoneAbstractCard showError(boolean hideContent, String message) {
        super.showError(hideContent, message);
        errorView.setMargins(0, 16, 0, 16);
        return this;
    }

    public void showErrorViewInsideCard() {
        errorView = new CardErrorLayout(getContext(),
                "Ne pare rău nu am reuşit să încărcăm toate detaliile, apasă aici pentru a reîncerca.")
                .setMargins(0, 16, 0, 16);
        addButton(errorView);
        setOnErrorClickListner();
    }

    private Double toMb(String amountUnitEnum, Double value){
        Double result = Double.valueOf(0);

        if(value != null){
            switch (amountUnitEnum)
            {
                case KB_DATA_UNIT:
                    result = value/1024.0;
                    break;
                case MB_DATA_UNIT:
                    result = value;
                    break;
                case GB_DATA_UNIT:
                    result = value*1024.0;
                    break;
            }
        }
        return result;
    }
}

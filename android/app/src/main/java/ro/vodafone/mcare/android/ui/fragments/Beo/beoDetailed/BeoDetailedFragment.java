package ro.vodafone.mcare.android.ui.fragments.Beo.beoDetailed;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.card.offers.ExpandableWebViewCard;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleCategoriesPost;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleOffersPostSuccess;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleCategories;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleOffersSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.PrepaidOfferRow;
import ro.vodafone.mcare.android.client.model.identity.CustomerRestrictionsSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.system.BEOLabels;
import ro.vodafone.mcare.android.client.model.realm.system.OffersLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.offers.BeoActivationEbuActivity;
import ro.vodafone.mcare.android.ui.activities.offers.BeoActivationPostPaidActivity;
import ro.vodafone.mcare.android.ui.activities.offers.BeoActivationPrepaidActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.fragments.Beo.BaseBeoFragment;
import ro.vodafone.mcare.android.ui.fragments.Beo.BeoFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.SmallErrorView;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.StringUtils;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Alex on 3/10/2017.
 */

public class BeoDetailedFragment extends BaseBeoFragment{

    public static final String TAG = "BeoDetailedFragment ";
    public static String IS_SERVICE_KEY = "isServiceKey";
    public static String IS_OFFERS_IN_PENDING = "isOffersInPending";
    public static String ROAMING_CATEGORY = "Roaming";
    public static String INTERNATIONAL_CATEGORY = "International";

    private View roorView;

    public OfferRowInterface offerRow;

    private boolean isOffersInPending;
    private boolean isServices;
    private boolean isActiveButton;
    private String screen_name =TealiumConstants.bonusDetailsScreen;
    private long lastClickTime = 0;

    @BindView(R.id.wrong_container) RelativeLayout wrongContainer;
    @BindView(R.id.content_description_container) RelativeLayout contentDescriptionContainer;
    @BindView(R.id.offer_description_card) VodafoneGenericCard offerDescriptionCard;

    @BindView(R.id.content_text) VodafoneTextView contentText;
    @BindView(R.id.duration_label) VodafoneTextView durationLabel;
    @BindView(R.id.activation_date) VodafoneTextView activationDate;
    @BindView(R.id.deactivation_date) VodafoneTextView deactivationDate;

    @BindView(R.id.beo_activate_offer_container) VodafoneButton beoActivateOfferContainer;

    public static BeoDetailedFragment createFragment(OfferRowInterface incomingOfferRow, boolean isOfferInPending, boolean isFromServicesPage) {
        BeoDetailedFragment fragment = new BeoDetailedFragment();
        fragment.setOfferRow(incomingOfferRow);
        fragment.setIsOffersInPending(isOfferInPending);
        fragment.setIsServices(isFromServicesPage);
        return fragment;
    }

    public  void setOfferRow(OfferRowInterface offerRow) {
        this.offerRow = offerRow;
    }

    public boolean isServices() {
        return isServices;
    }

    public void setIsServices(boolean isServices) {
        this.isServices = isServices;
    }

    public void setIsOffersInPending(boolean isOffersInPending) {
        this.isOffersInPending = isOffersInPending;
    }

    @Override
    public String getTitle() {
        return "Bonusuri şi Opţiuni";
    }

    @Override
    public void onEligibleOffersPrepaidRequestSucces(EligibleOffersSuccess activeOffersSuccess) {
        setupOfferFromBundle();
        if(offerRow != null){
            setupContent(offerRow);
        }else{
            displayErrorMessage(BEOLabels.getBeoOffersNotAvailableMessage(), false, false);
        }
    }

    @Override
    public void onEligibleOffersPrepaidRequestFailed() {
        displayErrorMessage(BEOLabels.getGeneral_error_mesage_tap_to_refresh(), true, false);
    }

    //This call is made for extraoptions (According to diagam we do not make this call)
    @Override
    public void onActiveOffersPrepaidRequestSucces(ActiveOffersSuccess activeOffersSuccess) {}

    @Override
    public void onActiveOffersPrepaidRequestFailed() {}

    @Override
    public void onEligibleOffersCBURequestSucces(EligibleOffersPostSuccess eligibleOffersPostSuccess) {
        setupOfferFromBundle();
        if(offerRow != null){
            getPendingOffers4PostPaid(UserSelectedMsisdnBanController.getInstance().getSubscriberSid(),"offer");
        }else{
            displayErrorMessage(BEOLabels.getBeoOffersNotAvailableMessage(), false, false);
        }
    }

    @Override
    public void onEligibleOffersCBURequestFailed() {
        displayErrorMessage(BEOLabels.getGeneral_error_mesage_tap_to_refresh(), true, false);
    }

    @Override
    public void onPendingOffersCBURequestSucces(GeneralResponse generalResponse) {
        if(generalResponse.getTransactionStatus() != 0){
            isOffersInPending = true;
        }
        setupOfferFromBundle();
        setupContent(offerRow);
    }

    @Override
    public void onPendingOffersCBURequestFailed() {
        displayErrorMessage(BEOLabels.getGeneral_error_mesage_tap_to_refresh(), true, false);
    }

    @Override
    public void onEligibleOffersEBURequestSucces(EligibleOffersPostSuccess eligibleOffersPostSuccess) {
        setupOfferFromBundle();
        if(offerRow != null){
            if(EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null){
                getPendingOffers4EBU(EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole(),
                        UserSelectedMsisdnBanController.getInstance().getSubscriberSid());
            }
        }else{
            if(isServices){
                displayErrorMessage(BEOLabels.getNot_eligibile_for_service_activation_message(), false, false);
            }else{
                displayErrorMessage(BEOLabels.getNot_eligibile_for_offer_activation_message(), false, false);
            }
        }
    }

    @Override
    public void onEligibleOffersEBURequestFailed() {
        displayErrorMessage(BEOLabels.getGeneral_error_mesage_tap_to_refresh(), true, false);
    }

    @Override
    public void onPendingOffersEBURequestSucces(GeneralResponse generalResponse) {
        if(generalResponse.getTransactionStatus() != 0){
            isOffersInPending = true;
        }
        setupContent(offerRow);
    }

    @Override
    public void onPendingOffersEBURequestFailed() {
        displayErrorMessage(BEOLabels.getGeneral_error_mesage_tap_to_refresh(), true, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        stopLoadingDialog();
        ((OffersActivity)getActivity()).setTitle(getTitle());

        clearHeaderContainer();
        if (offerExists() && isNeededOfferRow()){
            //setup current offer
            setupContent(offerRow);
        } else {
            //Get offers from api and then search needed offer with offerId from bundle.
            requestOffers();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        roorView = inflater.inflate(R.layout.fragment_beo_offer_details, null);
        ButterKnife.bind(this,roorView);



        trackView();

        return roorView;
    }

    private boolean offerExists(){
        return offerRow!=null;
    }

    /*
    *Need to make this check because app can be running in background on this page with another offer setet.
    */
    private boolean isNeededOfferRow(){
        boolean result = true;
        Bundle bundle = getArguments();

        if(bundle != null){
            String id = bundle.getString(OfferRowInterface.KEY_ID);
            if(!String.valueOf(offerRow.getOfferId()).equals(id)){
                result = false;
            }
        }
        return result;
    }

    private boolean existDataInDB(){
        boolean existDataInDb = false;
        User user = VodafoneController.getInstance().getUser();

        if(user instanceof PrepaidUser){
            existDataInDb = RealmManager.getRealmObject(EligibleOffersSuccess.class) != null;
        }else if (user instanceof PostPaidUser){
            existDataInDb = RealmManager.getRealmObject(EligibleOffersPostSuccess.class) != null;
        }
        return existDataInDb;
    }

    public void requestOffers() {

        hideOfferDescriptionCard();

        User user = VodafoneController.getInstance().getUser();
        if(user instanceof PrepaidUser){
            getEligibleOffers4PrePaid();
        }else if(user instanceof CBUUser){
            getEligibleOffers4PostPaid(UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn(),
                    UserSelectedMsisdnBanController.getInstance().getSubscriberSid(),
                    getBillCycleDate());
        }else if(user instanceof EbuMigrated){
            getEligibleOffer4EBUIfAllowed();
        }
    }

    private void getEligibleOffer4EBUIfAllowed(){
        if(isAccesRestricted()){
            displayErrorMessage(BEOLabels.getCustomer_restriction_message(), false, false);
        }else if (isUserNotAllowedToActiveEO()){
            displayErrorMessage(BEOLabels.getContact_account_specialist_message(), false, false);
        }else{
            if(EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null){
                getEligibleOffers4EBU(EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole(),
                        UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn());
            }
        }
    }

    private boolean isAccesRestricted(){
        CustomerRestrictionsSuccess customerRestrictionsSuccess = (CustomerRestrictionsSuccess)
                RealmManager.getRealmObject(CustomerRestrictionsSuccess.class);

        return customerRestrictionsSuccess != null && (customerRestrictionsSuccess.getIsBlacklistForever()
                || customerRestrictionsSuccess.getIsCollectionRestricted() || customerRestrictionsSuccess.getIsDeviceBlacklist()
                || customerRestrictionsSuccess.getIsServiceBadDebt());
    }

    private boolean isUserNotAllowedToActiveEO(){
        return  EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null
                && EbuMigratedIdentityController.getInstance().getSelectedIdentity().getTreatmentSegment() != null
                && AppConfiguration.getEbuMigratedIneligibleToDeleteOfferSegment()
                .contains(EbuMigratedIdentityController.getInstance().getSelectedIdentity().getTreatmentSegment());
    }

    public void setupContent(OfferRowInterface offerRow) {

        if(offerRow == null || getActivity() == null) {
            return;
        }

        wrongContainer.setVisibility(View.GONE);

        BonusDetailsTrackingEvent event = new BonusDetailsTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        setupLabels();
        setupHeader();
        displayOfferDescriptionCard();
        initExpandableDescriptionCard();

        beoActivateOfferContainer.setOnClickListener(activateOffer);
        contentDescriptionContainer.setVisibility(View.VISIBLE);

        if (VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
            activateFilterButton();
        } else {
            if(VodafoneController.getInstance().getUser() instanceof EbuMigrated){
                durationLabel.setVisibility(View.VISIBLE);
                activationDate.setVisibility(View.VISIBLE);
                deactivationDate.setVisibility(View.VISIBLE);
                activationDate.setText(getActivationDate(((PostpaidOfferRow)offerRow).getActivationDate(),
                        ((PostpaidOfferRow)offerRow).getActivateInMonths()));
                deactivationDate.setText(getDeactivationDate(((PostpaidOfferRow)offerRow).getDeactivationDate(),
                        ((PostpaidOfferRow)offerRow).getDeactivateAfterMonths()));
            }

            if (isOffersInPending) {
                displayErrorMessage(BEOLabels.getBeo_another_offers_and_services_active(), false, true);
                inactivateFilterButton();
            } else {
                activateFilterButton();
            }
        }
        contentText.setText(offerRow.getOfferShortDescription());
    }

    private void setupLabels() {
        beoActivateOfferContainer.setText(BEOLabels.getActivate_offer_text());

        if(VodafoneController.getInstance().getUser() instanceof EbuMigrated){
            durationLabel.setText(BEOLabels.getDuration_label());
        }
    }

    private void clearHeaderContainer() {
        if(getActivity() == null)
            return;

        ((OffersActivity)getActivity()).getNavigationHeader().removeViewFromContainer();
    }

    public void activateFilterButton() {
        isActiveButton = true;
        beoActivateOfferContainer.setEnabled(true);
    }

    public void inactivateFilterButton() {
        isActiveButton = false;
        beoActivateOfferContainer.setEnabled(false);
    }

    public void showActivateBtnAndOfferDescriptionTv(boolean visible) {
        beoActivateOfferContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
        contentText.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 232 || (data == null))
            return;

        if (requestCode == 42 && resultCode == 43)
            getActivity().onBackPressed();
    }

    public void initExpandableDescriptionCard() {
        if (isRoamingOrInternationalCategory(offerRow)) {
            if ((isNotEmptyOfferDescription(offerRow))) {

                ExpandableWebViewCard expandableWebViewCard = (ExpandableWebViewCard) roorView.findViewById(R.id.expandable_description);
                VodafoneTextView expandableCardTitleView = (VodafoneTextView) expandableWebViewCard.findViewById(R.id.card_title_tv);
                expandableCardTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                expandableWebViewCard.setVisibility(View.VISIBLE);
                expandableWebViewCard.setTitle("Informații tarife");
                expandableWebViewCard.hideLine();
                String htmlContent;

                if (offerRow instanceof PrepaidOfferRow) {
                    htmlContent = ((PrepaidOfferRow) offerRow).getOfferDetailedDescription();
                } else {
                    htmlContent = ((PostpaidOfferRow) offerRow).getOfferDetailedDescription();
                }

                expandableWebViewCard.setWebViewUrl(htmlContent);
                expandableWebViewCard.setImageArrowDirectionDown();
                expandableWebViewCard.build();

                expandableWebViewCard.setCardPaddingsInDp(9, 16, 9, 16);
            }
        }
    }

    private boolean isRoamingOrInternationalCategory(OfferRowInterface offerRow){
        return offerRow.getOfferCategory().equals(ROAMING_CATEGORY) || offerRow.getOfferCategory().equals(INTERNATIONAL_CATEGORY);
    }

    private boolean isNotEmptyOfferDescription(OfferRowInterface offerRow){
        return (offerRow instanceof PrepaidOfferRow && ((PrepaidOfferRow) offerRow).getOfferDetailedDescription() != null && !((PrepaidOfferRow) offerRow).getOfferDetailedDescription().isEmpty()) ||
                (offerRow instanceof PostpaidOfferRow && ((PostpaidOfferRow) offerRow).getOfferDetailedDescription() != null && !((PostpaidOfferRow) offerRow).getOfferDetailedDescription().isEmpty());
    }

    public void setupHeader() {
        NavigationHeader navigationHeader = ((OffersActivity) getActivity()).getNavigationHeader();

        try {
            ((OffersActivity) getActivity()).getToolbar().showToolBar();
        } catch (Exception e) {
            e.printStackTrace();
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.beo_details_header, null);

        if (offerRow != null) {
            TextView headerTittleText = (TextView) view.findViewById(R.id.header_tittle_text);
            headerTittleText.setText(offerRow.getOfferName());
        }

        TextView priceTitte = (TextView) view.findViewById(R.id.price_titte);
        String offerPrice = "";

        if (offerRow != null && offerRow.getOfferPrice() != null) {
            if (VodafoneController.getInstance().getUser() instanceof PostPaidUser) {
                offerPrice = String.format(BEOLabels.getEuro_amount_currency(), NumbersUtils.twoDigitsAfterDecimal(offerRow.getOfferPrice()));
            } else {
                offerPrice = String.format(BEOLabels.getEuro_price_currency(), NumbersUtils.twoDigitsAfterDecimal(offerRow.getOfferPrice()));
            }
        }
        offerPrice = offerPrice.replaceAll(",", ".");

        priceTitte.setText(StringUtils.concatenateStrings(BEOLabels.getBeo_price_label(), " ", offerPrice));

        navigationHeader.addViewToContainer(view);
        navigationHeader.showBannerView();
        navigationHeader.setTitle(BEOLabels.getBeo_page_tittle());
        if (isServices) {
            navigationHeader.setTitle(OffersLabels.getOffers_for_you_services_button_label());
        }

        navigationHeader.hideSelectorView();
    }

    public void displayErrorMessage(String text, boolean refresh, boolean displayOfferDescription) {
        Log.d(TAG, "displayErrorMessage()");
        if(getContext() == null) {
            return;
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);

        wrongContainer.removeAllViews();
        wrongContainer.setVisibility(View.VISIBLE);

        SmallErrorView smallErrorView = new SmallErrorView(getContext(), text, new BeoFragment(), refresh);
        smallErrorView.addSmallErrorView(wrongContainer, layoutParams);

        if (refresh) {
            smallErrorView.setErrorViewHandler((new SmallErrorView.ErrorEventHandler() {
                @Override
                public void onErrorPress() {
                    requestOffers();
                }
            }));
        } else {
            smallErrorView.setErrorViewHandler(null);
        }

        displayOfferDescriptionCard();

        if(displayOfferDescription){
            contentDescriptionContainer.setVisibility(View.VISIBLE);
        }else{
            contentDescriptionContainer.setVisibility(View.GONE);
        }
    }

    private void displayOfferDescriptionCard(){
        offerDescriptionCard.setVisibility(View.VISIBLE);
    }

    private void hideOfferDescriptionCard(){
        offerDescriptionCard.setVisibility(View.GONE);
    }

    private void setupOfferFromBundle(){
        Bundle bundle = getArguments();
        if(bundle != null){
            getOfferFromDB(bundle.getString(OfferRowInterface.KEY_ID));
        }
    }

    public void getOfferFromDB(String offerId) {
        offerRow = getOfferRowByOfferIdOrMatrix(offerId); //offerRow = postpaidUserHaveMatrixId?getOfferRowByOfferIdOrMatrix(id):getOfferRowByOfferId(id);
        isServices = isServiceOffer(offerRow);
    }

    private OfferRowInterface getOfferRowByOfferIdOrMatrix(String offerId) {
        OfferRowInterface offerRowInterface = null;
        if (VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
            offerRowInterface = (PrepaidOfferRow) RealmManager.getRealmObjectAfterLongField(PrepaidOfferRow.class, PrepaidOfferRow.OFFER_ID, Long.valueOf(offerId));
        } else if (VodafoneController.getInstance().getUser() instanceof CBUUser) {
            offerRowInterface = (PostpaidOfferRow) RealmManager.getRealmObjectAfterStringField(PostpaidOfferRow.class, PostpaidOfferRow.MATRIX_ID, offerId);
        } else if(VodafoneController.getInstance().getUser() instanceof EbuMigrated){
            offerRowInterface = (PostpaidOfferRow) RealmManager.getRealmObjectAfterLongField(PostpaidOfferRow.class, PostpaidOfferRow.OFFER_ID, Long.valueOf(offerId));
        }
        return offerRowInterface;
    }

    private boolean isEligiblPostpaideOffer(OfferRowInterface offerRow , boolean isServices){
        boolean isEligible = false;
        EligibleOffersPostSuccess eligibleOffersPostSuccess =
                (EligibleOffersPostSuccess) RealmManager.getRealmObject(EligibleOffersPostSuccess.class);

        if(eligibleOffersPostSuccess != null){
            List<EligibleCategoriesPost> categoriesList = isServices
                    ? eligibleOffersPostSuccess.getEligibleServicesCategories()
                    : eligibleOffersPostSuccess.getEligibleOptionsCategories();

            for(EligibleCategoriesPost category : categoriesList){
                if(category.getEligibleOffersList() != null && category.getEligibleOffersList().contains(offerRow)){
                    return true;
                }
            }
        }
        return isEligible;
    }

    private boolean isServiceOffer(OfferRowInterface offerRow){
        boolean isOfferFromService = false;
        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
            EligibleOffersSuccess eligibleOffersSuccess = (EligibleOffersSuccess) RealmManager.getRealmObject(EligibleOffersSuccess.class);
            if (eligibleOffersSuccess != null && eligibleOffersSuccess.getEligibleServicesCategories() != null
                    && !eligibleOffersSuccess.getEligibleServicesCategories().isEmpty()){
                for (EligibleCategories eligibleCategories : eligibleOffersSuccess.getEligibleServicesCategories()){
                    if(eligibleCategories.getEligibleOffersList().contains(offerRow)){
                        isOfferFromService = true;
                        break;
                    }
                }
            }
        }else{
            EligibleOffersPostSuccess eligibleOffersPostSuccess = (EligibleOffersPostSuccess) RealmManager.getRealmObject(EligibleOffersPostSuccess.class);
            if(eligibleOffersPostSuccess != null && eligibleOffersPostSuccess.getEligibleServicesCategories() != null
                    && !eligibleOffersPostSuccess.getEligibleServicesCategories().isEmpty()){
                for(EligibleCategoriesPost eligibleCategoriesPost : eligibleOffersPostSuccess.getEligibleServicesCategories()){
                    if(eligibleCategoriesPost.getEligibleOffersList().contains(offerRow)){
                        isOfferFromService = true;
                        break;
                    }
                }
            }
        }
        return isOfferFromService;
    }

    View.OnClickListener activateOffer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                return;
            }
            lastClickTime = SystemClock.elapsedRealtime();

            showLoadingDialog();
            trackOfferActivationEvent();

            if (isActiveButton) {
                User user = VodafoneController.getInstance().getUser();
                if (user instanceof PrepaidUser) {
                    startActivityForResult(getBeoActivationIntentForPrepaid(), 42);
                } else if(user instanceof CBUUser){
                    startActivity(getBeoActivationIntentForPostpaid());
                } else if (user instanceof EbuMigrated){
                    startActivity(getBeoActivationIntentForEbu());
                }
            }

            //stopLoadingDialog();
        }
    };

    private Intent getBeoActivationIntentForPrepaid(){
        Intent intent = new Intent(getContext(), BeoActivationPrepaidActivity.class);
        intent.putExtra(IS_SERVICE_KEY, isServices);
        intent.putExtra(IS_OFFERS_IN_PENDING, isOffersInPending);
        intent.putExtra(OfferRowInterface.KEY_ID, offerRow.getOfferId());
        return intent;
    }

    private Intent getBeoActivationIntentForPostpaid(){
        Intent intent = new Intent(getContext(), BeoActivationPostPaidActivity.class);
        intent.putExtra(IS_SERVICE_KEY, isServices);
        intent.putExtra(IS_OFFERS_IN_PENDING, isOffersInPending);
        intent.putExtra(OfferRowInterface.KEY_ID, ((PostpaidOfferRow) offerRow).getMatrixId());
        return intent;
    }

    private Intent getBeoActivationIntentForEbu(){
        Intent intent = new Intent(getContext(), BeoActivationEbuActivity.class);
        intent.putExtra(IS_SERVICE_KEY, isServices);
        intent.putExtra(IS_OFFERS_IN_PENDING, isOffersInPending);
        intent.putExtra(OfferRowInterface.KEY_ID, offerRow.getOfferId());
        return intent;
    }

    public String getActivationDate(Long date, Integer activationInMonth){
        String result;

        if(date == null && activationInMonth == null){
            result = String.format(BEOLabels.getActivation_date_label(), BEOLabels.getImmediate_label());
        } else if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", new Locale("RO", "RO"));
            String formatedDate = DateUtils.getDate(date, sdf);
            result = String.format(BEOLabels.getActivation_date_label(), formatedDate);
        } else {
            result = String.format(BEOLabels.getActivation_date_label(),
                    String.format(BEOLabels.getDuration_in_month_label(),activationInMonth));
        }
        return result;
    }

    public String getDeactivationDate(Long date, Integer deactivationAfterMonth){
        String result;

        if(date == null && deactivationAfterMonth == null){
            result = String.format(BEOLabels.getDeactivation_date_label(), BEOLabels.getRequested_date_label());
        } else if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", new Locale("RO", "RO"));
            String formatedDate = DateUtils.getDate(date, sdf);
            result = String.format(BEOLabels.getDeactivation_date_label(), formatedDate);
        } else {
            result = String.format(BEOLabels.getDeactivation_date_label(),
                    String.format(BEOLabels.getDuration_in_month_label(),deactivationAfterMonth));
        }
        return result;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(offerRow != null) {
            callForAdobeTarget(AdobePageNamesConstants.PG_BONUSS_OR_SERVICE_DETAILS + offerRow.getOfferName());
        }
    }

    public class BonusDetailsTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);


            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            /*s.pageName = s.prop21 + "bonus or service details:" + offerRow.getOfferName();*/
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "bonus or service details:" + offerRow.getOfferName());


            s.prop5 = "sales:buy services";
            s.getContextData().put("prop5", s.prop5);
            s.channel = "bonuses and options";
            s.getContextData().put("&&channel", s.channel);
            s.prop21 = "mcare:" + "bonus or service details";
            s.getContextData().put("prop21", s.prop21);
        }
    }

    /*
    Tealium Track view
    */
    private void trackView(){
        if(isServices){
            screen_name = TealiumConstants.serviceDetailsScreen;
        }
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name, screen_name);
        tealiumMapView.put(TealiumConstants.journey_name,TealiumConstants.bonusesOptionsJourney);
        tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);
    }

    /*
    Tealium Track Event
    */
    private void trackOfferActivationEvent(){
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put(TealiumConstants.screen_name,screen_name);
        if(isServices){
            tealiumMapEvent.put(TealiumConstants.event_name,TealiumConstants.activateServiceButton);
        }else{
            tealiumMapEvent.put(TealiumConstants.event_name,TealiumConstants.activateOfferButton);
        }
        tealiumMapEvent.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);
    }
}

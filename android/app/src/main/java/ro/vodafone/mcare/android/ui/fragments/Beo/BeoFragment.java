package ro.vodafone.mcare.android.ui.fragments.Beo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleCategoriesPost;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleOffersPostSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffersSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleCategories;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleOffersSuccess;
import ro.vodafone.mcare.android.client.model.identity.CustomerRestrictionsSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.system.BEOLabels;
import ro.vodafone.mcare.android.client.model.realm.system.OffersLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.fragments.Beo.adapter.BeoExtraOfferAdapter;
import ro.vodafone.mcare.android.ui.fragments.Beo.adapter.BeoOfferAdapter;
import ro.vodafone.mcare.android.ui.fragments.Beo.beoDetailed.BeoDetailedFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.AdapterBackedLinearLayout;
import ro.vodafone.mcare.android.ui.views.ExpandableAdapterBackedLinearLayout;
import ro.vodafone.mcare.android.ui.views.SmallErrorView;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;


import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;


/**
 * Created by Alex on 3/9/2017.
 */

public class BeoFragment extends BaseBeoFragment {

    public static final String TAG = "BeoFragmentBeoFragment";
    private boolean isOffersInPending = false;
    private boolean displayError = false;

    private View v;
    private NavigationHeader navigationHeader;
    private OffersService offersService;

    @BindView(R.id.beo_list)  ExpandableAdapterBackedLinearLayout beoList;
    @BindView(R.id.wrong_container) LinearLayout wrongContainer;
    @BindView(R.id.extra_offer_list) LinearLayout extraOfferContainer;
    @BindView(R.id.extra_beo_list_view) AdapterBackedLinearLayout extraBeoListView;

    ExpandableListView.OnGroupClickListener onGroupClickListener = new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            beoList.expandGroup(groupPosition);
            return true;
        }
    };

    public static BeoFragment createFragment(boolean isFromServices, boolean displayErrorInPage) {
        BeoFragment fragment = new BeoFragment();
        fragment.setIsServices(isFromServices);
        fragment.setDisplayError(displayErrorInPage);
        return fragment;
    }

    public void setIsServices(boolean services) {
        this.isServices = services;
    }

    public void setDisplayError(boolean displayError) {
        this.displayError = displayError;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        showLoadingDialog();
        v = inflater.inflate(R.layout.fragment_beo, null, false);
        ButterKnife.bind(this, v);

        offersService = new OffersService(getContext());

        trackView();

        if(displayError){
            displayErrorMessageInsideCard(BEOLabels.getGenericRetryErrorMessage(), true);
        }else{
            setUpView();
            runFlow();
        }

        return v;
    }

    public void setUpView() {
        //added to keep listview expanded
        beoList.setOnGroupClickListener(onGroupClickListener);

        /*showLoadingDialog();*/
        setupLabels();
        setupHeader();
    }

    private void setupLabels() {
        ((TextView) v.findViewById(R.id.extra_category_tittle)).setText(BEOLabels.getExtra_category_tittle());
    }

    private void setupHeader() {
        try {
            navigationHeader = ((OffersActivity) getActivity()).getNavigationHeader();
            if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
                navigationHeader.hideSelectorView();
            }
            navigationHeader.setTitle(isServices ? OffersLabels.getOffers_for_you_services_button_label() : BEOLabels.getBeo_page_tittle());
            navigationHeader.hideBannerView();
            navigationHeader.removeViewFromContainer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!(VodafoneController.getInstance().getUser() instanceof PrepaidUser)) {
            setSubscriberListOnSelector();
        }
    }

    public void runFlow() {
        try {
            showLoadingDialog();
            clearView(); //Remove all view.

            if (VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
                getEligibleOffers4PrePaid();
            }else if(VodafoneController.getInstance().getUser() instanceof CBUUser){
                getPendingOffers4PostPaid(UserSelectedMsisdnBanController.getInstance().getSubscriberSid(),"offer");
            }else if(VodafoneController.getInstance().getUser() instanceof EbuMigrated){
                getPendingOffer4EBUIfAllowed();
            }
        } catch (Exception e){
            e.printStackTrace();
            stopLoadingDialog();
        }
    }

    @Override
    public void onEligibleOffersPrepaidRequestSucces(EligibleOffersSuccess activeOffersSuccess) {
        manageEligibleOffers4PrePaid(activeOffersSuccess);
    }

    @Override
    public void onEligibleOffersPrepaidRequestFailed() {
        displayErrorMessageInsideCard(BEOLabels.getGenericRetryErrorMessage(), true);
    }

    @Override
    public void onActiveOffersPrepaidRequestSucces(ActiveOffersSuccess activeOffersSuccess) {
        manageEligibleActiveOffers4PrePaid(activeOffersSuccess);
    }

    @Override
    public void onActiveOffersPrepaidRequestFailed() {
        //nothing to do
    }

    @Override
    public void onEligibleOffersCBURequestSucces(EligibleOffersPostSuccess eligibleOffersPostSuccess) {
        manageEligibleOffers4PostPaid(eligibleOffersPostSuccess);
    }

    @Override
    public void onEligibleOffersCBURequestFailed() {
        displayErrorMessageInsideCard(BEOLabels.getGenericRetryErrorMessage(), true);
    }

    @Override
    public void onPendingOffersCBURequestSucces(GeneralResponse generalResponse) {
        isOffersInPending = generalResponse.getTransactionFault() != null &&
                generalResponse.getTransactionFault().getFaultCode().equals(ErrorCodes.API38_USER_HAS_OFFER_IN_PENDING.getErrorCode());

        getEligibleOffers4PostPaid(UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn(),
                UserSelectedMsisdnBanController.getInstance().getSubscriberSid(),
                getBillCycleDate());
    }

    @Override
    public void onPendingOffersCBURequestFailed() {
        displayErrorMessageInsideCard(BEOLabels.getGenericRetryErrorMessage(), true);
    }

    @Override
    public void onEligibleOffersEBURequestSucces(EligibleOffersPostSuccess eligibleOffersPostSuccess) {
        manageEligibleOffers4EBU(eligibleOffersPostSuccess);
    }

    @Override
    public void onEligibleOffersEBURequestFailed() {
        displayErrorMessageInsideCard(BEOLabels.getGenericRetryErrorMessage(), true);
    }

    @Override
    public void onPendingOffersEBURequestSucces(GeneralResponse generalResponse) {
        if(generalResponse.getTransactionStatus() == 2){
            displayErrorMessageInsideCard(BEOLabels.getGenericRetryErrorMessage(), true);
        }else{
            isOffersInPending = generalResponse.getTransactionFault() != null &&
                    generalResponse.getTransactionFault().getFaultCode().equals(ErrorCodes.API38_USER_HAS_OFFER_IN_PENDING.getErrorCode());

            if(EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null){
                getEligibleOffers4EBU(EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole(),
                        UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn());
            }
        }
    }

    @Override
    public void onPendingOffersEBURequestFailed() {
        displayErrorMessageInsideCard(BEOLabels.getGenericRetryErrorMessage(), true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;

        if (resultCode == RESULT_SELECTOR_UPDATED) {
            hideErrorMessage();

            if (VodafoneController.findFragment(OffersActivity.class, BeoFragment.class) != null) {
                runFlow();
            }

            if(isServices)
                callForAdobeTarget(AdobePageNamesConstants.PG_SERVICES);
            else
                callForAdobeTarget(AdobePageNamesConstants.PG_BONUSES);
        }
    }

    private void setSubscriberListOnSelector() {
        Log.d(TAG, "setSubscriberListOnSelector");
        navigationHeader.buildMsisdnSelectorHeader();
    }


    private void clearView(){
        hideErrorMessage();
        beoList.removeAllViews();
        extraBeoListView.removeAllViews();
    }

    public void initAdapter(final List<EligibleCategories> eligibleCategories, final List<EligibleCategoriesPost> eligibleCategoriesPost, final boolean isPrepaid) {

        if (!haveElementsToDisplay(eligibleCategories, eligibleCategoriesPost)) {
            displayErrorMessageInsideCard(BEOLabels.getBeo_no_offers_or_services(), false);
            return;
        }

        BeoOfferAdapter beoOfferAdapter = new BeoOfferAdapter(getContext(), eligibleCategories, eligibleCategoriesPost, isPrepaid, isServices);

        beoList.setAdapter(beoOfferAdapter);

        if (isPrepaid) {
            for (int i = 0; i < eligibleCategories.size(); i++) {
                beoList.expandGroup(i);
            }
        } else {
            for (int i = 0; i < eligibleCategoriesPost.size(); i++) {
                beoList.expandGroup(i);
            }
        }

        beoList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.d(TAG, "On Child Item Click");
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                if(isServices){
                    tealiumMapEvent.put(TealiumConstants.screen_name,TealiumConstants.servicesPageScreen);
                }else{
                    tealiumMapEvent.put(TealiumConstants.screen_name,TealiumConstants.bonusesPageScreen);
                }
                tealiumMapEvent.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());

                tealiumMapEvent.put(TealiumConstants.event_name,TealiumConstants.bonusesOptionsSelectOffer);
                tealiumMapEvent.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent(BeoFragment.this.getClass().getSimpleName(), tealiumMapEvent);
                OfferRowInterface offerRowObject = null;

                offerRowObject = isPrepaid
                        ? eligibleCategories.get(groupPosition).getEligibleOffersList().get(childPosition)
                        : eligibleCategoriesPost.get(groupPosition).getEligibleOffersList().get(childPosition);

                final BeoDetailedFragment fragment = BeoDetailedFragment.createFragment(offerRowObject, isOffersInPending, isServices);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.beo_fragment_container, fragment, FragmentUtils.getTagForFragment(fragment))
                        .addToBackStack("tag")
                        .commit();

                return false;
            }
        });
        stopLoadingDialog();
    }

    private boolean haveElementsToDisplay(final List<EligibleCategories> eligibleCategoriesList, final List<EligibleCategoriesPost> eligibleCategoriesPostList) {

        if (eligibleCategoriesList != null && !eligibleCategoriesList.isEmpty()) {
            for (EligibleCategories eligibleCategories : eligibleCategoriesList) {
                if (!eligibleCategories.getIsHidden() && !eligibleCategories.getEligibleOffersList().isEmpty()) {
                    return true;
                }
            }
        }

        if (eligibleCategoriesPostList != null && !eligibleCategoriesPostList.isEmpty()) {
            for (EligibleCategoriesPost eligibleCategoriesPost : eligibleCategoriesPostList) {
                if (!eligibleCategoriesPost.getIsHidden() && !eligibleCategoriesPost.getEligibleOffersList().isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    public void initExtraAdapter(final List<ActiveOffer> offerRowList) {
        Log.d(TAG, "initExtraAdapter() with list Size: " + offerRowList.size());
        extraOfferContainer.setVisibility(View.GONE);

        if(offerRowList.size()>0){
            extraOfferContainer.setVisibility(View.VISIBLE);
        }

        BeoExtraOfferAdapter beoExtraOfferAdapter = new BeoExtraOfferAdapter(getContext(), offerRowList);
        extraBeoListView.setAdapter(beoExtraOfferAdapter);


        extraBeoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Extra Item Clicked");

                OfferRowInterface listItem = (OfferRowInterface) extraBeoListView.getAdapter().getItem(position);
                final BeoDetailedFragment fragment = BeoDetailedFragment.createFragment(listItem, isOffersInPending, isServices);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.beo_fragment_container, fragment, FragmentUtils.getTagForFragment(fragment))
                        .addToBackStack("tag")
                        .commit();
            }
        });

/*        if (offerIdForCPCOffer != null) {
            Log.d(TAG, offerIdForCPCOffer);
            boolean found = false;
            for (int i = 0; i < offerRowList.size(); i++) {
                if (offerRowList.get(i).getOfferId().toString().equals(offerIdForCPCOffer)) {
                    found = true;
                    final BeoDetailedFragment fragment = BeoDetailedFragment.createFragment(getActivity(), offerRowList.get(i), isOffersInPending, isServices);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.beo_fragment_container, fragment, FragmentUtils.getTagForFragment(fragment))
                            .addToBackStack("tag")
                            .commit();
                }
            }

            if (!found) {
                Log.d(TAG, "error, offer not found");
                displayErrorMessage(BEOLabels.getBeo_no_offers_or_services(), false);
            }
        }*/
    }

    public synchronized void displayErrorMessageInsideCard(String text, final boolean refresh) {
        stopLoadingDialog();
        if (getContext()==null)
            return;

        try {

            wrongContainer.setVisibility(View.VISIBLE);
            wrongContainer.removeAllViews();

            VodafoneGenericCard systemErrorView = new VodafoneGenericCard(getContext());
            systemErrorView.showError(true, text);

            if(refresh){
                beoList.removeAllViews();

                systemErrorView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshFragment();
                    }
                });
            }

            wrongContainer.addView(systemErrorView);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized void displayErrorMessage(String text) {
        if (getContext()==null)
            return;

        try {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(ScreenMeasure.dpToPx(12), ScreenMeasure.dpToPx(12), ScreenMeasure.dpToPx(12), 0);

            wrongContainer.setVisibility(View.VISIBLE);
            wrongContainer.removeAllViews();

            SmallErrorView smallErrorView = new SmallErrorView(getActivity() != null
                    ? getActivity()
                    : VodafoneController.getInstance(), text, new BeoFragment(), false);
            smallErrorView.addSmallErrorView(wrongContainer, layoutParams);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void refreshFragment() {
        BeoFragment beoFragment = (BeoFragment) VodafoneController.findFragment(OffersActivity.class, BeoFragment.class);
        hideErrorMessage();
        if (beoFragment != null) {
            beoFragment.runFlow();
        }
    }

    private void hideErrorMessage() {
        Log.d(TAG, "hideErrorMessage: ");
        wrongContainer.removeAllViews();
        wrongContainer.setVisibility(View.GONE);
    }

    private void manageEligibleOffers4PrePaid(EligibleOffersSuccess eligibleOffersSuccess) {
        boolean tsNotNullOrNoOffersOrServices = eligibleOffersSuccess != null; // tsNotNull -> Transaction Success Not Null
        if (!isServices) {
            if (tsNotNullOrNoOffersOrServices && eligibleOffersSuccess.getEligibleOptionsCategories() != null
                    && !eligibleOffersSuccess.getEligibleOptionsCategories().isEmpty()) {

                BonusesPageTrackingEvent event = new BonusesPageTrackingEvent();
                VodafoneController.getInstance().getTrackingService().track(event);

                initAdapter(eligibleOffersSuccess.getEligibleOptionsCategories(), null, true);
                getEligibleActiveOffers4PrePaid(UserSelectedMsisdnBanController.getInstance().getSubscriberSid());
                return;
            }

            tsNotNullOrNoOffersOrServices = tsNotNullOrNoOffersOrServices
                    && eligibleOffersSuccess.getEligibleOptionsCategories() != null
                    && eligibleOffersSuccess.getEligibleOptionsCategories().isEmpty();

        } else {
            if (tsNotNullOrNoOffersOrServices && eligibleOffersSuccess.getEligibleServicesCategories() != null
                    && !eligibleOffersSuccess.getEligibleServicesCategories().isEmpty()) {

                ServicesPageTrackingEvent event = new ServicesPageTrackingEvent();
                VodafoneController.getInstance().getTrackingService().track(event);

                initAdapter(eligibleOffersSuccess.getEligibleServicesCategories(), null, true);
                return;
            }

            tsNotNullOrNoOffersOrServices = tsNotNullOrNoOffersOrServices
                    && eligibleOffersSuccess.getEligibleServicesCategories() != null
                    && eligibleOffersSuccess.getEligibleServicesCategories().isEmpty();
        }

        if (tsNotNullOrNoOffersOrServices) {
            displayErrorMessageInsideCard(BEOLabels.getBeo_no_offers_or_services(), false);
        }
    }

    private void manageEligibleActiveOffers4PrePaid(ActiveOffersSuccess activeOffersSuccessResponse) {
        if (activeOffersSuccessResponse != null && activeOffersSuccessResponse.getActiveOffersList() != null && !activeOffersSuccessResponse.getActiveOffersList().isEmpty()) {
            List<ActiveOffer> filteredList = new ArrayList<ActiveOffer>();
            Log.d(TAG, "Transaction 0:  " + activeOffersSuccessResponse.getActiveOffersList().size());
            for (int i = 0; i < activeOffersSuccessResponse.getActiveOffersList().size(); i++) {
                if (isConditionsMetForDisplayExtraoption(activeOffersSuccessResponse.getActiveOffersList().get(i)))
                    filteredList.add(activeOffersSuccessResponse.getActiveOffersList().get(i));
                Log.d(TAG, "Is Renewable: " + activeOffersSuccessResponse.getActiveOffersList().get(i).getIsRenewable());
            }
            Log.d(TAG, "Filtered list   " + filteredList.size());
            initExtraAdapter(filteredList); //activeOffersSuccessResponse.getTransactionSuccess().getActiveOffersList()
        }
    }

    private boolean isConditionsMetForDisplayExtraoption(ActiveOffer activeOffer) {
        return activeOffer != null && activeOffer.getIsRenewable() && activeOffer.getIsMultipleActivation() && activeOffer.getOfferStatus().equals("1");
    }

    private void manageEligibleOffers4PostPaid(EligibleOffersPostSuccess eligibleOffersPostSuccess) {
        boolean tsNotNullOrNoOffersOrServices = eligibleOffersPostSuccess != null; // tsNotNull -> Transaction Success Not Null
        if (isServices) {
            if (eligibleOffersPostSuccess != null && eligibleOffersPostSuccess.getEligibleServicesCategories() != null && !eligibleOffersPostSuccess.getEligibleServicesCategories().isEmpty()) {

                for (EligibleCategoriesPost categoriesPost :eligibleOffersPostSuccess.getEligibleServicesCategories()){
                    Log.d(TAG, "Eligible Services Categories: " + categoriesPost.getCategory());
                }


                ServicesPageTrackingEvent event = new ServicesPageTrackingEvent();
                VodafoneController.getInstance().getTrackingService().track(event);
                initAdapter(null, eligibleOffersPostSuccess.getEligibleServicesCategories(), false);

                if(isOffersInPending){
                    displayErrorMessage(BEOLabels.getBeo_another_offers_and_services_active());
                }
                return;
            }

            tsNotNullOrNoOffersOrServices = tsNotNullOrNoOffersOrServices
                    && eligibleOffersPostSuccess.getEligibleServicesCategories() != null
                    && eligibleOffersPostSuccess.getEligibleServicesCategories().isEmpty();

        } else {
            if (eligibleOffersPostSuccess != null && eligibleOffersPostSuccess.getEligibleOptionsCategories() != null && !eligibleOffersPostSuccess.getEligibleOptionsCategories().isEmpty()) {
                Log.d(TAG, "Eligible Offers Categories: " + eligibleOffersPostSuccess.getEligibleOptionsCategories().size());

                BonusesPageTrackingEvent event = new BonusesPageTrackingEvent();
                VodafoneController.getInstance().getTrackingService().track(event);
                initAdapter(null, eligibleOffersPostSuccess.getEligibleOptionsCategories(), false);

                if(isOffersInPending){
                    displayErrorMessage(BEOLabels.getBeo_another_offers_and_services_active());
                }
                return;
            }

            tsNotNullOrNoOffersOrServices = tsNotNullOrNoOffersOrServices
                    && eligibleOffersPostSuccess.getEligibleOptionsCategories() != null
                    && eligibleOffersPostSuccess.getEligibleOptionsCategories().isEmpty();
        }

        if (tsNotNullOrNoOffersOrServices) {
            displayErrorMessageInsideCard(BEOLabels.getBeo_no_offers_or_services(), false);
        }
    }

    /*
    EBU Methods
    */
    private void getPendingOffer4EBUIfAllowed(){
        if(isAccesRestricted()){
            navigationHeader.hideSelectorView();
            displayErrorMessageInsideCard(BEOLabels.getCustomer_restriction_message(), false);
        }else if (isUserNotAllowedToActiveEO()){
            navigationHeader.hideSelectorView();
            displayErrorMessageInsideCard(BEOLabels.getContact_account_specialist_message(), false);
        }else{
            if(EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null){
                getPendingOffers4EBU(EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole(),
                        UserSelectedMsisdnBanController.getInstance().getSubscriberSid());
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

    private void manageEligibleOffers4EBU(EligibleOffersPostSuccess eligibleOffersPostSuccess){
        if(eligibleOffersPostSuccess == null){
            displayErrorMessageInsideCard(BEOLabels.getBeo_no_offers_or_services(), false);
        }else{
            if(!isServices){
                if(eligibleOffersPostSuccess.getEligibleOptionsCategories()!= null
                        && !eligibleOffersPostSuccess.getEligibleOptionsCategories().isEmpty()){
                    initAdapter(null, eligibleOffersPostSuccess.getEligibleOptionsCategories(), false);

                    if(isOffersInPending){
                        displayErrorMessage(BEOLabels.getPending_orders_message());
                    }
                }else{
                    displayErrorMessageInsideCard(BEOLabels.getBeo_no_offers_or_services(), false);
                }
            }else{
                if(eligibleOffersPostSuccess.getEligibleServicesCategories()!= null
                        && !eligibleOffersPostSuccess.getEligibleServicesCategories().isEmpty()){
                    initAdapter(null, eligibleOffersPostSuccess.getEligibleServicesCategories(), false);

                    if(isOffersInPending){
                        displayErrorMessage(BEOLabels.getPending_orders_message());
                    }
                }else{
                    displayErrorMessageInsideCard(BEOLabels.getBeo_no_offers_or_services(), false);
                }
            }
        }

    }

    @Override
    public String getTitle() {
        return isServices ? OffersLabels.getOffers_for_you_services_button_label() : OffersLabels.getOffers_for_you_beo_button_label();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        ((OffersActivity) getActivity())
                .getNavigationHeader()
                .setTitle(VodafoneController.getInstance().getUser() instanceof PrepaidUser ? OffersLabels.getOffers_for_you_page_prepaid_title() : OffersLabels.getOffers_for_you_page_postpaid_title());
    }

    private void trackView(){
        //Tealium Track view
        Map<String, Object> tealiumMapView = new HashMap(6);

        if(isServices){
            tealiumMapView.put(TealiumConstants.screen_name,TealiumConstants.servicesPageScreen);
            tealiumMapView.put(TealiumConstants.journey_name,TealiumConstants.bonusesOptionsJourney);
            tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);
        }else{
            tealiumMapView.put(TealiumConstants.screen_name,TealiumConstants.bonusesPageScreen);
            tealiumMapView.put(TealiumConstants.journey_name,TealiumConstants.bonusesOptionsJourney);
            tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isServices)
           callForAdobeTarget(AdobePageNamesConstants.PG_SERVICES);
        else
           callForAdobeTarget(AdobePageNamesConstants.PG_BONUSES);
    }

    public static class BonusesPageTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "bonuses page";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "bonuses page");


            s.prop5 = "sales:buy options";
            s.getContextData().put("prop5", s.prop5);
            s.channel = "bonuses and options";
            s.getContextData().put("&&channel", s.channel);
        }
    }

    public static class ServicesPageTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "services page";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "services page");


            s.prop5 = "sales:buy services";
            s.getContextData().put("prop5", s.prop5);
            s.channel = "bonuses and options";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}

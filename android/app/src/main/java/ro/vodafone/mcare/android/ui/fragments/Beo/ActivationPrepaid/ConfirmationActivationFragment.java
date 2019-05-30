package ro.vodafone.mcare.android.ui.fragments.Beo.ActivationPrepaid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.DashboardController;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.ActivatePrepaidOfferRequest;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleOffersSuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.PrepaidOfferRow;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.system.BEOLabels;
import ro.vodafone.mcare.android.client.model.realm.system.CallDetailsLabels;
import ro.vodafone.mcare.android.client.model.realm.system.VodafoneTvLabels;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.ui.activities.offers.BeoActivationPrepaidActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsItem;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsUtils;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Alex on 3/14/2017.
 */



public class ConfirmationActivationFragment  extends BaseFragment {

    public static final String TAG = "ConfirmationActivation";
    View v;

    ImageView backImage;
    VodafoneButton beoConfirmActivationContainer;
    VodafoneButton beoConfirmActivationBackContainer;
    OfferRowInterface offerRow;

    boolean isServices=false;


    public static  ConfirmationActivationFragment newInstance(boolean isServices) {


        ConfirmationActivationFragment fragment = new ConfirmationActivationFragment();
        fragment.isServices = isServices;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG,"onCreateView");
        v = inflater.inflate(R.layout.overlay_dialog_notifications, null);

        backImage = (ImageView) v.findViewById(R.id.overlayDismissButton);
        backImage.setOnClickListener(backListener);

        beoConfirmActivationContainer = (VodafoneButton) v.findViewById(R.id.buttonKeepOn);
        beoConfirmActivationContainer.setOnClickListener(activateOffer);
        beoConfirmActivationBackContainer = (VodafoneButton) v.findViewById(R.id.buttonTurnOff);
        beoConfirmActivationBackContainer.setOnClickListener(backListener);

        offerRow =  ((BeoActivationPrepaidActivity)getActivity()).getOfferRow();
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.serviceActivationOverlay);
        tealiumMapView.put(TealiumConstants.journey_name,TealiumConstants.bonusesOptionsJourney);
        if (VodafoneController.getInstance().getUserProfile().getUserRole() != null)
            tealiumMapView.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);


        setupLabels();
        return  v;
    }


    View.OnClickListener activateOffer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "beoConfirmActivationContainer activation clicked");

            beoConfirmActivationContainer.setClickable(false);
  /*
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.serviceActivationOverlay);
            tealiumMapEvent.put(TealiumConstants.event_name,TealiumConstants.serviceActivationOverlayActiveaza);
            tealiumMapEvent.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent(ConfirmationActivationFragment.TAG, tealiumMapEvent);

*/
            Observable.timer(500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            beoConfirmActivationContainer.setClickable(true);

                        }
                    });

            showLoadingDialog();

            ActivatePrepaidOfferRequest activatePrepaidRequest = new ActivatePrepaidOfferRequest();
            if(offerRow instanceof PrepaidOfferRow){
                activatePrepaidRequest.setMsgId(((PrepaidOfferRow) offerRow).getMsgId());
                activatePrepaidRequest.setApplyCharge(((PrepaidOfferRow) offerRow).getApplyCharge());
                activatePrepaidRequest.setOperation(((PrepaidOfferRow) offerRow).getOperation());
                activatePrepaidRequest.setPromoId(((PrepaidOfferRow) offerRow).getPromoId());
            }
            activateEligibleOffer(offerRow.getOfferId() + "", activatePrepaidRequest, offerRow.getOfferCategory());

        }
    };

    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
  /*          //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.serviceActivationOverlay);
            tealiumMapEvent.put(TealiumConstants.event_name,TealiumConstants.serviceOverlayInapoi);
            tealiumMapEvent.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent(ConfirmationActivationFragment.TAG, tealiumMapEvent);
*/
            getActivity().finish();
        }
    };



    private void setupLabels(){

        ((TextView)v.findViewById(R.id.overlayTitle)).setText(BEOLabels.getConfirm_activation_tittle_text());
        ((TextView)v.findViewById(R.id.overlaySubtext)).setText(BEOLabels.getConfirm_activation_content_text_first_part()+ " " + NumbersUtils.twoDigitsAfterDecimal(offerRow.getOfferPrice()) + BEOLabels.getConfirm_activation_content_text_second_part() );

        beoConfirmActivationContainer.setText(BEOLabels.getActivate_offer_text());
        beoConfirmActivationBackContainer.setText(BEOLabels.getActivation_back_text());

    }



    private void activateEligibleOffer(String offer_id, ActivatePrepaidOfferRequest activatePrepaidRequest, final String offerCategory){
        Log.d(TAG, "activateEligibleOffer() " +  offer_id  );
        OffersService offersService = new OffersService(getContext());
        showLoadingDialog();
        offersService.activateEligibleOffer(offer_id, activatePrepaidRequest).subscribe(new RequestSessionObserver<GeneralResponse<EligibleOffersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<EligibleOffersSuccess> eligibleOffersSuccessResponse) {
                stopLoadingDialog();
                Log.d(TAG, "activateEligibleOffer onNext");
                if(eligibleOffersSuccessResponse.getTransactionStatus() == 0){
                    Log.d(TAG, "activateEligibleOffer Transaction Status 0");
                    logGoogleAnalytics();

                    new CustomToast.Builder(getContext()).message(isServices ? BEOLabels.getBeo_activate_prepaid_offer_service_part2() :
                            BEOLabels.getBeo_activate_prepaid_offer_part2()).success(true).show();

                    addVov(offerCategory);
                    DashboardController.reloadDashboardOnResume();
                    new NavigationAction(getActivity()).startAction(IntentActionName.DASHBOARD);

                    trackView();

                }else{
                    Log.d(TAG, "activateEligibleOffer Transaction Status !0");
                    new CustomToast.Builder(VodafoneController.getInstance().getApplicationContext()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
                    try {
                        getActivity().finish();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                Log.d(TAG, "activateEligibleOffer onError");
                new CustomToast.Builder(VodafoneController.getInstance().getApplicationContext()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
                try {
                    if(getActivity() != null)
                        getActivity().finish();
                }catch (Exception excp){
                    excp.printStackTrace();
                }
            }
        });
    }


    private void addVov(String offerCategory){
        VoiceOfVodafone vov;
        if(AppConfiguration.getVodafoneTvCategories().contains(offerCategory)){
            vov = new VoiceOfVodafone(11,20, VoiceOfVodafoneCategory.EO_Activation, null,  String.format(
                    isServices ? BEOLabels.getBeo_activate_prepaid_offer_service_part1():BEOLabels.getBeo_activate_prepaid_offer_part1()
                    , offerRow.getOfferName()), VodafoneTvLabels.getVtvActivationVovDismissButton(), VodafoneTvLabels.getVtvAppVovLabel(), true, true,VoiceOfVodafoneAction.Dismiss, VoiceOfVodafoneAction.ExternalAppRedirect);
            vov.setRightActionUrl(AppConfiguration.getVodafoneTvVovExternalLink());
        }else {
            vov = new VoiceOfVodafone(11, 20, VoiceOfVodafoneCategory.EO_Activation, null, String.format(
                    isServices ? BEOLabels.getBeo_activate_prepaid_offer_service_part1():BEOLabels.getBeo_activate_prepaid_offer_part1()
                    , offerRow.getOfferName()), "Ok", null,
                    true, false, VoiceOfVodafoneAction.Dismiss, null);
        }
        VoiceOfVodafoneController.getInstance().pushStashToView(vov.getCategory(), vov);
    }

    private void trackView(){
        //Tealium trigger survey
        //Tealium Track view
        Map<String, Object> tealiumMapView =new HashMap<>(4);
        tealiumMapView.put("screen_name","buyaddon");
        //add Qualtrics survey
        TealiumHelper.addQualtricsCommand();
        //track
        TealiumHelper.trackView("buyaddon", tealiumMapView);
    }

    private void logGoogleAnalytics() {
        String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();

        FirebaseAnalyticsItem firebaseAnalyticsItem = new FirebaseAnalyticsItem();
        firebaseAnalyticsItem.setFirebaseAnalyticsEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE);
        firebaseAnalyticsItem.addEncryptedFirebaseAnalyticsParam("eo_activation_MSISDN", msisdn);
        firebaseAnalyticsItem.addFirebaseAnalyticsParams("eo_activation_eo_name", offerRow.getOfferName(),
                "eo_activation_id", offerRow.getOfferId()+"", "eo_activation_xxEUR", offerRow.getOfferPrice() + "EUR");
        baseFragmentCommunicationListener.sendFirebaseEvent(firebaseAnalyticsItem.getFirebaseAnalyticsEvent(),
                FirebaseAnalyticsUtils.getBundleFromParams(firebaseAnalyticsItem.getFirebaseAnalyticsParams()));
    }

}

package ro.vodafone.mcare.android.ui.fragments.Beo.ActivationPostpaid;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.ActivationRequest;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.OfferBasicInfo;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.BEOLabels;
import ro.vodafone.mcare.android.ui.activities.offers.BeoActivationPostPaidActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Alex on 3/15/2017.
 */


public class ImmediateOrNextBillActivationFragment extends BaseFragment {

    public static final String TAG = "Immediate|CycleActiv";
    View v;
    boolean isEmidiate = true;
    ImageView backImage;
    Button immediateActivationContainer;
    Button immediateActivationBackContainer;
    OfferRowInterface offer;
    List<OfferBasicInfo> incompatibleOffers;
    Profile profile;
    View.OnClickListener activateOffer = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "activation clicked");
            immediateActivationContainer.setClickable(false);
/*
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put("screen_name","bonus or service activation overlay postpaid");
            tealiumMapEvent.put("event_name","mcare:bonus or service activation overlay postpaid:button:activeaza");
            tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent("event_name", tealiumMapEvent);

*/
            Observable.timer(500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            immediateActivationContainer.setClickable(true);

                        }
                    });

            //showLoadingDialog();

            if (incompatibleOffers != null && !incompatibleOffers.isEmpty()) {
                Log.d(TAG, "Going to EOincompatibleFragment");
                Log.d(TAG,"IS ETF");
                BeoActivationPostPaidActivity beoActivationPostPaidActivity = (BeoActivationPostPaidActivity) VodafoneController.findActivity(BeoActivationPostPaidActivity.class);

                if(beoActivationPostPaidActivity != null){
                    beoActivationPostPaidActivity.addFragment(EOincompatibleFragment.create(offer, isEmidiate, incompatibleOffers));
                }else{
                    //Todo
                }
            } else {
                Log.d(TAG, "Going to API 41");
                ActivationRequest activationRequest = new ActivationRequest();
                if(profile!=null){
                    activationRequest.setBillCycleDate(profile.getBillCycleDate());
                }
                if(offer!=null){
                    activationRequest.setOfferName(offer.getOfferName());
                    activationRequest.setOfferType(offer.getOfferCategory());
                }
                String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
                if(msisdn!=null && !msisdn.isEmpty()){
                    try {
                        Log.d(TAG, "msisdn is : " + msisdn);

                        if (String.valueOf(msisdn.charAt(0)).equals(0)) msisdn = "4" + msisdn;
                        Log.d(TAG, " concat msisdn " + msisdn);

                        String subscriberId = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null ? UserSelectedMsisdnBanController.getInstance().getSubscriberSid() : null;

                        Log.d(TAG, "Going to API 41 number: " + VodafoneController.getInstance().getUserProfile().getMsisdn() + " sid: " + VodafoneController.getInstance().getUserProfile().getSid() + "  matrix: " + ((PostpaidOfferRow) offer).getMatrixId() + " emidiate? " + isEmidiate);

                        BeoActivationPostPaidActivity beoActivationPostPaidActivity = (BeoActivationPostPaidActivity) VodafoneController.findActivity(BeoActivationPostPaidActivity.class);

                        if (beoActivationPostPaidActivity != null) {
                            beoActivationPostPaidActivity.putActivatePostPaidEligibleOffer(activationRequest, msisdn, subscriberId, ((PostpaidOfferRow) offer).getMatrixId(), isEmidiate, offer.getOfferCategory());
                        } else {
                            //Todo
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

        }
    };
    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "back clicked");
/*
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put("screen_name","bonus or service activation overlay postpaid");
            tealiumMapEvent.put("event_name","mcare:bonus activation overlay:button:renunta");
            tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent("event_name", tealiumMapEvent);

            //Tealium Track Event
            Map<String, Object> tealiumMapEvent2 = new HashMap(6);
            tealiumMapEvent2.put("screen_name","bonus or service activation overlay postpaid");
            tealiumMapEvent2.put("event_name","mcare:bonus activation overlay:button:(x)");
            tealiumMapEvent2.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent("event_name", tealiumMapEvent2);
*/
            getActivity().finish();
        }
    };

    public static ImmediateOrNextBillActivationFragment create(OfferRowInterface offerRow, boolean isEmidiate, List<OfferBasicInfo> incompatibleOffers) {
        ImmediateOrNextBillActivationFragment fragment = new ImmediateOrNextBillActivationFragment();

        fragment.setEmidiate(isEmidiate);
        fragment.setOffer(offerRow);
        fragment.setIncompatibleOffers(incompatibleOffers);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView");
        v = inflater.inflate(R.layout.overlay_dialog_notifications, null);

        backImage = (ImageView) v.findViewById(R.id.overlayDismissButton);
        backImage.setOnClickListener(backListener);

        immediateActivationContainer = (Button) v.findViewById(R.id.buttonKeepOn);
        immediateActivationContainer.setOnClickListener(activateOffer);

        immediateActivationBackContainer = (Button) v.findViewById(R.id.buttonTurnOff);
        immediateActivationBackContainer.setOnClickListener(backListener);

        profile = (Profile) RealmManager.getRealmObject(Profile.class);
        setupLabels();
        return v;
    }

    private void setupLabels() {

        ((TextView) v.findViewById(R.id.overlayTitle)).setText(BEOLabels.getConfirm_activation_tittle_text());
        if (isEmidiate) {
            ((TextView) v.findViewById(R.id.overlaySubtext)).setText(BEOLabels.getImedite_activation_content_text_first_part() + " " + NumbersUtils.twoDigitsAfterDecimal(offer.getOfferPrice()) + BEOLabels.getImedite_activation_content_text_second_part());
        } else {
            ((TextView) v.findViewById(R.id.overlaySubtext)).setText(BEOLabels.getNext_bill_activation_content_text_first_part() + " " + NumbersUtils.twoDigitsAfterDecimal(offer.getOfferPrice()) + BEOLabels.getNext_bill_activation_content_text_second_part());
        }
        immediateActivationContainer.setText(BEOLabels.getActivate_offer_text());
        immediateActivationBackContainer.setText(BEOLabels.getActivation_back_text());

    }

    public boolean isEmidiate() {
        return isEmidiate;
    }

    public void setEmidiate(boolean emidiate) {
        isEmidiate = emidiate;
    }

    public OfferRowInterface getOffer() {
        return offer;
    }

    public void setOffer(OfferRowInterface offer) {
        this.offer = offer;
    }

    public List<OfferBasicInfo> getIncompatibleOffers() {
        return incompatibleOffers;
    }

    public void setIncompatibleOffers(List<OfferBasicInfo> incompatibleOffers) {
        this.incompatibleOffers = incompatibleOffers;
    }


}
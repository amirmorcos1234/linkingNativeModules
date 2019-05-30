package ro.vodafone.mcare.android.ui.fragments.offers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.realm.RealmList;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomOffer;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomPendingOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.OffersLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.AncomOffersService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.views.AdapterBackedLinearLayout;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by Deaconescu Eliza on 08.03.2017.
 */
public class AncomPendingOffersFragment extends OffersFragment {

    public static String TAG = "AncomPendingOffersFrag";

    LinearLayout v;
    AdapterBackedLinearLayout ancomOffersList;
    RelativeLayout wrongContainer;
    AncomPendingOffersAdapter ancomPendingOffersAdapter;
    public NavigationHeader navigationHeader;
    ViewGroup rootView;
    OffersSelectionPageFragment offersSelectionPageFragment;
    AncomPendingOffersSuccess ancomPendingOffersSuccess;

    VodafoneGenericCard errorCard;
    VodafoneGenericCard loadingCard;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildNavigationHeader();
    }

    @Override
    public void onResume() {
        super.onResume();
        showHeaderIfRescorp();
    }

    public AncomPendingOffersFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        v = (LinearLayout) inflater.inflate(R.layout.fragment_offers, null);

        //Tealium Track view
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.waitingOffers);
        tealiumMapView.put(TealiumConstants.journey_name,TealiumConstants.waitingOffers);
        tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);

        wrongContainer = (RelativeLayout) v.findViewById(R.id.wrong_container);
        rootView = container;

        ((OffersActivity) getActivity()).scrolltoTop();//we are changing only the fragment , so we have to reset parent's scrollview possition
        offersSelectionPageFragment = new OffersSelectionPageFragment();
        PendingOffersTrackingEvent event = new PendingOffersTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        navigationHeader.setTitle(getTitle());

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoadingCard();
        //addContentWithDelayForPerformanceIssues();
        updateScreenOnSelectedMsisdnChanged();
    }

    @Override
    public String getTitle() {
        return OffersLabels.getOffers_for_you_pending_offers();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        rootView.removeAllViews();

        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
            ((OffersActivity) getActivity()).getNavigationHeader().setTitle(OffersLabels.getOffers_for_you_page_prepaid_title());
        }else{
            ((OffersActivity) getActivity()).getNavigationHeader().setTitle(OffersLabels.getOffers_for_you_page_postpaid_title());
        }

        OffersLabels.getOffers_for_you_pending_offers();
 }

    public void initAdapter (RealmList<AncomOffer> ancomPendingOfferList){
        Log.d(TAG,"initAdapter()");
        Log.d(TAG,"ancomOfferList: " + ancomPendingOfferList.size());
        ancomPendingOffersAdapter = new AncomPendingOffersAdapter(getContext(),ancomPendingOfferList);
        ancomOffersList.setAdapter(ancomPendingOffersAdapter);
        hideLoadingCard();
    }

    public static class PendingOffersTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "pending offers";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"pending offers");


            s.channel = "pending offers";
            s.getContextData().put("&&channel", s.channel);
        }
    }

    private void buildNavigationHeader(){
        navigationHeader = ((OffersActivity) getActivity()).getNavigationHeader();
        navigationHeader.removeViewFromContainer();
        navigationHeader.hideBannerView();
    }

    private void showHeaderIfRescorp() {
        if (VodafoneController.getInstance().getUser() instanceof ResCorp ) {
            navigationHeader.buildMsisdnSelectorHeader();
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.PG_PENDING_OFFERS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_SELECTOR_UPDATED && resultCode == RESULT_SELECTOR_UPDATED) {
            updateScreenOnSelectedMsisdnChanged();

            callForAdobeTarget(AdobePageNamesConstants.PG_PENDING_OFFERS);
        }

    }

    public void updateScreenOnSelectedMsisdnChanged() {
        String selectedMsisdn;
        Subscriber subscriber = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber();
        D.w("my subscriber = "+subscriber);
        if (subscriber != null) {
            Log.d(TAG, "Subscriber Selector number: " + subscriber.getMsisdn());
        }
        if (UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber()!=null) {
            selectedMsisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
            D.w("3 = "+selectedMsisdn);
          //  showLoadingDialog();
            showLoadingCard();
            getAncomPendingOffers(selectedMsisdn);
            //addContentWithDelayForPerformanceIssues();
        }else {
            getAncomPendingOffers(VodafoneController.getInstance().getUserProfile().getMsisdn());
            //addContentWithDelayForPerformanceIssues();
        }
    }

    public void getAncomPendingOffers(String msisdn) {
        if (msisdn.startsWith("0")) {
            msisdn = msisdn.replaceFirst("0", "40");
        }
        final AncomOffersService ancomOffersService = new AncomOffersService(VodafoneController.getInstance());

        ancomOffersService.getVOSPendingOffers(msisdn).subscribe(new RequestSaveRealmObserver<GeneralResponse<AncomPendingOffersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<AncomPendingOffersSuccess> ancomOffersSuccessGeneralResponse) {
                super.onNext(ancomOffersSuccessGeneralResponse);
                hideLoadingCard();

                if (ancomOffersSuccessGeneralResponse != null && ancomOffersSuccessGeneralResponse.getTransactionStatus() == 0) {
                    Log.d("getAncomOffers", "getTransactionSuccess");
                    createView();
                    return;
                }
                systemError();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d("getAncomOffers", "onError");
                hideLoadingCard();
                systemError();
            }

            @Override
            public void onCompleted() { }
        });

        D.w("last line in ancom geter");
    }

    //added a 0.5 seconds delay to prevent a crash on some devices
    //DO NOT REMOVE THIS!!!!!
    /*private void addContentWithDelayForPerformanceIssues(){

        Observable.timer(700, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if(getContext()==null){
                            return;
                        }
                        createView();
                    }
                });
    }*/

    public void createView (){
        D.w("first line in createview");
        ancomPendingOffersSuccess = (AncomPendingOffersSuccess) RealmManager.getRealmObject(AncomPendingOffersSuccess.class);

        rootView.removeAllViews();

        if(ancomPendingOffersSuccess != null ) {
            Log.d(TAG, "ancomAcceptedOffersSuccess: " + ancomPendingOffersSuccess.toString());
            if (ancomPendingOffersSuccess.getAncomOffers().size() != 0) {
                Log.d(TAG, " offer for msisdn:" +ancomPendingOffersSuccess.getAncomOffers().get(0).getMsisdn());
                View v = View.inflate(getContext(), R.layout.fragment_offers,null);
                ancomOffersList = (AdapterBackedLinearLayout) v.findViewById(R.id.offersListview);
                initAdapter(ancomPendingOffersSuccess.getAncomOffers());
                rootView.addView(v);
            } else {
                noPendingOffers();
            }
        }else{
            Log.d(TAG, "error in getAncomOffers");
            noPendingOffers();
        }
    }

    public void noPendingOffers(){
       // stopLoadingDialog();
        hideLoadingCard();

        if(getContext() != null)
            errorCard = new VodafoneGenericCard(getContext());
        else
            errorCard = new VodafoneGenericCard(VodafoneController.findActivity(OffersActivity.class));

        rootView.addView(errorCard);
        errorCard.showError(true, OffersLabels.getProposalsPendingNoOffers());
    }


    public void systemError() {
        hideLoadingCard();

        if(getContext() == null) {
            return;
        } else {
            errorCard = new VodafoneGenericCard(getContext());
        }

        rootView.addView(errorCard);
        errorCard.showError(true, OffersLabels.getAPIFailedError());
        errorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateScreenOnSelectedMsisdnChanged();
            }
        });
    }

    private void showLoadingCard(){
        if(rootView.getChildCount()>0){
            rootView.removeAllViews();
        }
        if(loadingCard==null){
            loadingCard = new VodafoneGenericCard(getContext());
        }
        loadingCard.showLoading(true);
        rootView.addView(loadingCard);
    }

    private void hideLoadingCard(){
        if(loadingCard!=null){
            rootView.removeView(loadingCard);
        }
    }


}


package ro.vodafone.mcare.android.ui.fragments.travelingAboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmList;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.card.offers.ExpandableWebViewCard;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.TravellingAboardLabels;
import ro.vodafone.mcare.android.client.model.travellingAboard.HintsList;
import ro.vodafone.mcare.android.client.model.travellingAboard.TravallingHintSuccess;
import ro.vodafone.mcare.android.client.model.users.NonVodafoneUser;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.TravellingAboardService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.travellingAboard.TravelingAboardActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.TextUtils;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by Alex on 4/18/2017.
 */

public class TravellingHintsFragment extends BaseFragment {

    public static final String TAG = "TravellingHintsFragment";
    private RealmList<HintsList> hintsList;
    private LinearLayout pontsCardContainer;
    private NavigationHeader navigationHeader;
    boolean isEbu= false;
    private VodafoneGenericCard errorCard;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationHeader = ((TravelingAboardActivity) getActivity()).getNavigationHeader();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.travelling_hints_fragment, null);

        pontsCardContainer = (LinearLayout) v.findViewById(R.id.ponts_card_container);


        getTravellingHints(getContext());
        setUpNavigationHeader();
        initTealium();
        return v;
    }

    private void initTealium() {
        //Tealium Track AbroadView
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.countryFaq);
        tealiumMapView.put(TealiumConstants.journey_name, TealiumConstants.roaming);
        tealiumMapView.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);

        TravelingFAQTrackingEvent event = new TravelingFAQTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }


    public void setUpNavigationHeader() {
        navigationHeader.setTitle(TravellingAboardLabels.getTravelling_aboard_ponts_page_tittle());
        navigationHeader.hideSelectorView();
    }

    public void setHints(RealmList<HintsList> hintsList) {
        for (int i = 0; i < hintsList.size(); i++) {
            addExpandableWebViewCard(hintsList.get(i).getSummary(), hintsList.get(i).getWebviewURL(), hintsList.get(i).getDetails());
        }
    }

    private void addExpandableWebViewCard(String title, String url, String details) {
        title = TextUtils.fromHtml(title).toString();

        ExpandableWebViewCard expandableWebViewCard = new ExpandableWebViewCard(getContext())
                .setTitle(title)
                .setWebViewUrl(url)
                .setLoadHtmlContent(true)
                .setHtmlContent(details)
                .setLoadHtmlContent(url == null || url.isEmpty())
                .setDefaultFontDimension(11)
                .hideLine()
                .setImageArrowDirectionDown()
                .build();
        expandableWebViewCard.setHeaderPadding(16, 16, 16, 16);
        expandableWebViewCard.setExpandableViewClickListener();
        pontsCardContainer.addView(expandableWebViewCard);
    }

    private void addErrorCard() {
        if(errorCard == null) {
            errorCard = new VodafoneGenericCard(getContext());
            errorCard.showError(true, TravellingAboardLabels.getTravellingAbroadErrorRetry());
            errorCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getTravellingHints(getContext());
                }
            });
        } else {
            errorCard.setVisibility(View.VISIBLE);
        }
    }

    private void hideErrorCard() {
        if(errorCard != null) {
            errorCard.setVisibility(View.GONE);
        }
    }

//    @Override
//    public String getTitle() {
//        return null;
//    }

    public void getTravellingHints(final Context context) {

        hideErrorCard();
        showLoadingDialog();
        hintsList = new RealmList<>();
        TravellingAboardService travellingAboardService = new TravellingAboardService(getContext());
        User user = VodafoneController.getInstance().getUser();
        if(user instanceof EbuMigrated) {
            isEbu = true;
        }

        travellingAboardService.getTravellingHints(user instanceof PrepaidUser ? true : false, isEbu).subscribe(new RequestSaveRealmObserver<GeneralResponse<TravallingHintSuccess>>() {
            @Override
            public void onNext(GeneralResponse<TravallingHintSuccess> travallingHintSuccessGeneralResponse) {
                super.onNext(travallingHintSuccessGeneralResponse);

                if (travallingHintSuccessGeneralResponse != null && travallingHintSuccessGeneralResponse.getTransactionStatus() == 0 &&
                        travallingHintSuccessGeneralResponse.getTransactionSuccess() != null) {
                    hintsList = travallingHintSuccessGeneralResponse.getTransactionSuccess().getHintsList();
                    if (hintsList != null && hintsList.size() != 0) {
                        setHints(hintsList);
                    }
                } else {
                    if(context != null) {
                        new CustomToast.Builder(context).message(AppLabels.getToastErrorMessage()).success(false).show();
                        addErrorCard();
                    }
                    stopLoadingDialog();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if(context != null) {
                    new CustomToast.Builder(context).message(AppLabels.getToastErrorMessage()).success(false).show();
                    addErrorCard();
                }
                stopLoadingDialog();
            }

            @Override
            public void onCompleted() {
                stopLoadingDialog();
            }
        });
    }

    public static class TravelingFAQTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "roaming faq";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "roaming faq");


            s.channel = "roaming";
            s.getContextData().put("&&channel", s.channel);
        }
    }

}

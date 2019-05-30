package ro.vodafone.mcare.android.ui.fragments.topUp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.client.model.topUp.TransferCreditTerms;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.TopUpActivity;
import ro.vodafone.mcare.android.utils.TealiumHelper;


public class TopUpTransferCreditTermsConditionsFragment extends BaseTopUpFragment{

    WebView termsWebView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CreditTransferTermsConditionsTrackingEvent event = new CreditTransferTermsConditionsTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_top_up_use_terms, container, false);

        termsWebView = (WebView) view.findViewById(R.id.terms_web_view);

        populateWebView();

        ((TopUpActivity)getActivity()).getNavigationHeader().setTitle(TopUPLabels.getTop_up_transfer_credit_page_title());
        ((TopUpActivity)getActivity()).getNavigationHeader().hideSelectorView();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TealiumHelper.tealiumTrackView(TopUpTransferCreditTermsConditionsFragment.class.getSimpleName(),
                TealiumConstants.topUpJourney,TealiumConstants.transferCreditTermsConditionsScreenName);
    }

    public void populateWebView(){

        final String mime = "text/html";
        final String encoding = "utf-8";
        termsWebView.getSettings().setJavaScriptEnabled(true);


        rechargeService.getTransferCreditTerms().subscribe(new RequestSessionObserver<TransferCreditTerms>() {
            @Override
            public void onNext(TransferCreditTerms transferCreditTerms) {
                termsWebView.loadData(transferCreditTerms.getTransferCreditTerms(), mime, encoding);
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    public static class CreditTransferTermsConditionsTrackingEvent extends TrackingEvent {
        @Override
        protected void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);
            if (getErrorMessage() != null) {
                s.event11 = "event11";
                s.getContextData().put(TrackingVariable.EV_ERROR_EVENT, s.event11);
            }
            s.pageName = "mcare:credit transfer terms and conditions";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, s.pageName);
            s.channel = "credit transfer";
            s.getContextData().put("&&channel", s.channel);
            s.prop21 = "mcare:credit transfer terms and conditions";
            s.getContextData().put(TrackingVariable.P_PAGE_LEVEL_2, s.prop21);
            s.eVar73 = "credit transfer";
            s.getContextData().put(TrackingVariable.P_PROP73, s.eVar73);
        }
    }
}


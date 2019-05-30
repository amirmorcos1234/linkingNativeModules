package ro.vodafone.mcare.android.ui.fragments.yourServices;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomAcceptedOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomOffer;
import ro.vodafone.mcare.android.client.model.realm.system.OffersLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.AncomOffersService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.yourServices.YourServicesActivity;
import ro.vodafone.mcare.android.ui.fragments.yourServices.details.OffersContractDetailsFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Deaconescu Eliza on 08.03.2017.
 */
public class AncomAcceptedOffersFragment extends YourServicesFragment {

    public static String TAG = "AcceptedOffersFragment";

    RealmList<AncomOffer> listOffers = new RealmList<AncomOffer>();
    AncomOffersService ancomOffersService;

    LinearLayout ancomOffersList;
    LinearLayout v;
    VodafoneGenericCard errorCard;
    VodafoneGenericCard loadingCard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        ancomOffersService = new AncomOffersService(getContext());

        String msisdnFromProfile = getMsisdnFromProfile();
        
        v = (LinearLayout) inflater.inflate(R.layout.fragment_accepted_offers, null);

        ancomOffersList = (LinearLayout) v.findViewById(R.id.offers_container);
        getAncomOffers(VodafoneController.getInstance(), msisdnFromProfile);

        //Tealium Track View
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put("screen_name","accepted offers");
        tealiumMapView.put("journey_name","accepted offers");
        tealiumMapView.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapView);

        AcceptedOffersTrackingEvent event = new AcceptedOffersTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        return v;
    }
    private String getMsisdnFromProfile(){
        if (VodafoneController.getInstance().getUser() instanceof PostPaidUser && UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn()!=null){
            return UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
        } else if(VodafoneController.getInstance().getUserProfile()!=null){
            return VodafoneController.getInstance().getUserProfile().getMsisdn();
        }else{
            return null;
        }
    }


    public String getTitle() {
        return OffersLabels.getOffersForYouAcceptedOffers();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((YourServicesActivity) getActivity()).setTitle();
    }

    //Api42 - Get accepted offers
    public void getAncomOffers(final Context context, String msisdn) {
        showLoadingCard();
        listOffers = new RealmList<AncomOffer>();
        ancomOffersService.getVOSAcceptedOffers(formatMsisdn(msisdn)).subscribe(new RequestSaveRealmObserver<GeneralResponse<AncomAcceptedOffersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<AncomAcceptedOffersSuccess> ancomOffersSuccessGeneralResponse) {
                super.onNext(ancomOffersSuccessGeneralResponse);

                if (ancomOffersSuccessGeneralResponse != null && ancomOffersSuccessGeneralResponse.getTransactionStatus() == 0 &&
                        ancomOffersSuccessGeneralResponse.getTransactionSuccess() != null) {
                    Log.d("getAncomOffers", "getTransactionSuccess");
                    listOffers = ancomOffersSuccessGeneralResponse.getTransactionSuccess().getAncomOffers();
                    if (listOffers != null && listOffers.size() != 0) {

                        initAdapter(listOffers);

                        hideLoadingCard();
                        hideErrorMessage();
                    } else {
                        hideLoadingCard();
                        showErrorMessage(false);
                    }
                } else {
                    Log.d("getAncomOffers", "getTransactionFault");
                    hideLoadingCard();
                    showErrorMessage(true);
                    new CustomToast.Builder(context).message("Serviciu momentan indisponibil!").success(false).show();
//                    CustomToast customToast = new CustomToast(VodafoneController.getInstance().getDashboardActivity(), context, "Serviciu momentan indisponibil", false);
//                    customToast.show();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                try {
                    Log.d("getAncomOffers", "onError");
                    hideLoadingCard();
                    showErrorMessage(true);
                    new CustomToast.Builder(context).message("Serviciu momentan indisponibil!").success(false).show();
                }catch (Exception e1){
                    e.printStackTrace();
                }
//                CustomToast customToast = new CustomToast(VodafoneController.getInstance().getDashboardActivity(), context, "Serviciu momentan indisponibil", false);
//                customToast.show();
            }

            @Override
            public void onCompleted() {
            }
        });
    }

    public void initAdapter(List<AncomOffer> ancomPendingOfferList) {
        Log.d(TAG, "initAdapter()");
        Log.d(TAG, "ancomOfferList: " + ancomPendingOfferList.size());
        ancomOffersList.removeAllViews();
        for (AncomOffer ancomOffer : ancomPendingOfferList) {
            ancomOffersList.addView(makeAncomOfferView(ancomOffersList, ancomOffer));
        }
    }

    private View makeAncomOfferView(ViewGroup viewGroup, final AncomOffer child) {
        final View view = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_your_services_accepted_offers, viewGroup, false);

        TextView acceptedOffersTC = (TextView) view.findViewById(R.id.accepted_offers_t_c);
        TextView acceptedOffersGeneralTC = (TextView) view.findViewById(R.id.accepted_offers_general_t_c);
        RelativeLayout image = (RelativeLayout) view.findViewById(R.id.redirect_contrat_details);
        TextView contractDuration = (TextView) view.findViewById(R.id.contractDuration);
        TextView proposalId = (TextView) view.findViewById(R.id.proposalId);
        TextView creationDate = (TextView) view.findViewById(R.id.creationDateAcceptedOffers);
        TextView msisdn = (TextView) view.findViewById(R.id.msisdnAcceptedOffers);

        SpannableString myString = new SpannableString(getContext().getResources().getString(R.string.accepted_offers_t_c));
        openWebView(myString, 0, 29, acceptedOffersTC, SettingsLabels.getLinkAncomOffersTC());

        SpannableString myStringSupported = new SpannableString(OffersLabels.getAcceptedOffersGeneralTC());
        openWebView(myStringSupported, 0, 52, acceptedOffersGeneralTC, SettingsLabels.getLinkContractDetailsAccepted());

        if (child.getMsisdn() != null) {
            Log.d("ChildVIew", "child.getPrepaidMsisdn() " + child.getMsisdn());
            msisdn.setText(child.getMsisdn());
        }
        if (child.getProposalId() != null) {
            Log.d("ChildVIew", "child.getChannelTypeId() " + child.getProposalId());
            proposalId.setText(child.getProposalId());
        }
        if (child.getCreationDate() != null) {
            Log.d("ChildVIew", "child.getAmount() " + child.getCreationDate());
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            String dateString = formatter.format(new Date(child.getCreationDate()));
            creationDate.setText(dateString);
        }
        if (child.getContractDuration() != null) {
            Log.d("ChildVIew", "child.getActionTypeId() " + child.getContractDuration());
            contractDuration.setText(String.format(VodafoneController.getInstance().getApplicationContext().getResources().getString(R.string.your_option_months), child.getContractDuration()));
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(VodafoneController.getInstance(), WebviewActivity.class);
//                intent.putExtra(WebviewActivity.KEY_URL, SettingsLabels.getLinkContractDetails());
//                intent.putExtra(WebviewActivity.PAGE_TITLE, OffersLabels.getAcceptedOffersContractDetails());
//                getContext().startActivity(intent);
                ((YourServicesActivity) getActivity()).attachFragment(OffersContractDetailsFragment
                        .newInstance(child.getOfferId(), OffersContractDetailsFragment.ACCEPTED_OFFER));
            }
        });


        return view;
    }

    public static class AcceptedOffersTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "accepted offers";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "accepted offers");


            s.channel = "accepted offers";
            s.getContextData().put("&&channel", s.channel);
        }
    }

    private void showErrorMessage(boolean isClickable) {
        ancomOffersList.setVisibility(View.GONE);
        Log.d(TAG, "showErrorMessage: ");
        errorCard = new VodafoneGenericCard(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        v.addView(errorCard, layoutParams);

        if (isClickable) {
            errorCard.showError(true, OffersLabels.getAPIFailedError());
            errorCard.setOnClickListener(errorRefreshListener);
        } else {
            errorCard.showError(true, OffersLabels.getNoAcceptedOffers());
        }
    }

    private void hideErrorMessage() {
        Log.d(TAG, "hideErrorMessage: ");
        if (errorCard != null && errorCard.getVisibility() == View.VISIBLE) {
            v.removeView(errorCard);
        }

    }

    private void showLoadingCard() {
        ancomOffersList.setVisibility(View.GONE);
        hideErrorMessage();
        Log.d(TAG, "showLoadingCard: ");
        if (loadingCard != null) {
            v.removeView(loadingCard);
        }
        loadingCard = new VodafoneGenericCard(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        v.addView(loadingCard, layoutParams);
        loadingCard.showLoading(true);
    }

    private void hideLoadingCard() {
        ancomOffersList.setVisibility(View.VISIBLE);
        Log.d(TAG, "hideLoadingCard: ");
        if (loadingCard != null && loadingCard.getVisibility() == View.VISIBLE) {
            v.removeView(loadingCard);
        }
    }

    View.OnClickListener errorRefreshListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getAncomOffers(VodafoneController.getInstance(), VodafoneController.getInstance().getUserProfile().getMsisdn());
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        getAncomOffers(getContext(), UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn());
        callForAdobeTarget(AdobePageNamesConstants.PG_OFFERS_ACCEPTED);
    }

    private String formatMsisdn(String msisdn) {
        String formattedMsisdn = msisdn;
        if (msisdn.startsWith("07"))
            formattedMsisdn = "4" + msisdn;

        return formattedMsisdn;
    }

    private void openWebView(SpannableString spannableString, int start, int end, TextView textView, final String url) {

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                //Tealium Track Event
                Map<String, Object> tealiumMapEventTC = new HashMap(6);
                tealiumMapEventTC.put("screen_name","accepted offers");
                tealiumMapEventTC.put("event_name","mcare:accepted offers:link:T&C");
                tealiumMapEventTC.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("event_name", tealiumMapEventTC);

                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put("screen_name","accepted offers");
                tealiumMapEvent.put("event_name","mcare:accepted offers:link:clauze specifice contractului la distanta");
                tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("event_name", tealiumMapEvent);

                Intent intent = new Intent(VodafoneController.getInstance(), WebviewActivity.class);
                intent.putExtra(WebviewActivity.KEY_URL, url);
                getContext().startActivity(intent);
            }
        };

        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableString);
        textView.setHighlightColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.pay_bill_bold_text_color));
        textView.setLinkTextColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.pay_bill_bold_text_color));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setTextColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.pay_bill_bold_text_color));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void makeAdobeRequest() {
        callForAdobeTarget(AdobePageNamesConstants.PG_OFFERS_ACCEPTED);
    }
}

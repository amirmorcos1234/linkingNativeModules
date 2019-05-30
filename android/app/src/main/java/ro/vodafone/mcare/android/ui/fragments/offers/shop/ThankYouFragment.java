package ro.vodafone.mcare.android.ui.fragments.offers.shop;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.offers.GeneralCardsWithTitleBodyAndTwoButtons;
import ro.vodafone.mcare.android.card.settings.SimDetailsCard;
import ro.vodafone.mcare.android.client.model.realm.system.OffersLabels;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsItem;
import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsUtils;
import ro.vodafone.mcare.android.utils.navigation.RedirectFragmentListener;


/**
 * Created by Serban Radulescu on 5/4/2017.
 */

public class ThankYouFragment extends OffersFragment {

    public static final String TAG = ThankYouFragment.class.getSimpleName();
    private static final String PRICE_PLAN_KEY = "pricePlanValue";
    LinearLayout rootView;
    NavigationHeader navigationHeader;
    SimDetailsCard infoCard;
    GeneralCardsWithTitleBodyAndTwoButtons historyCard;

    public static ThankYouFragment newInstance(FirebaseAnalyticsItem pricePlanValue) {
        ThankYouFragment thankYouFragment = new ThankYouFragment();

        Bundle args = new Bundle();
        args.putSerializable(PRICE_PLAN_KEY, pricePlanValue);
        thankYouFragment.setArguments(args);

        return thankYouFragment;
    }

    public ThankYouFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "create view");

        rootView = (LinearLayout) inflater.inflate(R.layout.fragment_thank_you_page, null);

        ButterKnife.bind(this, rootView);

        configureHeader();
        configureBody();

        //Tealium Track View
        Map<String, Object> tealiumMapView =new HashMap(6);
        tealiumMapView.put(TealiumConstants.screen_name, TealiumConstants.retentionScreen);
        tealiumMapView.put(TealiumConstants.journey_name,TealiumConstants.retentionJourney);
        tealiumMapView.put(TealiumConstants.user_type,VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView(getClass().getSimpleName(), tealiumMapView);

        ThankYouTrackingEvent event = new ThankYouTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event8 = "event8";
        journey.getContextData().put("event8", journey.event8);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);

        logGoogleAnalytics();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.RETENTION_THANK_YOU);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "detach");
        if(navigationHeader != null) {
            navigationHeader.removeViewFromContainer();
            navigationHeader.hideBannerView();
        }
    }

    public FirebaseAnalyticsItem getPricePlanValue() {
        return getArguments() != null ? (FirebaseAnalyticsItem)getArguments().getSerializable(PRICE_PLAN_KEY) : null;
    }

    public void configureHeader(){
    	if(getActivity() == null || getContext() == null) {
    		return;
		}

        navigationHeader = ((OffersActivity) getActivity()).getNavigationHeader();
        navigationHeader.removeViewFromContainer();
        navigationHeader.hideSelectorView();
        navigationHeader.showBannerView();

        View v = (View) View.inflate(getActivity(), R.layout.order_details_header, null);

        DynamicColorImageView thankYouImageHeader = (DynamicColorImageView) v.findViewById(R.id.order_status_iv);

        VodafoneTextView thankYouHeaderLabel = (VodafoneTextView) v.findViewById(R.id.order_status_label);
        VodafoneTextView thankYouHeaderSmallLabel = (VodafoneTextView) v.findViewById(R.id.order_phone_number_label);
        View separatorLineView = (View) v.findViewById(R.id.second_separator_line);

//        separatorLineView.setVisibility(View.GONE);
        Drawable img = ContextCompat.getDrawable(getContext(),R.drawable.like_48);
        thankYouImageHeader.setContextDrawableColor(img,R.color.white);
        thankYouImageHeader.setImageDrawable(img);

        thankYouHeaderLabel.setText(OffersLabels.getThankYouHeaderLabel());
        thankYouHeaderSmallLabel.setText(OffersLabels.getThankYouHeaderSmallLabel());

        Log.d(TAG, "header");
        navigationHeader.addViewToContainer(v);
    }

    public void configureBody(){
        final String contactEmailVdf = OffersLabels.getThankYouEmailLabel();
        String finalDetailsLabel = OffersLabels.getThankYouInfoSmallLabel();

        Spannable sb = new SpannableString(finalDetailsLabel);

        int startOfEmailUnderline = finalDetailsLabel.indexOf(contactEmailVdf);
        int endOfEmailUnderline = finalDetailsLabel.indexOf(contactEmailVdf) + contactEmailVdf.length();

        sb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                final String emailAddresses[] = new String[1];
                emailAddresses[0] = contactEmailVdf;
                composeEmail(emailAddresses, null);
            }
        }, startOfEmailUnderline, endOfEmailUnderline, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        infoCard = new SimDetailsCard(getContext());
        infoCard.setTitle(OffersLabels.getThankYouInfoLabel())
                .setDetailsSpannable(sb);

        historyCard = new GeneralCardsWithTitleBodyAndTwoButtons(getContext());
        historyCard.setTitle(OffersLabels.getThankYouHistoryTitle())
                .setTitleBold()
                .setSecondaryButtonMessage(OffersLabels.getThankYouHistoryButton())
                .setSecondaryButtonClickListener(
                        new RedirectFragmentListener(getActivity(), new OrderHistoryFragment()))
                .build();

        rootView.addView(infoCard);
        rootView.addView(historyCard);
        stopLoadingDialog();
    }

    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        ActivityInfo activityInfo = intent.resolveActivityInfo(getActivity().getPackageManager(), intent.getFlags());
        if (activityInfo != null && activityInfo.exported) {
            try {
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "There are no Apps installed on this device to handle the intent!");
            }
        }
    }

    @Override
    public String getTitle() {
        return OffersLabels.getThankYouTitle();
    }

    private void logGoogleAnalytics() {
        FirebaseAnalyticsItem firebaseAnalyticsItem = getPricePlanValue();
		if(firebaseAnalyticsItem == null) {
			return;
		}

        baseFragmentCommunicationListener.sendFirebaseEvent(firebaseAnalyticsItem.getFirebaseAnalyticsEvent(),
                FirebaseAnalyticsUtils.getBundleFromParams(firebaseAnalyticsItem.getFirebaseAnalyticsParams()));
    }

    public static class ThankYouTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "retention:thank you";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"retention:thank you");


            s.prop5 = "sales:thank you";
            s.getContextData().put("prop5", s.prop5);
            s.channel = "retention in self care";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "buy options";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "task";
            s.getContextData().put("eVar19", s.eVar19);
        }
    }
}

package ro.vodafone.mcare.android.ui.fragments.yourProfile.internationalCalls;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.system.InternationalCallsLabels;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.YourProfileBaseFragment;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.YourProfileFragment;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.internationalCalls.selectMSISDN.InternationalCallsMsisdnFragment;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.TealiumHelper;

public class InternationalCallsFragment extends YourProfileFragment
{
    @Override
    protected List<View> getCards()
    {
        InternationalCallsMsisdnFragment fragmentDestinationNumber = InternationalCallsMsisdnFragment.newInstance(InternationalCallsMsisdnFragment.NumberType.RECIPIENT_NUMBER);
        InternationalCallsMsisdnFragment fragmentCallerAndDestination = InternationalCallsMsisdnFragment.newInstance(InternationalCallsMsisdnFragment.NumberType.RECIPIENT_NUMBER, InternationalCallsMsisdnFragment.NumberType.CALLER_NUMBER);

        return Arrays.asList(getCard(fragmentDestinationNumber, InternationalCallsLabels.yourAccountCardTitle(), InternationalCallsLabels.yourAccountCardSubtitle()),
                getCard(fragmentCallerAndDestination, InternationalCallsLabels.otherMsisdnCardTitle(), InternationalCallsLabels.otherMsisdnCardSubtitle()));
    }

    @Override
    protected void showAliasIfAvailable()
    {
        if (activityInterface != null)
        {
            activityInterface.getNavigationHeader().getClientCodeLabel().setVisibility(View.GONE);
            activityInterface.getNavigationHeader().hideSelectorView();
        }
    }

    protected View getCard(final InternationalCallsMsisdnFragment fragment, final String cardTitle, String cardSubtext)
    {
        View accountSpecialistCard = LayoutInflater.from(getContext()).inflate(R.layout.settings_card_arrow, card_selector_container, false);

        ((TextView) accountSpecialistCard.findViewById(R.id.cardTitle)).setText(cardTitle);
        ((VodafoneTextView) accountSpecialistCard.findViewById(R.id.cardTitle)).setFont(VodafoneTextView.TextStyle.VODAFONE_RGBD);
        ((TextView) accountSpecialistCard.findViewById(R.id.cardSubtext)).setText(cardSubtext);
        accountSpecialistCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    activityInterface.attachFragment(fragment);
                    TealiumHelper.tealiumTrackEvent(InternationalCallsFragment.class.getSimpleName(), cardTitle,TealiumConstants.internationalCallsScreenName,"button=");
                } catch (Exception e) {
                }
            }
        });

        return accountSpecialistCard;
    }

    @Override
    public String getTitle()
    {
        return InternationalCallsLabels.internationalCallsCardTitle();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InternationalCallingTrackingEvent event = new InternationalCallingTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
        TealiumHelper.tealiumTrackView(InternationalCallsFragment.class.getSimpleName(), TealiumConstants.yourProfileJourney,TealiumConstants.internationalCallsScreenName);
    }

    public static class InternationalCallingTrackingEvent extends TrackingEvent{
        @Override
        protected void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);
            if (getErrorMessage() != null) {
                s.event11 = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "international calling rates";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "international calling rates");
            s.channel = "international calling rates";
            s.getContextData().put("&&channel", s.channel);
            s.prop21 = "mcare:" + "international calling rates";
            s.getContextData().put("prop21", s.prop21);
            s.eVar5 = "content";
            s.getContextData().put("eVar5", s.eVar5);
        }
    }
}

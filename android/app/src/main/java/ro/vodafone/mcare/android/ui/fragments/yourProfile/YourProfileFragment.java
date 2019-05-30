package ro.vodafone.mcare.android.ui.fragments.yourProfile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.IonController;
import ro.vodafone.mcare.android.client.model.realm.system.AccountSpecialistLabels;
import ro.vodafone.mcare.android.client.model.realm.system.UserRequestsLabels;
import ro.vodafone.mcare.android.client.model.realm.system.YourProfileLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.PrivateUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResSub;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.AuthorisedPersonUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.ChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.DelegatedChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.PowerUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.SubUserMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessEbuUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidHighAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidsLowAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.interfaces.ActivityFragmentInterface;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.UserRequests.UserRequestsFragment;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.internationalCalls.InternationalCallsFragment;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.ion.unlimited.base.UnlimitedBaseFragment;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.ion.unlimited.italy.UnlimitedItalyFragment;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by George B. on 7/13/2017.
 */

public class YourProfileFragment extends YourProfileBaseFragment
{
    private static final String TAG = YourProfileFragment.class.getCanonicalName();

    @BindView(R.id.card_selector_container)
    protected LinearLayout card_selector_container;

    protected ActivityFragmentInterface activityInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ActivityFragmentInterface)
            activityInterface = (ActivityFragmentInterface) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_your_profile, container, false);
        ButterKnife.bind(this, v);

        int defaultPadding = ScreenMeasure.dpToPx(10);
        card_selector_container.setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding);

        for (View view : getCards())
            card_selector_container.addView(view);

        YourProfileTrackingEvent event = new YourProfileTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TealiumHelper.tealiumTrackView(YourProfileFragment.class.getSimpleName(), TealiumConstants.yourProfileJourney,TealiumConstants.yourProfileScreenName);
    }

    protected List<View> getCards() {
        List<View> cards = new ArrayList<>();

        User user = VodafoneController.getInstance().getUser();

        if (user instanceof ChooserUser || user instanceof DelegatedChooserUser || user instanceof AuthorisedPersonUser)
            cards.add(getCard(
                    AccountSpecialistFragment.class, AccountSpecialistLabels.getAccountSpecialistCardTitle(), ""));

        if (user instanceof AuthorisedPersonUser || user instanceof ChooserUser || user instanceof DelegatedChooserUser)
            cards.add(getCard(UserRequestsFragment.class,
                    UserRequestsLabels.getUserRequestsCardTitle(), UserRequestsLabels.getUserRequestsCardSubTitle()));

        if (IonController.shouldPerformUnlimitedIonPage())
            cards.add(getUnlimitedIonCard(YourProfileLabels.getUnlimitedIonCardTitle(), YourProfileLabels.getUnlimitedIonCardSubTitle()));

        if ((user instanceof PrepaidUser || user instanceof PrivateUser || user instanceof ResCorp || user instanceof ResSub
                || user instanceof ChooserUser || user instanceof DelegatedChooserUser || user instanceof PowerUser || user instanceof AuthorisedPersonUser || user instanceof SubUserMigrated)
				&& !(user instanceof SeamlessPostPaidHighAccess || user instanceof SeamlessPrepaidHybridUser || user instanceof SeamlessEbuUser || user instanceof SeamlessPostPaidsLowAccess || user instanceof SeamlessPrepaidUser))
        {
            cards.add(getCard(
                    InternationalCallsFragment.class, YourProfileLabels.getICRCardTitle(), ""));     //InternationalCallsLabels.yourAccountCardSubtitle()
        }



        return cards;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (activityInterface != null) {
            activityInterface.getNavigationHeader().setDefaultAvatarWithUserDataAndNoIdentity();
            activityInterface.getNavigationHeader().setTitle(getTitle());
        }

        showAliasIfAvailable();
    }

    @Override
    public String getTitle() {
        return AccountSpecialistLabels.getYourProfileTitle();
    }

    protected void showAliasIfAvailable(){
        String alias = UserSelectedMsisdnBanController.getInstance().getAliasFromUserDataProfile();

        if (activityInterface != null && (alias == null || alias.equals("")))
            activityInterface.getNavigationHeader().getClientCodeLabel().setVisibility(View.INVISIBLE);
    }

    protected View getCard(final Class<? extends YourProfileBaseFragment> fragment, final String cardTitle, String cardSubtext)
    {
        View resultCard = LayoutInflater.from(getContext()).inflate(R.layout.settings_card_arrow, card_selector_container, false);

        ((TextView) resultCard.findViewById(R.id.cardTitle)).setText(cardTitle);
        ((TextView) resultCard.findViewById(R.id.cardSubtext)).setText(cardSubtext);
        resultCard.findViewById(R.id.cardSubtext).setVisibility(TextUtils.isEmpty(cardSubtext) ? View.GONE : View.VISIBLE);
        resultCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    activityInterface.attachFragment(fragment.newInstance());
                    TealiumHelper.tealiumTrackEvent(YourProfileFragment.class.getSimpleName(), cardTitle, TealiumConstants.yourProfileScreenName, "button=");
                } catch (Exception e) {
                    Log.e(TAG, "getCard: " + e.getMessage());
                }
            }
        });

        return resultCard;
    }

    private View getUnlimitedIonCard(final String cardTitle, String cardSubtext) {
        View unlimitedIonCard = LayoutInflater.from(getContext()).inflate(R.layout.settings_card_arrow, card_selector_container, false);

        ((TextView) unlimitedIonCard.findViewById(R.id.cardTitle)).setText(cardTitle);
        ((TextView) unlimitedIonCard.findViewById(R.id.cardSubtext)).setText(cardSubtext);
        unlimitedIonCard.findViewById(R.id.cardSubtext).setVisibility(TextUtils.isEmpty(cardSubtext) ? View.GONE : View.VISIBLE);

        unlimitedIonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UnlimitedBaseFragment fragment = new UnlimitedItalyFragment();
                    activityInterface.attachFragment(fragment);
                    TealiumHelper.tealiumTrackEvent(YourProfileFragment.class.getSimpleName(), cardTitle, TealiumConstants.yourProfileScreenName, "button=");
                } catch (Exception e) {
                    Log.e(TAG, "getCard: " + e.getMessage());
                }
            }
        });

        return unlimitedIonCard;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (activityInterface != null)
            activityInterface.getNavigationHeader().hideSelectorViewWithoutTriangle();
        activityInterface = null;
    }

    public static class YourProfileTrackingEvent extends TrackingEvent{
        @Override
        protected void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);
            if (getErrorMessage() != null) {
                s.event11 = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "your profile";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "your profile");
            s.channel = "your profile";
            s.getContextData().put("&&channel", s.channel);
            s.prop21 = "mcare:" + "your profile";
            s.getContextData().put("prop21", s.prop21);
            s.eVar5 = "content";
            s.getContextData().put("eVar5", s.eVar5);
        }
    }
}

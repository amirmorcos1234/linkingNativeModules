package ro.vodafone.mcare.android.ui.fragments.yourProfile;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.identity.AccountSpecialist;
import ro.vodafone.mcare.android.client.model.identity.EntityDetailsSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.AccountSpecialistLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.UserIdentitiesService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.yourProfile.YourProfileActivity;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by George B. on 7/10/2017.
 */

public class AccountSpecialistFragment extends YourProfileBaseFragment {

    public static final String TAG = AccountSpecialist.class.getCanonicalName();
    LinearLayout rootView;
    String dataAccountName;
    String voiceAccountName;
    String dataAccountEmail;
    String voiceAccountEmail;
    boolean hasData = false;
    boolean hasVoice = false;
    NavigationHeader navigationHeader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationHeader = ((YourProfileActivity) getActivity()).getNavigationHeader();
        navigationHeader.setTitle(getTitle());
        //de mutat in onCreateView
        //shouldShowMsisdnSelector = false;
        AccountSpecialistTrackingEvent event = new AccountSpecialistTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        shouldShowMsisdnSelector = false;
        super.onCreateView(inflater, container, savedInstanceState);
        return emptyLayout();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TealiumHelper.tealiumTrackView(AccountSpecialistFragment.class.getSimpleName(),
                TealiumConstants.yourProfileJourney,TealiumConstants.accountSpecialistScreenName);
    }

    @Override
    public void onStart() {
        super.onStart();
        rootView = ((LinearLayout) getView());
        showLoadingDialog();
        init();

    }

    void init() {

        String vfodsCid = "null";

        if (EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null && EbuMigratedIdentityController.getInstance().getSelectedIdentity().getVfOdsCid() != null)
            vfodsCid = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getVfOdsCid();
        Log.i("vfodsCid", "vfodsCid: " + vfodsCid);

        UserIdentitiesService userIdentityService = new UserIdentitiesService(getContext());

        userIdentityService.getUserDetails(vfodsCid).subscribe(new RequestSessionObserver<GeneralResponse<EntityDetailsSuccess>>() {

            @Override
            public void onNext(GeneralResponse<EntityDetailsSuccess> userEntitiesSuccessGeneralResponse) {
                stopLoadingDialog();

                if (userEntitiesSuccessGeneralResponse.getTransactionStatus() == 0) {
                    if (userEntitiesSuccessGeneralResponse.getTransactionSuccess() != null) {

                        D.i("userEntitiesSuccessGeneralResponse.getTransactionSuccess() + " + userEntitiesSuccessGeneralResponse.getTransactionSuccess());


                        EntityDetailsSuccess userDetailsSuccess = (EntityDetailsSuccess) userEntitiesSuccessGeneralResponse.getTransactionSuccess();
                        if (userDetailsSuccess.getDataSpecialist() != null) {
                            hasData = true;
                            dataAccountName = userDetailsSuccess.getDataSpecialist().getFirstName() + " " + userDetailsSuccess.getDataSpecialist().getLastName();
                            dataAccountEmail = userDetailsSuccess.getDataSpecialist().getEmail();
                            Log.i(TAG, "dataAccountName: " + dataAccountName);
                        }
                        if (userDetailsSuccess.getVoiceSpecialist() != null) {
                            hasVoice = true;
                            voiceAccountName = userDetailsSuccess.getVoiceSpecialist().getFirstName() + " " + userDetailsSuccess.getVoiceSpecialist().getLastName();
                            voiceAccountEmail = userDetailsSuccess.getVoiceSpecialist().getEmail();
                            Log.i(TAG, "voiceAccountName: " + voiceAccountName);
                        }

                        rootView.addView(configureLayout());
                        setUpCards();


                    } else {
                        D.e("STATUS == 0 SUCCESS ==NULL");
                    }
                } else {
                    //status == 1 sau 2
                    D.e("status == 1 sau 2");
                    inflateErrorLayout();

                }


            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);

                stopLoadingDialog();

                D.e("error is == " + e);
                inflateErrorLayout();
            }

            @Override
            public void onCompleted() {
                D.w("onCompleted");
            }
        });


    }

    private void inflateErrorLayout() {
        rootView.removeAllViews();

        VodafoneGenericCard errorCard = new VodafoneGenericCard(getActivity());
        errorCard.showError(true, SettingsLabels.getSmallErrorMessage());

        rootView.addView(errorCard);
        errorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                init();
            }
        });
    }

    View configureLayout() {
        if (rootView != null) {
            rootView.removeAllViews();
        }
        View v = View.inflate(getContext(), R.layout.fragment_account_specialist, null);
        return v;
    }

    void setEmailClickListener(final TextView emailToBeClicked) {

        emailToBeClicked.setPaintFlags(emailToBeClicked.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        emailToBeClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String[] emailStringArray = new String[1];
                emailStringArray[0] = emailToBeClicked.getText().toString();

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, emailStringArray);
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }


            }
        });

    }

    View emptyLayout() {
        LinearLayout v = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        v.setLayoutParams(params);
        return v;

    }


    private void setUpCards() {
        if (!hasData && !hasVoice) {
            ((LinearLayout) getView().findViewById(R.id.voice_container)).setVisibility(View.INVISIBLE);

            ((VodafoneTextView) getView().findViewById(R.id.data_account_name)).setVisibility(View.INVISIBLE);
            ((VodafoneTextView) getView().findViewById(R.id.data_account_email)).setVisibility(View.INVISIBLE);


            ((VodafoneTextView) getView().findViewById(R.id.data_account_label)).setText(AccountSpecialistLabels.getDataAccountLabelSetText());
            ((VodafoneTextView) getView().findViewById(R.id.data_account_label)).setPadding(0, 0, 0, ScreenMeasure.dpToPx(15));

        } else {

            if (hasData) {
                ((VodafoneTextView) getView().findViewById(R.id.data_account_name)).setText(dataAccountName);
                ((VodafoneTextView) getView().findViewById(R.id.data_account_name)).setTypeface(Typeface.DEFAULT_BOLD);
                ((VodafoneTextView) getView().findViewById(R.id.data_account_email)).setText(dataAccountEmail);

                VodafoneTextView data_account_email = (VodafoneTextView) getView().findViewById(R.id.data_account_email);

                String emailAddress = (String) data_account_email.getText();
                data_account_email.setText(emailAddress);

                setEmailClickListener(data_account_email);
            } else
                ((LinearLayout) getView().findViewById(R.id.data_container)).setVisibility(View.GONE);


            if (hasVoice) {
                VodafoneTextView voice_account_email = (VodafoneTextView) getView().findViewById(R.id.voice_account_email);

                ((VodafoneTextView) getView().findViewById(R.id.voice_account_name)).setText(voiceAccountName);
                ((VodafoneTextView) getView().findViewById(R.id.voice_account_name)).setTypeface(Typeface.DEFAULT_BOLD);
                ((VodafoneTextView) getView().findViewById(R.id.voice_account_email)).setText(voiceAccountEmail);

                String emailAddress = (String) voice_account_email.getText();
                voice_account_email.setText(emailAddress);

                setEmailClickListener(voice_account_email);

            } else
                ((LinearLayout) getView().findViewById(R.id.voice_container)).setVisibility(View.GONE);
        }

        ((LinearLayout) getView().findViewById(R.id.account_specialist_cards_linear_layout)).setVisibility(View.VISIBLE);

    }

    public String getTitle() {
        return AccountSpecialistLabels.getAccountSpecialistTitle();
    }

    public static class AccountSpecialistTrackingEvent extends TrackingEvent{
        @Override
        protected void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);
            if (getErrorMessage() != null) {
                s.event11 = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "account specialist";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "account specialist");
            s.channel = "account specialist";
            s.getContextData().put("&&channel", s.channel);
            s.prop21 = "mcare:" + "account specialist";
            s.getContextData().put("prop21", s.prop21);
            s.eVar5 = "content";
            s.getContextData().put("eVar5", s.eVar5);
        }
    }
}

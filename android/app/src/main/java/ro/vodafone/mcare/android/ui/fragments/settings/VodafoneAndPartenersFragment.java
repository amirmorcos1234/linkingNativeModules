package ro.vodafone.mcare.android.ui.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.GdprController;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.AuthorisedPersonUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.ChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.DelegatedChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.PowerUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.SubUserMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.widget.SwitchButton.SwitchButton;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by cosmin deliu on 3/31/2018.
 */

public class VodafoneAndPartenersFragment extends PermissionDetailsFragment {

    // -> for Basic Profile, Online Data, Network Data logic
    protected SwitchButton basicProfileCustServiceDataSwitch;
    protected RelativeLayout specifyBasicProfileCustServiceDataSwitch;
    protected ImageView cardSwitchButtonMiddleImageProfile;
    protected LinearLayout tapContainerMiddleImageProfile;
    protected View tapLeftMiddleImageProfile;
    protected View tapRightMiddleImageProfile;

    protected SwitchButton advancedProfileOnlineDataSwitch;
    protected RelativeLayout specifyAdvancedProfileOnlineDataSwitch;
    protected ImageView cardSwitchButtonMiddleImageOnlineData;
    protected LinearLayout tapContainerMiddleImageOnlineData;
    protected View tapLeftMiddleImageOnlineData;
    protected View tapRightMiddleImageOnlineData;

    protected SwitchButton advancedProfileNetworkDataSwitch;
    protected RelativeLayout specifyAdvancedProfileNetworkDataSwitch;
    protected ImageView cardSwitchButtonMiddleImageNetworkData;
    protected LinearLayout tapContainerMiddleImageNetworkData;
    protected View tapLeftMiddleImageNetworkData;
    protected View tapRightMiddleImageNetworkData;

    // -> Instructional cards
    protected View instructionalCardProfile;
    protected View instructionalCardOnlineData;
    protected View instructionalCardNetworkData;
    protected View instructionalCardCommercial;
    protected View instructionalCardSurvey;

    protected VodafoneTextView instructionalCardProfileText;
    protected VodafoneTextView instructionalCardOnlineDataText;
    protected VodafoneTextView instructionalCardNetworkDataText;
    protected VodafoneTextView instructionalCardCommercialText;
    protected VodafoneTextView instructionalCardSurveyText;


    @Override
    protected void createCards() {
        parent.removeAllViews();
        advancedProfileNetworkDataSwitch = null;
        advancedProfileOnlineDataSwitch = null;
        inflateVodafoneCard();
    }

    private void inflateVodafoneCard() {
        inflateDescriptionText();
        inflateCreateProfileCard();
        inflateNetworkDataCard();
        inflateOnlineDataCard();
        inflateCommercialCommunicationsCard();
        inflateSurveyCard();
        inflateSaveButton();
    }

    private void inflateDescriptionText() {

        if (getActivity() == null)
            return;

        View v = View.inflate(getActivity(), R.layout.permissions_description_text, null);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                //TODO code cleanup - getResources need fragment to be attached. Please verify if fragment is alwais attached when calling getResources
                //TODO - as resources is kept on activity
                Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+ getResources().getString(R.string.link_from_instructional_text)));
                startActivity(intent);
            }

            @Override
            public void updateDrawState(final TextPaint textPaint) {
                textPaint.setColor(getResources().getColor(R.color.pay_bill_bold_text_color));
                textPaint.setUnderlineText(true);
            }
        };

        ((VodafoneTextView) v.findViewById(R.id.electronicBillBody)).setText(SettingsLabels.getPermissionsInstructionalText());
        GdprController.makeLink((VodafoneTextView) v.findViewById(R.id.electronicBillBody),
                getResources().getString(R.string.link_from_instructional_text),
                clickableSpan);

        parent.addView(v);

    }

    private void inflateCommercialCommunicationsCard() {
        if (getActivity() == null)
            return;

        final View v = View.inflate(getActivity(), R.layout.settings_card_permissions, null);

        inflateInstructionalCard(SettingsLabels.getInstructionalText1(), COMMERCIAL_COMMUNICATIONS_TYPE);

        if (isSwitchChecked(SMS_MMS_PUSH) && isSwitchChecked(EMAIL)
                && isSwitchChecked(POST) && isSwitchChecked(OUTBOUND_CALL)) {
            instructionalCardCommercial.setVisibility(View.GONE);
        }

        v.findViewById(R.id.cardSwitchButton).setVisibility(View.GONE);
        v.findViewById(R.id.itemsContainer).setVisibility(View.VISIBLE);
        v.findViewById(R.id.seeDetailsContainer).setVisibility(View.GONE);

        ((VodafoneTextView) v.findViewById(R.id.cardTitle)).setText(SettingsLabels.getCommercialCommunicationTitle());
        ((VodafoneTextView) v.findViewById(R.id.cardSubtext)).setText(card_type.equals(PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                ? SettingsLabels.getCommercialCommunicationSubtextVodafone() :SettingsLabels.getCommercialCommunicationSubtextParteners());

        ((VodafoneTextView) v.findViewById(R.id.itemTitle1)).setText(SettingsLabels.getSmsMmsPushTitle());
        ((VodafoneTextView) v.findViewById(R.id.itemTitle2)).setText(SettingsLabels.getEmailTitle());
        ((VodafoneTextView) v.findViewById(R.id.itemTitle3)).setText(SettingsLabels.getPostTitle());
        ((VodafoneTextView) v.findViewById(R.id.itemTitle4)).setText(SettingsLabels.getOutboundCallTitle());

        final SwitchButton smsMmsPushSwitch = v.findViewById(R.id.itemSwitchButton1);
        final SwitchButton emailSwitch = v.findViewById(R.id.itemSwitchButton2);
        final SwitchButton postSwitch = v.findViewById(R.id.itemSwitchButton3);
        final SwitchButton outboundCallSwitch = v.findViewById(R.id.itemSwitchButton4);

        RelativeLayout specifySmsMmsPushSwitch = v.findViewById(R.id.itemSwitchButton1Container);
        RelativeLayout specifyEmailSwitch = v.findViewById(R.id.itemSwitchButton2Container);
        RelativeLayout specifyPostSwitch = v.findViewById(R.id.itemSwitchButton3Container);
        RelativeLayout specifyOutboundCallSwitch = v.findViewById(R.id.itemSwitchButton4Container);

        ImageView itemSwitchButton1MiddleImage = v.findViewById(R.id.itemSwitchButton1MiddleImage);
        ImageView itemSwitchButton2MiddleImage = v.findViewById(R.id.itemSwitchButton2MiddleImage);
        ImageView itemSwitchButton3MiddleImage = v.findViewById(R.id.itemSwitchButton3MiddleImage);
        ImageView itemSwitchButton4MiddleImage = v.findViewById(R.id.itemSwitchButton4MiddleImage);

        smsMmsPushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setGdprPermissionWhenSwitchChanged(isChecked, SMS_MMS_PUSH);

                boolean instructionalCardVisibility = isChecked && isSwitchChecked(EMAIL)
                        && isSwitchChecked(POST) && isSwitchChecked(OUTBOUND_CALL)
                        && !checkForDisabledSwitchesCases(COMMERCIAL_COMMUNICATIONS_TYPE);

                instructionalCardCommercial.setVisibility(instructionalCardVisibility ? View.GONE : View.VISIBLE);
            }
        });

        emailSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setGdprPermissionWhenSwitchChanged(isChecked, EMAIL);

                boolean instructionalCardVisibility = isChecked && isSwitchChecked(SMS_MMS_PUSH)
                        && isSwitchChecked(POST) && isSwitchChecked(OUTBOUND_CALL)
                        && !checkForDisabledSwitchesCases(COMMERCIAL_COMMUNICATIONS_TYPE);

                instructionalCardCommercial.setVisibility(instructionalCardVisibility ? View.GONE : View.VISIBLE);
            }
        });

        postSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setGdprPermissionWhenSwitchChanged(isChecked, POST);

                boolean instructionalCardVisibility = isChecked && isSwitchChecked(SMS_MMS_PUSH)
                        && isSwitchChecked(EMAIL) && isSwitchChecked(OUTBOUND_CALL)
                        && !checkForDisabledSwitchesCases(COMMERCIAL_COMMUNICATIONS_TYPE);

                instructionalCardCommercial.setVisibility(instructionalCardVisibility ? View.GONE : View.VISIBLE);
            }
        });

        outboundCallSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setGdprPermissionWhenSwitchChanged(isChecked, OUTBOUND_CALL);

                boolean instructionalCardVisibility = isChecked && isSwitchChecked(SMS_MMS_PUSH)
                        && isSwitchChecked(EMAIL) && isSwitchChecked(POST)
                        && !checkForDisabledSwitchesCases(COMMERCIAL_COMMUNICATIONS_TYPE);

                instructionalCardCommercial.setVisibility(instructionalCardVisibility ? View.GONE : View.VISIBLE);
            }
        });

        if (checkForDisabledSwitchesCases(COMMERCIAL_COMMUNICATIONS_TYPE)) {

            instructionalCardCommercial.setVisibility(View.VISIBLE);

            smsMmsPushSwitch.setChecked(isSwitchChecked(SMS_MMS_PUSH));
            emailSwitch.setChecked(isSwitchChecked(EMAIL));
            postSwitch.setChecked(isSwitchChecked(POST));
            outboundCallSwitch.setChecked(isSwitchChecked(OUTBOUND_CALL));

            setSpecifyAndSwitchVisibility(specifySmsMmsPushSwitch, smsMmsPushSwitch,
                    itemSwitchButton1MiddleImage, (LinearLayout) v.findViewById(R.id.tapContainer1),
                    v.findViewById(R.id.tapLeftItemSwitchButton1),
                    v.findViewById(R.id.tapRightItemSwitchButton1), SMS_MMS_PUSH, false);

            setSpecifyAndSwitchVisibility(specifyEmailSwitch, emailSwitch,
                    itemSwitchButton2MiddleImage, (LinearLayout) v.findViewById(R.id.tapContainer2),
                    v.findViewById(R.id.tapLeftItemSwitchButton2),
                    v.findViewById(R.id.tapRightItemSwitchButton2), EMAIL, false);

            setSpecifyAndSwitchVisibility(specifyPostSwitch, postSwitch,
                    itemSwitchButton3MiddleImage, (LinearLayout) v.findViewById(R.id.tapContainer3),
                    v.findViewById(R.id.tapLeftItemSwitchButton3),
                    v.findViewById(R.id.tapRightItemSwitchButton3), POST, false);

            setSpecifyAndSwitchVisibility(specifyOutboundCallSwitch, outboundCallSwitch,
                    itemSwitchButton4MiddleImage, (LinearLayout) v.findViewById(R.id.tapContainer4),
                    v.findViewById(R.id.tapLeftItemSwitchButton4),
                    v.findViewById(R.id.tapRightItemSwitchButton4), OUTBOUND_CALL, false);

        } else {

            setSpecifyAndSwitchVisibility(specifySmsMmsPushSwitch, smsMmsPushSwitch,
                    itemSwitchButton1MiddleImage, (LinearLayout) v.findViewById(R.id.tapContainer1),
                    v.findViewById(R.id.tapLeftItemSwitchButton1),
                    v.findViewById(R.id.tapRightItemSwitchButton1), SMS_MMS_PUSH, true);

            setSpecifyAndSwitchVisibility(specifyEmailSwitch, emailSwitch,
                    itemSwitchButton2MiddleImage, (LinearLayout) v.findViewById(R.id.tapContainer2),
                    v.findViewById(R.id.tapLeftItemSwitchButton2),
                    v.findViewById(R.id.tapRightItemSwitchButton2), EMAIL, true);

            setSpecifyAndSwitchVisibility(specifyPostSwitch, postSwitch,
                    itemSwitchButton3MiddleImage, (LinearLayout) v.findViewById(R.id.tapContainer3),
                    v.findViewById(R.id.tapLeftItemSwitchButton3),
                    v.findViewById(R.id.tapRightItemSwitchButton3), POST, true);

            setSpecifyAndSwitchVisibility(specifyOutboundCallSwitch, outboundCallSwitch,
                    itemSwitchButton4MiddleImage, (LinearLayout) v.findViewById(R.id.tapContainer4),
                    v.findViewById(R.id.tapLeftItemSwitchButton4),
                    v.findViewById(R.id.tapRightItemSwitchButton4), OUTBOUND_CALL, true);

        }

        parent.addView(v);
    }

    private void inflateSurveyCard() {
        if (getActivity() == null)
            return;

        View v = View.inflate(getActivity(), R.layout.settings_card_permissions, null);

        inflateInstructionalCard(SettingsLabels.getInstructionalText1(), SURVEY_TYPE);

        if (isSwitchChecked(SURVEY_TYPE)) {
            instructionalCardSurvey.setVisibility(View.GONE);
        }

        v.findViewById(R.id.cardSwitchButton).setVisibility(View.VISIBLE);
        v.findViewById(R.id.itemsContainer).setVisibility(View.GONE);

        ((VodafoneTextView) v.findViewById(R.id.cardTitle)).setText(SettingsLabels.getSurveyTitle());
        ((VodafoneTextView) v.findViewById(R.id.cardSubtext)).setText(card_type.equals(PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                ? SettingsLabels.getSurveySubtextVodafone() : SettingsLabels.getSurveySubtextParteners());

        setExpandableView(v, card_type.equals(PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                ? SettingsLabels.getSurveySeeDetailsVodafone() : SettingsLabels.getSurveySeeDetailsParteners());

        final SwitchButton surveyCategorySwitch = v.findViewById(R.id.cardSwitchButton);

        RelativeLayout specifySurveyCategorySwitch = v.findViewById(R.id.cardSwitchButtonContainer);

        ImageView cardSwitchButtonMiddleImage = v.findViewById(R.id.cardSwitchButtonMiddleImage);

        surveyCategorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setGdprPermissionWhenSwitchChanged(isChecked, SURVEY_TYPE);

                boolean instructionalCardVisibility = isChecked && !checkForDisabledSwitchesCases(SURVEY_TYPE);

                instructionalCardSurvey.setVisibility(instructionalCardVisibility ? View.GONE : View.VISIBLE);
            }
        });

        if (checkForDisabledSwitchesCases(SURVEY_TYPE)) {

            instructionalCardSurvey.setVisibility(View.VISIBLE);

            setSpecifyAndSwitchVisibility(specifySurveyCategorySwitch, surveyCategorySwitch,
                    cardSwitchButtonMiddleImage, (LinearLayout) v.findViewById(R.id.tapContainer),
                    v.findViewById(R.id.tapLeftCardSwitchButton),
                    v.findViewById(R.id.tapRightCardSwitchButton), SURVEY_TYPE, false);

        } else {

            setSpecifyAndSwitchVisibility(specifySurveyCategorySwitch, surveyCategorySwitch,
                    cardSwitchButtonMiddleImage, (LinearLayout) v.findViewById(R.id.tapContainer),
                    v.findViewById(R.id.tapLeftCardSwitchButton),
                    v.findViewById(R.id.tapRightCardSwitchButton), SURVEY_TYPE, true);

        }

        parent.addView(v);
    }

    private void inflateCreateProfileCard() {
        if (getActivity() == null)
            return;

        View v = View.inflate(getActivity(), R.layout.settings_card_permissions, null);

        inflateInstructionalCard(SettingsLabels.getInstructionalText1(), CREATE_PROFILE_TYPE);

        if (isSwitchChecked(CREATE_PROFILE_TYPE)) {
            instructionalCardProfile.setVisibility(View.GONE);
        }

        v.findViewById(R.id.cardSwitchButton).setVisibility(View.VISIBLE);
        v.findViewById(R.id.itemsContainer).setVisibility(View.GONE);

        ((VodafoneTextView) v.findViewById(R.id.cardTitle)).setText(SettingsLabels.getBasicProfileTitle());
        ((VodafoneTextView) v.findViewById(R.id.cardSubtext)).setText(card_type.equals(PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                ? SettingsLabels.getBasicProfileSubtextVodafone() : SettingsLabels.getBasicProfileSubtextParteners());

        setExpandableView(v, card_type.equals(PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                ? SettingsLabels.getBasicProfileSeeDetailsVodafone() : SettingsLabels.getBasicProfileSeeDetailsParteners());

        basicProfileCustServiceDataSwitch = v.findViewById(R.id.cardSwitchButton);
        specifyBasicProfileCustServiceDataSwitch = v.findViewById(R.id.cardSwitchButtonContainer);
        cardSwitchButtonMiddleImageProfile = v.findViewById(R.id.cardSwitchButtonMiddleImage);
        tapContainerMiddleImageProfile = v.findViewById(R.id.tapContainer);
        tapLeftMiddleImageProfile = v.findViewById(R.id.tapLeftCardSwitchButton);
        tapRightMiddleImageProfile = v.findViewById(R.id.tapRightCardSwitchButton);

        basicProfileCustServiceDataSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setGdprPermissionWhenSwitchChanged(isChecked, CREATE_PROFILE_TYPE);
                boolean instructionalCardVisibility = isChecked && !checkForDisabledSwitchesCases(CREATE_PROFILE_TYPE);
                instructionalCardProfile.setVisibility(instructionalCardVisibility ? View.GONE : View.VISIBLE);
                manageAdvancedProfilingCases(isChecked);
            }
        });

        if (checkForDisabledSwitchesCases(CREATE_PROFILE_TYPE)) {

            instructionalCardProfile.setVisibility(View.VISIBLE);

            setSpecifyAndSwitchVisibility(specifyBasicProfileCustServiceDataSwitch, basicProfileCustServiceDataSwitch,
                    cardSwitchButtonMiddleImageProfile, tapContainerMiddleImageProfile,
                    tapLeftMiddleImageProfile,
                    tapRightMiddleImageProfile,CREATE_PROFILE_TYPE, false);


        } else {
            setSpecifyAndSwitchVisibility(specifyBasicProfileCustServiceDataSwitch, basicProfileCustServiceDataSwitch,
                    cardSwitchButtonMiddleImageProfile, tapContainerMiddleImageProfile,
                    tapLeftMiddleImageProfile,
                    tapRightMiddleImageProfile, CREATE_PROFILE_TYPE, true);
        }

        parent.addView(v);
    }

    private void manageAdvancedProfilingCases(boolean isChecked) {
        if (advancedProfileNetworkDataSwitch != null && advancedProfileOnlineDataSwitch != null) {

            if (!isChecked) {
                advancedProfileOnlineDataSwitch.setCheckedAndCallListener(false);
                advancedProfileNetworkDataSwitch.setCheckedAndCallListener(false);
            }

            if (checkForDisabledSwitchesCases(ADVANCED_PROFILE_ONLINE_DATA_TYPE)) {

                instructionalCardOnlineData.setVisibility(View.VISIBLE);

                setSpecifyAndSwitchVisibility(specifyAdvancedProfileOnlineDataSwitch, advancedProfileOnlineDataSwitch,
                        cardSwitchButtonMiddleImageOnlineData, tapContainerMiddleImageOnlineData,
                        tapLeftMiddleImageOnlineData, tapRightMiddleImageOnlineData, NONE, false);

            } else {

                setSpecifyAndSwitchVisibility(specifyAdvancedProfileOnlineDataSwitch, advancedProfileOnlineDataSwitch,
                        cardSwitchButtonMiddleImageOnlineData, tapContainerMiddleImageOnlineData,
                        tapLeftMiddleImageOnlineData, tapRightMiddleImageOnlineData, isChecked ? ADVANCED_PROFILE_ONLINE_DATA_TYPE : NONE, isChecked);

                if (isChecked && !advancedProfileOnlineDataSwitch.isChecked()) {
                    instructionalCardOnlineDataText.setText(SettingsLabels.getInstructionalText1());
                } else if (isChecked && advancedProfileOnlineDataSwitch.isChecked()) {
                    instructionalCardOnlineData.setVisibility(View.GONE);
                }
            }

            if (checkForDisabledSwitchesCases(ADVANCED_PROFILE_NETWORK_DATA_TYPE)) {

                instructionalCardNetworkData.setVisibility(View.VISIBLE);

                setSpecifyAndSwitchVisibility(specifyAdvancedProfileNetworkDataSwitch, advancedProfileNetworkDataSwitch,
                        cardSwitchButtonMiddleImageNetworkData, tapContainerMiddleImageNetworkData,
                        tapLeftMiddleImageNetworkData, tapRightMiddleImageNetworkData, NONE, false);


            } else {
                setSpecifyAndSwitchVisibility(specifyAdvancedProfileNetworkDataSwitch, advancedProfileNetworkDataSwitch,
                        cardSwitchButtonMiddleImageNetworkData, tapContainerMiddleImageNetworkData,
                        tapLeftMiddleImageNetworkData, tapRightMiddleImageNetworkData, isChecked ? ADVANCED_PROFILE_NETWORK_DATA_TYPE : NONE, isChecked);

                if (isChecked && !advancedProfileNetworkDataSwitch.isChecked()) {
                    instructionalCardNetworkDataText.setText(SettingsLabels.getInstructionalText1());
                } else if (isChecked && advancedProfileNetworkDataSwitch.isChecked()) {
                    instructionalCardNetworkData.setVisibility(View.GONE);
                }
            }
        }
    }

    private void inflateOnlineDataCard() {
        if (getActivity() == null)
            return;

        View v = View.inflate(getActivity(), R.layout.settings_card_permissions, null);

        inflateInstructionalCard(SettingsLabels.getInstructionalText1(), ADVANCED_PROFILE_ONLINE_DATA_TYPE);

        if (isSwitchChecked(ADVANCED_PROFILE_ONLINE_DATA_TYPE)) {
            instructionalCardOnlineData.setVisibility(View.GONE);
        }

        v.findViewById(R.id.cardSwitchButton).setVisibility(View.VISIBLE);
        v.findViewById(R.id.itemsContainer).setVisibility(View.GONE);

        ((VodafoneTextView) v.findViewById(R.id.cardTitle)).setText(SettingsLabels.getOnlineDataTitle());
        ((VodafoneTextView) v.findViewById(R.id.cardSubtext)).setText(card_type.equals(PermissionsFragment.VODAFONE_PERMISSIONS_CARD) ?
                SettingsLabels.getOnlineDataSubtextVodafone() : SettingsLabels.getOnlineDataSubtextParteners());

        setExpandableView(v, card_type.equals(PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                ? SettingsLabels.getOnlineDataSeeDetailsVodafone() : SettingsLabels.getOnlineDataSeeDetailsParteners());

        advancedProfileOnlineDataSwitch = v.findViewById(R.id.cardSwitchButton);
        specifyAdvancedProfileOnlineDataSwitch = v.findViewById(R.id.cardSwitchButtonContainer);
        cardSwitchButtonMiddleImageOnlineData = v.findViewById(R.id.cardSwitchButtonMiddleImage);
        tapContainerMiddleImageOnlineData = v.findViewById(R.id.tapContainer);
        tapLeftMiddleImageOnlineData = v.findViewById(R.id.tapLeftCardSwitchButton);
        tapRightMiddleImageOnlineData = v.findViewById(R.id.tapRightCardSwitchButton);

        advancedProfileOnlineDataSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setGdprPermissionWhenSwitchChanged(isChecked, ADVANCED_PROFILE_ONLINE_DATA_TYPE);

                boolean disabledCases = checkForDisabledSwitchesCases(ADVANCED_PROFILE_ONLINE_DATA_TYPE);

                instructionalCardOnlineData.setVisibility(isChecked && !disabledCases ? View.GONE : View.VISIBLE);

                if (!isChecked && !disabledCases)
                    instructionalCardOnlineDataText.setText(SettingsLabels.getInstructionalText1());

            }
        });

        if (checkForDisabledSwitchesCases(ADVANCED_PROFILE_ONLINE_DATA_TYPE)) {

            instructionalCardOnlineData.setVisibility(View.VISIBLE);

            setSpecifyAndSwitchVisibility(specifyAdvancedProfileOnlineDataSwitch, advancedProfileOnlineDataSwitch,
                    cardSwitchButtonMiddleImageOnlineData, tapContainerMiddleImageOnlineData,
                    tapLeftMiddleImageOnlineData, tapRightMiddleImageOnlineData, ADVANCED_PROFILE_ONLINE_DATA_TYPE, false);

        } else {

            setSpecifyAndSwitchVisibility(specifyAdvancedProfileOnlineDataSwitch, advancedProfileOnlineDataSwitch,
                    cardSwitchButtonMiddleImageOnlineData, tapContainerMiddleImageOnlineData,
                    tapLeftMiddleImageOnlineData, tapRightMiddleImageOnlineData, ADVANCED_PROFILE_ONLINE_DATA_TYPE, true);

        }

        parent.addView(v);
    }

    private void inflateNetworkDataCard() {
        if (getActivity() == null)
            return;

        View v = View.inflate(getActivity(), R.layout.settings_card_permissions, null);

        inflateInstructionalCard(SettingsLabels.getInstructionalText1(), ADVANCED_PROFILE_NETWORK_DATA_TYPE);

        if (isSwitchChecked(ADVANCED_PROFILE_NETWORK_DATA_TYPE)) {
            instructionalCardNetworkData.setVisibility(View.GONE);
        }

        v.findViewById(R.id.cardSwitchButton).setVisibility(View.VISIBLE);
        v.findViewById(R.id.itemsContainer).setVisibility(View.GONE);

        ((VodafoneTextView) v.findViewById(R.id.cardTitle)).setText(SettingsLabels.getNetworkDataTitle());
        ((VodafoneTextView) v.findViewById(R.id.cardSubtext)).setText(card_type.equals(PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                ? SettingsLabels.getNetworkDataSubtextVodafone() : SettingsLabels.getNetworkDataSubtextParteners());

        setExpandableView(v, card_type.equals(PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                ? SettingsLabels.getNetworkDataSeeDetailsVodafone() : SettingsLabels.getNetworkDataSeeDetailsParteners());

        advancedProfileNetworkDataSwitch = v.findViewById(R.id.cardSwitchButton);
        specifyAdvancedProfileNetworkDataSwitch = v.findViewById(R.id.cardSwitchButtonContainer);
        cardSwitchButtonMiddleImageNetworkData = v.findViewById(R.id.cardSwitchButtonMiddleImage);
        tapContainerMiddleImageNetworkData = v.findViewById(R.id.tapContainer);
        tapLeftMiddleImageNetworkData = v.findViewById(R.id.tapLeftCardSwitchButton);
        tapRightMiddleImageNetworkData = v.findViewById(R.id.tapRightCardSwitchButton);

        advancedProfileNetworkDataSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setGdprPermissionWhenSwitchChanged(isChecked, ADVANCED_PROFILE_NETWORK_DATA_TYPE);

                boolean disabledCases = checkForDisabledSwitchesCases(ADVANCED_PROFILE_NETWORK_DATA_TYPE);

                instructionalCardNetworkData.setVisibility(isChecked && !disabledCases ? View.GONE : View.VISIBLE);

                if (!isChecked && !disabledCases)
                    instructionalCardNetworkDataText.setText(SettingsLabels.getInstructionalText1());

            }
        });

        if (checkForDisabledSwitchesCases(ADVANCED_PROFILE_NETWORK_DATA_TYPE)) {

            instructionalCardNetworkData.setVisibility(View.VISIBLE);

            setSpecifyAndSwitchVisibility(specifyAdvancedProfileNetworkDataSwitch, advancedProfileNetworkDataSwitch,
                    cardSwitchButtonMiddleImageNetworkData, tapContainerMiddleImageNetworkData,
                    tapLeftMiddleImageNetworkData, tapRightMiddleImageNetworkData, ADVANCED_PROFILE_NETWORK_DATA_TYPE, false);

        } else {

            setSpecifyAndSwitchVisibility(specifyAdvancedProfileNetworkDataSwitch, advancedProfileNetworkDataSwitch,
                    cardSwitchButtonMiddleImageNetworkData, tapContainerMiddleImageNetworkData,
                    tapLeftMiddleImageNetworkData,
                    tapRightMiddleImageNetworkData, ADVANCED_PROFILE_NETWORK_DATA_TYPE, true);

        }

        parent.addView(v);
    }

    private void inflateInstructionalCard(String text, int type) {

        if (getActivity() == null)
            return;

        switch (type) {
            case CREATE_PROFILE_TYPE:
                instructionalCardProfile = View.inflate(getActivity(), R.layout.permissions_instructional_card, null);
                instructionalCardProfileText = instructionalCardProfile.findViewById(R.id.instructional_text);
                instructionalCardProfileText.setText(text);
                parent.addView(instructionalCardProfile);
                break;
            case ADVANCED_PROFILE_ONLINE_DATA_TYPE:
                instructionalCardOnlineData = View.inflate(getActivity(), R.layout.permissions_instructional_card, null);
                instructionalCardOnlineDataText = instructionalCardOnlineData.findViewById(R.id.instructional_text);
                instructionalCardOnlineDataText.setText(text);
                parent.addView(instructionalCardOnlineData);
                break;
            case ADVANCED_PROFILE_NETWORK_DATA_TYPE:
                instructionalCardNetworkData = View.inflate(getActivity(), R.layout.permissions_instructional_card, null);
                instructionalCardNetworkDataText = instructionalCardNetworkData.findViewById(R.id.instructional_text);
                instructionalCardNetworkDataText.setText(text);
                parent.addView(instructionalCardNetworkData);
                break;
            case COMMERCIAL_COMMUNICATIONS_TYPE:
                instructionalCardCommercial = View.inflate(getActivity(), R.layout.permissions_instructional_card, null);
                instructionalCardCommercialText = instructionalCardCommercial.findViewById(R.id.instructional_text);
                instructionalCardCommercialText.setText(text);
                parent.addView(instructionalCardCommercial);
                break;
            case SURVEY_TYPE:
                instructionalCardSurvey = View.inflate(getActivity(), R.layout.permissions_instructional_card, null);
                instructionalCardSurveyText = instructionalCardSurvey.findViewById(R.id.instructional_text);
                instructionalCardSurveyText.setText(text);
                parent.addView(instructionalCardSurvey);
                break;
        }

    }

    private void inflateSaveButton() {

        if (getActivity() == null)
            return;

        save_button = settingsActivity.getSaveButton();
        save_button_card = settingsActivity.getSaveButtonContainer();

//        save_button_card.setVisibility(View.VISIBLE);
//        setButtonAvailability(save_button, true);

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TrackingAppMeasurement journey = new TrackingAppMeasurement();

                journey.event65 = "button:" + "save permission";
                journey.getContextData().put("event65", journey.event65);

                journey.eVar82 = (card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                        ? "mcare:permissions:vodafone:button:" + "save"
                        : "mcare:permissions:vodafone partners:button:" + "save";
                journey.getContextData().put("eVar82", journey.eVar82);

                trackEvent(card_type, journey);

                gdprPermissionsChanged.setToSendMinorAccount(null);
                if (card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                    gdprPermissionsChanged.setToSendVodafonePermissions(null);
                else if (card_type == PermissionsFragment.VODAFONE_PARTENERS_CARD)
                    gdprPermissionsChanged.setToSendVodafonePartenersPermissions(null);

                setPermissions(false, ACTIVATE, VodafoneController.getInstance().getUser() instanceof EbuMigrated ? "MSEB" : "UPCB");
            }
        });
    }

    private void setGdprPermissionWhenSwitchChanged(boolean isChecked, int type) {
        switch (type) {
            //TODO code cleaunp - please replace gdprPermissionsChanged is a safe getter
            case SMS_MMS_PUSH:
                if (card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                    gdprPermissionsChanged.setVfSmsMmsPush(isChecked ? "YES" : "NO");
                else
                    gdprPermissionsChanged.setExtSmsMmsPush(isChecked ? "YES" : "NO");
                break;
            case EMAIL:
                if (card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                    gdprPermissionsChanged.setVfEmail(isChecked ? "YES" : "NO");
                else
                    gdprPermissionsChanged.setExtEmail(isChecked ? "YES" : "NO");
                break;
            case POST:
                if (card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                    gdprPermissionsChanged.setVfPost(isChecked ? "YES" : "NO");
                else
                    gdprPermissionsChanged.setExtPost(isChecked ? "YES" : "NO");
                break;
            case OUTBOUND_CALL:
                if (card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                    gdprPermissionsChanged.setVfOutboundCall(isChecked ? "YES" : "NO");
                else
                    gdprPermissionsChanged.setExtOutboundCall(isChecked ? "YES" : "NO");
                break;
            case SURVEY_TYPE:
                if (card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                    gdprPermissionsChanged.setVfSurveyCategory(isChecked ? "YES" : "NO");
                else
                    gdprPermissionsChanged.setExtSurveyCategory(isChecked ? "YES" : "NO");
                break;
            case CREATE_PROFILE_TYPE:
                if (card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                    gdprPermissionsChanged.setVfBasicProfileCustServiceData(isChecked ? "YES" : "NO");
                else
                    gdprPermissionsChanged.setExtBasicProfileCustServiceData(isChecked ? "YES" : "NO");
                break;
            case ADVANCED_PROFILE_NETWORK_DATA_TYPE:
                if (card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                    gdprPermissionsChanged.setVfAdvancedProfileNetworkData(isChecked ? "YES" : "NO");
                else
                    gdprPermissionsChanged.setExtAdvancedProfileNetworkData(isChecked ? "YES" : "NO");
                break;
            case ADVANCED_PROFILE_ONLINE_DATA_TYPE:
                if (card_type == PermissionsFragment.VODAFONE_PERMISSIONS_CARD)
                    gdprPermissionsChanged.setVfAdvancedProfileOnlineData(isChecked ? "YES" : "NO");
                else
                    gdprPermissionsChanged.setExtAdvancedProfileOnlineData(isChecked ? "YES" : "NO");
                break;
            default:
                return;
        }

        //TODO code cleaunp - please replace gdprPermissionsChanged is a safe getter

        if (!gdprPermissionsChanged.equals(gdprPermissions)) {
            if (save_button_card == null)
                inflateSaveButton();
            else
                save_button_card.setVisibility(View.VISIBLE);
        }

        if (save_button != null)
            setButtonAvailability(save_button, !gdprPermissionsChanged.checkIfSpecify(card_type,
                    checkForDisabledSwitchesCases(COMMERCIAL_COMMUNICATIONS_TYPE),
                    checkForDisabledSwitchesCases(SURVEY_TYPE),
                    checkForDisabledSwitchesCases(CREATE_PROFILE_TYPE),
                    checkForDisabledSwitchesCases(ADVANCED_PROFILE_ONLINE_DATA_TYPE),
                    checkForDisabledSwitchesCases(ADVANCED_PROFILE_NETWORK_DATA_TYPE)));

    }

    private boolean checkForDisabledSwitchesCases(int permission_type) {
        User user = VodafoneController.getInstance().getUser();

        if (user instanceof ResCorp)
            return disabledSwitchesCasesForRescorp(permission_type);
        else if (user instanceof EbuMigrated)
            return disabledSwitchesCasesForEbuMigrated(user, permission_type);
        else if (isResSubGeoNumber())
            return disabledSwitchesCasesForResSubGeoNumber(permission_type);
        else if (user instanceof CBUUser || user instanceof PrepaidUser)
            return disabledSwitchesCasesForPrepaidAndCBUUser(permission_type);

        return false;
    }

    private  boolean disabledSwitchesCasesForRescorp(int permission_type) {
        switch (permission_type) {
            case CREATE_PROFILE_TYPE:
                if (own_subscription_is_minor) {
                    instructionalCardProfileText.setText(SettingsLabels.getInstructionalText3());
                    return true;
                }
                return false;
            case ADVANCED_PROFILE_NETWORK_DATA_TYPE:

                if (own_subscription_is_minor) {
                    instructionalCardNetworkDataText.setText(SettingsLabels.getInstructionalText3());
                    return true;
                }

                if (!own_subscription_is_minor && GdprController.checkMinorStatus(gdprPermissions)) {
                    instructionalCardNetworkDataText.setText(SettingsLabels.getInstructionalText3());
                    return true;
                }

                if (isBasicProfilingDisablingAdvancedProfiling()) {
                    instructionalCardNetworkDataText.setText(SettingsLabels.getInstructionalText4());
                    return true;
                }

                return false;

            case ADVANCED_PROFILE_ONLINE_DATA_TYPE:

                if (own_subscription_is_minor) {
                    instructionalCardOnlineDataText.setText(SettingsLabels.getInstructionalText3());
                    return true;
                }

                if (!own_subscription_is_minor && GdprController.checkMinorStatus(gdprPermissions)) {
                    instructionalCardOnlineDataText.setText(SettingsLabels.getInstructionalText3());
                    return true;
                }

                if (isBasicProfilingDisablingAdvancedProfiling()) {
                    instructionalCardOnlineDataText.setText(SettingsLabels.getInstructionalText4());
                    return true;
                }

                return false;
            case COMMERCIAL_COMMUNICATIONS_TYPE:

                if (own_subscription_is_minor
                        || (!own_subscription_is_minor && GdprController.checkMinorStatus(gdprPermissions))) {
                    instructionalCardCommercialText.setText(SettingsLabels.getInstructionalText3ForCommercialCommunications());
                    return true;
                }

                return false;
            case SURVEY_TYPE:

                if (own_subscription_is_minor
                        || (!own_subscription_is_minor && GdprController.checkMinorStatus(gdprPermissions))) {
                    instructionalCardSurveyText.setText(SettingsLabels.getInstructionalText3());
                    return true;
                }

                return false;
            default:
                return own_subscription_is_minor || (!own_subscription_is_minor && GdprController.checkMinorStatus(gdprPermissions));
        }
    }

    private boolean disabledSwitchesCasesForEbuMigrated(User user, int permission_type) {

        switch (permission_type) {
            case CREATE_PROFILE_TYPE:

                if ((user instanceof ChooserUser || user instanceof DelegatedChooserUser
                        || user instanceof AuthorisedPersonUser)
                        && GdprController.checkMinorStatus(gdprPermissions)) {
                    instructionalCardProfileText.setText(SettingsLabels.getInstructionalText3());
                    return true;
                }

                if ((user instanceof PowerUser || user instanceof SubUserMigrated)
                        && UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().isGeoNumber()) {
                    instructionalCardProfileText.setText(SettingsLabels.getInstructionalText2());
                    return true;
                }

                return false;

            case ADVANCED_PROFILE_NETWORK_DATA_TYPE:

                if ((user instanceof ChooserUser || user instanceof DelegatedChooserUser
                        || user instanceof AuthorisedPersonUser)
                        && GdprController.checkMinorStatus(gdprPermissions)) {
                    instructionalCardNetworkDataText.setText(SettingsLabels.getInstructionalText3());
                    return true;
                }

                if ((user instanceof PowerUser || user instanceof SubUserMigrated)
                        && UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().isGeoNumber()) {
                    instructionalCardNetworkDataText.setText(SettingsLabels.getInstructionalText2());
                    return true;
                }

                if (isBasicProfilingDisablingAdvancedProfiling()) {
                    instructionalCardNetworkDataText.setText(SettingsLabels.getInstructionalText4());
                    return true;
                }

                return false;

            case ADVANCED_PROFILE_ONLINE_DATA_TYPE:

                if ((user instanceof ChooserUser || user instanceof DelegatedChooserUser
                        || user instanceof AuthorisedPersonUser)
                        && GdprController.checkMinorStatus(gdprPermissions)) {
                    instructionalCardOnlineDataText.setText(SettingsLabels.getInstructionalText3());
                    return true;
                }

                if ((user instanceof PowerUser || user instanceof SubUserMigrated)
                        && UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().isGeoNumber()) {
                    instructionalCardOnlineDataText.setText(SettingsLabels.getInstructionalText2());
                    return true;
                }

                if (isBasicProfilingDisablingAdvancedProfiling()) {
                    instructionalCardOnlineDataText.setText(SettingsLabels.getInstructionalText4());
                    return true;
                }

                return false;

            case COMMERCIAL_COMMUNICATIONS_TYPE:

                if ((user instanceof ChooserUser || user instanceof DelegatedChooserUser
                        || user instanceof AuthorisedPersonUser)
                        && GdprController.checkMinorStatus(gdprPermissions)) {
                    instructionalCardCommercialText.setText(SettingsLabels.getInstructionalText3ForCommercialCommunications());
                    return true;
                }

                if ((user instanceof PowerUser || user instanceof SubUserMigrated)
                        && UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().isGeoNumber()) {
                    instructionalCardCommercialText.setText(SettingsLabels.getInstructionalText2ForCommercialCommunications());
                    return true;
                }

                return false;

            case SURVEY_TYPE:

                if ((user instanceof ChooserUser || user instanceof DelegatedChooserUser
                        || user instanceof AuthorisedPersonUser)
                        && GdprController.checkMinorStatus(gdprPermissions)) {
                    instructionalCardSurveyText.setText(SettingsLabels.getInstructionalText3());
                    return true;
                }

                if ((user instanceof PowerUser || user instanceof SubUserMigrated)
                        && UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().isGeoNumber()) {
                    instructionalCardSurveyText.setText(SettingsLabels.getInstructionalText2());
                    return true;
                }

                return false;
        }
        return false;

    }

    private boolean disabledSwitchesCasesForResSubGeoNumber(int permission_type) {
        switch (permission_type) {
            case CREATE_PROFILE_TYPE:
                instructionalCardProfileText.setText(SettingsLabels.getInstructionalText2());
                return true;

            case ADVANCED_PROFILE_NETWORK_DATA_TYPE:
                instructionalCardNetworkDataText.setText(SettingsLabels.getInstructionalText2());
                return true;

            case ADVANCED_PROFILE_ONLINE_DATA_TYPE:
                instructionalCardOnlineDataText.setText(SettingsLabels.getInstructionalText2());
                return true;

            case COMMERCIAL_COMMUNICATIONS_TYPE:
                instructionalCardCommercialText.setText(SettingsLabels.getInstructionalText2ForCommercialCommunications());
                return true;

            case SURVEY_TYPE:
                instructionalCardSurveyText.setText(SettingsLabels.getInstructionalText2());
                return true;
        }
        return false;
    }

    private boolean disabledSwitchesCasesForPrepaidAndCBUUser(int permission_type) {
        switch (permission_type) {
            case CREATE_PROFILE_TYPE:
                if (GdprController.checkMinorStatus(gdprPermissions)) {
                    instructionalCardProfileText.setText(SettingsLabels.getInstructionalText3());
                    return true;
                }

                return false;
            case ADVANCED_PROFILE_NETWORK_DATA_TYPE:
                if (GdprController.checkMinorStatus(gdprPermissions)) {
                    instructionalCardNetworkDataText.setText(SettingsLabels.getInstructionalText3());
                    return true;
                }

                if (isBasicProfilingDisablingAdvancedProfiling()) {
                    instructionalCardNetworkDataText.setText(SettingsLabels.getInstructionalText4());
                    return true;
                }

                return false;
            case ADVANCED_PROFILE_ONLINE_DATA_TYPE:
                if (GdprController.checkMinorStatus(gdprPermissions)) {
                    instructionalCardOnlineDataText.setText(SettingsLabels.getInstructionalText3());
                    return true;
                }

                if (isBasicProfilingDisablingAdvancedProfiling()) {
                    instructionalCardOnlineDataText.setText(SettingsLabels.getInstructionalText4());
                    return true;
                }

                return false;
            case COMMERCIAL_COMMUNICATIONS_TYPE:
                if (GdprController.checkMinorStatus(gdprPermissions)) {
                    instructionalCardCommercialText.setText(SettingsLabels.getInstructionalText3ForCommercialCommunications());
                    return true;
                }
                return false;
            case SURVEY_TYPE:
                if (GdprController.checkMinorStatus(gdprPermissions)) {
                    instructionalCardSurveyText.setText(SettingsLabels.getInstructionalText3());
                    return true;
                }
                return false;
        }
        return false;
    }

}

package ro.vodafone.mcare.android.card.travelling;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.realm.system.TravellingAboardLabels;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.utils.TextUtils;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;

/**
 * Created by Prodan Pavel on 19.01.2018.
 */

public class RoamingStatusCard extends VodafoneGenericCard {
    @BindView(R.id.travelling_aboard_activation_status_container)
    RelativeLayout activationStatusContainer;
    @BindView(R.id.roaming_status_container)
    RelativeLayout roamingStatusContainer;
    @BindView(R.id.header_active)
    TextView headerActive;
    @BindView(R.id.roaming_alias_text)
    TextView roamingAliasText;
    @BindView(R.id.roaming_header_text)
    TextView roamingHeaderText;
    @BindView(R.id.roaming_status_desscription)
    TextView roamingStatusDescription;
    @BindView(R.id.service_administration_button)
    VodafoneButton serviceAdministrationButton;
    @BindView(R.id.roaming_card_title)
    TextView roamingCardTitle;
    @BindView(R.id.roaming_error_inside_card)
    CardErrorLayout roamingInsideError;
    @BindView(R.id.relativeLayout2)
    RelativeLayout relativeLayoutForStatus;

    private boolean isPrepaid;
    private boolean isActive;
    private boolean isNationalOnly;
    private boolean isOffersFromListPrepaid;

    public RoamingStatusCard(Context context) {
        super(context);
        init();
    }

    public RoamingStatusCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoamingStatusCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        roamingHeaderText.setTypeface(Fonts.getVodafoneRG());
    }

    @Override
    protected int setContent() {
        return R.layout.card_roaming_status;
    }

    public RoamingStatusCard setIsPrepaid(boolean isPrepaid) {
        this.isPrepaid = isPrepaid;
        return this;
    }

    public RoamingStatusCard setIsNationalOnly(boolean isNationalOnly) {
        this.isNationalOnly = isNationalOnly;
        return this;
    }

    public RoamingStatusCard setIsActive(boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public RoamingStatusCard setIsOffersFromList(boolean offersFromListPrepaid) {
        isOffersFromListPrepaid = offersFromListPrepaid;
        return this;
    }

    public RoamingStatusCard setCardTitle(String title) {
        if(title != null) {
            roamingCardTitle.setText(title);
            roamingCardTitle.setVisibility(VISIBLE);
        } else {
            roamingCardTitle.setVisibility(GONE);
        }
        return this;
    }

    public void buildInactiveCardForPPYDepositNotRequired() {
        roamingStatusContainer.setVisibility(VISIBLE);
        roamingInsideError.setVisibility(GONE);
        activateButton();
        setInactiveRoaming();
        hideNationalOnlyPpDescription();
    }

    public void buildStatusCard( boolean noRoamingOffers) {
        roamingStatusContainer.setVisibility(VISIBLE);
        roamingInsideError.setVisibility(GONE);

        if (noRoamingOffers) {
            setInactiveRoaming();
            setNationalOnlyPpDescription(TravellingAboardLabels.getTravelling_aboard_administration_pp_description());
        }else if (isNationalOnly) {
            setInactiveRoaming();
            setNationalOnlyPpDescription(TravellingAboardLabels.getTravelling_aboard_administration_pp_description());
        } else {
            if (isActive)
                setActiveRoaming();
            else {
                setInactiveRoaming();
                if (isPrepaid) {
                    if (!isOffersFromListPrepaid)
                        setNationalOnlyPpDescription(TravellingAboardLabels.getTravelling_aboard_roaming_inactive_description_status());
//                } else {
//                    setNationalOnlyPpDescription(TravellingAboardLabels.getTravelling_aboard_administration_pp_description());
                }
            }
        }
    }

    public RoamingStatusCard setActivationButton(boolean isActive) {
        if (isActive) {
            serviceAdministrationButton.setText(TravellingAboardLabels.getTravelling_aboard_deactivation_button_label());
            serviceAdministrationButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white_text_color));
            serviceAdministrationButton.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.selector_button_background_card_secondary));
        } else {
            serviceAdministrationButton.setText(TravellingAboardLabels.getTravelling_aboard_activation_button_label());
            serviceAdministrationButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white_text_color));
            serviceAdministrationButton.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.selector_button_background_card_primary));
        }
        return this;
    }

    private void setActiveRoaming() {
        headerActive.setText(TravellingAboardLabels.getTravelling_aboard_header_activ());
        headerActive.setTextColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.roaming_active_background));
        roamingStatusContainer.setBackgroundColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.roaming_active_background));
        roamingHeaderText.setTextColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.white));
        roamingAliasText.setTextColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.white));
        roamingAliasText.setTypeface(Typeface.DEFAULT_BOLD);
        roamingStatusDescription.setVisibility(GONE);
    }

    private void setInactiveRoaming() {
        headerActive.setText(TravellingAboardLabels.getTravelling_aboard_header_inactive());
        headerActive.setTextColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.roaming_in_active_background));
        roamingStatusContainer.setBackgroundColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.roaming_in_active_background));
        roamingHeaderText.setTextColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.blackNormal));
        roamingAliasText.setTextColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.blackNormal));
        roamingAliasText.setTypeface(Typeface.DEFAULT_BOLD);
        roamingStatusDescription.setTextColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.blackNormal));
    }

    private void setNationalOnlyPpDescription(String description) {
        roamingStatusDescription.setText(TravellingAboardLabels.getTravelling_aboard_roaming_inactive_description_status());
        roamingStatusDescription.setText(description);
        roamingStatusDescription.setVisibility(VISIBLE);
    }

    public RoamingStatusCard setAliasName(String alias) {
        roamingAliasText.setText(alias);
        roamingAliasText.setVisibility(View.VISIBLE);
        return this;
    }

    public RoamingStatusCard setAliasOrStatus(String alias) {
        roamingAliasText.setVisibility(View.VISIBLE);

        if (alias != null && !alias.isEmpty()) {
            roamingAliasText.setText(alias);
        } else {
            if (isActive)
                roamingAliasText.setText("Serviciu activ");
            else
                roamingAliasText.setText("Serviciu inactiv");
        }

        return this;
    }

    public RoamingStatusCard setButtonTextAndListener(OnClickListener clickListener, String buttonLabel) {
        serviceAdministrationButton.setOnClickListener(clickListener);
        serviceAdministrationButton.setText(buttonLabel);
        serviceAdministrationButton.setVisibility(VISIBLE);
        return this;
    }

    public RoamingStatusCard setButtonClickListener(OnClickListener clickListener) {
        serviceAdministrationButton.setOnClickListener(clickListener);
        return this;
    }

    public RoamingStatusCard inactivateButton(boolean isActive) {
        serviceAdministrationButton
                .setText(isActive
                        ? TravellingAboardLabels.getTravelling_aboard_deactivation_button_label()
                        : TravellingAboardLabels.getTravelling_aboard_activation_button_label());

        serviceAdministrationButton.setClickable(false);
        serviceAdministrationButton.setEnabled(false);
        return this;
    }

    public RoamingStatusCard activateButton() {
        serviceAdministrationButton.setClickable(true);
        serviceAdministrationButton.setEnabled(true);
        return this;
    }

    public RoamingStatusCard setRoamingDescription(String description) {
        roamingHeaderText.setText(Html.fromHtml(description));

        return this;
    }

    public RoamingStatusCard setRoamingDescriptionFromtml(String description) {
        TextUtils.setTextViewClickableLinks(roamingHeaderText);
        roamingHeaderText.setText(Html.fromHtml(description));

        return this;
    }


    public RoamingStatusCard addCardToViewgroup(ViewGroup parentView) {
        parentView.addView(this);
        return this;
    }

    public void addOnErrorClickListener(OnClickListener onClickListener) {
        getErrorView().setOnClickListener(onClickListener);
    }

    public void showInsideErrorCard(String errorMessage, boolean isDescriptionVisible) {
        roamingStatusContainer.setVisibility(GONE);
        roamingInsideError.setText(errorMessage);
        roamingInsideError.setVisibility(VISIBLE);
        if (isDescriptionVisible) {
            roamingStatusDescription.setText(TravellingAboardLabels.getTravelling_aboard_administration_pp_description());
            roamingStatusDescription.setVisibility(VISIBLE);
            roamingStatusDescription.setPadding(50,0,16,0);
        }else {
            roamingStatusDescription.setVisibility(GONE);
        }
        inactivateButton(false);
    }

    public void hideButton(){
        serviceAdministrationButton.setVisibility(GONE);
    }

    public void hideNationalOnlyPpDescription(){
        roamingStatusDescription.setVisibility(GONE);
    }

    public void hideBubleForNationalOnly(boolean isNationalOnly) {
        if(isNationalOnly) {
            relativeLayoutForStatus.setVisibility(GONE);
        }else{
            relativeLayoutForStatus.setVisibility(VISIBLE);
        }
    }

}

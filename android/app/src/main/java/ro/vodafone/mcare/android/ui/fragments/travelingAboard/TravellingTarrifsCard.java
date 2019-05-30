package ro.vodafone.mcare.android.ui.fragments.travelingAboard;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.realm.system.TravellingAboardLabels;
import ro.vodafone.mcare.android.client.model.travellingAboard.ZonesList;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by Alex on 4/29/2017.
 */

public class TravellingTarrifsCard extends VodafoneAbstractCard {

    @BindView(R.id.tarrifes_tittle)
    TextView titleTextView;

    @BindView(R.id.operator)
    TextView operator;

    @BindView(R.id.call_local)
    TextView callLocal;

    @BindView(R.id.call_ro)
    TextView callRo;

    @BindView(R.id.call_eu)
    TextView callEu;

    @BindView(R.id.call_non_eu)
    TextView callNonEu;

    @BindView(R.id.call_received)
    TextView callReceived;

    @BindView(R.id.sms_sent)
    TextView smsSent;

    @BindView(R.id.internet)
    TextView internet;

    @BindView(R.id.add_extraoptions_button)
    VodafoneButton addExtraoptionsButton;


    private ZonesList zonesList;
    private boolean show_button = true;

    public TravellingTarrifsCard(Context context) {
        super(context);
    }

    public TravellingTarrifsCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TravellingTarrifsCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TravellingTarrifsCard build(String country) {
        ButterKnife.bind(this);

        titleTextView.setText(TravellingAboardLabels.getTravelling_aboard_tarrifes_tittle());
        setupTariffsLabels(country);
        setupExtraoptionsButton();
        setupCardMargins();

        return this;
    }

    private void setupCardMargins() {
        int horizontalMargin = getResources().getDimensionPixelSize(R.dimen.card_horizontal_padding);
        int verticalMargin = getResources().getDimensionPixelSize(R.dimen.default_margin_vertical);
        setCardMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
    }
    private void setupExtraoptionsButton() {
        addExtraoptionsButton.setText(TravellingAboardLabels.getTravelling_aboard_add_extraoption_button());
        addExtraoptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.standardCharges);
                tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.roamingStatusButtonOffers);
                tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("TravellingTarrifsCard", tealiumMapEvent);

                User user = VodafoneController.getInstance().getUser();
                if (user instanceof PrepaidUser)
                    new NavigationAction(getContext()).startAction(IntentActionName.OFFERS_BEO, true);
                else
                    new NavigationAction(getContext()).startAction(IntentActionName.OFFERS_EXTRAOPTIONS, true);

            }
        });
    }

    private void setupTariffsLabels(String country) {
        operator.setText(setTextBold(zonesList.getOperator(), zonesList.getOperator().length()));
        callLocal.setText(setTextBold(zonesList.getCallLocal() + TravellingAboardLabels.getTravelling_aboard_local_min() + " " + country,
                zonesList.getCallRo().length()));
        callRo.setText(setTextBold(zonesList.getCallRo() + " " + TravellingAboardLabels.getTravelling_aboard_local_min_ro(), zonesList.getCallRo().length()));
        callEu.setText(setTextBold(zonesList.getCallEU() + " " + TravellingAboardLabels.getTravelling_aboard_local_call_EU(), zonesList.getCallEU().length()));
        callNonEu.setText(setTextBold(zonesList.getCallNonEU() + " " + TravellingAboardLabels.getTravelling_aboard_min_non_EU(), zonesList.getCallNonEU().length()));
        callReceived.setText(setTextBold(zonesList.getCallReceived() + " " + TravellingAboardLabels.getTravelling_aboard_local_received_call(),
                zonesList.getCallReceived().length()));
        smsSent.setText(setTextBold(zonesList.getSmsSent() + " " + TravellingAboardLabels.getTravelling_aboard_send_sms(), zonesList.getSmsSent().length()));
        internet.setText(setTextBold(zonesList.getInternet() + " " + TravellingAboardLabels.getTravelling_aboard_internet(), zonesList.getInternet().length()));

    }

    @Override
    protected int setContent() {
        return R.layout.travelling_tarrifs_card;
    }
    public TravellingTarrifsCard setButtonVisibility(boolean show_button){
        this.show_button = show_button;
        return this;
    }
    public ZonesList getZonesList() {
        return zonesList;
    }

    public TravellingTarrifsCard setZonesList(ZonesList zonesList) {
        this.zonesList = zonesList;
        return this;
    }

    public SpannableString setTextBold(String boldText, int lenght) {
        SpannableString spannablecontentBold = new SpannableString(boldText);
        spannablecontentBold.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, lenght, 0);

        return spannablecontentBold;
    }

    public TravellingTarrifsCard hideExtraOptionsButton() {
        addExtraoptionsButton.setVisibility(GONE);
        return this;
    }
}
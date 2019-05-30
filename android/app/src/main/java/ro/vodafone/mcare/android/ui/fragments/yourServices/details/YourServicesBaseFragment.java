package ro.vodafone.mcare.android.ui.fragments.yourServices.details;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.UserDataController;
import ro.vodafone.mcare.android.card.BaseExpandableCard;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.card.offers.ExpandableWebViewCard;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.ActivationEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.ActivationRequest;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.EbuOfferEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.ActivatePrepaidOfferRequest;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleOffersSuccess;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.identity.CustomerRestrictionsSuccess;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.Benefit;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.BillingOffer;
import ro.vodafone.mcare.android.client.model.offers.activeOffersEbu.Promo;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.RetentionLabels;
import ro.vodafone.mcare.android.client.model.realm.system.ServicesLabels;
import ro.vodafone.mcare.android.client.model.shop.ShopEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopLoginSuccess;
import ro.vodafone.mcare.android.client.model.shop.ShopProductsSuccess;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.CBUUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.SubUserMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.rest.observables.RetentionObservables;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.utils.ResponseValidatorUtils;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.service.ShopService;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;
import ro.vodafone.mcare.android.ui.activities.ChatBubbleActivity;
import ro.vodafone.mcare.android.ui.activities.DashboardActivity;
import ro.vodafone.mcare.android.ui.activities.SplashScreenActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.activities.yourServices.YourServicesActivity;
import ro.vodafone.mcare.android.ui.fragments.yourServices.AncomAcceptedOffersFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.utils.navigation.notification.DeepLinkDispatcher;
import ro.vodafone.mcare.android.utils.navigation.notification.NotificationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.Subscription;

import static android.view.View.GONE;
import static com.urbanairship.UAirship.getPackageManager;
import static ro.vodafone.mcare.android.ui.activities.offers.OffersActivity.DEEP_LINK_KEY;
import static ro.vodafone.mcare.android.utils.navigation.notification.NotificationNavigationAction.setLastTimeWhenValidNotificationNavigationActionStarted;

/**
 * Created by Bivol Pavel on 20.03.2017.
 */
public abstract class YourServicesBaseFragment extends OffersFragment {
    public static String TAG = "YourServicesBaseF";
    private static final int WAITING_TIME = 5000;

    @BindView(R.id.section_title_label)
    VodafoneTextView sectionTitleLabel;

    @BindView(R.id.offer_name)
    VodafoneTextView offerName;

    @BindView(R.id.cost_label)
    VodafoneTextView costLabel;

    @BindView(R.id.offer_cost)
    VodafoneTextView cost;

    @BindView(R.id.duration_label)
    VodafoneTextView durationLabel;

    @BindView(R.id.duration)
    VodafoneTextView duration;

    @BindView(R.id.start_date)
    VodafoneTextView startDateText;

    @BindView(R.id.end_date)
    VodafoneTextView endDateText;

    @BindView(R.id.contract_period_label)
    VodafoneTextView contractPeriodLabel;

    @BindView(R.id.contract_period)
    VodafoneTextView contractPeriod;

    @BindView(R.id.contract_details_duration_layout)
    LinearLayout contractDetailsDurationLayout;

    @BindView(R.id.contract_details_valability)
    VodafoneTextView contractDetailsValability;

    @BindView(R.id.contract_details_valability_date)
    VodafoneTextView contractDetailsValabilityDate;

    @BindView(R.id.contract_details_period)
    VodafoneTextView contractDetailsPeriod;

    @BindView(R.id.contract_details_period_date)
    VodafoneTextView contractDetailsPeriodDate;
    @BindView(R.id.contract_details_expired)
    VodafoneTextView contractDetailsExpired;

    @BindView(R.id.go_to_accepted_offers)
    Button goToAcceptedOffersButton;
    @BindView(R.id.go_to_my_offers)
    Button goToMyOffersButton;

    @BindView(R.id.expandable_cards_container)
    LinearLayout expandableCardsContainer;

    @BindView(R.id.bottom_view_container)
    LinearLayout bottomViewContainer;

    @BindView(R.id.error_card)
    CardErrorLayout cardErrorLayout;

    @BindView(R.id.cost_container)
    LinearLayout costContainer;
    @BindView(R.id.title_section_container)
    LinearLayout titleSectionContainer;
    @BindView(R.id.contract_period_layout)
    LinearLayout contractPeriodLayout;
    @BindView(R.id.contract_duration_layout)
    LinearLayout contractDurationLayout;

    EbuOfferEligibilitySuccess ebuOfferEligibilitySuccess;

    public Promo promo;
    public BillingOffer billingOffer;
    public OfferRowInterface offerRow;

    private Dialog etfOverlay;
    String oneUsageSerializedData;
    User user;
    String oferteleMeleDeeplink;


    long startTime;
    long endTime;

    private Runnable r;

    public YourServicesBaseFragment setArgsOnBundle(String key, Serializable serializableObject) {

        Bundle args = new Bundle();
        args.putSerializable(key, serializableObject);
        setArguments(args);

        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "checkBenBan: selectedBan: " + UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan());
        Log.d(TAG, "checkBenBan: selectedBen: " + UserSelectedMsisdnBanController.getInstance().getSelectedEbuBen());

        getParametersFromBundle(getArguments());
        user = VodafoneController.getInstance().getUser();
    }

    abstract void getParametersFromBundle(Bundle bundle);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.card_contract_details, container, false);
        ButterKnife.bind(this, v);

        try {
            ((YourServicesActivity) getActivity()).getNavigationHeader().hideSelectorView();
            ((YourServicesActivity) getActivity()).getToolbar().showToolBar();
            ((YourServicesActivity) getActivity()).getMenuScrollView().scrollTo(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setLabels();
        setAttributes();
        addingAdditionalView();
        trackView();
        return v;
    }

    public void checkIfContainersHasVisibleChilds() {
        if (!isVisibleChildInLayout(contractDurationLayout))
            contractDurationLayout.setVisibility(GONE);
        if (!isVisibleChildInLayout(titleSectionContainer))
            titleSectionContainer.setVisibility(GONE);
        if (!isVisibleChildInLayout(costContainer))
            costContainer.setVisibility(GONE);
        if (!isVisibleChildInLayout(contractPeriodLayout))
            contractPeriodLayout.setVisibility(GONE);
        if (!isVisibleChildInLayout(contractDetailsDurationLayout))
            contractDetailsDurationLayout.setVisibility(GONE);
    }

    private boolean isVisibleChildInLayout(LinearLayout layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            if (layout.getChildAt(i).getVisibility() == View.VISIBLE)
                return true;
        }
        return false;
    }

    abstract void setLabels();

    abstract void setAttributes();

    abstract void trackView();

    abstract void addingAdditionalView();

    public void setOfferName(String offerName) {
        this.offerName.setText(offerName);
    }

    public void setCost(String cost) {
        //According to new requirements , cost should not be bolded!
        //this.cost.setText(makeOfferPriceBoldText(cost));
        if (cost != null) {
            this.cost.setText(cost);
        } else {
            this.costContainer.setVisibility(GONE);
        }
    }

    public void setStartDate(String startDate) {
        if (startDate != null) {
            startDateText.setText(startDate);
            startDateText.setVisibility(View.VISIBLE);
        }
    }

    public void setEndDate(String endDate) {
        if (endDate != null) {
            endDateText.setText(endDate);
            endDateText.setVisibility(View.VISIBLE);
        }
    }

    public void setDuration(String duration) {
        if (duration != null) {
            this.duration.setText(duration);
            this.duration.setVisibility(View.VISIBLE);
        }
    }

    public void setLimits(String duration, String startDate, String endDate) {
        if (duration == null && startDate == null && endDate == null) {
            contractDurationLayout.setVisibility(GONE);
            return;
        }

        contractDurationLayout.setVisibility(View.VISIBLE);
        durationLabel.setVisibility(View.VISIBLE);

        setDuration(duration);
        setStartDate(startDate);
        setEndDate(endDate);
    }

    public void setLimitsContractDetails(Boolean contractExpired, String startDate, String endDate) {
        contractDurationLayout.setVisibility(GONE);
        contractPeriodLayout.setVisibility(GONE);
        if (startDate != null) {
            contractDetailsValabilityDate.setText(startDate);
            contractDetailsValabilityDate.setVisibility(View.VISIBLE);
            contractDetailsValability.setVisibility(View.VISIBLE);
        } else {
            contractDetailsValabilityDate.setVisibility(View.GONE);
            contractDetailsValability.setVisibility(View.GONE);
            contractDetailsExpired.setVisibility(GONE);
        }
        if (endDate != null) {
            if (contractExpired == true) {
                contractDetailsExpired.setVisibility(View.VISIBLE);
                contractDetailsExpired.setText(ServicesLabels.getServicesContractPeriodExpired());
            } else {
                contractDetailsPeriodDate.setText(endDate);
                contractDetailsPeriodDate.setVisibility(View.VISIBLE);
                contractDetailsPeriod.setVisibility(View.VISIBLE);
            }
        } else {
            contractDetailsPeriodDate.setVisibility(View.GONE);
            contractDetailsPeriod.setVisibility(View.GONE);
        }
    }

    public void setConfigurableButtonOferteleMele(String oferteleMeleButtonLabel, String oferteleMeleDeeplink, Boolean oferteleMeleShowButton) {
        if (oferteleMeleShowButton == true) {
            goToMyOffersButton.setVisibility(View.VISIBLE);
            goToMyOffersButton.setText(oferteleMeleButtonLabel);
            this.oferteleMeleDeeplink = oferteleMeleDeeplink;
        } else {
            goToMyOffersButton.setVisibility(GONE);
        }
    }

    public void setContractPeriod(String contractPeriod) {
        if (contractPeriod != null) {
            this.contractPeriod.setText(contractPeriod);
            this.contractPeriod.setVisibility(View.VISIBLE);
            contractPeriodLabel.setVisibility(View.VISIBLE);
        }
    }

    private SpannableString makeOfferPriceBoldText(String offerPrice) {

        SpannableString spannableString = null;

        if (offerPrice != null) {
            spannableString = new SpannableString(offerPrice);
            setBoldTypeFace(spannableString, 0, getLastBoldPosition(spannableString.toString(), "€"));
        }

        return spannableString;
    }

    private void setBoldTypeFace(SpannableString spannableString, int start, int end) {
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, 0);
    }

    private int getLastBoldPosition(String label, String lastBoldSequence) {
        return label.indexOf(lastBoldSequence);
    }

    public void initGoToAcceptedOffersButton() {
        goToAcceptedOffersButton.setVisibility(View.VISIBLE);
    }

    public void hideOfferPriceAndCostLabel() {
        costContainer.setVisibility(GONE);
    }

    @OnClick(R.id.go_to_accepted_offers)
    public void goToacceptedOffersClick() {
        ((YourServicesActivity) getContext()).attachFragment(new AncomAcceptedOffersFragment());
    }

    @OnClick(R.id.go_to_my_offers)
    public void goToMyOffersClick() {
        try {
            if (oferteleMeleDeeplink.toLowerCase().contains("mcareRedirect.jsp".toLowerCase()) ||
                    oferteleMeleDeeplink.toLowerCase().contains("myvodafone://".toLowerCase())) {
                startNavigationAction(DeepLinkDispatcher.getNotificationAction(Uri.parse(oferteleMeleDeeplink)));
                if (getActivity() != null)
                    getActivity().finish();
            } else {
                openWebPageInBrowser(oferteleMeleDeeplink);
            }
        } catch (Exception e) {
        }
    }

    public void openWebPageInBrowser(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void startNavigationAction(NotificationAction notificationAction) {
        if (notificationAction != null)
            new NavigationAction(getContext()).startAction(notificationAction.getIntentActionName());
    }


    public List<Benefit> getSortedListOfBenefits() {
        List<Benefit> benefits = new ArrayList<>();

        if (promo != null && promo.getBoList() != null && !promo.getBoList().isEmpty()) {
            for (BillingOffer billingOffer : promo.getBoList()) {
                if (billingOffer.getBenefitList() != null && !billingOffer.getBenefitList().isEmpty()) {
                    benefits.addAll(billingOffer.getBenefitList());
                }
            }
        } else if (billingOffer != null) {
            if (billingOffer.getBenefitList() != null && !billingOffer.getBenefitList().isEmpty()) {
                benefits.addAll(billingOffer.getBenefitList());
            }
        }

        if (!benefits.isEmpty()) {
            Collections.sort(benefits, new Comparator<Benefit>() {
                @Override
                public int compare(Benefit o1, Benefit o2) {
                    return o1.getseqNumber().compareTo(o2.getseqNumber());
                }
            });
        }

        return benefits;
    }

    public void addOfferBenefitsCard(String title) {

        List<String> stringBenefitsList = new ArrayList<>();

        for (Benefit benefit : getSortedListOfBenefits()) {
            stringBenefitsList.add(benefit.getBenefitDescription());
        }

        if (stringBenefitsList.isEmpty())
            return;

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(ScreenMeasure.dpToPx(10), ScreenMeasure.dpToPx(10), ScreenMeasure.dpToPx(10), ScreenMeasure.dpToPx(10));

        for (String benefit : stringBenefitsList) {
            linearLayout.addView(makeBenefitsItem(benefit));
        }

        BaseExpandableCard pricePlanDetailsCard = new BaseExpandableCard(getContext());
        pricePlanDetailsCard.setTitle(title)
                .setContent(linearLayout)
                .build()
                .expand();

        expandableCardsContainer.addView(pricePlanDetailsCard);
    }

    private View makeBenefitsItem(String benefit) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.benefits_list_item, null);
        ((TextView) view.findViewById(R.id.beneffit_description)).setText(benefit);
        return view;
    }

    public void addOfferDisciption(String title, Promo promo) {
        if (promo == null || promo.getEShopDescription() == null || promo.getEShopDescription().equals("")) {
            return;
        }

        TextView textView = new TextView(getContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(ScreenMeasure.dpToPx(16), ScreenMeasure.dpToPx(16), ScreenMeasure.dpToPx(16), ScreenMeasure.dpToPx(16));
        textView.setLayoutParams(params);
        textView.invalidate();
        textView.setTextColor(Color.BLACK);
        textView.setText(promo.getEShopDescription());

        BaseExpandableCard pricePlanDetailsCard = new BaseExpandableCard(getContext());
        pricePlanDetailsCard.setTitle(title)
                .setContent(textView)
                .build()
                .expand();

        expandableCardsContainer.addView(pricePlanDetailsCard);
    }

    public void addExpandableWebViewCard(String webUrl) {
        ExpandableWebViewCard expandableCardInfo = new ExpandableWebViewCard(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        expandableCardInfo.setLayoutParams(params);

        expandableCardInfo.setVisibility(View.VISIBLE);
        VodafoneTextView expandableCardTitleView = (VodafoneTextView) expandableCardInfo.findViewById(R.id.card_title_tv);
        expandableCardTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        expandableCardTitleView.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray_text_color));
        expandableCardInfo.setVisibility(View.VISIBLE);
        expandableCardInfo.setTitle("Informații tarife");
        expandableCardInfo.hideLine();
        expandableCardInfo.setWebViewUrl(webUrl);

        expandableCardInfo.setImageArrowDirectionDown();
        expandableCardInfo.build();

        expandableCardInfo.setCardPaddingsInDp(0, 16, 16, 16);

        expandableCardsContainer.addView(expandableCardInfo);
    }

    public boolean isRemoveButtonDisplayedForEbu(boolean allowDelete) {
        CustomerRestrictionsSuccess customerRestrictionsSuccess = (CustomerRestrictionsSuccess)
                RealmManager.getRealmObject(CustomerRestrictionsSuccess.class);
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
        User user = VodafoneController.getInstance().getUser();

        if (customerRestrictionsSuccess == null || customerRestrictionsSuccess.getIsBlacklistForever()
                || customerRestrictionsSuccess.getIsCollectionRestricted() || customerRestrictionsSuccess.getIsDeviceBlacklist()
                || customerRestrictionsSuccess.getIsServiceBadDebt()) {
            return false;
        }

        String customerSegment = null;
        if (user instanceof EbuMigrated) {
            if (EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null)
                customerSegment = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getTreatmentSegment();
        } else {
            customerSegment = profile.getCustomerSegment();
        }

        if (customerSegment == null || AppConfiguration.getEbuMigratedIneligibleToDeleteOfferSegment().contains(customerSegment)) {
            return false;
        }

        if (promo.getPromoName() == null || promo.getOfferId() == null) {
            return false;
        }

        if (promo.getDeactivationDate() != null && promo.getDeactivationDate() == -1) {
            return false;
        }

        if (promo.getRelationType()) {
            return false;
        }

        if (!allowDelete) {
            return false;
        }

        return true;
    }


    public void addDeleteOptionButton(String label) {
        Log.d(TAG, "addDeleteOptionButton: ");
        final VodafoneAbstractCard deleteOptionBtnCard = new VodafoneAbstractCard(getContext()) {
            @Override
            protected int setContent() {
                return 0;
            }
        };

        VodafoneButton deleteOptionButton = makeDeleteButton(label);

        deleteOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put("screen_name", "option details");
                tealiumMapEvent.put("event_name", "mcare:option details:button:sterge optiunea");
                tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("event_name", tealiumMapEvent);

                if (user instanceof PrepaidUser) {
                    displayStopOptionPrepaidOverlay();
                } else if (user instanceof CBUUser) {
                    displayStopOptionPostPaidOverlay();
                } else if (user instanceof EbuMigrated) {
                    displayStopOptionEbuOverlay();
                }
            }
        });

        deleteOptionBtnCard.addButton(deleteOptionButton);
        bottomViewContainer.addView(deleteOptionBtnCard);
    }

    private VodafoneButton makeDeleteButton(String label) {
        VodafoneButton deleteOptionButton = new VodafoneButton(getContext());
        deleteOptionButton.setTransformationMethod(null);
        deleteOptionButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        deleteOptionButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white_text_color));
        deleteOptionButton.setTypeface(Fonts.getVodafoneRG());
        deleteOptionButton.setBackgroundResource(R.drawable.selector_button_background_card_secondary);
        deleteOptionButton.setText(label);
        return deleteOptionButton;
    }

    private void displayStopOptionPrepaidOverlay() {
        Log.d(TAG, "displayStopOptionPrepaidOverlay()");
        final Dialog confirmationOverlay;
        confirmationOverlay = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        confirmationOverlay.setContentView(R.layout.overlay_dialog_notifications);
        confirmationOverlay.show();

        Button buttonAccept = (Button) confirmationOverlay.findViewById(R.id.buttonKeepOn);
        Button buttonRefuse = (Button) confirmationOverlay.findViewById(R.id.buttonTurnOff);

        VodafoneTextView overlayTitle = (VodafoneTextView) confirmationOverlay.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) confirmationOverlay.findViewById(R.id.overlaySubtext);

        buttonAccept.setText(ServicesLabels.getServices_delete_option_button());
        buttonRefuse.setText(AppLabels.getGiveUpButton());

        overlayTitle.setText(ServicesLabels.getServices_delete_option_page_title());
        overlaySubtext.setText(ServicesLabels.getServices_delete_option_page_content());

        ImageView overlayDismissButton = (ImageView) confirmationOverlay.findViewById(R.id.overlayDismissButton);

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationOverlay.dismiss();
            }
        });

        buttonRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationOverlay.dismiss();
            }
        });

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOptionPrepaid();
                confirmationOverlay.dismiss();
            }
        });
    }

    private void deleteOptionPrepaid() {
        Log.d(TAG, "deleteOptionPrepaid()");

        if (offerRow == null)
            return;

        ActivatePrepaidOfferRequest activatePrepaidOfferRequest = new ActivatePrepaidOfferRequest();
        activatePrepaidOfferRequest.setOperation("4");

        OffersService offersService = new OffersService(getContext());
        Subscription subscription = offersService.activateEligibleOffer(String.valueOf(offerRow.getOfferId()), activatePrepaidOfferRequest)
                .subscribe(new RequestSessionObserver<GeneralResponse<EligibleOffersSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<EligibleOffersSuccess> eligibleOffersSuccessResponse) {
                        if (eligibleOffersSuccessResponse.getTransactionStatus() == 0) {
                            Log.d(TAG, "activateOrInactivateRoaming Transaction Status 0");

                            new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD, true);
                            new CustomToast.Builder(getContext()).message(ServicesLabels.getService_stop_offer()).success(true).show();
                        } else {
                            Log.d(TAG, "activateOrInactivateRoaming Transaction Status !0");
                            new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.d(TAG, "activateOrInactivateRoaming onError");
                        new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
                    }
                });
        addToActivityCompositeSubcription(subscription);
    }

    private void deleteOptionPostpaid() {

        String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null
                ? UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getMsisdn() : null;
        String subscriberId = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null
                ? UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid() : null;

        if (offerRow == null)
            return;

        ActiveOfferPostpaid activeOfferPostpaid = (ActiveOfferPostpaid) offerRow;
        OffersService offersService = new OffersService(getContext());
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
        int bilCycleDate = 0;

        if (msisdn == null) {
            msisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();
        }

        if (subscriberId == null) {
            subscriberId = VodafoneController.getInstance().getUserProfile().getSid();
        }

        if (profile != null) {
            bilCycleDate = profile.getBillCycleDate();
        }

        if (msisdn.startsWith("0")) {
            msisdn = msisdn.replaceFirst("0", "40");
        }
        Log.d(TAG, " concat msisdn " + msisdn);

        Subscription subscription = offersService.deletePostPaidOffer(msisdn, subscriberId, activeOfferPostpaid.getOfferId(), activeOfferPostpaid.getOfferInstanceId(),
                new ActivationRequest(activeOfferPostpaid.getOfferName(), bilCycleDate, activeOfferPostpaid.getOfferType()))
                .subscribe(new RequestSessionObserver<GeneralResponse<ActivationEligibilitySuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<ActivationEligibilitySuccess> response) {
                        if (response.getTransactionStatus() == 0) {
                            ((BaseActivity) getContext()).stopLoadingDialog();
                            new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD, true);
                            new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_option_inactivated_succes_message()).success(true).show();
                        } else {
                            ((BaseActivity) getContext()).stopLoadingDialog();
                            new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ((BaseActivity) getContext()).stopLoadingDialog();
                    }
                });
        addToActivityCompositeSubcription(subscription);
    }

    private void deactivateOfferForEbu() {

        if (promo == null)
            return;

        showLoadingDialog();

        OffersService offersService = new OffersService(getContext());
        UserProfile userProfile = VodafoneController.getInstance().getUserProfile();
        EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();

        final Subscription subscription = offersService.deactivateOfferForEbu(entityChildItem.getVfOdsCid(),
                userProfile.getContactId(), entityChildItem.getCrmRole(), UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan(),
                UserSelectedMsisdnBanController.getInstance().getSubscriberSid(), UserSelectedMsisdnBanController.getInstance().getSelectedEbuBen(), UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn(), promo.getOfferId(),
                promo.getPromoName())
                .subscribe(new RequestSessionObserver<GeneralResponse<ActivationEligibilitySuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<ActivationEligibilitySuccess> activationEligibilitySuccessGeneralResponse) {
                        stopLoadingDialog();
                        manageDeactivationOfferForEBu(activationEligibilitySuccessGeneralResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        stopLoadingDialog();
                        new CustomToast.Builder(VodafoneController.getInstance()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
                    }
                });

        initRunnableIfIsNotSubUser(subscription);
    }

    private void manageDeactivationOfferForEBu(GeneralResponse<ActivationEligibilitySuccess> activationEligibilitySuccessGeneralResponse) {
        if (VodafoneController.getInstance().getUser() instanceof SubUserMigrated) {
            new NavigationAction(getActivity()).startAction(IntentActionName.DASHBOARD, true);
            if (activationEligibilitySuccessGeneralResponse.getTransactionStatus() == 0) {
                displaySubUserConfirmationMessage();
            } else {
                new CustomToast.Builder(VodafoneController.getInstance()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
            }
        } else {
            unsubscribeSubscriptionFromController();
            VodafoneController.getInstance().handler.removeCallbacks(r);

            if (activationEligibilitySuccessGeneralResponse.getTransactionStatus() == 0) {
                if (getApiWaitingTime() < WAITING_TIME) {
                    new NavigationAction(getActivity()).startAction(IntentActionName.DASHBOARD, true);
                    displayConfirmationMessage(false);
                } else {
                    displayConfirmationMessage(true);
                }
            } else {
                displayErrorVov();
            }
        }
    }

    private void initRunnableIfIsNotSubUser(final Subscription subscription) {
        if (!(VodafoneController.getInstance().getUser() instanceof SubUserMigrated)) {
            startTime = System.nanoTime();
            r = new Runnable() {
                @Override
                public void run() {
                    VodafoneController.getInstance().setSubscription(subscription);
                    new NavigationAction(getActivity()).startAction(IntentActionName.DASHBOARD, true);
                    displaySendRequestMessage();
                }
            };
            VodafoneController.getInstance().handler.postDelayed(r, WAITING_TIME);
        }
    }

    private Long getApiWaitingTime() {
        endTime = System.nanoTime();
        return endTime - startTime;
    }

    private void displayConfirmationMessage(boolean isMessageDelayed) {

        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(9, 20, VoiceOfVodafoneCategory.EO_Deactivation, null,
                createVovMessage(ServicesLabels.getService_deactivate_promo_success_message(), promo.getPromoName()),
                ServicesLabels.getService_vov_button_text(), null, true, false, VoiceOfVodafoneAction.Dismiss, null);
        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);

        if (isMessageDelayed) {
            new CustomToast.Builder(VodafoneController.getInstance()).message(ServicesLabels.getService_deactivate_promo_success_toast_message()).success(true).show();
        }
    }

    private void displaySubUserConfirmationMessage() {
        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(9, 20, VoiceOfVodafoneCategory.EO_Deactivation, null,
                createVovMessage(ServicesLabels.getService_deactivate_promo_subuser_succes_message(), promo.getPromoName()),
                ServicesLabels.getService_vov_button_text(), null, true, false, VoiceOfVodafoneAction.Dismiss, null);
        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
    }

    private void displaySendRequestMessage() {
        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(9, 20, VoiceOfVodafoneCategory.EO_Deactivation, null,
                ServicesLabels.getService_deactivate_promo_delayed_success_message(),
                ServicesLabels.getService_vov_button_text(), null, true, false, VoiceOfVodafoneAction.Dismiss, null);
        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);

        new CustomToast.Builder(VodafoneController.getInstance()).message(ServicesLabels.getService_deactivate_promo_delayed_toast_success_message()).success(true).show();
    }

    private void displayErrorVov() {
        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(9, 20, VoiceOfVodafoneCategory.EO_Deactivation, null,
                createVovMessage(ServicesLabels.getService_activation_error_message(), promo.getPromoName()),
                ServicesLabels.getService_vov_button_text(), null, true, false, VoiceOfVodafoneAction.Dismiss, null);
        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
        VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();
    }

    private void unsubscribeSubscriptionFromController() {
        VodafoneController.getInstance().getSubscription().unsubscribe();
        VodafoneController.getInstance().setSubscription(null);
    }

    private String createVovMessage(String label, String promoName) {
        return String.format(label, promoName);
    }


    private void displayStopOptionPostPaidOverlay() {
        Log.d(TAG, "displayStopOptionPostPaidOverlay()");
        final Dialog confirmationOverlay;
        confirmationOverlay = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        confirmationOverlay.setContentView(R.layout.overlay_dialog_notifications);
        confirmationOverlay.show();

        Button buttonAccept = (Button) confirmationOverlay.findViewById(R.id.buttonKeepOn);
        Button buttonRefuse = (Button) confirmationOverlay.findViewById(R.id.buttonTurnOff);

        VodafoneTextView overlayTitle = (VodafoneTextView) confirmationOverlay.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) confirmationOverlay.findViewById(R.id.overlaySubtext);

        buttonAccept.setText(ServicesLabels.getServices_inactivate_option_button());
        buttonRefuse.setText(AppLabels.getGiveUpButton());


        String offerName = "";
        if (promo != null) {
            offerName = promo.getPromoName();
        } else if (billingOffer != null) {
            offerName = billingOffer.getBoName();
        }

        overlayTitle.setText(ServicesLabels.getServices_inactivate_option_page_title());
        overlaySubtext.setText(ServicesLabels.getServices_inactivate_option_page_content() + offerName);

        ImageView overlayDismissButton = (ImageView) confirmationOverlay.findViewById(R.id.overlayDismissButton);

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationOverlay.dismiss();
            }
        });

        buttonRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationOverlay.dismiss();
            }
        });

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPendingOffers4PostPaid();
                confirmationOverlay.dismiss();
            }
        });
    }

    private void getPendingOffers4PostPaid() {
        Log.d(TAG, "getPendingOffers4PostPaid()");
        ((BaseActivity) getContext()).showLoadingDialog();
        OffersService offersService = new OffersService(getContext());

        Subscription subscription = offersService.getPendingOffers4PostPaid(UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber()
                != null ? UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getSid()
                : VodafoneController.getInstance().getUserProfile().getSid(), "offer").subscribe(new RequestSessionObserver<GeneralResponse>() {
            @Override
            public void onNext(GeneralResponse activeOffersSuccessResponse) {
                Log.d(TAG, "getPendingOffers4PostPaid() Transaction Status = " + activeOffersSuccessResponse.getTransactionStatus());
                if (activeOffersSuccessResponse.getTransactionStatus() == 0) {
                    checkETF();
                } else if (activeOffersSuccessResponse.getTransactionStatus() == 1) {
                    ((BaseActivity) getContext()).stopLoadingDialog();
                    new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_pending_requests_message_cbu()).success(false).show();
                } else if (activeOffersSuccessResponse.getTransactionStatus() == 2) {
                    ((BaseActivity) getContext()).stopLoadingDialog();
                    new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d(TAG, "getPendingOffers4PostPaid() onError");
                ((BaseActivity) getContext()).stopLoadingDialog();
                new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
            }
        });

        addToActivityCompositeSubcription(subscription);
    }

    private void checkETF() {
        Log.d(TAG, "checkETF()");
        try {
            if (offerRow == null)
                return;

            Boolean hasETF = ((ActiveOfferPostpaid) offerRow).getHasETF();

            if (hasETF != null && hasETF) {
                ((BaseActivity) getContext()).stopLoadingDialog();
                displayEtfOverlay();
            } else {
                deleteOptionPostpaid();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ;
            ((BaseActivity) getContext()).stopLoadingDialog();
            new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
        }
    }

    private void displayEtfOverlay() {
        etfOverlay = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        etfOverlay.setContentView(R.layout.overlay_dialog_notifications);

        Button dismissButton = (Button) etfOverlay.findViewById(R.id.buttonTurnOff);
        Button okButton = (Button) etfOverlay.findViewById(R.id.buttonKeepOn);
        VodafoneTextView etfOverlayTitle = (VodafoneTextView) etfOverlay.findViewById(R.id.overlayTitle);
        VodafoneTextView etfOverlayMessage = (VodafoneTextView) etfOverlay.findViewById(R.id.overlaySubtext);
        ImageView closeButton = (ImageView) etfOverlay.findViewById(R.id.overlayDismissButton);

        etfOverlayTitle.setText(ServicesLabels.getServices_etf_overlay_title());

        if (user instanceof EbuMigrated) {
            if (!(user instanceof SubUserMigrated)) {
                etfOverlayMessage.setText(String.format(ServicesLabels.getServices_subuser_etf_overlay_message(),
                        NumbersUtils.twoDigitsAfterDecimal(ebuOfferEligibilitySuccess.getTotalCostAmount())));
                okButton.setText(ServicesLabels.getServices_subuser_etf_ok_button_text());
                okButton.setOnClickListener(eftOverlayOkButtonsListener);
                dismissButton.setText(ServicesLabels.getServices_subuser_etf_dismiss_button_text());
            } else {
                etfOverlayMessage.setText(ServicesLabels.getServices_ebu_etf_overlay_message());
                okButton.setVisibility(GONE);
                dismissButton.setText(ServicesLabels.getServices_delete_option_dismiss_button_text());
            }
        } else {
            //CBU users
            etfOverlayMessage.setText(ServicesLabels.getServices_etf_overlay_message());
            okButton.setVisibility(GONE);
            dismissButton.setText(AppLabels.getGiveUpButton());
        }

        dismissButton.setOnClickListener(eftOverlayDismissButtonsListener);
        closeButton.setOnClickListener(eftOverlayDismissButtonsListener);

        etfOverlay.show();
    }

    View.OnClickListener eftOverlayDismissButtonsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            etfOverlay.dismiss();
            if (user instanceof CBUUser) {
                ((ChatBubbleActivity) getContext()).getChatBubble().displayBubble(true);
            }
        }
    };

    View.OnClickListener eftOverlayOkButtonsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            etfOverlay.dismiss();
            deactivateOfferForEbu();
        }
    };

    private void displayStopOptionEbuOverlay() {
        Log.d(TAG, "displayStopOptionPostPaidOverlay()");
        final Dialog confirmationOverlay;
        confirmationOverlay = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        confirmationOverlay.setContentView(R.layout.overlay_dialog_notifications);
        confirmationOverlay.show();

        Button buttonAccept = (Button) confirmationOverlay.findViewById(R.id.buttonKeepOn);
        Button buttonRefuse = (Button) confirmationOverlay.findViewById(R.id.buttonTurnOff);

        VodafoneTextView overlayTitle = (VodafoneTextView) confirmationOverlay.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) confirmationOverlay.findViewById(R.id.overlaySubtext);

        buttonAccept.setText(ServicesLabels.getServices_inactivate_option_button());
        buttonRefuse.setText(ServicesLabels.getServices_delete_option_dismiss_button_text());

        overlayTitle.setText(ServicesLabels.getServices_delete_option_page_title_ebu());
        overlaySubtext.setText(ServicesLabels.getServices_delete_option_content_text_ebu() + offerName.getText());

        ImageView overlayDismissButton = (ImageView) confirmationOverlay.findViewById(R.id.overlayDismissButton);

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationOverlay.dismiss();
            }
        });

        buttonRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationOverlay.dismiss();
            }
        });

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationOverlay.dismiss();
                if (checkEbuEtf()) {
                    displayEtfOverlay();
                } else {
                    deactivateOfferForEbu();
                }
            }
        });
    }

    private boolean checkEbuEtf() {
        return ebuOfferEligibilitySuccess != null && ebuOfferEligibilitySuccess.getTotalCostAmount() != null
                && ebuOfferEligibilitySuccess.getTotalCostAmount() > 0;
    }

    public void checkEbuOfferDeleteEligibility() {

        if (promo == null)
            return;

        showLoadingDialog();

        OffersService offersService = new OffersService(getContext());
        UserProfile userProfile = VodafoneController.getInstance().getUserProfile();
        EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
        Subscriber subscriber = UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber();

        Subscription subscription = offersService.checkEbuOfferDeleteEligibility(entityChildItem.getCrmRole(),
                subscriber.getSid(), userProfile.getContactId(), promo.getOfferId())
                .subscribe(new RequestSessionObserver<GeneralResponse<EbuOfferEligibilitySuccess>>() {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        stopLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        stopLoadingDialog();
                    }

                    @Override
                    public void onNext(GeneralResponse<EbuOfferEligibilitySuccess> ebuOfferEligibilitySuccessGeneralResponse) {

                        if (ebuOfferEligibilitySuccessGeneralResponse.getTransactionStatus() == 0) {
                            ebuOfferEligibilitySuccess = ebuOfferEligibilitySuccessGeneralResponse.getTransactionSuccess();
                            if (isRemoveButtonDisplayedForEbu(ebuOfferEligibilitySuccess.getAllowDelete())) {
                                addDeleteOptionButton(ServicesLabels.getServices_delete_option_card_button());
                            }
                        } else {
                            if (ebuOfferEligibilitySuccessGeneralResponse.getTransactionFault().getFaultCode()
                                    .equals(ErrorCodes.API41_HAS_PENDING_REQUESTS.getErrorCode())) {
                                displayPendingRequestMessage(ServicesLabels.getServices_pending_request_message());
                            }
                        }
                    }
                });

        addToActivityCompositeSubcription(subscription);
    }

    public void displayPendingRequestMessage(String message) {
        cardErrorLayout.setMargins(0, 16, 0, 0);
        cardErrorLayout.setText(message);
        cardErrorLayout.setVisibility(View.VISIBLE);
    }

    public String getActivationDate(Long date) {
        String result = "Data activare: Opţiune activă";
//        String result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", new Locale("RO", "RO"));
        String formatedDate = WordUtils.capitalize(DateUtils.getDate(date, sdf));

        if (formatedDate != null) {
            result = String.format(getContext().getResources().getString(R.string.your_option_activation_date),
                    formatedDate);
            //         result = String.format("%1$s",
            //               formatedDate);
        }

        return result;
    }

    public String getPromoDeactivationDate(Long date) {
        String result = "Data dezactivare: La cerere";
//        String result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", new Locale("RO", "RO"));
        String formatedDate = WordUtils.capitalize(DateUtils.getDate(date, sdf));

        if (formatedDate != null) {
            result = String.format(getContext().getResources().getString(R.string.your_option_deactivation_date),
                    formatedDate);
//            result = String.format("%1$s",
            //                  formatedDate);
        }

        return result;
    }

    public String getContractPeriodText(String contractPeriod) {

        if (contractPeriod == null || contractPeriod.equals("0") || contractPeriod.equals(""))
            return null;

        return String.format(getContext().getResources().getString(R.string.your_option_months), (contractPeriod));
    }
}



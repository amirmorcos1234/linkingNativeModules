package ro.vodafone.mcare.android.card;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.offers.ExpandableWebViewCard;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.ActivationEligibilitySuccess;
import ro.vodafone.mcare.android.client.model.beo.postpaid.activation.ActivationRequest;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleCategoriesPost;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.EligibleOffersPostSuccess;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow;
import ro.vodafone.mcare.android.client.model.beo.prepaid.activeOffers.ActiveOffer;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.ActivatePrepaidOfferRequest;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.EligibleOffersSuccess;
import ro.vodafone.mcare.android.client.model.offers.ActiveOfferPostpaid;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.ServicesLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.CorpSubUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.CorpUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.EbuNonMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.SubUserNonMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;
import ro.vodafone.mcare.android.ui.activities.ChatBubbleActivity;
import ro.vodafone.mcare.android.ui.fragments.yourServices.YourServicesOptionDetailsFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by Bivol Pavel on 11.04.2017.
 */

@Deprecated
public class OptionDetailsCard extends VodafoneAbstractCard {
    public static String TAG = "ServicesDetailsCard";

    private CustomToast toast=new CustomToast.Builder(getContext()).build();

    List<View> views = new ArrayList<>();
    List<View> btnsList = new ArrayList<>();
    User user = VodafoneController.getInstance().getUser();
    String webUrl;
    VodafoneButton deleteOptionButton;
    private Dialog etfOverlay;

    public OptionDetailsCard(Context context) {
        super(context);
        init(null);
    }

    public OptionDetailsCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public OptionDetailsCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected int setContent() {
        return R.layout.card_option_details;
    }

    private void init(AttributeSet attrs){
        setCardPaddingsInDp(16, 16, 16, 16);
    }

   /* private void addInternationalAndRoamingCard() {
        VodafoneAbstractCard internationalButton = new VodafoneAbstractCard(mContext) {
            @Override
            protected int setContent() {
                return 0;
            }
        };
        internationalButton.setCardPaddingsInDp(16, 16, 16, 16);
        internationalButton.addButton(new CardButton(mContext)
                .buildButton(new CardButtonModel(ServicesLabels.getServices_info_tariffs_title(), new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //((YourServicesActivity)mContext).attachFragment(new YourServicesContractDetailsFragment());
                    }
                }, null)));
        views.add(internationalButton);
        addViewsToBottom(views);
    }*/

    private void addDeleteOptionButton() {
        Log.d(TAG, "addDeleteOptionButton: ");
        final VodafoneAbstractCard deleteOptionBtnCard = new VodafoneAbstractCard(getContext()) {
            @Override
            protected int setContent() {
                return 0;
            }
        };


        // ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), R.style.OverlaySecondaryButton);
        deleteOptionButton = new VodafoneButton(getContext());
        deleteOptionButton.setTransformationMethod(null);
        deleteOptionButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        deleteOptionButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white_text_color));
        deleteOptionButton.setTypeface(Fonts.getVodafoneRG());
        deleteOptionButton.setBackgroundResource(R.drawable.selector_button_background_card_secondary);
        deleteOptionButton.setText(ServicesLabels.getServices_delete_option_card_button());

        deleteOptionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put("screen_name","option details");
                tealiumMapEvent.put("event_name","mcare:option details:button:sterge optiunea");
                tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("event_name", tealiumMapEvent);

                if (user instanceof SeamlessPrepaidUser || user instanceof PrepaidUser) {
                    displayStopOptionPrepaidOverlay();
                } else if (user instanceof PostPaidUser) {
                    displayStopOptionPostPaidOverlay();
                }

            }
        });

        deleteOptionBtnCard.addButton(deleteOptionButton);
        btnsList.add(deleteOptionBtnCard);
        addViewsToBottom(btnsList);
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

        overlayDismissButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationOverlay.dismiss();
            }
        });

        buttonRefuse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationOverlay.dismiss();
            }
        });

        buttonAccept.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOptionPrepaid();
                confirmationOverlay.dismiss();
            }
        });
    }

    private void deleteOptionPrepaid() {
        Log.d(TAG, "deleteOptionPrepaid()");


        YourServicesOptionDetailsFragment yourServicesOptionDetailsFragment = (YourServicesOptionDetailsFragment) VodafoneController.findFragment(YourServicesOptionDetailsFragment.class);

        if(yourServicesOptionDetailsFragment == null)
            return;

        OfferRowInterface activeOfferPostpaid = yourServicesOptionDetailsFragment.getActiveOffer();

        ActivatePrepaidOfferRequest activatePrepaidOfferRequest = new ActivatePrepaidOfferRequest();
        activatePrepaidOfferRequest.setOperation("4");

        OffersService offersService = new OffersService(getContext());
        offersService.activateEligibleOffer(String.valueOf(activeOfferPostpaid.getOfferId()), activatePrepaidOfferRequest)
                .subscribe(new RequestSessionObserver<GeneralResponse<EligibleOffersSuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<EligibleOffersSuccess> eligibleOffersSuccessResponse) {
                        if (eligibleOffersSuccessResponse.getTransactionStatus() == 0) {
                            Log.d(TAG, "activateOrInactivateRoaming Transaction Status 0");
                            new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD,true);

                            toast.cancel();
                            toast=new CustomToast.Builder(getContext()).message(ServicesLabels.getService_stop_offer()).success(true).show();
//                            CustomToast customToast = new CustomToast((AppCompatActivity) getContext(), getContext(), ServicesLabels.getServices_option_stoped_succes_message(), true);
//                            customToast.show();
                        } else {
                            Log.d(TAG, "activateOrInactivateRoaming Transaction Status !0");
                            toast.cancel();
                            toast=new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
//                            CustomToast customToast = new CustomToast((AppCompatActivity) getContext(), getContext(), ServicesLabels.getServices_api_failed_message(), false);
//                            customToast.show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.d(TAG, "activateOrInactivateRoaming onError");
                        toast.cancel();
                        toast=new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
//                        CustomToast customToast = new CustomToast((AppCompatActivity) getContext(), getContext(), ServicesLabels.getServices_api_failed_message(), false);
//                        customToast.show();
                    }
                });
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

        overlayTitle.setText(ServicesLabels.getServices_inactivate_option_page_title());
        overlaySubtext.setText(ServicesLabels.getServices_inactivate_option_page_content());

        ImageView overlayDismissButton = (ImageView) confirmationOverlay.findViewById(R.id.overlayDismissButton);

        overlayDismissButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationOverlay.dismiss();
            }
        });

        buttonRefuse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationOverlay.dismiss();
            }
        });

        buttonAccept.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getPendingOffers4PostPaid();
                confirmationOverlay.dismiss();
            }
        });
    }

    private void getPendingOffers4PostPaid(){
        Log.d(TAG, "getPendingOffers4PostPaid()");
        ((BaseActivity)getContext()).showLoadingDialog();
        OffersService offersService = new OffersService(getContext());
        ;
        offersService.getPendingOffers4PostPaid(UserSelectedMsisdnBanController.getInstance().getSubscriberSid() != null
                ? UserSelectedMsisdnBanController.getInstance().getSubscriberSid()
                    : VodafoneController.getInstance().getUserProfile().getSid(), "offer")
                        .subscribe(new RequestSessionObserver<GeneralResponse>() {
            @Override
            public void onNext(GeneralResponse activeOffersSuccessResponse) {
                Log.d(TAG, "getPendingOffers4PostPaid() Transaction Status = " + activeOffersSuccessResponse.getTransactionStatus());
                if(activeOffersSuccessResponse.getTransactionStatus() == 0){
                    //getActivatePostPaidEligibleOffer();
                    checkETF();
                }else if(activeOffersSuccessResponse.getTransactionStatus() == 1){
                    ((BaseActivity)getContext()).stopLoadingDialog();
                    new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_pending_request_message()).success(false).show();
//                    CustomToast customToast = new CustomToast((AppCompatActivity) getContext(), getContext(), ServicesLabels.getServices_pending_request_message(), false);
//                    customToast.show();
                }else if(activeOffersSuccessResponse.getTransactionStatus() == 2){
                    ((BaseActivity)getContext()).stopLoadingDialog();
                    new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
//                    CustomToast customToast = new CustomToast((AppCompatActivity) getContext(), getContext(), ServicesLabels.getServices_api_failed_message(), false);
//                    customToast.show();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d(TAG, "getPendingOffers4PostPaid() onError");
                ((BaseActivity)getContext()).stopLoadingDialog();
                new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
//                CustomToast customToast = new CustomToast((AppCompatActivity) getContext(), getContext(), ServicesLabels.getServices_api_failed_message(), false);
//                customToast.show();
            }
        });
    }

    private void checkETF(){
        Log.d(TAG, "checkETF()");
        try {

            YourServicesOptionDetailsFragment yourServicesOptionDetailsFragment = (YourServicesOptionDetailsFragment) VodafoneController.findFragment(YourServicesOptionDetailsFragment.class);

            if(yourServicesOptionDetailsFragment == null)
                return;

            Boolean hasETF = ((ActiveOfferPostpaid) yourServicesOptionDetailsFragment.getActiveOffer()).getHasETF();

            if(hasETF != null && hasETF){
                ((BaseActivity)getContext()).stopLoadingDialog();
                displayEtfOverlay();
            }else{
                deleteOptionPostpaid();
            }
        }catch (Exception e){
            e.printStackTrace();;
            ((BaseActivity)getContext()).stopLoadingDialog();
            new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
//            CustomToast customToast = new CustomToast((AppCompatActivity) getContext(), getContext(), ServicesLabels.getServices_api_failed_message(), false);
//            customToast.show();
        }
    }

   /* private void getActivatePostPaidEligibleOffer(){
        Log.d(TAG, "getActivatePostPaidEligibleOffer()");

        OffersService offersService = new OffersService(getContext());

        YourServicesOptionDetailsFragment yourServicesOptionDetailsFragment = (YourServicesOptionDetailsFragment) VodafoneController.findFragment(YourServicesOptionDetailsFragment.class);

        if(yourServicesOptionDetailsFragment == null)
            return;

        ActiveOfferPostpaid activeOfferPostpaid = (ActiveOfferPostpaid) yourServicesOptionDetailsFragment.getActiveOffer();
        EligibleOffersPostSuccess eligibleOffersPostSuccess = (EligibleOffersPostSuccess) RealmManager.getRealmObject(EligibleOffersPostSuccess.class);

        String msisdn = NavigationHeader.getSelectedSubscriber() != null ? NavigationHeader.getSelectedSubscriber().getMsisdn() : VodafoneController.getInstance().getUserProfile().getMsisdn();
        String sid = NavigationHeader.getSelectedSubscriber() != null ? NavigationHeader.getSelectedSubscriber().getSid() : VodafoneController.getInstance().getUserProfile().getSid();

        offersService.getActivatePostPaidEligibleOffer(msisdn, sid,
                getOfferByOfferId(eligibleOffersPostSuccess, activeOfferPostpaid.getOfferId()) != null?
                        getOfferByOfferId(eligibleOffersPostSuccess, activeOfferPostpaid.getOfferId()).getMatrixId():null)
                .subscribe(new RequestSessionObserver<GeneralResponse<ActivationEligibilitySuccess>>() {
            @Override
            public void onNext(GeneralResponse<ActivationEligibilitySuccess> activationEligibilitySuccessGeneralResponse) {

                if(activationEligibilitySuccessGeneralResponse.getTransactionStatus() == 0){
                    if(activationEligibilitySuccessGeneralResponse.getTransactionSuccess().getHasETF() != null
                            && activationEligibilitySuccessGeneralResponse.getTransactionSuccess().getHasETF()){
                        ((BaseActivity)getContext()).stopLoadingDialog();
                        displayEtfOverlay();
                    }else{
                        deleteOptionPostpaid();
                    }
                }else{
                    ((BaseActivity)getContext()).stopLoadingDialog();
                    new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
//                    CustomToast customToast = new CustomToast((AppCompatActivity) getContext(), getContext(), ServicesLabels.getServices_api_failed_message(), false);
//                    customToast.show();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ((BaseActivity)getContext()).stopLoadingDialog();
                new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
//                CustomToast customToast = new CustomToast((AppCompatActivity) getContext(), getContext(), ServicesLabels.getServices_api_failed_message(), false);
//                customToast.show();
            }
        });
    }*/

    private PostpaidOfferRow getOfferByOfferId(EligibleOffersPostSuccess eligibleOffersPostSuccess, Long offerId){
        Log.d(TAG, "getOfferByOfferId() , offerId = " + offerId);
        PostpaidOfferRow result = null;

        if(eligibleOffersPostSuccess != null){
            if(eligibleOffersPostSuccess.getEligibleOptionsCategories() != null){
                for(EligibleCategoriesPost eligibleCategoriesPost : eligibleOffersPostSuccess.getEligibleOptionsCategories()){
                    for(PostpaidOfferRow postpaidOfferRow : eligibleCategoriesPost.getEligibleOffersList()){
                        if(postpaidOfferRow.getOfferId() != null && postpaidOfferRow.getOfferId().equals(offerId)){
                            result = postpaidOfferRow;
                            Log.d(TAG, "Option found ");
                        }
                    }

                }
            }
        }

        return result;
    }

    private void displayEtfOverlay() {
        etfOverlay = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        etfOverlay.setContentView(R.layout.overlay_dialog_notifications);
        Button dismissButton = (Button) etfOverlay.findViewById(R.id.buttonTurnOff);
        dismissButton.setText(AppLabels.getGiveUpButton());
        dismissButton.setOnClickListener(eftOverlayButtonsListener);

        Button okButton = (Button) etfOverlay.findViewById(R.id.buttonKeepOn);
        okButton.setVisibility(GONE);

        VodafoneTextView etfOverlayTitle = (VodafoneTextView) etfOverlay.findViewById(R.id.overlayTitle);
        etfOverlayTitle.setText(ServicesLabels.getServices_etf_overlay_title());
        VodafoneTextView etfOverlayMessage = (VodafoneTextView) etfOverlay.findViewById(R.id.overlaySubtext);
        etfOverlayMessage.setText(ServicesLabels.getServices_etf_overlay_message());

        ImageView closeButton = (ImageView) etfOverlay.findViewById(R.id.overlayDismissButton);
        closeButton.setOnClickListener(eftOverlayButtonsListener);

        etfOverlay.show();
    }

    private void deleteOptionPostpaid() {

        String msisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
        String subscriberId = UserSelectedMsisdnBanController.getInstance().getSubscriberSid();

        YourServicesOptionDetailsFragment yourServicesOptionDetailsFragment = (YourServicesOptionDetailsFragment) VodafoneController.findFragment(YourServicesOptionDetailsFragment.class);

        if(yourServicesOptionDetailsFragment == null)
            return;

        ActiveOfferPostpaid activeOfferPostpaid = (ActiveOfferPostpaid) yourServicesOptionDetailsFragment.getActiveOffer();
        OffersService offersService = new OffersService(getContext());
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);
        int bilCycleDate = 0;

        if (msisdn == null) {
            msisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();
        }

        if (subscriberId == null) {
            subscriberId = VodafoneController.getInstance().getUserProfile().getSid();
        }

        if(profile != null){
            bilCycleDate = profile.getBillCycleDate();
        }

        if (msisdn.startsWith("0")) {
            msisdn = msisdn.replaceFirst("0", "40");
        }
        Log.d(TAG," concat msisdn " +  msisdn);

        offersService.deletePostPaidOffer(msisdn, subscriberId, activeOfferPostpaid.getOfferId(), null,
                new ActivationRequest(activeOfferPostpaid.getOfferName(), bilCycleDate, activeOfferPostpaid.getOfferType()))
                .subscribe(new RequestSessionObserver<GeneralResponse<ActivationEligibilitySuccess>>() {
                    @Override
                    public void onNext(GeneralResponse<ActivationEligibilitySuccess> response) {
                        if (response.getTransactionStatus() == 0) {
                            ((BaseActivity)getContext()).stopLoadingDialog();
                            new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD,true);
                            new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_option_inactivated_succes_message()).success(true).show();
//                            CustomToast customToast = new CustomToast((AppCompatActivity) getContext(), getContext(), ServicesLabels.getServices_option_inactivated_succes_message(), true);
//                            customToast.show();
                        } else {
                            ((BaseActivity)getContext()).stopLoadingDialog();
                            new CustomToast.Builder(getContext()).message(ServicesLabels.getServices_api_failed_message()).success(false).show();
//                            CustomToast customToast = new CustomToast((AppCompatActivity) getContext(), getContext(), ServicesLabels.getServices_api_failed_message(), false);
//                            customToast.show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ((BaseActivity)getContext()).stopLoadingDialog();
                    }
                });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            Log.d(TAG, "onAttachedToWindow: ");
            Log.d(TAG, "user : " + user.toString());

            YourServicesOptionDetailsFragment yourServicesOptionDetailsFragment = (YourServicesOptionDetailsFragment) VodafoneController.findFragment(YourServicesOptionDetailsFragment.class);

            if(yourServicesOptionDetailsFragment == null)
                return;

            if (user instanceof SeamlessPrepaidUser || user instanceof PrepaidUser) {
                if (((ActiveOffer) yourServicesOptionDetailsFragment.getActiveOffer()).getIsRenewable() ||
                        ((ActiveOffer) yourServicesOptionDetailsFragment.getActiveOffer()).getAllowOfferDelete()) {
                    Log.d(TAG, "onAttachedToWindow: condition " + ((ActiveOffer) yourServicesOptionDetailsFragment.getActiveOffer()).getAllowOfferDelete());
                    addDeleteOptionButton();
                }
            } else if (user instanceof PostPaidUser && !(user instanceof EbuNonMigrated) && !(user instanceof EbuMigrated)) {
                if (((ActiveOfferPostpaid) yourServicesOptionDetailsFragment.getActiveOffer()).getAllowOfferDelete()) {
                    Log.d(TAG, "onAttachedToWindow: condition " + ((ActiveOfferPostpaid) yourServicesOptionDetailsFragment.getActiveOffer()).getAllowOfferDelete());
                    addDeleteOptionButton();
                }
            }else if (user instanceof CorpUser || user instanceof CorpSubUser || user instanceof SubUserNonMigrated){
                deleteOptionButton.setVisibility(GONE);
            }
            if (getInfoWebUrl() != null && !getInfoWebUrl().equals("")) {
                configureExpandCard(getInfoWebUrl());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    View.OnClickListener eftOverlayButtonsListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            etfOverlay.dismiss();
            ((ChatBubbleActivity)getContext()).getChatBubble().displayBubble(true);
        }
    };

    @Override
    public VodafoneAbstractCard showError(boolean hideContent) {
        super.showError(hideContent);
        errorView.setMargins(0,0,0,0);
        return this;
    }

    public void setInfoWebUrl(String webUrl){
        this.webUrl = webUrl;
    }

    public String getInfoWebUrl(){
        return webUrl;
    }

    private void configureExpandCard(String webUrl){
        ExpandableWebViewCard expandableCardInfo = new ExpandableWebViewCard(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        expandableCardInfo.setLayoutParams(params);

        expandableCardInfo.setVisibility(View.VISIBLE);
        VodafoneTextView expandableCardTitleView = (VodafoneTextView) expandableCardInfo.findViewById(R.id.card_title_tv);
        expandableCardTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        expandableCardTitleView.setTextColor(ContextCompat.getColor(getContext(),R.color.dark_gray_text_color));
        expandableCardInfo.setVisibility(View.VISIBLE);
        expandableCardInfo.setTitle("Informații tarife");
        expandableCardInfo.hideLine();
        expandableCardInfo.setWebViewUrl(webUrl);

        expandableCardInfo.setImageArrowDirectionDown();
        expandableCardInfo.build();

        expandableCardInfo.setCardPaddingsInDp(0,16,16,16);


        views.add(expandableCardInfo);
        addViewsToBottom(views);

    }
}

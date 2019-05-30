package ro.vodafone.mcare.android.card.travelling;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmList;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.offers.UnicaOffer;
import ro.vodafone.mcare.android.client.model.realm.offers.UnicaOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.CallDetailsLabels;
import ro.vodafone.mcare.android.client.model.realm.system.TravellingAboardLabels;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.OffersService;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.specialgroupview.CustomWidgetLoadingLayout;
import ro.vodafone.mcare.android.ui.views.specialgroupview.ViewGroupParamsEnum;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import rx.Observable;

/**
 * Created by Alex on 4/22/2017.
 */

public class TravellingTextExpandCard extends VodafoneAbstractCard {

    @BindView(R.id.travelling_card_title)
    VodafoneTextView titleTextView;

    @BindView(R.id.travelling_card_arrow)
    AppCompatImageView arrowIndicator;

    @BindView(R.id.travelling_card_description_and_activation_container)
    RelativeLayout travellingCardDescription;

    @BindView(R.id.travelling_card_description)
    VodafoneTextView travellingCardDescriptionText;

    @BindView(R.id.travelling_card_activation_button)
    VodafoneButton travellingCardActivationButton;

    @OnClick(R.id.travelling_card_arrow)
    public void toggle(){
        if(isExpanded()){
            unExpand();
        }else{
            expand();
        }
    }

    private String title;

    private String description;
    private String offerName;
    String offerId;

    private String msisdn;

    public static AppConfiguration configurations;
    String roamingOfferCode;

    protected CustomWidgetLoadingLayout loadingView;
    protected CardErrorLayout errorView;

    public TravellingTextExpandCard(Context context) {
        super(context);
    }

    public TravellingTextExpandCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TravellingTextExpandCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TravellingTextExpandCard build(){
        ButterKnife.bind(this);

        titleTextView.setText(title);
        arrowIndicator.setRotation(90);

        return this;

    }

    @Override
    protected int setContent() {
        return R.layout.card_expandable_text_activation;
    }

    private void unExpand(){
        final RotateAnimation animRotateDown = new RotateAnimation(90.0f, 0.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotateDown.setFillAfter(true);
        animRotateDown.setDuration(120);
        arrowIndicator.startAnimation(animRotateDown);
        hideDetails();
        hideContent();
        hideError();
    }

    private void expand(){
        ((RelativeLayout) findViewById(R.id.travelling_card_description_and_activation_container)).invalidate();



            final RotateAnimation animRotateUp = new RotateAnimation(90.0f, 180.0f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);

            animRotateUp.setFillAfter(true);
            animRotateUp.setDuration(120);
            arrowIndicator.startAnimation(animRotateUp);

        showDetails();
    }

    private void hideDetails(){
        travellingCardDescription.setVisibility(GONE);
        travellingCardDescriptionText.setText("");

    }
    private void showDetails(){

        requestRoamingOffers();
        travellingCardDescription.setVisibility(VISIBLE);


    }
    private boolean isExpanded(){
        if(travellingCardDescription.getVisibility() == VISIBLE){
            return true;
        }
        return false;
    }

    public String getTittle() {
        return title;
    }

    public TravellingTextExpandCard setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public TravellingTextExpandCard setMsisdn(String msisdn) {
        this.msisdn = msisdn;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TravellingTextExpandCard setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public VodafoneAbstractCard showError(boolean hideContent) {
        super.showError(hideContent);
        errorView.setMargins(0,0,0,0);
        return this;
    }

    private void requestRoamingOffers(){


        msisdn = VodafoneController.getInstance().getUserProfile().getMsisdn();

        Log.d(TAG, "UnicaOffers call for msisdn: "+msisdn);

        if (msisdn != null && msisdn.startsWith("0")) {
            msisdn = msisdn.replaceFirst("0", "40");
        }

        Log.d(TAG," concat msisdn " +  msisdn);
        loading();

        Observable<GeneralResponse<UnicaOffersSuccess>> responseObservable = null;
        try {
            responseObservable = new OffersService(VodafoneController.getInstance()).getDialViewOffer(msisdn);
            responseObservable.subscribe(new RequestSaveRealmObserver<GeneralResponse<UnicaOffersSuccess>>() {
                @Override
                public void onCompleted() {
                    Log.d(TAG, "UnicaOffers: onCompleted");
                }

                @Override
                public void onNext(GeneralResponse<UnicaOffersSuccess> response) {
                    super.onNext(response);
                    Log.d(TAG, "UnicaOffers: onNext");
                    if(response!=null && response.getTransactionSuccess()!=null){
                        Log.d(TAG, "UnicaOffersSuccess getTransactionSuccess: "+response.getTransactionSuccess());
                        RealmList<UnicaOffer> getOffersList = response.getTransactionSuccess().getOffersList();
                        roamingOfferCode = AppConfiguration.getRoamingOfferId();
                        checkforRoamingOffers(getOffersList);

                    } else {
                        if(isExpanded()) {
                            showError();
                        }
                    }
                     hideLoading();
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    Log.d(TAG, "UnicaOffers: onError");
                    hideLoading();
                    if(isExpanded()) {
                        showError();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            hideLoading();
        }

    }

    public void checkforRoamingOffers( final RealmList<UnicaOffer> getOffersList){
        Log.d(TAG,"checkforRoamingOffers");

        if(getOffersList != null){
            for(int i = 0; i< getOffersList.size(); i++) {
                Log.d(TAG, "Roaming code found! ");
                offerName = getOffersList.get(i).getDisplayName();
                Log.d(TAG, "Roaming offer name: " + offerName);
                offerId = getOffersList.get(i).getOfferCode();
                if (offerId != null) {
                    travellingCardActivationButton.setVisibility(VISIBLE);
                    travellingCardActivationButton.setText(TravellingAboardLabels.getTravelling_aboard_activation_button_label());
                    travellingCardDescriptionText.setText(getOffersList.get(i).getCostMessage() + "\n"
                            + "\n" + TravellingAboardLabels.getTravelling_aboard_offer() + getOffersList.get(i).getDisplayName() + " ." + "\n"
                            + "\n" + getOffersList.get(i).getOfferMessage());

                }else {
                    travellingCardDescriptionText.setText(getOffersList.get(i).getOfferMessage());
                }

                final int finalI = i;
                travellingCardActivationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayAcceptOffersDialog(msisdn, getOffersList.get(finalI));
                    }
                });
            }
        }else {
                Log.d(TAG,"offer not found");
                travellingCardDescriptionText.setText(TravellingAboardLabels.getTravelling_aboard_roaming_active_using_message() );
        }

    }

    public void loading(){

        if(loadingView == null){
            loadingView = new CustomWidgetLoadingLayout(getContext()).build(
                    travellingCardDescription,
                    Color.RED,
                    ViewGroupParamsEnum.relative_center);
        }
        loadingView.show();

    }

    public void hideError() {
        if (errorView != null) {
            errorView.setVisibility(GONE);
        }
    }

    public void showError() {

        if(errorView == null){
            errorView = new CardErrorLayout(getContext());
            travellingCardDescription.addView(errorView);
            errorView.setText(TravellingAboardLabels.getTravelling_aboard_system_error());

            errorView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorView.setVisibility(View.GONE);
                    showDetails();
                }
            });

            hideContent();
        }else{
            errorView.setVisibility(VISIBLE);
        }

    }

    public void hideLoading(){
        if(loadingView != null && loadingView.isVisible()){
            showContent();
            travellingCardDescription.removeView(loadingView);
            loadingView.hide();
        }
    }

    public void hideContent(){
        travellingCardActivationButton.setVisibility(GONE);
        travellingCardDescriptionText.setVisibility(GONE);

    }

    public void showContent(){
        travellingCardDescriptionText.setVisibility(VISIBLE);
    }

    protected void displayAcceptOffersDialog(final String msisdn, final UnicaOffer unicaOffer) {

        final Dialog overlyDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        overlyDialog.setContentView(R.layout.overlay_dialog_notifications);
        overlyDialog.show();

        Button buttonActivate = (Button) overlyDialog.findViewById(R.id.buttonKeepOn);
        Button buttonRefuze = (Button) overlyDialog.findViewById(R.id.buttonTurnOff);

        VodafoneTextView overlayTitle = (VodafoneTextView) overlyDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) overlyDialog.findViewById(R.id.overlaySubtext);

        overlayTitle.setText(TravellingAboardLabels.getTravelling_aboard_confirmation_tittle());

        String contentText = TravellingAboardLabels.getTravelling_aboard_roaming_offer_activation_part1() + " " +
                unicaOffer.getDisplayName() + ". " +
                TravellingAboardLabels.getTravelling_aboard_roaming_offer_activation_part2();

        overlaySubtext.setText(contentText);

        buttonActivate.setText(TravellingAboardLabels.getTravelling_aboard_confirmation_button_label());
        buttonRefuze.setText(TravellingAboardLabels.getTravelling_aboard_cancel_button_label());

        ImageView overlayDismissButton = (ImageView) overlyDialog.findViewById(R.id.overlayDismissButton);

        buttonRefuze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlyDialog.dismiss();
            }
        });

        buttonActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateUnicaRoamingOffer( msisdn, unicaOffer);
                overlyDialog.dismiss();
            }
        });

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlyDialog.dismiss();
            }
        });

        overlyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                overlyDialog.dismiss();
            }
        });
    }

    private  void activateUnicaRoamingOffer(String msisdn, final  UnicaOffer unicaOffer){

        if (msisdn.startsWith("0")) {
            msisdn = msisdn.replaceFirst("0", "40");
        }

        Log.d(TAG, "activateUnicaRoamingOffer msisdn: "+msisdn); //putDialViewOffer

        Observable<GeneralResponse<UnicaOffersSuccess>> responseObservable = null;
        try {
            responseObservable = new OffersService(VodafoneController.getInstance()).putDialViewOffer(msisdn, unicaOffer);
            responseObservable.subscribe(new RequestSaveRealmObserver<GeneralResponse<UnicaOffersSuccess>>() {
                @Override
                public void onCompleted() {
                    Log.d(TAG, "activateUnicaRoamingOffer: onCompleted");
                }

                @Override
                public void onNext(GeneralResponse<UnicaOffersSuccess> response) {
                    super.onNext(response);
                    Log.d(TAG, "activateUnicaRoamingOffer: onNext");
                    if(response!=null && response.getTransactionStatus()==0){
                        Log.d(TAG, "UnicaOffersSuccess getTransactionSuccess: "+response.getTransactionSuccess());


                        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(19, 10, VoiceOfVodafoneCategory.Roaming, null, " Îți mulțumim! Oferta a fost activată cu success! ", "Ok", "",
                                true, false, VoiceOfVodafoneAction.Dismiss, null);
                        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);

                        new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD,true);
                        new CustomToast.Builder(getContext()).message(TravellingAboardLabels.getTravelling_aboard_offer_has_been_activated()).success(true).show();
//                        CustomToast customToast = new CustomToast((AppCompatActivity) getContext(), getContext(), TravellingAboardLabels.getTravelling_aboard_offer_has_been_activated() , true);
//                        customToast.show();
                    } else {
                        Log.e(TAG, "UnicaOffers getTransactionFault: " + response.getTransactionFault().getFaultMessage());
                        new CustomToast.Builder(getContext()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
//                        CustomToast customToast = new CustomToast((AppCompatActivity) getContext(), getContext(), CallDetailsLabels.getCall_details_system_error_text(), false);
//                        customToast.show();

                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    Log.d(TAG, "activateUnicaRoamingOffer: onError");
                    new CustomToast.Builder(getContext()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
//                    CustomToast customToast = new CustomToast((AppCompatActivity) getContext(), getContext(), CallDetailsLabels.getCall_details_system_error_text(), false);
//                    customToast.show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            new CustomToast.Builder(getContext()).message(CallDetailsLabels.getCall_details_system_error_text()).success(false).show();
//            CustomToast customToast = new CustomToast((AppCompatActivity) getContext(), getContext(), CallDetailsLabels.getCall_details_system_error_text(), false);
//            customToast.show();
        }

    }

}


package ro.vodafone.mcare.android.ui.fragments.offers;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmList;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomOffer;
import ro.vodafone.mcare.android.client.model.realm.offers.ancom.AncomPendingOffersSuccess;
import ro.vodafone.mcare.android.client.model.realm.system.OffersLabels;
import ro.vodafone.mcare.android.client.model.realm.system.RegisterLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.AncomOffersService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.fragments.yourServices.details.OffersContractDetailsFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.TealiumHelper;

import static android.view.View.inflate;

/**
 * Created by Deaconescu Eliza on 19.03.2017.
 */

public class AncomPendingOffersAdapter extends BaseAdapter {

    public static String TAG = "AncomOffersAdapter";

    String msisdnFromApi = null ;
    String dateString = null;
    String date;
    private Context context;
    private RealmList<AncomOffer> ancomOffersList;
    AncomOffersService ancomOffersService = new AncomOffersService(VodafoneController.getInstance());

    public AncomPendingOffersAdapter(Context context, RealmList<AncomOffer> ancomOffersList) {
        this.context = context;
        this.ancomOffersList = ancomOffersList;
    }

    @Override
    public int getCount() {

        if (ancomOffersList.isValid() && ancomOffersList != null)
        return ancomOffersList.size();
        else return 0;
    }

    @Override
    public Object getItem(int i) {
        return ancomOffersList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final AncomOffer child = ancomOffersList.get(i);

        if (view == null) {
            view = inflate(context, R.layout.fragment_offers_pending_offers, null);
        }

        final TextView msisdn = (TextView) view.findViewById(R.id.msisdnPendingOffers);
        TextView contractDuration = (TextView) view.findViewById(R.id.contractDurationPendingOffers);
        final TextView proposalId = (TextView) view.findViewById(R.id.proposalIdPendingOffers);
        TextView creationDate = (TextView) view.findViewById(R.id.creationDatePendingOffers);
        final VodafoneButton acceptOffersButton = (VodafoneButton) view.findViewById(R.id.accept_offers_button);
        final VodafoneButton refuzeOffersButton = (VodafoneButton) view.findViewById(R.id.refuse_offers_button);
        RelativeLayout image = (RelativeLayout) view.findViewById(R.id.redirect_contrat_details_pending_offers);
        final CheckBox termsAndConditionsCheckBox = (CheckBox) view.findViewById(R.id.terms_and_conditions_checkbox_pending_offers);
        TextView termsAndConditionsCheckBoxLabel = (TextView) view.findViewById(R.id.terms_and_conditions_checkbox_label_po);
        final CheckBox supportedClausestermsCheckbox = (CheckBox) view.findViewById(R.id.supported_clausesterms_checkbox_po);
        TextView supportedClausestermsLablel = (TextView) view.findViewById(R.id.supported_clausesterms_label);

        acceptOffersButton.setEnabled(false);
        date = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        msisdnFromApi = child.getMsisdn();
        if (msisdnFromApi.startsWith("0")) {
            msisdnFromApi = msisdnFromApi.replaceFirst("0", "40");
        }

        if (msisdnFromApi != null) {
            Log.d("ChildVIew", "child.getPrepaidMsisdn() " + msisdnFromApi);
            msisdn.setText(child.getMsisdn());
        }
        if (child.getProposalId() != null) {
            Log.d("ChildVIew", "child.getChannelTypeId() " + child.getProposalId());
            proposalId.setText(child.getProposalId());
        }
        if (child.getCreationDate() != null) {
            Log.d("ChildVIew", "child.getAmount() " + child.getCreationDate());
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            dateString = formatter.format(new Date(child.getCreationDate()));
            creationDate.setText(dateString);
        }
        if (child.getContractDuration() != null) {
            Log.d("ChildVIew", "child.getActionTypeId() " + child.getContractDuration());
            contractDuration.setText(String.format(VodafoneController.getInstance().getApplicationContext().getResources().getString(R.string.your_option_months),child.getContractDuration()));
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(VodafoneController.getInstance(), WebviewActivity.class);
//                intent.putExtra("webviewUrl", SettingsLabels.getLinkContractDetails());
//                intent.putExtra(WebviewActivity.PAGE_TITLE, OffersLabels.getAcceptedOffersContractDetails());
//                context.startActivity(intent);
                ((OffersActivity) context).addFragment(OffersContractDetailsFragment
                        .newInstance(ancomOffersList.get(i).getOfferId(), OffersContractDetailsFragment.PENDING_OFFER));
            }
        });

        termsAndConditionsCheckBox.setChecked(false);
        supportedClausestermsCheckbox.setChecked(false);

        termsAndConditionsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

               if (isChecked && supportedClausestermsCheckbox.isChecked()){
                   acceptOffersButton.setEnabled(true);
               } else {
                   acceptOffersButton.setEnabled(false);
               }
            }
        });

        supportedClausestermsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked && termsAndConditionsCheckBox.isChecked()){
                    acceptOffersButton.setEnabled(true);
                } else {
                    acceptOffersButton.setEnabled(false);
                }
            }
        });

        SpannableString myString = new SpannableString(RegisterLabels.getRegisterInputTermsAndConditionLabel());
        openWebView(myString, 17, 39, termsAndConditionsCheckBoxLabel, SettingsLabels.getLinkAncomOffersTC());

        SpannableString myStringSupported = new SpannableString(context.getResources().getString(R.string.proposals_pending_general_t_c));
        openWebView(myStringSupported, 17, 70, supportedClausestermsLablel, SettingsLabels.getLinkAncomOffersSpecific());

        acceptOffersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAcceptOffersDialog(msisdnFromApi, child.getProposalId(), child.getOfferId());
            }
        });


        if (child.getIsRefuseEnabled()) {
            refuzeOffersButton.setVisibility(View.VISIBLE);
            refuzeOffersButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayRefusedOffersDialog(msisdnFromApi, child.getProposalId(), child.getOfferId(),dateString);
                }
            });
        }

        return view;
    }

    private void openWebView(SpannableString spannableString, int start, int end, TextView textView, final String webviewUrl) {

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Log.d("", "Terms  Text Clicked ");
                Intent intent = new Intent(VodafoneController.getInstance(), WebviewActivity.class);
                intent.putExtra("webviewUrl", webviewUrl);
                context.startActivity(intent);
            }
        };

        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableString);
        textView.setHighlightColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.pay_bill_bold_text_color));
        textView.setLinkTextColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.pay_bill_bold_text_color));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setTextColor(ContextCompat.getColor(VodafoneController.getInstance(), R.color.pay_bill_bold_text_color));

        PendingOfferClauseTrackingEvent event = new PendingOfferClauseTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

    }

    protected void displayAcceptOffersDialog(final String msisdn, final String proposalId, final String offerId) {

        final Dialog overlyDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar);
        overlyDialog.setContentView(R.layout.overlay_dialog_notifications);
        overlyDialog.show();
        Log.d(TAG, "msisdnFromApi" + msisdnFromApi );

        Button buttonActivate = (Button) overlyDialog.findViewById(R.id.buttonKeepOn);
        Button buttonRefuze = (Button) overlyDialog.findViewById(R.id.buttonTurnOff);

        VodafoneTextView overlayTitle = (VodafoneTextView) overlyDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) overlyDialog.findViewById(R.id.overlaySubtext2);
        overlyDialog.findViewById(R.id.overlaySubtext).setVisibility(View.GONE);

        overlayTitle.setText(OffersLabels.getAcceptedOffersOverlayTitle());

        overlaySubtext.setText(OffersLabels.getOverlaySubtextAcceptedOffers());
        overlaySubtext.setVisibility(View.VISIBLE);

        buttonActivate.setText(OffersLabels.getAcceptedOffersOverlayTitle());
        buttonRefuze.setText(OffersLabels.getNoButtonAcceptedOffers());

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
                setAncomAcceptedOffers(VodafoneController.getInstance(), msisdn, proposalId);
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


    protected void displayRefusedOffersDialog(final String msisdn, final String proposalId, final String offerId, final  String dateString) {

        final Dialog overlyDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        overlyDialog.setContentView(R.layout.overlay_dialog_notifications);
        overlyDialog.show();

        Button buttonActivate = (Button) overlyDialog.findViewById(R.id.buttonKeepOn);
        Button buttonRefuze = (Button) overlyDialog.findViewById(R.id.buttonTurnOff);

        VodafoneTextView overlayTitle = (VodafoneTextView) overlyDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) overlyDialog.findViewById(R.id.overlaySubtext);

        overlayTitle.setText(context.getResources().getString(R.string.overlayTitleRefusedOffers));
        overlaySubtext.setText(OffersLabels.getOverlaySubtextRefusedOffers());

        buttonActivate.setText(OffersLabels.getrefusedOffersButton());
        buttonRefuze.setText(OffersLabels.getNoButtonAcceptedOffers());

        ImageView overlayDismissButton = (ImageView) overlyDialog.findViewById(R.id.overlayDismissButton);

        buttonActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put("screen_name","waiting offers");
                tealiumMapEvent.put("event_name","mcare:waiting offers:button:accepta oferta");
                tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("event_name", tealiumMapEvent);

                setAncomRefusedOffers(VodafoneController.getInstance(), msisdn, proposalId, offerId, dateString);
                overlyDialog.dismiss();
            }
        });

        buttonRefuze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put("screen_name","waiting offers");
                tealiumMapEvent.put("event_name","mcare:waiting offers:button:refuza oferta");
                tealiumMapEvent.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("event_name", tealiumMapEvent);

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

    //Api42 - PUT Accepted Offers
    public void setAncomAcceptedOffers(final Context context, String msisdn, final String proposalId) {

        ancomOffersService.setVOSOAcceptedOffers(msisdn, proposalId).subscribe(new RequestSaveRealmObserver<GeneralResponse<AncomPendingOffersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<AncomPendingOffersSuccess> ancomOffersSuccessGeneralResponse) {
                super.onNext(ancomOffersSuccessGeneralResponse);

                if (ancomOffersSuccessGeneralResponse != null && ancomOffersSuccessGeneralResponse.getTransactionStatus() == 0) {
                    Log.d("setAncomOffers", "getTransactionSuccess");

                    new CustomToast.Builder(context).message("Oferta a fost acceptată").success(true).show();
                    VoiceOfVodafone vov = new VoiceOfVodafone(15, 20, VoiceOfVodafoneCategory.Accepted_Offers, null,
                            "Oferta cu numărul " + proposalId + " a fost acceptată în data de " + date + ".", "Ok, am înțeles.", null,
                            true, false, VoiceOfVodafoneAction.Dismiss, null);
                    VoiceOfVodafoneController.getInstance().pushStashToView(vov.getCategory(), vov);
                    VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();
                    VodafoneController.currentActivity().finish();

                } else {

                    new CustomToast.Builder(context).message("Serviciu momentan indisponibil!").success(false).show();
                }

            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d("addFavoriteNumber", "onError");
                new CustomToast.Builder(context).message("Serviciu momentan indisponibil!").success(false).show();
            }

            @Override
            public void onCompleted() {
            }
        });
    }

    //Api42 - PUT Accepted Offers
    public void setAncomRefusedOffers(final Context context, String msisdn, final String proposalId, String offerMsisd, final String dateString) {

        ancomOffersService.setVOSRefusedOffers(msisdn, proposalId, offerMsisd).subscribe(new RequestSaveRealmObserver<GeneralResponse<AncomPendingOffersSuccess>>() {
            @Override
            public void onNext(GeneralResponse<AncomPendingOffersSuccess> ancomOffersSuccessGeneralResponse) {
                super.onNext(ancomOffersSuccessGeneralResponse);

                if (ancomOffersSuccessGeneralResponse != null && ancomOffersSuccessGeneralResponse.getTransactionStatus() == 0) {
                    Log.d("setAncomRefusedOffers", "getTransactionSuccess");
                    String text = "Ai refuzat oferta cu numarul " + proposalId + " din data " + dateString +"." ;
                    new CustomToast.Builder(context).message(text).success(true).show();
                    VodafoneController.currentActivity().finish();
                } else {
                    Log.d("setAncomRefusedOffers", "getTransactionFault");
                    new CustomToast.Builder(context).message("Serviciu momentan indisponibil!").success(false).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Log.d("addFavoriteNumber", "onError");
                new CustomToast.Builder(context).message("Serviciu momentan indisponibil!").success(false).show();
            }

            @Override
            public void onCompleted() {
            }
        });
    }

    public static class PendingOfferClauseTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "contract clause details";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:"+"contract clause details");


            s.channel = "pending offers";
            s.getContextData().put("&&channel", s.channel);

        }
    }

    public AncomPendingOffersAdapter showLoading(boolean hideContent){
        showLoading(true);
        return this;
    }
}

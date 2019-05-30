package ro.vodafone.mcare.android.ui.fragments.billingOverview;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.offers.GeneralCardsWithTitleBodyAndTwoButtons;
import ro.vodafone.mcare.android.card.settings.PaymentAgreementCard;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.paymentAgreement.PaymentAgreementSuccess;
import ro.vodafone.mcare.android.client.model.realm.profile.Profile;
import ro.vodafone.mcare.android.client.model.realm.system.PaymentAgreementLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.realm.system.YourProfileLabels;
import ro.vodafone.mcare.android.interfaces.fragment.base.OnScrollViewCreatedListener;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.PaymentAgreementService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.BillingOverviewActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.datepickers.PACalendarPickerView;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.CalendarDateValidatorController;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.DateUtils;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

/**
 * Created by user2 on 4/25/2017.
 */

public class PaymentAgreementFragment extends BaseFragment implements PACalendarPickerView.Callback {

    private static final String TAG = "PaymentAgreement";
    BillingOverviewActivity activity;
    NavigationHeader navigationHeader;
    PaymentAgreementService paymentAgreementService;
    GeneralCardsWithTitleBodyAndTwoButtons paymentAgreementCardError;
    PaymentAgreementCard loadingCard;
    PaymentAgreementCard paymentAgreementCard;
    PACalendarPickerView calendarPickerView;
    Integer maxAgreementDays;
    Long currentDate;
    Date selectedCalendarDate;
    String segment = "Consumer";
    FrameLayout containerFrame;
    ScrollView scrollView;

	private OnScrollViewCreatedListener scrollViewCreatedListener;

    View.OnClickListener onChangeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Tealium Track Event
            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put("screen_name", TealiumConstants.paymentAgreement);
            tealiumMapEvent.put("event_name", TealiumConstants.payment_agreement_button);
            if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
                tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
            TealiumHelper.trackEvent("event_name", tealiumMapEvent);

            selectedCalendarDate(selectedCalendarDate);

            Calendar calendarTest = Calendar.getInstance();
            calendarTest.setTime(selectedCalendarDate);

            Date formattedDate = trimDate(selectedCalendarDate);
            long agreementDate = formattedDate.getTime();
            Log.d(TAG, "Hour:" + formattedDate.getHours());
            Log.d(TAG, "Minute:" + formattedDate.getMinutes());
            Log.d(TAG, "Second:" + formattedDate.getSeconds());

            showOverlay(agreementDate);

            Log.d(TAG, calendarTest.get(Calendar.DAY_OF_MONTH) + " - " + calendarTest.get(Calendar.MONTH) + " - " + calendarTest.get(Calendar.YEAR));


        }
    };

    public PaymentAgreementFragment() { }

    public static Date trimDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);

        return calendar.getTime();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view =  inflater.inflate(R.layout.fragment_payment_agreement, container, false);

		containerFrame = (FrameLayout)view.findViewById(R.id.fragment_container);

		navigationHeader = view.findViewById(R.id.navigation_header);

		scrollView = view.findViewById(R.id.scrollView);

        initializeNavigationHeader();

		if (scrollViewCreatedListener != null) {
			scrollViewCreatedListener.onScrollViewCreated((ScrollView) scrollView);
		}

        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        activity = (BillingOverviewActivity) getActivity();
        paymentAgreementService = new PaymentAgreementService(getContext());
//        configureHeader();


        setAdobeVariables();

    }


    public void inflatePaymentAgreement(String type) {



            if (type.equals("show_loading")) {
                loadingCard = new PaymentAgreementCard(activity);
                loadingCard.showLoading(true);
                containerFrame.addView(loadingCard);
            } else if (type.equals("calendar_view")) {
                String paymentDaysSubtitle = PaymentAgreementLabels.getPaymentAgreementNoDaysPostpone() + maxAgreementDays + " zile.";

                Calendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(currentDate);
                ArrayList<Date> selectableDates = CalendarDateValidatorController.getInstance().getSelectableDatesForPA(maxAgreementDays, calendar);

                paymentAgreementCard = new PaymentAgreementCard(activity, maxAgreementDays, currentDate, selectableDates);

                calendarPickerView = paymentAgreementCard.getCalendarPickerView();
                calendarPickerView.setCallback(this);

                paymentAgreementCard.setTitle(PaymentAgreementLabels.getPaymentAgreementSelectDate())
                        .setSubTitleDetails(paymentDaysSubtitle)
                        .build();
                paymentAgreementCard.disableButton();

                paymentAgreementCard.getPostponePaymentButton().setText(SettingsLabels.getPostPoneButton());

                containerFrame.addView(paymentAgreementCard);
            } else if (type.equals("payment_postponed")) {
                GeneralCardsWithTitleBodyAndTwoButtons alreadyPostponedCard = alreadyPostponed();
                containerFrame.addView(alreadyPostponedCard);
            } else if (type.equals("informative_message")) {
                GeneralCardsWithTitleBodyAndTwoButtons informativeMessageCard = informativeMessageCard();
                informativeMessageCard.getBodyTextView().setPadding(0, ScreenMeasure.dpToPx(20), 0, ScreenMeasure.dpToPx(20));
                containerFrame.addView(informativeMessageCard);
            }
            stopLoadingDialog();



    }




    public void initializeNavigationHeader() {

        navigationHeader.setId(R.id.navigation_header);
        navigationHeader.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        navigationHeader.setTitle(YourProfileLabels.getPayBillDelayPageTitle());
        navigationHeader.hideSelectorView();
        navigationHeader.showTriangleView();

    }


//    public NavigationHeader getNavigationHeader() {
//        return navigationHeader == null ? initializeNavigationHeader() : navigationHeader;
//    }


    public String getTitle() {
        return PaymentAgreementLabels.getPaymentAgreementTitle();
    }


    public void inflateErrorLayout(String reason) {


            GeneralCardsWithTitleBodyAndTwoButtons errorCard = null;
            if (reason.equals("server_fail")) {
                errorCard = showServerError();
                errorCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getPaymentAgreement();
                    }
                });
            } else if (reason.equals("user_not_eligible")) {
                errorCard = showUserNotEligible();
            } else if (reason.equals("user_not_eligible_month")) {
                errorCard = notEligibleThisMonth();
            } else if (reason.equals("bill_expired")) {
                errorCard = showBillExpired();
            } else if (reason.equals("error_postpone")) {
                errorCard = showPostponeError();
            } else if (reason.equals("bill_delay_message"))
                errorCard = showBillDelayMessage();

            containerFrame.addView(errorCard);
            stopLoadingDialog();

    }

    public GeneralCardsWithTitleBodyAndTwoButtons notEligibleThisMonth() {
        paymentAgreementCardError = new GeneralCardsWithTitleBodyAndTwoButtons(activity);
        paymentAgreementCardError.setBody(PaymentAgreementLabels.getPaymentAgreementNotEligibleMonth())
                .setSecondaryButtonMessage(PaymentAgreementLabels.getPaymentAgreementPayBillNow())
                .setWhiteColor()
                .setSecondaryButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Tealium Track Event
                        Map<String, Object> tealiumMapEvent = new HashMap(6);
                        tealiumMapEvent.put("screen_name", "payment agreement");
                        tealiumMapEvent.put("event_name", "mcare:payment agreemen:button:plateste factura");
                        tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                        TealiumHelper.trackEvent("event_name", tealiumMapEvent);

                        redirectToPayBill();
                    }
                })
                .build();

        return paymentAgreementCardError;
    }

    public GeneralCardsWithTitleBodyAndTwoButtons alreadyPostponed() {
        paymentAgreementCardError = new GeneralCardsWithTitleBodyAndTwoButtons(activity);
        paymentAgreementCardError.setBody(PaymentAgreementLabels.getPaymentAgreementAlreadyPostponed())
                .setSecondaryButtonMessage(PaymentAgreementLabels.getPaymentAgreementPayBillNow())
                .setWhiteColor()
                .setSecondaryButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Tealium Track Event
                        Map<String, Object> tealiumMapEvent = new HashMap(6);
                        tealiumMapEvent.put("screen_name", "payment agreement");
                        tealiumMapEvent.put("event_name", "mcare:payment agreemen:button:plateste factura");
                        tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                        TealiumHelper.trackEvent("event_name", tealiumMapEvent);

                        redirectToPayBill();
                    }
                })
                .build();

        return paymentAgreementCardError;
    }

    public GeneralCardsWithTitleBodyAndTwoButtons informativeMessageCard() {
        paymentAgreementCardError = new GeneralCardsWithTitleBodyAndTwoButtons(activity);
        paymentAgreementCardError.setBody(PaymentAgreementLabels.getPaymentAgreementInformativeMessageCard()).setPrimaryButtonMessage("Trimite").setPrimaryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Trimite Button >>PRESSED<<");
                showLoadingDialog();
                postPaymentAgreement();
            }
        }).setSecondaryButtonMessage("Renunță").setSecondaryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Renunta Button >>PRESSED<<");
                Log.i(TAG, "redirect the user to Profilul meu page");
                activity.onBackPressed();
            }
        }).build();
        return paymentAgreementCardError;
    }

    public GeneralCardsWithTitleBodyAndTwoButtons showServerError() {
        paymentAgreementCardError = new GeneralCardsWithTitleBodyAndTwoButtons(activity);
        paymentAgreementCardError.showError(true, SettingsLabels.getSmallErrorMessage());
        //rootView.addView(paymentAgreementCardError);

        return paymentAgreementCardError;
    }

    public GeneralCardsWithTitleBodyAndTwoButtons showUserNotEligible() {
        paymentAgreementCardError = new GeneralCardsWithTitleBodyAndTwoButtons(activity);
        paymentAgreementCardError.showError(true, PaymentAgreementLabels.getPaymentAgreementUserNotEligible(), ContextCompat.getDrawable(getContext(), R.drawable.yellow_error_triangle));

        return paymentAgreementCardError;
    }

    public GeneralCardsWithTitleBodyAndTwoButtons showPostponeError() {
        paymentAgreementCardError = new GeneralCardsWithTitleBodyAndTwoButtons(activity);
        paymentAgreementCardError.showError(true, PaymentAgreementLabels.getPaymentAgreementErrorCouldntPostpone());
        //rootView.addView(paymentAgreementCardError);

        return paymentAgreementCardError;
    }

    public GeneralCardsWithTitleBodyAndTwoButtons showBillExpired() {
        paymentAgreementCardError = new GeneralCardsWithTitleBodyAndTwoButtons(activity);
        paymentAgreementCardError.showError(true, PaymentAgreementLabels.getPaymentAgreementBillExpired());

        return paymentAgreementCardError;
    }

    public GeneralCardsWithTitleBodyAndTwoButtons showBillDelayMessage() {
        paymentAgreementCardError = new GeneralCardsWithTitleBodyAndTwoButtons(activity);
        paymentAgreementCardError.showError(true, PaymentAgreementLabels.getPaymentAgreementBillDelayMessage(), ContextCompat.getDrawable(getContext(), R.drawable.yellow_error_triangle));

        return paymentAgreementCardError;
    }

    public void redirectToPayBill() {

        IntentActionName.PAY_BILL.setExtraParameter("pay_own_bill");
        new NavigationAction(activity).finishCurrent(true).startAction(IntentActionName.PAY_BILL);
    }

    public void getPaymentAgreement() {
        showLoadingDialog();
        Profile profile = (Profile) RealmManager.getRealmObject(Profile.class);

        if (profile == null) {
            return;
        }

        if (EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null && EbuMigratedIdentityController.getInstance().getSelectedIdentity().getTreatmentSegment() !=null){
            segment = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getTreatmentSegment();
        }

        String crmRole = "null";
        String phone = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();

        if (EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null &&
                EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole() != null) {
            crmRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole();
        }

        Log.i(TAG, "crmRole: " + crmRole);
        Log.i(TAG, "segment: " + segment);
        Log.i(TAG, "phone: " + phone);

        if ((crmRole.equalsIgnoreCase("Chooser") || crmRole.equalsIgnoreCase("DelegatedChooser") || crmRole.equalsIgnoreCase("AuthorizedPerson"))
                && (segment.equals("XXL0") || segment.equals("TOP0"))) {

            //Pentru amânare de plată, vă rugăm să contactaţi specialistul dvs de plăţi, program de lucru L-V, 09:00-18:00
            inflateErrorLayout("bill_delay_message");

        } else if ((crmRole.equalsIgnoreCase("Chooser") || crmRole.equalsIgnoreCase("DelegatedChooser") || crmRole.equalsIgnoreCase("AuthorizedPerson"))
                && (segment.equals("XL10"))) {

            // Trimite o solicitare către specialistul de plăţi. Te vom contacta în maximum 2 zile lucrătoare
            inflatePaymentAgreement("informative_message");

        } else {

            paymentAgreementService.getPaymentAgreement(segment, phone, crmRole).subscribe(new RequestSessionObserver<GeneralResponse<PaymentAgreementSuccess>>() {
                @Override
                public void onNext(GeneralResponse<PaymentAgreementSuccess> paymentAgreementSuccessGeneralResponse) {

                    D.i("getTransactionStatus(): " + paymentAgreementSuccessGeneralResponse.getTransactionStatus());

                    if (paymentAgreementSuccessGeneralResponse.getTransactionStatus() == 2) {
                        if (paymentAgreementSuccessGeneralResponse.getTransactionFault().getFaultCode().equals("EC05703")) {
                            //User is not eligible for payment agreement because bill due date has expired.
                            inflateErrorLayout("bill_expired");
                        } else if (paymentAgreementSuccessGeneralResponse.getTransactionFault().getFaultCode().equals("EC00004")) {
                            inflateErrorLayout("server_fail");
                        }else {
                            onError(new Throwable("Server failed"));
                        }
                    } else if (paymentAgreementSuccessGeneralResponse.getTransactionStatus() == 1) {
                        if (paymentAgreementSuccessGeneralResponse.getTransactionFault().getFaultCode().equals("WC05702")) {
                            //User not eligible for payment agreement this month
                            inflateErrorLayout("user_not_eligible_month");
                        } else if (paymentAgreementSuccessGeneralResponse.getTransactionFault().getFaultCode().equals("WC05701")) {
                            //User role not eligible for payment agreement
                            inflateErrorLayout("user_not_eligible");
                        } else if (paymentAgreementSuccessGeneralResponse.getTransactionFault().getFaultCode().equals("WC05704")) {
                            //User already postponed payment
                            inflatePaymentAgreement("payment_postponed");
                        } else {
                            onError(new Throwable("Server failed"));
                        }
                    } else if (paymentAgreementSuccessGeneralResponse.getTransactionStatus() == 0) {
                        if (paymentAgreementSuccessGeneralResponse.getTransactionSuccess() != null) {

                            maxAgreementDays = paymentAgreementSuccessGeneralResponse.getTransactionSuccess().getMaxAgreementDays();
                            currentDate = paymentAgreementSuccessGeneralResponse.getTransactionSuccess().getCurrentDate();
                        }
                        inflatePaymentAgreement("calendar_view");
                    } else {
                        onError(new Throwable("Server failed"));
                    }
                }

                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    inflateErrorLayout("server_fail");
                }
            });
        }
    }

    public void putPaymentAgreement(long agreementDate) {
        String phone = VodafoneController.getInstance().getUserProfile().getMsisdn();
        String ownAccountId = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() != null ? UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() : "";
        String ownuserStatus = VodafoneController.getInstance().getUserProfile().getUserStatus();

        String crmRole = "null";
        if (EbuMigratedIdentityController.getInstance().getSelectedIdentity() != null &&
                EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole() != null) {
            crmRole = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getCrmRole();
        }


        paymentAgreementService.putPaymentAgreement(agreementDate, ownAccountId, ownuserStatus, "fgdgf", crmRole).subscribe(new RequestSessionObserver<GeneralResponse<PaymentAgreementSuccess>>() {
            @Override
            public void onNext(GeneralResponse<PaymentAgreementSuccess> paymentAgreementSuccessGeneralResponse) {
                stopLoadingDialog();

                D.i("getTransactionStatus(): " + paymentAgreementSuccessGeneralResponse.getTransactionStatus());

                if (paymentAgreementSuccessGeneralResponse.getTransactionStatus() == 2) {
                    if (paymentAgreementSuccessGeneralResponse.getTransactionFault().getFaultCode().equals("EC00004")) {
                        //User is not eligible for payment agreement because bill due date has expired.
                        inflateErrorLayout("server_fail");
                        new CustomToast.Builder(getContext()).message(SettingsLabels.getSimpleSmallError()).success(false).show();
                    } else {
                        throw new Error("Server failed");
                    }
                } else if (paymentAgreementSuccessGeneralResponse.getTransactionStatus() == 0) {
                    redirectToDashboard();
                    new CustomToast.Builder(getContext()).message("Ai amânat plata cu succes.").success(true).show();

                } else {
                    throw new Error("Server failed");
                }

            }

            @Override
            public void onCompleted() {
                D.d("on Completed");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                new CustomToast.Builder(getContext()).message(SettingsLabels.getSimpleSmallError()).success(false).show();
            }
        });
    }


    public void postPaymentAgreement() {
        String ban = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() != null ? UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() : "";

        paymentAgreementService.postPaymentAgreement(ban).subscribe(new RequestSessionObserver<GeneralResponse<PaymentAgreementSuccess>>() {
            @Override
            public void onNext(GeneralResponse<PaymentAgreementSuccess> paymentAgreementSuccessGeneralResponse) {

                stopLoadingDialog();
                D.i("getTransactionStatus(): " + paymentAgreementSuccessGeneralResponse.getTransactionStatus());
                if (paymentAgreementSuccessGeneralResponse.getTransactionStatus() == 1) {
                    if (paymentAgreementSuccessGeneralResponse.getTransactionFault().getFaultCode().equals("WC057005")) {
                        inflateErrorLayout("user_not_eligible");
                    } else {
                        onError(new Throwable("Server failed"));
                    }
                } else if (paymentAgreementSuccessGeneralResponse.getTransactionStatus() == 0) {
                    redirectToDashboardEmailSent();
                }

            }


            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                new CustomToast.Builder(getContext()).message(PaymentAgreementLabels.getPaymentAgreementOnErrorToastMessage()).success(false).show();
            }
        });
    }


    public void redirectToDashboard() {
        Calendar calendarTest = Calendar.getInstance();
        calendarTest.setTime(selectedCalendarDate);

        String dateCapitalized = DateUtils.getDate(String.valueOf(calendarTest.getTimeInMillis()),
                new SimpleDateFormat("dd MMMM yyyy", new Locale("RO", "RO")));
        dateCapitalized = WordUtils.capitalize(dateCapitalized);
        String vovMessage = PaymentAgreementLabels.getPaymentAgreementVovMessage() + dateCapitalized;

        Log.i(TAG, "vovMessage is: " + vovMessage );

        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(17, 20, VoiceOfVodafoneCategory.BAR_UNBAR, null, vovMessage, "Ok, am înțeles.", null,
                true, false, VoiceOfVodafoneAction.Dismiss, null);
        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);

        IntentActionName.DASHBOARD.setExtraParameter("payment_agreement_success");
        new NavigationAction(activity).finishCurrent(true).startAction(IntentActionName.DASHBOARD);
    }

    public void redirectToDashboardEmailSent() {

        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(17, 20, VoiceOfVodafoneCategory.BAR_UNBAR, null, PaymentAgreementLabels.getPaymentAgreementVovMessageEmailSent(), "Ok, am înțeles", null,
                true, false, VoiceOfVodafoneAction.Dismiss, null);
        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);

        new CustomToast.Builder(getContext()).message(PaymentAgreementLabels.getPaymentAgreementToastMessage()).success(true).show();
        new NavigationAction(getActivity()).finishCurrent(true).startAction(IntentActionName.DASHBOARD);
    }

    private void showOverlay(final long agreementDate) {
        final Dialog overlayDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar);
        overlayDialog.setContentView(R.layout.overlay_dialog_notifications);
        overlayDialog.show();

        VodafoneTextView overlayTitle = (VodafoneTextView) overlayDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llp.setMargins(0, 0, 0, 120); // llp.setMargins(left, top, right, bottom);
        overlaySubtext.setLayoutParams(llp);

        overlayTitle.setText(PaymentAgreementLabels.getPaymentAgreementTitle() + "?");
        overlaySubtext.setText(PaymentAgreementLabels.getPaymentAgreementOverlayDetails());

        VodafoneButton overlayPostPoneButton = (VodafoneButton) overlayDialog.findViewById(R.id.buttonKeepOn);
        VodafoneButton overlayCancelBlockButton = (VodafoneButton) overlayDialog.findViewById(R.id.buttonTurnOff);

        ImageView overlayDismissButton = (ImageView) overlayDialog.findViewById(R.id.overlayDismissButton);

        overlayPostPoneButton.setText(SettingsLabels.getOverlayPostPoneButton());
        overlayCancelBlockButton.setText(SettingsLabels.getOverlayCancelPostPoneButton());

        overlayCancelBlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayDialog.dismiss();
            }
        });

        overlayPostPoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayDialog.dismiss();
                showLoadingDialog();
                putPaymentAgreement(agreementDate);
            }
        });

        overlayDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayDialog.dismiss();
            }
        });

        overlayDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                overlayDialog.dismiss();
            }
        });
    }

    @Override
    public void selectedCalendarDate(Date date) {
        this.selectedCalendarDate = date;
        Log.d(TAG, selectedCalendarDate + "");
        paymentAgreementCard.setButtonClickListener(onChangeButtonListener);
        paymentAgreementCard.enableButton();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getPaymentAgreement();
    }

    public void  setAdobeVariables(){

        //Tealium Track View
        TealiumHelper.tealiumTrackView(getClass().getSimpleName(),TealiumConstants.billingOverview,TealiumConstants.paymentAgreement);

        PaymentAgreementTrackingEvent event = new PaymentAgreementTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        if (maxAgreementDays == null) {
            //inflateErrorLayout("user_not_eligible");
            getPaymentAgreement();
        }

    callForAdobeTarget(AdobePageNamesConstants.SETTINGS_PAYMENT_AGREE);
    }


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			scrollViewCreatedListener = (OnScrollViewCreatedListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement FragmentLifeCycleListener");
		}
	}



    public static class PaymentAgreementTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "payment agreement";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "payment agreement");


            s.channel = "payment agreement";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}

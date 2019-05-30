package ro.vodafone.mcare.android.ui.fragments.settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.eligibility.EBillSuccess;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.TransactionFault;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.client.model.realm.system.mentenance.PayBillLabels;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.PrivateUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResCorp;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.AuthorisedPersonUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.ChooserUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.requests.EBillRequest;
import ro.vodafone.mcare.android.service.ElectronicBillService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.settings.SettingsActivity;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static ro.vodafone.mcare.android.ui.activities.selectorDialogActivity.SelectorDialogActivity.RESULT_SELECTOR_UPDATED;

/**
 * Created by bogdan.marica on 3/9/2017.
 */

public class ElectronicBillFragment extends SettingsFragment implements OnErrorIconClickListener {

    NavigationHeader navigationHeader;
    VodafoneButton activateButton;
    CustomEditTextCompat emailInput;
    Boolean isElectronicBillActivated = false;
    ViewGroup rootView;
    String userRole;
    String username;
    int billMediaType;
    LinearLayout emailErrorLayout;
    TextView emailErrorMessage;
    VodafoneTextView status;
    String receivedEmail = "";

    ElectronicBillService billService;

    RequestSessionObserver<GeneralResponse> changeEBillRequestObserver = new RequestSessionObserver<GeneralResponse>() {
        @Override
        public void onNext(GeneralResponse eBillSuccessGeneralResponse) {
            D.w("eBillSuccessGeneralResponse = " + eBillSuccessGeneralResponse);
            stopLoadingDialog();

            if (eBillSuccessGeneralResponse != null && eBillSuccessGeneralResponse.getTransactionStatus() == 0) {
                ElectronicBillTrackingEvent event = new ElectronicBillTrackingEvent();
                TrackingAppMeasurement journey = new TrackingAppMeasurement();
                journey.event10 = "event10";
                journey.getContextData().put("event10", journey.event10);
                event.defineTrackingProperties(journey);
                VodafoneController.getInstance().getTrackingService().trackCustom(event);

                new CustomToast.Builder(getContext()).message("Ai schimbat adresa de email cu succes. Următoarea factură o vei primi pe adresa de e-mail " +
                        emailInput.getText() + ".").success(true).show();
                return;
            }

            manageErrorCodes("Toast");
        }

        @Override
        public void onCompleted() {
            D.w();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            stopLoadingDialog();

            ElectronicBillTrackingEvent event = new ElectronicBillTrackingEvent();
            TrackingAppMeasurement journey = new TrackingAppMeasurement();
            journey.event9 = "event9";
            journey.getContextData().put("event9", journey.event9);
            event.defineTrackingProperties(journey);
            VodafoneController.getInstance().getTrackingService().trackCustom(event);

            manageErrorCodes("Toast");
        }
    };

    RequestSessionObserver<GeneralResponse<EBillSuccess>> activateEBillRequestObserver = new RequestSessionObserver<GeneralResponse<EBillSuccess>>() {
        @Override
        public void onNext(GeneralResponse<EBillSuccess> eBillSuccessGeneralResponse) {
            if(getActivity()==null){
                return;
            }
            D.w("eBillSuccessGeneralResponse = " + eBillSuccessGeneralResponse);
            stopLoadingDialog();

            if (eBillSuccessGeneralResponse != null && eBillSuccessGeneralResponse.getTransactionStatus() == 0) {

                new CustomToast.Builder(getContext()).message("Cererea ta a fost procesată!").success(true).show();
                new NavigationAction(getContext()).startAction(IntentActionName.DASHBOARD, true);

                VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(6, 20, VoiceOfVodafoneCategory.Chat, null,
                        "Cererea ta a fost procesată!", "Ok, am înțeles", null, true, false, VoiceOfVodafoneAction.Dismiss, null);
                VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
                VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();
                return;
            }
            manageErrorCodes("Toast");

        }

        @Override
        public void onCompleted() {
            D.w();
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            stopLoadingDialog();
            manageErrorCodes("Toast");
            D.e("e = " + e);
        }
    };
    boolean unhandledError = true;
    boolean pageInit = true;
    RequestSessionObserver<GeneralResponse<EBillSuccess>> getEBillRequestObserver = new RequestSessionObserver<GeneralResponse<EBillSuccess>>() {


        @Override
        public void onNext(GeneralResponse<EBillSuccess> eBillSuccessGeneralResponse) {
            if(getActivity()==null) {
                return;
            }
            Observable.timer(500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            stopLoadingDialog();
                        }
                    });
            unhandledError = false;
            if (eBillSuccessGeneralResponse != null) {
                if (eBillSuccessGeneralResponse.getTransactionStatus() == 0 && eBillSuccessGeneralResponse.getTransactionSuccess() != null) {
                    D.w("getTransactionSuccess = " + eBillSuccessGeneralResponse.getTransactionSuccess().getActive());
                    D.w("getTransactionSuccess = " + eBillSuccessGeneralResponse.getTransactionSuccess().getBillMediaType());
                    D.w("getTransactionSuccess = " + eBillSuccessGeneralResponse.getTransactionSuccess().getEmail());
//                D.w("getTransactionSuccess = " + eBillSuccessGeneralResponse.getTransactionSuccess());

                    isElectronicBillActivated = eBillSuccessGeneralResponse.getTransactionSuccess().getActive();
                    billMediaType = eBillSuccessGeneralResponse.getTransactionSuccess().getBillMediaType();
                    receivedEmail = eBillSuccessGeneralResponse.getTransactionSuccess().getEmail();

                    if (isElectronicBillActivated != null) {
                        if (billMediaType == 7)
                            inflateCDState();
                        else {

                            if (!isElectronicBillActivated)
                                inflateNotActivatedState();
                            else
                                inflateActivatedState();
                        }
                        return;
                    }
                } else {
                    TransactionFault transactionFault = eBillSuccessGeneralResponse.getTransactionFault();
                    if (transactionFault != null && transactionFault.getFaultCode() != null) {
                        if (transactionFault.getFaultCode().equals(ErrorCodes.API45_BILL_MEDIA_TYPE_7.getErrorCode()))
                            inflateCDState();
                        else if (transactionFault.getFaultCode().equals(ErrorCodes.API45_EDIT_NOT_ALLOWED.getErrorCode()))
                            inflateNotActivatedState();
                        else
                            manageErrorCodes(transactionFault.getFaultCode());

                        return;
                    }
                }
            }
            manageErrorCodes("");
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            if(getActivity()==null){
                return;
            }
            if (unhandledError)
                try {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            inflateErrorLayout();
                        }
                    });
                } catch (Exception err) {
                    err.printStackTrace();
                }
            else
                try {
                    Observable.timer(500, TimeUnit.MILLISECONDS)
                            .subscribeOn(Schedulers.newThread())
                            .onBackpressureDrop()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<Long>() {
                                @Override
                                public void onCompleted() {
                                    stopLoadingDialog();
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(Long aLong) {

                                }
                            });
                } catch (Exception ex) {
                    D.e("ERROR : " + ex);
                }
        }
    };

    public ElectronicBillFragment() {
    }

    public boolean isAllowedToActivateEBill(User user) {
        return user instanceof ChooserUser
                || user instanceof PrivateUser
                || user instanceof ResCorp;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSettingsFragment = false;

        userRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();
        username = VodafoneController.getInstance().getUserProfile().getUserName();

        navigationHeader = ((SettingsActivity) getActivity()).getNavigationHeader();
        showMsisdnSelector();
//        navigationHeader.showBanSelector();

        billService = new ElectronicBillService(getContext());
//        updateEBill();
    }

    public void showMsisdnSelector() {

        if (VodafoneController.getInstance().getUser() instanceof PrivateUser
                || VodafoneController.getInstance().getUser() instanceof AuthorisedPersonUser
                || !navigationHeader.isMultipleBansForEbu()) {
            navigationHeader.hideSelectorViewWithoutTriangle();
        } else {
            navigationHeader.displayDefaultHeader();
            navigationHeader.showBanSelector();
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view == null) {
            view = new View(getActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = container;
        showLoadingDialog();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateEBill();
    }

    @Override
    public void makeAdobeRequest() {
        callForAdobeTarget(AdobePageNamesConstants.SETTINGS_EBIL);
    }

    public String getTitle() {
        return ((String) getResources().getText(R.string.electronicBill));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        rootView.removeAllViews();
        if (emailInput != null)
            emailInput.clearFocus();
        navigationHeader.hideSelectorViewWithoutTriangle();
        ((SettingsActivity) getActivity()).setTitle();
    }

    public void completeEmail(UserProfile userProfile) {

        emailInput.setHintTextColor(ContextCompat.getColorStateList(getContext(), R.color.gray_button_color));
        emailInput.setHint("nume@exemplu.com");


        //if (isElectronicBillActivated) {
        if (receivedEmail != null && !receivedEmail.equals("") && !receivedEmail.equals("null"))
            emailInput.setText(receivedEmail);
        else if (userProfile != null && userProfile.getEmail() != null && !userProfile.getEmail().equals(""))
            emailInput.setText(userProfile.getEmail());
    }

    public boolean isValidEmail(String emailAdress) {
        return !TextUtils.isEmpty(emailAdress) && ro.vodafone.mcare.android.ui.utils.TextUtils.EMAIL_PATTERN.matcher(emailAdress).matches() && emailAdress.substring(emailAdress.lastIndexOf(".")).length() > 2;
    }

    public void disableButton() {
        activateButton.setClickable(false);
        activateButton.setEnabled(false);
    }

    public void enableElectronicBillButton() {
        activateButton.setClickable(true);
        activateButton.setEnabled(true);

        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tealium Track Event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put("screen_name", "ebill");
                tealiumMapEvent.put("event_name", "mcare:ebill:button:activeaza");
                tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent("event_name", tealiumMapEvent);
                displayOverlay();
            }
        });
    }

    public void displayOverlay() {
        final Dialog overlyDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar);
        overlyDialog.setContentView(R.layout.overlay_dialog_notifications);
        //overlyDialog.getWindow().setBackgroundDrawableResource(R.color.black_opacity_80);
        overlyDialog.show();

        Button cancelEBill = (Button) overlyDialog.findViewById(R.id.buttonTurnOff);
        Button activateEBill = (Button) overlyDialog.findViewById(R.id.buttonKeepOn);

        VodafoneTextView overlayTitle = (VodafoneTextView) overlyDialog.findViewById(R.id.overlayTitle);
        VodafoneTextView overlaySubtext = (VodafoneTextView) overlyDialog.findViewById(R.id.overlaySubtext);

        overlayTitle.setText(SettingsLabels.getElectronicBillOverlayTitle());
        overlaySubtext.setText(SettingsLabels.getElectricBillOverlaySubtext());

        activateEBill.setText("Activează");
        //activateEBill.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_button_color));
        cancelEBill.setText("Nu");
        //cancelEBill.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_button_color));

        ImageView overlayDismissButton = (ImageView) overlyDialog.findViewById(R.id.overlayDismissButton);

        cancelEBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlyDialog.dismiss();
            }
        });

        activateEBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoadingDialog();

                EBillRequest billRequest = new EBillRequest();
                billRequest.setActive(true);
                billRequest.setEmail(emailInput.getText().toString());
                billRequest.setBillMediaType(billMediaType);
                String selectedBan = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() != null ? UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() : "null";
                if (checkEBUMigratedUsers()) {

                    EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance()
                            .getSelectedIdentity();

                    putEBillEbuBan(billRequest, selectedBan, entityChildItem);

                } else
                    billService.putEBillBan(billRequest, selectedBan, userRole, username).subscribe(activateEBillRequestObserver);

                overlyDialog.dismiss();
                if (getActivity() != null && ((SettingsActivity) getActivity()).getToolbar() != null)
                    ((SettingsActivity) getActivity()).getToolbar().showToolBar();
                hideKeyboard();
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

    private void putEBillEbuBan(EBillRequest billRequest, String selectedBan, EntityChildItem entityChildItem) {
        if(billRequest==null || entityChildItem==null){
            return;
        }
        UserProfile userProfile = VodafoneController.getInstance()
                .getUserProfile();

        String vfCRMRole = entityChildItem.getCrmRole();
        String vfOdsCid = entityChildItem.getVfOdsCid();
        String vfOdsEntityType = entityChildItem.getEntityType();
        String vfOdsEntityId = entityChildItem.getEntityId();
        String vfOdsSsn = entityChildItem.getVfOdsSsn();
        String vfOdsBen = UserSelectedMsisdnBanController.getInstance().getSelectedEbuBen();
        String vfContactID = userProfile.getContactId();
        String vfSsoLastName = userProfile.getLastName();
        String vfSsoFirstName = userProfile.getFirstName();

        billService.putEBillEBUBan(billRequest, selectedBan, userRole, vfCRMRole, username,
                vfOdsCid, vfOdsEntityType, vfOdsEntityId, vfContactID, vfSsoFirstName,
                vfSsoLastName, vfOdsSsn, vfOdsBen).subscribe(activateEBillRequestObserver);
    }

    public void enableChangeEmailButton() {
        activateButton.setClickable(true);
        activateButton.setEnabled(true);

        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                EBillRequest billRequest = new EBillRequest();
                billRequest.setActive(true);
                billRequest.setEmail(emailInput.getText().toString());
                billRequest.setBillMediaType(billMediaType);

                D.w("emailInput.getText().toString() = " + emailInput.getText().toString());
                String selectedBan = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() != null ? UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() : "null";

                if (checkEBUMigratedUsers()) {

                    EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance()
                            .getSelectedIdentity();

                    patchEBillEBUBan(billRequest, selectedBan, entityChildItem);

                } else
                    billService.patchEBillBan(billRequest, selectedBan, userRole, username).subscribe(changeEBillRequestObserver);

                if (getActivity() != null && ((SettingsActivity) getActivity()).getToolbar() != null)
                    ((SettingsActivity) getActivity()).getToolbar().showToolBar();
                hideKeyboard();

            }
        });
    }

    private void patchEBillEBUBan(EBillRequest billRequest, String selectedBan, EntityChildItem entityChildItem) {
        if(billRequest==null || entityChildItem==null){
            return;
        }
        UserProfile userProfile = VodafoneController.getInstance()
                .getUserProfile();

        String vfCRMRole = entityChildItem.getCrmRole();
        String vfOdsCid = entityChildItem.getVfOdsCid();
        String vfOdsEntityType = entityChildItem.getEntityType();
        String vfOdsEntityId = entityChildItem.getEntityId();
        String vfContactID = userProfile.getContactId();
        String vfSsoLastName = userProfile.getLastName();
        String vfSsoFirstName = userProfile.getFirstName();

        billService.patchEBillEBUBan(billRequest, selectedBan, userRole, vfCRMRole, username,
                vfOdsCid, vfOdsEntityType, vfOdsEntityId, vfContactID, vfSsoLastName,
                vfSsoFirstName).subscribe(changeEBillRequestObserver);
    }

    public void inflateNotActivatedState() {
        stopLoadingDialog();
        pageInit = true;
        rootView.removeAllViews();

        final User user = VodafoneController.getInstance().getUser();

        View v = View.inflate(getActivity(), R.layout.fragment_settings_electronic_bill, null);
        status = (VodafoneTextView) v.findViewById(R.id.statusTv);
        VodafoneTextView electronicBillBody = (VodafoneTextView) v.findViewById(R.id.electronicBillBody);
        emailInput = (CustomEditTextCompat) v.findViewById(R.id.emailInput);
        emailInput.setInputType(InputType.TYPE_CLASS_TEXT);
        emailInput.setFocusable(true);
        emailInput.setFocusableInTouchMode(true);
        emailInput.setOnErrorIconClickListener(this);
        activateButton = (VodafoneButton) v.findViewById(R.id.activateButton);
        emailErrorLayout = (LinearLayout) v.findViewById(R.id.email_error_layout);
        emailErrorMessage = (TextView) v.findViewById(R.id.email_error_message);
        emailErrorMessage.setText(PayBillLabels.getPayBillInvalidEmail());

        StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD);
        SpannableStringBuilder statusText = new SpannableStringBuilder("Status factură electronică: " + SettingsLabels.getElectronicBillInactiv());
        statusText.setSpan(b, "Status factură electronică: ".length(), statusText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        status.setText(statusText);
        electronicBillBody.setText(isAllowedToActivateEBill(user) ? SettingsLabels.getElectronicInactivBillTextAllowedUsers() : SettingsLabels.getElectronicInactivBillTextNotAllowedUsers());
        activateButton.setText(isAllowedToActivateEBill(user) ? AppLabels.getElectronicBillActivateButton() : AppLabels.getElectronicBillSendButton());

        disableButton();

        //Tealium Track View
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put("screen_name", "ebill");
        tealiumMapView.put("journey_name", "settings");
        tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapView);

        ElectronicBillTrackingEvent event = new ElectronicBillTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String email = emailInput.getText().toString();

                if (isValidEmail(email) && !pageInit) {
                    if (isAllowedToActivateEBill(user)) {
                        enableElectronicBillButton();
                    } else {
                        enableChangeEmailButton();
                    }
                } else
                    disableButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        View.OnClickListener clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString();
                if (!TextUtils.isEmpty(email) && !isValidEmail(email)) {
                    status.setFocusable(true);
                    status.setFocusableInTouchMode(true);
                    status.requestFocus();
                }

            }
        };

        v.setOnClickListener(clickListener);


        emailInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    displayEditTextError(emailInput, emailErrorLayout, true);
                } else {
                    if (emailInput.getText().toString().equals(""))
                        displayEditTextError(emailInput, emailErrorLayout, false);
                    else if (!isValidEmail(emailInput.getText().toString()))
                        displayEditTextError(emailInput, emailErrorLayout, false);
                    else
                        displayEditTextError(emailInput, emailErrorLayout, true);
                }
            }
        });


        D.w("received email = " + receivedEmail);

        completeEmail(VodafoneController.getInstance().getUserProfile());

        if (isValidEmail(emailInput.getText().toString())) {
            if (isAllowedToActivateEBill(user)) {
                enableElectronicBillButton();
            } else {
                enableChangeEmailButton();
            }
        }

        rootView.addView(v);
        pageInit = false;
    }

    public void inflateCDState() {
        stopLoadingDialog();
        rootView.removeAllViews();

        View v = View.inflate(getActivity(), R.layout.fragment_settings_cd, null);
        VodafoneTextView electronicBillBody = (VodafoneTextView) v.findViewById(R.id.electronicBillBody);
        electronicBillBody.setText(SettingsLabels.getElectronicMediaTypeCD());

        rootView.addView(v);
    }

    public void inflateActivatedState() {
        stopLoadingDialog();
        pageInit = true;
        rootView.removeAllViews();

        final User user = VodafoneController.getInstance().getUser();

        View v = View.inflate(getActivity(), R.layout.fragment_settings_electronic_bill, null);
        status = (VodafoneTextView) v.findViewById(R.id.statusTv);
        VodafoneTextView electronicBillBody = (VodafoneTextView) v.findViewById(R.id.electronicBillBody);
        emailInput = (CustomEditTextCompat) v.findViewById(R.id.emailInput);
        emailInput.setInputType(InputType.TYPE_CLASS_TEXT);
        emailInput.setFocusable(true);
        emailInput.setFocusableInTouchMode(true);
        emailErrorLayout = (LinearLayout) v.findViewById(R.id.email_error_layout);
        emailErrorMessage = (TextView) v.findViewById(R.id.email_error_message);
        emailErrorMessage.setText(PayBillLabels.getPayBillInvalidEmail());
        activateButton = (VodafoneButton) v.findViewById(R.id.activateButton);

        StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD);
        SpannableStringBuilder statusText = new SpannableStringBuilder("Status factură electronică: " + SettingsLabels.getElectronicBillActiv());
        statusText.setSpan(b, "Status factură electronică: ".length(), statusText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        status.setText(statusText);

        electronicBillBody.setText(isAllowedToActivateEBill(user) ? SettingsLabels.getElectronicActivBillTextAllowedUsers() : SettingsLabels.getElectronicActivBillTextNotAllowedUsers());
        activateButton.setText(AppLabels.getElectronicBillSendButton());

        disableButton();


        View.OnClickListener clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString();
                if (!TextUtils.isEmpty(email) && !isValidEmail(email)) {
                    status.setFocusable(true);
                    status.setFocusableInTouchMode(true);
                    status.requestFocus();
//                    emailInput.removeHighlight();
                }

            }
        };

        v.setOnClickListener(clickListener);

        emailInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    displayEditTextError(emailInput, emailErrorLayout, true);
                } else {
                    if (emailInput.getText().toString().equals(""))
                        displayEditTextError(emailInput, emailErrorLayout, false);
                    else if (!isValidEmail(emailInput.getText().toString()))
                        displayEditTextError(emailInput, emailErrorLayout, false);
                    else
                        displayEditTextError(emailInput, emailErrorLayout, true);
                }
            }
        });

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String email = emailInput.getText().toString();

                if (isValidEmail(email) && !pageInit)
                    enableChangeEmailButton();
                else
                    disableButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        completeEmail(VodafoneController.getInstance().getUserProfile());

        if (isValidEmail(emailInput.getText().toString()))
            enableChangeEmailButton();

        rootView.addView(v);
        pageInit = false;
    }

    public void inflateErrorLayout() {
        rootView.removeAllViews();

        stopLoadingAfterDuration(1);

        if (getActivity() == null)
            return;

        VodafoneGenericCard errorCard = new VodafoneGenericCard(getActivity());

        errorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                updateEBill();
            }
        });

        errorCard.showError(true, SettingsLabels.getRetryButton());

        rootView.addView(errorCard);
    }

    public boolean checkEBUMigratedUsers() {
        return VodafoneController.getInstance().getUser() instanceof EbuMigrated;
    }

    // when ban changes in EBUSelectorDialogActivity, onActivityResult is called and ebill is updated
    public void updateScreenOnSelectedBanChanged() {
        userRole = VodafoneController.getInstance().getUserProfile().getUserRoleString();
        username = VodafoneController.getInstance().getUserProfile().getUserName();
        updateEBill();
    }

    public void updateEBill() {

        String selectedBan;

        if (checkEBUMigratedUsers()) {

            EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance()
                    .getSelectedIdentity();

            UserProfile userProfile = VodafoneController.getInstance()
                    .getUserProfile();

            String vfCRMRole = entityChildItem.getCrmRole();
            String vfOdsCid = entityChildItem.getVfOdsCid();
            String vfOdsEntityType = entityChildItem.getEntityType();
            String vfOdsEntityId = entityChildItem.getEntityId();
            String vfContactID = userProfile.getContactId();
            String vfSsoLastName = userProfile.getLastName();
            String vfSsoFirstName = userProfile.getFirstName();

            if (UserSelectedMsisdnBanController.getInstance().getSelectedBan() != null) {

                selectedBan = UserSelectedMsisdnBanController.getInstance().getSelectedBan().getNumber() != null
                        ? UserSelectedMsisdnBanController.getInstance().getSelectedBan().getNumber() : "null";
                unhandledError = true;
                billService.getEBillEBUBan(selectedBan, userRole, vfCRMRole, vfOdsCid,
                        vfOdsEntityType, vfOdsEntityId, vfContactID, vfSsoLastName, vfSsoFirstName).subscribe(getEBillRequestObserver);

            } else if (UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() != null) {

                selectedBan = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() != null ? UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() : "null";
                unhandledError = true;
                billService.getEBillEBUBan(selectedBan, userRole, vfCRMRole, vfOdsCid,
                        vfOdsEntityType, vfOdsEntityId, vfContactID, vfSsoLastName, vfSsoFirstName).subscribe(getEBillRequestObserver);

            } else
                inflateErrorLayout();
        } else {
            if (UserSelectedMsisdnBanController.getInstance().getSelectedBan() != null) {

                selectedBan = UserSelectedMsisdnBanController.getInstance().getSelectedBan().getNumber() != null
                        ? UserSelectedMsisdnBanController.getInstance().getSelectedBan().getNumber() : "null";//
                unhandledError = true;
                billService.getEBillBan(selectedBan, userRole, username).subscribe(getEBillRequestObserver);

            } else if (UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() != null) {

                selectedBan = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() != null ? UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan() : "null";//
                unhandledError = true;
                billService.getEBillBan(selectedBan, userRole, username).subscribe(getEBillRequestObserver);

            } else
                inflateErrorLayout();
        }
    }

    void manageErrorCodes(String errorCode) {
        if (errorCode.equals("Toast")) {
            new CustomToast.Builder(getContext()).message(SettingsLabels.getSimpleSmallError()).success(false).show();
            return;
        }
        inflateErrorLayout();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_SELECTOR_UPDATED && resultCode == RESULT_SELECTOR_UPDATED) {
            updateScreenOnSelectedBanChanged();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navigationHeader.buildMsisdnSelectorHeader();
        navigationHeader.hideSelectorViewWithoutTriangle();
    }

    public void displayEditTextError(CustomEditTextCompat target, LinearLayout errorLayout, boolean visibility) {

        if (!visibility)
            errorLayout.setVisibility(View.VISIBLE);
        else
            errorLayout.setVisibility(View.GONE);

        if (!target.hasFocus())
            if (!visibility)
                target.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
            else
                target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
        else
            target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((SettingsActivity) getActivity()).getNavigationHeader().setTitle(SettingsLabels.getSettingsEbillTitle());
    }

    @Override
    public void onErrorIconClickListener() {
        if(emailInput.isErrorIconTap())
            displayEditTextError(emailInput, emailErrorLayout, true);
    }



    public static class ElectronicBillTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "ebill";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "ebill");


            s.channel = "settings";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}

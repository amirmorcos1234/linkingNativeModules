package ro.vodafone.mcare.android.ui.fragments.paybill;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.SaveCreditCard;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.card.myCard.CreditCardSelection;
import ro.vodafone.mcare.android.client.model.billing.InvoiceDetailsSuccess;
import ro.vodafone.mcare.android.client.model.myCards.Card;
import ro.vodafone.mcare.android.client.model.myCards.CardsResponse;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.mentenance.PayBillLabels;
import ro.vodafone.mcare.android.client.model.users.NonVodafoneUser;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.cbu.ResSub;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.BasicUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.SubUserNonMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidHybridUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPostPaidsLowAccess;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.custom.InputEventsListenerInterface;
import ro.vodafone.mcare.android.interfaces.BillingFragmentInterface;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.MyCardsService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.PayBillActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.fragments.paybill.paybillviews.PaybillAnonymousCardView;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.TealiumHelper;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Bivol Pavel on 26.01.2017.
 */
public class AnonymousPayBillFragment extends BaseFragment implements BillingFragmentInterface, InputEventsListenerInterface {
    @BindView(R.id.paybill_anonymous_card)
    PaybillAnonymousCardView paybillAnonymousCard;
    @BindView(R.id.pay_own_bill_card)
    VodafoneGenericCard payOwnBillCard;

    public static String TAG = "AnonymousPayBillFrag";
    private static String PREFS_FILE_NAME;
    final int PICK_CONTACT = 1;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    boolean before;
    boolean after = true;
    View.OnClickListener readContactsPermissionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }
    };
    private VodafoneButton acceptPermissionBtn;
    private VodafoneButton refusePermisionBtn;
    private VodafoneTextView overlayTitle;
    private VodafoneTextView overlayContext;

    private Dialog readContactsPermissionOverlay;
    private PayBillActivity payBillActivity;
    VodafoneButton payOwnBillButton;

    public static void firstTimeAskingPermission(Context context, String permission, boolean isFirstTime) {
        SharedPreferences sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        sharedPreference.edit().putBoolean(permission, isFirstTime).apply();
    }

    public static boolean isFirstTimeAskingPermission(Context context, String permission) {
        return context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE).getBoolean(permission, true);
    }

    @Override
    public Fragment updateFragment(InvoiceDetailsSuccess invoiceDetailsSuccess) {
        return this;
    }

    @Override
    public void populateView() {

    }

    @Override
    public String getAnonymousPhoneNumber() {
        return paybillAnonymousCard.getPhoneNumber();
    }

    @Override
    public String getAnonymousEmailAddress() {
        return paybillAnonymousCard.getEmail();
    }

    @Override
    public String getAnonymousInvoiceValue() {
        return paybillAnonymousCard.getInvoiceValue();
    }

    @Override
    public void manageAnonymousPaymentErrors(String errorCode) {
        if (errorCode.equals(ErrorCodes.API23_MISSING_BAN_ERROR.getErrorCode())) {
            paybillAnonymousCard.displayPhoneNumberError(PayBillLabels.getPayBillMsisdnNotRelatedToGsmSubscription());
        } else if (errorCode.equals(ErrorCodes.API23_SUBSCRIBER_NOT_FOUND.getErrorCode())) {
            paybillAnonymousCard.displayPhoneNumberError(PayBillLabels.getPayBillMsisdnNotRelatedToGsmSubscription());
        } else if (errorCode.equals(ErrorCodes.API23_INVOICE_NOT_AVAILABLE.getErrorCode())) {
            //paybillAnonymousCard.displayPhoneNumberError(PayBillLabels.getPayBillInvoiceNotAvailable());
            new CustomToast.Builder(getContext()).message(PayBillLabels.getPayBillInvoiceNotAvailable()).success(false).show();
        } else if (errorCode.equals(ErrorCodes.API23_INVOICE_ALREADY_PAID.getErrorCode())) {
            payBillActivity.setIsOwnBillPaid(true);
            new CustomToast.Builder(getContext()).message(PayBillLabels.getPayBillInvoiceAlreadyPaid()).success(false).show();
        } else if (errorCode.equals(ErrorCodes.API23_INVOICE_NOT_ISSUED.getErrorCode())) {
            payBillActivity.setNoBillIssued(true);
            new CustomToast.Builder(getContext()).message(PayBillLabels.getPayBillInvoiceNotAvailable()).success(false).show();
        } else if(errorCode.equals(ErrorCodes.API23_DISTRIBUTIONS_ON_MULTIPLE_ACCOUNTS.getErrorCode())){
            new CustomToast.Builder(getContext()).message(PayBillLabels.getPayBillDistributionOnMultipleAccountsError()).success(false).show();
        } else {
            payBillActivity.setApiFailed(true);
            new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil!").success(false).show();
        }
    }

    View.OnClickListener contactsButtonclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (Build.VERSION.SDK_INT >= 23) {
                requestReadContactsPermission();
            } else {
                opentContactsPage();
            }
        }
    };

    View.OnClickListener payAnotherBillListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Tealium Track Event
            trackEvent(TealiumConstants.paybill_anonymous_button);

            payBillActivity.getInvoiceDetails(AnonymousPayBillFragment.this, paybillAnonymousCard.getPhoneNumber(), null, true);
        }
    };

    View.OnClickListener payOwnBillClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //Tealium Track Event
            trackEvent(TealiumConstants.pay_own_bill_button);

            payBillActivity.getInvoiceDetails(new PayBillFragment(), null, null, false);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView: ");

        View v = inflater.inflate(R.layout.fragment_anonymous_pay_bill, container, false);
        ButterKnife.bind(this, v);

        payBillActivity = (PayBillActivity) getActivity();
        initNavigationHeader();
        payOwnBillButton = (VodafoneButton) payOwnBillCard.findViewById(R.id.pay_another_bill_button);
        payOwnBillButton.setText(PayBillLabels.getPayBillButton());

        initTracking();
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        payOwnBillButton.setOnClickListener(payOwnBillClickListener);
        paybillAnonymousCard.setPayAnotherBillButtonListener(payAnotherBillListener);
        paybillAnonymousCard.setContactsButtonClickListener(contactsButtonclickListener);
        paybillAnonymousCard.fillEmailInput(VodafoneController.getInstance().getUserProfile().getEmail());
        loadCards();
        setPayOwnBillCardVisibility();
    }
    private void addCards(boolean is_max, List<Card> myCards) {
        if (myCards!=null&&!myCards.isEmpty()) {
            for (int i=0;i<myCards.size();i++) {
                Card card = myCards.get(i);
                final CreditCardSelection creditCardSelection = new CreditCardSelection(getActivity());
                creditCardSelection.setCard(card);
                if(i==0) {
                    creditCardSelection.selectCard(true);
                    payBillActivity.selectedCreditCard = creditCardSelection;
                } else
                    creditCardSelection.selectCard(false);

                creditCardSelection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(payBillActivity.selectedCreditCard!=null) {
                            payBillActivity.selectedCreditCard.selectCard(false);
                        }
                        payBillActivity.selectedCreditCard = creditCardSelection;
                        payBillActivity.selectedCreditCard.selectCard(true);
                        payBillActivity.saveCreditCard.selectRadioButton(false);
                    }
                });
                paybillAnonymousCard.getCardsContainer().addView(creditCardSelection);

            }
        }
        boolean isSelected = false;
        if(myCards==null||myCards.size()==0)isSelected =true;
        addNewCreditCard(is_max,isSelected);

    }
    private void addNewCreditCard(boolean is_max,boolean isSelected){
        payBillActivity.saveCreditCard =new SaveCreditCard(getContext());
        payBillActivity.saveCreditCard.selectRadioButton(isSelected);
        if(!is_max)
            payBillActivity.saveCreditCard.enableCheckBox(true);
        else
            payBillActivity.saveCreditCard.enableCheckBox(false);

        payBillActivity.saveCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                payBillActivity.saveCreditCard.selectRadioButton(true);
                if(payBillActivity.selectedCreditCard!=null)
                    payBillActivity.selectedCreditCard.selectCard(false);
                payBillActivity.selectedCreditCard = null;
            }
        });
        paybillAnonymousCard.getOtherCardContainer().addView(payBillActivity.saveCreditCard);

    }
    private void loadCards() {
        showLoadingDialog();
        MyCardsService myCardsService = new MyCardsService(getContext());
        myCardsService.getCards(VodafoneController.getInstance().getUserProfile().getUserName()).subscribe(new RequestSaveRealmObserver<GeneralResponse<CardsResponse>>(){
            @Override
            public void onCompleted() {
                super.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                stopLoadingDialog();
                addCards(false,null);
            }

            @Override
            public void onNext(GeneralResponse<CardsResponse> response) {
                super.onNext(response);
                stopLoadingDialog();
                if(response!=null&&response.getTransactionSuccess()!=null&&response.getTransactionSuccess().getCardList().size()>0)
                    addCards(response.getTransactionSuccess().getMaxReached(),response.getTransactionSuccess().getCardList());
                else
                    addCards(false,null);
            }
        });
    }

    private void initNavigationHeader() {
        payBillActivity.getNavigationHeader().hideSelectorView();
        payBillActivity.getNavigationHeader().showTriangleView();
        payBillActivity.getNavigationHeader().setTitle(PayBillLabels.getPayBillTitle());
    }

    private void initTracking() {
        //Tealium Track View
        TealiumHelper.tealiumTrackView(getClass().getSimpleName(),TealiumConstants.billing,TealiumConstants.paybill_anonymous);

        PayBillAnonymousTrackingEvent event = new PayBillAnonymousTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event7 = "event7";
        journey.getContextData().put("event7", journey.event7);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    private void setPayOwnBillCardVisibility() {
        if (!payBillActivity.isOwnBillPaid() && !payBillActivity.isNoBillIssued() && isAllowUserToPayOwnBill() && !payBillActivity.isApiFailed()) {
            payOwnBillCard.setVisibility(View.VISIBLE);
        } else {
            payOwnBillCard.setVisibility(View.GONE);
        }
    }

    private boolean isAllowUserToPayOwnBill() {

        boolean isAllow = true;
        User user = VodafoneController.getInstance().getUser();

        if (user instanceof SubUserNonMigrated || user instanceof BasicUser || user instanceof NonVodafoneUser ||
               (user instanceof PrepaidUser && !(user instanceof PrepaidHybridUser)) || user instanceof ResSub || user instanceof SeamlessPrepaidUser ||
                user instanceof SeamlessPostPaidsLowAccess || (user instanceof EbuMigrated && isEbuMigratedSubscriberOrNotVerified())){
            isAllow = false;
        }
        return isAllow;
    }

    private boolean isEbuMigratedSubscriberOrNotVerified(){
        return EbuMigratedIdentityController.isEbuMigratedSubscriberOrNotVerified();
    }

    private void opentContactsPage() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_CONTACT:
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI};
                    Cursor c = getActivity().getContentResolver().query(contactData, projection, null, null, null);
                    c.moveToFirst();

                    int nameIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int phoneNumberIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    String name = c.getString(nameIdx);
                    String phoneNumber = c.getString(phoneNumberIdx);

                    if (phoneNumber != null) {

                        if (phoneNumber.contains(" ") || phoneNumber.contains(")") || phoneNumber.contains("(") || phoneNumber.contains("-")) {
                            phoneNumber = phoneNumber.replaceAll("[()\\s-]+", "");
                        }

                        if (phoneNumber.length() > 10) {
                            phoneNumber = phoneNumber.substring(phoneNumber.length() - 10, phoneNumber.length());
                        }
                    }

                    paybillAnonymousCard.fillNumberInput(phoneNumber);
                    c.close();
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestReadContactsPermission() {
        int hasWriteContactsPermission = getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            before = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
            if (isFirstTimeAskingPermission(getContext(), Manifest.permission.READ_CONTACTS)) {
                firstTimeAskingPermission(getContext(), Manifest.permission.READ_CONTACTS, false);
                displayReadContactsPermissionOverlay(false);
            } else if (!before && !after) {
                displayReadContactsPermissionOverlay(true);
            } else {
                displayReadContactsPermissionOverlay(false);
            }
        } else opentContactsPage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permision granted");
                    opentContactsPage();
                    readContactsPermissionOverlay.dismiss();
                } else {
                    if (readContactsPermissionOverlay.isShowing()) {
                        readContactsPermissionOverlay.dismiss();
                    }
                    after = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
                }
            }
        }
    }

    protected void displayReadContactsPermissionOverlay(boolean flagWasChecked) {
        readContactsPermissionOverlay = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        readContactsPermissionOverlay.setContentView(R.layout.overlay_dialog_notifications);
        readContactsPermissionOverlay.show();

        overlayTitle = (VodafoneTextView) readContactsPermissionOverlay.findViewById(R.id.overlayTitle);
        overlayContext = (VodafoneTextView) readContactsPermissionOverlay.findViewById(R.id.overlaySubtext);

        acceptPermissionBtn = (VodafoneButton) readContactsPermissionOverlay.findViewById(R.id.buttonKeepOn);
        refusePermisionBtn = (VodafoneButton) readContactsPermissionOverlay.findViewById(R.id.buttonTurnOff);
        ImageView closeBtn = (ImageView) readContactsPermissionOverlay.findViewById(R.id.overlayDismissButton);
        closeBtn.setOnClickListener(dismissOverlayClickListener);

        setupOverlayLabels(flagWasChecked);
    }

    private void setupOverlayLabels(boolean flagWasChecked) {
        if (flagWasChecked) {
            overlayContext.setText(AppLabels.getOverlay_contacts_permission_context());
            overlayTitle.setText(AppLabels.getOverlay_contacts_permission_title());
            setButtonsForCheckedFlagOverlay();
        } else {
            overlayTitle.setText(AppLabels.getOverlay_contacts_permission_title());
            overlayContext.setText(AppLabels.getOverlay_contacts_permission_context());
            setButtonsForUncheckedFlagOverlay();
        }
    }

    private void setButtonsForCheckedFlagOverlay() {
        acceptPermissionBtn.setText(AppLabels.getOk_label());
        refusePermisionBtn.setVisibility(View.GONE);
        acceptPermissionBtn.setOnClickListener(dismissOverlayClickListener);
    }

    private void setButtonsForUncheckedFlagOverlay() {
        acceptPermissionBtn.setText(AppLabels.getAccept_button_label());
        refusePermisionBtn.setText(AppLabels.getDo_later_button_label());
        refusePermisionBtn.setOnClickListener(dismissOverlayClickListener);
        acceptPermissionBtn.setOnClickListener(readContactsPermissionListener);
    }

    View.OnClickListener dismissOverlayClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            readContactsPermissionOverlay.dismiss();
        }
    };

    @Override
    public void displayErrorMessage() {
        paybillAnonymousCard.displayInputError();
    }

    @Override
    public void hideErrorMessage() {
        paybillAnonymousCard.hideInputError();
    }

    @Override
    public void activateButton() {
        paybillAnonymousCard.activatePaymentButton();
    }

    @Override
    public void inactivateButton() {
        paybillAnonymousCard.inactivatePaymentButton();
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.PG_PAYBILL_ANONIM);
    }

    public static class PayBillAnonymousTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "pay bill anonymous";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "pay bill anonymous");
            s.eVar20 = "eVar20";
            s.getContextData().put("eVar20", s.eVar20);
            s.channel = "pay bill";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "pay bill anonymous";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "task";
            s.getContextData().put("eVar19", s.eVar19);
            s.prop21 = "mcare:" + "pay bill anonymous";
            s.getContextData().put("prop21", s.prop21);
        }
    }
    private void trackEvent(String event) {
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.paybill_anonymous);
        tealiumMapEvent.put(TealiumConstants.event_name, event);
        if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);

    }
}

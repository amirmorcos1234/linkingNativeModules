package ro.vodafone.mcare.android.ui.fragments.topUp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.favoriteNumbers.FavoriteNumber;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.client.model.users.seamless.SeamlessPrepaidUser;
import ro.vodafone.mcare.android.custom.CustomEditText;
import ro.vodafone.mcare.android.custom.InputEventsListenerInterface;
import ro.vodafone.mcare.android.interfaces.TopUpFragmentTabInterface;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.TopUpActivity;
import ro.vodafone.mcare.android.ui.fragments.loyaltyPoints.LoyaltyHistoryTabHost;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.RechargeValueSection;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.FavoriteNumbersSpinnerAdapter;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinner;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by Bivol Pavel on 23.02.2017.
 */
public class TopUpPostpaidPost4PreFragment extends BaseTopUpFragment implements InputEventsListenerInterface, VodafoneSpinner.Callback,
        RechargeValueSection.Callback, TopUpFragmentTabInterface, FavoriteNumbersSpinnerAdapter.Callback {

    public final String TAG = "TopUpPostpaidPost4Pre";

    private Fragment tabFragment;
    private View tabView;

    private LinearLayout emailErrorLayout;

    private TextView emailErrorMessage;
    private LinearLayout tabHostContainer;
    private VodafoneGenericCard fragmentCardContainer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.fragment_top_up_postpaid_post4pre, container, false);

        fragmentCardContainer = (VodafoneGenericCard) view.findViewById(R.id.fragment_card_container);

        tabHostContainer = (LinearLayout) view.findViewById(R.id.topup_tabhost_container);
        rechargeValueSection = (RechargeValueSection) view.findViewById(R.id.recharge_value_section);

        phoneNumberErrorMessage = (TextView) view.findViewById(R.id.phone_number_error_message);
        emailErrorMessage = (TextView) view.findViewById(R.id.email_error_message);

        phoneNumberInput = (CustomEditText) view.findViewById(R.id.telephone_number_input);

        emailAddressInput = (CustomEditText) view.findViewById(R.id.email_address_input);

        favoriteNumbersLayout = (LinearLayout) view.findViewById(R.id.favorite_numbers_layout);
        phoneNumberErrorLayout = (LinearLayout) view.findViewById(R.id.phone_number_error_layout);
        emailErrorLayout = (LinearLayout) view.findViewById(R.id.email_error_layout);
        rechargeButton = (VodafoneButton) view.findViewById(R.id.recharge_button);
        rechargeButton.setOnClickListener(rechargeButtonListner);

        contactsButton = (LinearLayout) view.findViewById(R.id.contacts_button);
        contactsButton.setOnClickListener(contactsButtonListner);

        setupTabs();
        initSpinner();

        if (!VodafoneController.getInstance().isSeamless()) {
            emailAddressInput.setText(VodafoneController.getInstance().getUserProfile().getEmail());
        }
        rechargeValueSection.setRecommendedRechargeValues(null,true,false);
        rechargeValueSection.setDefaultSelectedBobble(1);
        rechargeValueSection.setOtherValueInput(1);

        //remove error message after off focus event
        phoneNumberInput.showDefaultInputStyle();
        phoneNumberErrorLayout.setVisibility(View.GONE);

        TopUpActivity.TopUpTrackingEvent event = new TopUpActivity.TopUpTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.prop21 = "mcare:" + "top up";
        journey.getContextData().put("prop21", journey.prop21);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);

        /*Added globalLayoutListener to follow dimens changes in fragment view. This is solution for VNM-3297 Recharge Button on tablets.*/
        view.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TopUpActivity) getActivity()).scrolltoTop();
        TealiumHelper.tealiumTrackView(TopUpPostpaidPost4PreFragment.class.getSimpleName(),
                TealiumConstants.topUpJourney,TealiumConstants.topUpPostpaidOtherNumberScreenName);
    }

    public void setupTabs() {
        Log.d(TAG, "setupTabs");

        if (mTabHost != null) {
            Log.d(TAG, "mTabHost is not null");
            mTabHost.clearAllTabs();
            tabHostContainer.removeAllViews();
        }
        mTabHost = new LoyaltyHistoryTabHost(getContext());
        mTabHost.setup(getContext(), getChildFragmentManager(), android.R.id.tabcontent);
        tabHostContainer.addView(mTabHost);

        mTabHost.getTabWidget().setDividerDrawable(null);

        mTabHost.addTab(
                mTabHost.newTabSpec("0")
                        .setIndicator(createTabIndicator(TopUPLabels.getTop_up_immediate_tab_title())),
                TopUpPostpaidImmediateTabFragment.class, null);

        mTabHost.addTab(
                mTabHost.newTabSpec("1")
                        .setIndicator(createTabIndicator(TopUPLabels.getTop_up_programed_tab_title())),
                TopUpPostpaidProgrammedTabFragment.class, null);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {

                if (tabId.equalsIgnoreCase("0")) {
                    TealiumHelper.tealiumTrackEvent(TopUpPostpaidPost4PreFragment.class.getSimpleName(),
                            TopUPLabels.getTop_up_immediate_tab_title(), TealiumConstants.topUpPostpaidOtherNumberScreenName, "tab=");
                } else if (tabId.equalsIgnoreCase("1")) {
                    TealiumHelper.tealiumTrackEvent(TopUpPostpaidPost4PreFragment.class.getSimpleName(),
                            TopUPLabels.getTop_up_programed_tab_title(), TealiumConstants.topUpPostpaidOtherNumberScreenName, "tab=");
                }

                hideKeyboard();

                if (tabId.equalsIgnoreCase("1")) {
                    rechargeValueSection.activateRadioButtons();
                }
                if (rechargeButton.getVisibility() != View.VISIBLE) {
                    rechargeButton.setVisibility(View.VISIBLE);
                }
                getChildFragmentManager().executePendingTransactions();
                setTabColor(mTabHost);

                activateButton();
            }
        });
        mTabHost.setFocusableInTouchMode(true);
        mTabHost.setFocusable(true);

        setTabColor(mTabHost);
    }

    public void setTabHeight(int height) {
        Log.d(TAG, "setTabHeight -" + height);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenMeasure.dpToPx(height));
        view.findViewById(android.R.id.tabcontent).setLayoutParams(params);
    }

    public void setTabHeightinpx(int height) {
        Log.d(TAG, "setTabHeight -" + height);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        view.findViewById(android.R.id.tabcontent).setLayoutParams(params);
    }

    private void initTabViewsElements() {
        if (tabFragment != null) {
            if (tabFragment instanceof TopUpPostpaidImmediateTabFragment) {

                paymentType = (RadioGroup) tabView.findViewById(R.id.payment_type);
                voucherCodeInput = (CustomEditText) tabView.findViewById(R.id.voucher_input);
                payWithVoucherLayout = (LinearLayout) tabView.findViewById(R.id.pay_with_voucher_layout);
                paymentIconsLayout = (LinearLayout) tabView.findViewById(R.id.payment_icons_layout);
                voucherErrorLayout = (LinearLayout) tabView.findViewById(R.id.voucher_error_layout);
                voucherErrorMessage = (VodafoneTextView) tabView.findViewById(R.id.voucher_error_message);

            } else if (tabFragment instanceof TopUpPostpaidProgrammedTabFragment) {

            }
        }
    }

    private View createTabIndicator(String label) {
        View tabIndicator = getActivity().getLayoutInflater().inflate(R.layout.tab_indicator_background, null);
        TextView tv = (TextView) tabIndicator.findViewById(R.id.label);
        tv.setText(label);
        return tabIndicator;
    }

    public void setTabColor(TabHost tabhost) {

        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
            tabhost.getTabWidget().getChildAt(i)
                    .setBackgroundResource(R.drawable.unselected_tab_shape); // unselected
            TextView tabTitle = (TextView) tabhost.getTabWidget().getChildAt(i).findViewById(R.id.label);
            tabTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.tab_title_color));

        }
        tabhost.getTabWidget().setCurrentTab(0);
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
                .setBackgroundResource(R.drawable.selected_tab_shape); // selected
        TextView tabTitle = (TextView) tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).findViewById(R.id.label);
        tabTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.selected_tab_indicator_color));
    }


    @Override
    public void displayErrorMessage() {
        Log.d(TAG, "displayErrorMessage");

        if (phoneNumberInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS && phoneNumberInput.isValide() != CustomEditText.EMPTY_FIELD_STATUS) {
            displayError(phoneNumberErrorLayout, phoneNumberErrorMessage, TopUPLabels.getTop_up_invalid_msisdn());
        }

        if (rechargeValueSection.otherValueInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS && rechargeValueSection.otherValueInput.isValide() != CustomEditText.EMPTY_FIELD_STATUS) {
            rechargeValueSection.showError(topUpErrorMessage());
        }

        if (emailAddressInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS && emailAddressInput.isValide() != CustomEditText.EMPTY_FIELD_STATUS) {
            displayError(emailErrorLayout, emailErrorMessage, TopUPLabels.getTop_up_invalid_email());
        }

        if (payWithVoucherLayout != null && payWithVoucherLayout.getVisibility() == View.VISIBLE) {
            if (voucherCodeInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS || voucherCodeInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS) {
                displayError(voucherErrorLayout, voucherErrorMessage, TopUPLabels.getTop_up_invalid_voucher_input());
            }
        }
    }

    @Override
    public void hideErrorMessage() {
        Log.d(TAG, "hideErrorMessage");

        if (phoneNumberInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && phoneNumberInput.isHighlighted()) {
            phoneNumberInput.removeHighlight();
        }

        if (rechargeValueSection.otherValueInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && rechargeValueSection.otherValueInput.isHighlighted()) {
            rechargeValueSection.otherValueInput.removeHighlight();
        }

        if (emailAddressInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS && emailAddressInput.isHighlighted()) {
            emailAddressInput.removeHighlight();
        }

        if (phoneNumberInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || phoneNumberInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS) {
            phoneNumberErrorLayout.setVisibility(View.GONE);

        }

        if (rechargeValueSection.otherValueInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || rechargeValueSection.otherValueInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS) {
            rechargeValueSection.hideError();
        }


        if (emailAddressInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS || emailAddressInput.isValide() == CustomEditText.EMPTY_FIELD_STATUS) {
            emailErrorLayout.setVisibility(View.GONE);
        }

        if (payWithVoucherLayout != null && payWithVoucherLayout.getVisibility() == View.VISIBLE) {
            if (voucherCodeInput.isValide() == CustomEditText.VALIDE_FIELD_STATUS) {
                voucherErrorLayout.setVisibility(View.GONE);
            }
        }

        activateButton();
    }

    @Override
    public void activateButton() {
        Log.d(TAG, "activateButton");

        boolean isValidePhoneNumber;
        boolean isValideEmail;

        //this parameters is true because by default its is hiden
        boolean isValideRechargeValue = true;
        boolean isValideVoucher = true;

        isValidePhoneNumber = !phoneNumberInput.isEmpty() && !phoneNumberInput.isHighlighted();
        isValideEmail = !emailAddressInput.isEmpty() && !emailAddressInput.isHighlighted();

        if (rechargeValueSection.isVisibleOtherValueLayout()) {
            isValideRechargeValue = !rechargeValueSection.otherValueInput.isEmpty() && !rechargeValueSection.otherValueInput.isHighlighted();
        }

        if (payWithVoucherLayout != null && payWithVoucherLayout.getVisibility() == View.VISIBLE) {
            isValideVoucher = !voucherCodeInput.isEmpty() && isVoucherCodeFormat(voucherCodeInput.getText().toString());
        }

        Log.d(TAG, "activateButton: email " + isValideEmail);
        Log.d(TAG, "activateButton: phone " + isValidePhoneNumber);
        Log.d(TAG, "activateButton: value " + isValideRechargeValue);
        Log.d(TAG, "activateButton: value " + isValideVoucher);

        if (isValideEmail && isValidePhoneNumber && isValideRechargeValue && isValideVoucher) {
            rechargeButton.setEnabled(true);
        }
    }

    @Override
    public void inactivateButton() {
        Log.d(TAG, "inactivateButton");

        rechargeButton.setEnabled(false);
    }

    public void displayError(LinearLayout errorLayout, TextView errorTextView, String errorMessage) {
        errorLayout.setVisibility(View.VISIBLE);
        errorTextView.setText(errorMessage);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void requestReadContactsPermission() {
        super.requestReadContactsPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void selectSpinnerElement(Object selectedValue) {
        Log.d(TAG, "selectSpinnerElement");
        phoneNumberInput.setText(((FavoriteNumber) selectedValue).getPrepaidMsisdn());
    }

    @Override
    public void selectElement(int amount) {
        Log.d(TAG, "selectSpinnerElement");
        this.amount = amount;
    }

    @Override
    public void displayOtherValueField() {
        if (!rechargeValueSection.isVisibleOtherValueLayout() && rechargeValueSection.otherValueInput.isEmpty()) {
            inactivateButton();
        }
    }

    @Override
    public void hideOtherValueField() {
        activateButton();
    }

    @Override
    public void fragmentViewInitialized(Fragment fragment) {
        tabFragment = fragment;
        tabView = fragment.getView();

        initTabViewsElements();
    }

    @Override
    public void deleteFavoriteNumeber(int position) {
        deleteFavoriteNumber(position);
    }


    private boolean isVoucherCodeFormat(String voucherCode) {
        boolean isValid = false;
        if (voucherCode != null && !voucherCode.equals("")) {

            Pattern pattern = Pattern.compile("^[0-9]{14}$");
            Matcher matcher = pattern.matcher(voucherCode);

            if (matcher.matches()) {
                Log.d(TAG, "isVoucherCodeFormat: matches");
                isValid = true;
            } else {
                Log.d(TAG, "isVoucherCodeFormat: don't match");
                isValid = false;
            }
        }
        return isValid;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if ((TopUpActivity) getActivity() != null) {
                //get navigationHeader height
                final int navigationHeaderHeight = ((TopUpActivity) getActivity()).getNavigationHeader().getHeight();

                //get display Height
                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                final int displayHeight = metrics.heightPixels;

                //check if activity height is < displayHeight. If so, setMinimumHeight for fragment's
                // view and disable scrolling because the cardView is smaller than screen so dont need scrolling

                if (view.getHeight() + navigationHeaderHeight < displayHeight) {
                    Log.d(TAG, "onGlobalLayout: activity height smaller ");
                    view.setMinimumHeight(displayHeight - navigationHeaderHeight);
                    ((TopUpActivity) getActivity()).getMenuScrollView().setEnableScrolling(false);

                    //in case activity height is larger -> enable scrolling
                } else if (view.getHeight() + navigationHeaderHeight > displayHeight) {
                    Log.d(TAG, "onGlobalLayout: activity height larger ");
                    ((TopUpActivity) getActivity()).getMenuScrollView().setEnableScrolling(true);
                    //in case activity height equals to displayHeight disable scrolling
                } else  {
                    Log.d(TAG, "onGlobalLayout: activity height equals screen height ");
                    ((TopUpActivity) getActivity()).getMenuScrollView().setEnableScrolling(false);
                }
            }
        }

    };

    public void removeGlobalLayoutListener() {
        view.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TopUpActivity) getActivity()).getNavigationHeader().setTitle(TopUPLabels.getTop_up_page_title());

        if (VodafoneController.getInstance().getUser() instanceof PrepaidUser || VodafoneController.getInstance().getUser() instanceof SeamlessPrepaidUser) {
            ((TopUpActivity)getActivity()).getNavigationHeader().buildMsisdnSelectorHeader();
        } else if (VodafoneController.getInstance().getUser() instanceof PostPaidUser) {
            ((TopUpActivity)getActivity()).getNavigationHeader().buildBanSelectorHeader();
        }
    }

    public void showOrNotRechargeButton(String tabId, boolean isRechargeButtonVisible){
        if(mTabHost != null){
            if (mTabHost.getCurrentTabTag().equals(tabId)){
                if(isRechargeButtonVisible)
                    rechargeButton.setVisibility(View.VISIBLE);
                else
                    rechargeButton.setVisibility(View.GONE);
            }
        }
    }


    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.TOP_UP);
    }
}

package ro.vodafone.mcare.android.ui.fragments.topUp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.creditInAdvance.CreditInAdvanceSuccess;
import ro.vodafone.mcare.android.client.model.creditInAdvance.EligibilityInfo;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.CreditInAdvanceLabels;
import ro.vodafone.mcare.android.constants.ErrorCodes;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.CreditInAdvanceService;
import ro.vodafone.mcare.android.ui.activities.TopUpActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by User on 11.07.2017.
 */

public class TopUpCreditInAdvanceFragment extends BaseFragment {

    @BindView(R.id.credit_in_advance_instructional_text)
    VodafoneTextView instructionalText;

    @BindView(R.id.first_point_text)
    VodafoneTextView firstPointText;

    @BindView(R.id.second_point_text)
    VodafoneTextView secondPointText;

    @BindView(R.id.credit_in_advance_recharge_button)
    VodafoneButton rechargeButton;

    @BindView(R.id.credit_in_advance_continue_button)
    VodafoneButton continueButton;

    @BindView(R.id.credit_in_advance_card)
    VodafoneGenericCard creditInAdvanceCard;

    @BindView(R.id.error_card)
    CardErrorLayout errorCard;

    private String TAG = "CreditInAdvanceFrag";
    private Dialog overlayDialog;

    VodafoneGenericCard vodafoneErrorCard;
    Unbinder binder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initNavigationHeader();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_top_up_credit_in_advance, container, false);
        binder = ButterKnife.bind(this, v);

        return v;
    }

    View errorLayout() {
        showLoadingDialog();
        Log.d(TAG, "errorLayout: ");
        vodafoneErrorCard = new VodafoneGenericCard(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vodafoneErrorCard.setLayoutParams(params);
        vodafoneErrorCard.showError(true, AppLabels.getGenericRetryErrorMessage());
        vodafoneErrorCard.getErrorView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();

            }
        });
        stopLoadingAfterDuration(1);
        return vodafoneErrorCard;
    }

    private void loadData() {
        ((TopUpActivity) getActivity()).getCreditInAdvanceEligibility();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setData(getArguments());
        TealiumHelper.tealiumTrackView(TopUpCreditInAdvanceFragment.class.getSimpleName(),
                TealiumConstants.topUpJourney,TealiumConstants.topUpCreditInAdvanceScreenName);
    }

    public void setData(Bundle arguments) {
        if (vodafoneErrorCard != null && getView() != null) {
            ((FrameLayout) getView()).removeView(vodafoneErrorCard);
        }

        boolean isError = arguments.getBoolean("isError");
        String errorCode = arguments.getString("errorCode");

        if (isError) {
            showApiError();
        } else if (errorCode != null) {
            refreshView(errorCode);
        } else {
            refreshView(null);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }

    View.OnClickListener rechargeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getFragmentManager().popBackStack();
            new NavigationAction(getContext()).finishCurrent(false).startAction(IntentActionName.TOP_UP_PREPAID_OWN_NUMBER);
        }
    };

    View.OnClickListener continueButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().onBackPressed();
        }
    };

    View.OnClickListener acceptCreditListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            displayConfirmCreditDialog();
        }
    };

    View.OnClickListener dismissListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            overlayDialog.dismiss();
        }
    };

    View.OnClickListener performCreditListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            performCredit();
        }
    };


    private void setupLabels() {
        instructionalText.setText(CreditInAdvanceLabels.getCredit_in_advance_instructional_text());
        firstPointText.setText(CreditInAdvanceLabels.getCredit_in_advance_first_dot_text());
        secondPointText.setText(CreditInAdvanceLabels.getCredit_in_advance_second_dot_text());
    }

    private void setupButtons(boolean isEligible) {
        if (isEligible) {
            rechargeButton.setOnClickListener(acceptCreditListener);
            rechargeButton.setText(CreditInAdvanceLabels.getCredit_in_advance_accept_credit());
            continueButton.setVisibility(View.GONE);
        } else {
            rechargeButton.setText(CreditInAdvanceLabels.getCredit_in_advance_recharge_button_label());
            rechargeButton.setOnClickListener(rechargeButtonListener);
            continueButton.setOnClickListener(continueButtonListener);
            continueButton.setVisibility(View.VISIBLE);
        }
    }

    private void initMessageForNonEligible(String message) {
        errorCard.setText(message);
        errorCard.setMargins(0, 0, 0, 10);

        ImageView attentionIcon = (ImageView) errorCard.findViewById(R.id.image);
        attentionIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.warning));
        ColorFilter filter = new PorterDuffColorFilter(ContextCompat.getColor(getContext(), R.color.widget_warning_icon_color), PorterDuff.Mode.SRC_IN);
        attentionIcon.setColorFilter(filter);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ScreenMeasure.dpToPx(32), ScreenMeasure.dpToPx(32));
        attentionIcon.setLayoutParams(params);
    }

    public void refreshView(String errorCode) {
        creditInAdvanceCard.setVisibility(View.VISIBLE);
        setupLabels();

        if (errorCode != null) {
            Log.d(TAG, "refreshView: errorcodes " + ErrorCodes.API_64_USER_IS_NOT_ELIGIBLE.getErrorCode());
            Log.d(TAG, "refreshView: errorcode " + errorCode);
            if (errorCode.equals(ErrorCodes.API_64_USER_IS_NOT_ELIGIBLE.getErrorCode())) {
                errorCard.setVisibility(View.VISIBLE);
                initMessageForNonEligible(CreditInAdvanceLabels.getCredit_in_advance_general_not_eligible_message());
                setupButtons(false);
            } else if (errorCode.equals(ErrorCodes.API_64_CREDIT_IN_ADVANCE_108_ERROR.getErrorCode())) {
                setupButtons(false);
                errorCard.setVisibility(View.VISIBLE);
                initMessageForNonEligible(CreditInAdvanceLabels.getCredit_in_advance_not_eligible_error_message());
            } else {
                showApiError();
            }
        } else {
            errorCard.setVisibility(View.GONE);
            setupButtons(true);
        }
    }

    public void showApiError() {
        if (creditInAdvanceCard != null && creditInAdvanceCard.getVisibility() == View.VISIBLE) {
            creditInAdvanceCard.setVisibility(View.GONE);
        }
        if ((FrameLayout) getView() != null)
            ((FrameLayout) getView()).addView(errorLayout());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binder.unbind();
        Log.d(TAG, "onDestroyView: ");
    }

    private void displayConfirmCreditDialog() {
        overlayDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        overlayDialog.setContentView(R.layout.overlay_dialog_notifications);

        Button dismissButton = (Button) overlayDialog.findViewById(R.id.buttonTurnOff);
        dismissButton.setText(AppLabels.getOverlayCancelButton());
        dismissButton.setOnClickListener(dismissListener);

        Button okButton = (Button) overlayDialog.findViewById(R.id.buttonKeepOn);
        okButton.setText(CreditInAdvanceLabels.get_ok_overlay_button());
        okButton.setOnClickListener(performCreditListener);

        ImageView closeButton = (ImageView) overlayDialog.findViewById(R.id.overlayDismissButton);
        closeButton.setOnClickListener(dismissListener);

        VodafoneTextView overlayTitle = (VodafoneTextView) overlayDialog.findViewById(R.id.overlayTitle);
        overlayTitle.setText(CreditInAdvanceLabels.getCredit_in_advance_overlay_title());

        VodafoneTextView overlayMessage = (VodafoneTextView) overlayDialog.findViewById(R.id.overlaySubtext);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            overlayMessage.setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);
        }

        overlayMessage.setText(createOverlayMessage());

        overlayDialog.show();
    }

    private String createOverlayMessage() {
        String message = "";
        CreditInAdvanceSuccess creditInAdvanceSuccess = (CreditInAdvanceSuccess) RealmManager.getRealmObject(CreditInAdvanceSuccess.class);
        if (creditInAdvanceSuccess != null) {
            EligibilityInfo eligibilityInfo = (EligibilityInfo) creditInAdvanceSuccess.getCIAEligibiliy();
            message = String.format(CreditInAdvanceLabels.getCredit_in_advance_overlay_text(),
                    NumbersUtils.twoDigitsAfterDecimal(Float.valueOf(eligibilityInfo.getCredit())), NumbersUtils.twoDigitsAfterDecimal(Float.valueOf(eligibilityInfo.getFee())));
        }

        return message;
    }

    private void performCredit() {
        CreditInAdvanceSuccess creditInAdvanceSuccess = (CreditInAdvanceSuccess) RealmManager.getRealmObject(CreditInAdvanceSuccess.class);
        if (creditInAdvanceSuccess != null) {

            CreditInAdvanceService creditService = new CreditInAdvanceService(getContext());
            creditService.performCreditInAdvance((EligibilityInfo) creditInAdvanceSuccess.getCIAEligibiliy()).subscribe(new RequestSaveRealmObserver<GeneralResponse<CreditInAdvanceSuccess>>() {

                @Override
                public void onNext(GeneralResponse<CreditInAdvanceSuccess> response) {
                    super.onNext(response);
                    //TODO: CR: we should check that the fragment is still visible. Can we try having
                    //          a queue of operations to be performed when the fragment is visible?
                    //          We could process the queue when the fragment resumes and remove it when
                    //          the view gets destroyed in onDestroyView

                    Log.d(TAG, "onNext: ");
                    if (response.getTransactionStatus() != 0) {
                        overlayDialog.dismiss();
                        new CustomToast.Builder(getContext()).message(AppLabels.getToastErrorMessage()).success(false).show();
                    } else {
                        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(10, 20, VoiceOfVodafoneCategory.Recharge, null, CreditInAdvanceLabels.getCredit_in_advance_vov_message(), "Continuă", null, true, false, VoiceOfVodafoneAction.Dismiss, null);
                        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);

                        new CustomToast.Builder(getContext()).message(CreditInAdvanceLabels.getCredit_in_advance_success_toast()).success(true).show();
                        new NavigationAction(getActivity()).startAction(IntentActionName.DASHBOARD, true);

                    }
                }

                @Override
                public void onCompleted() {
                    super.onCompleted();
                    Log.d(TAG, "onCompleted: ");
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    Log.d(TAG, "onError: ");
                    overlayDialog.dismiss();
                    new CustomToast.Builder(getContext()).message(AppLabels.getToastErrorMessage()).success(false).show();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initNavigationHeader();
    }

    private void initNavigationHeader() {
        ((TopUpActivity) getActivity()).getNavigationHeader().hideSelectorView();
        ((TopUpActivity) getActivity()).getNavigationHeader().setTitle(CreditInAdvanceLabels.getTop_up_credit_in_advance());
    }
}

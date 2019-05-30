package ro.vodafone.mcare.android.ui.fragments.loyaltyPoints;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.client.model.loyaltyProgram.OptInSuccess;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.service.ShopService;
import ro.vodafone.mcare.android.ui.activities.loyalty.LoyaltyPointsActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersFragment;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.Fonts;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;


/**
 * Created by User on 20.04.2017.
 */

public class EnrollInProgramPageFragment extends OffersFragment {
    private String shopToken;
    LinearLayout v;
    VodafoneGenericCard card;
    LoyaltyPointsActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (LoyaltyPointsActivity) VodafoneController.findActivity(LoyaltyPointsActivity.class);
        if (activity != null) {
            shopToken = activity.getShopSessionToken();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = (LinearLayout) inflater.inflate(R.layout.card_enroll_loyalty_program, null);
        if (activity != null) {
            activity.getNavigationHeader().setTitle(getTitle());
        }
        card = (VodafoneGenericCard) v.findViewById(R.id.enroll_in_program_card);

        TealiumHelper.tealiumTrackView(getClass().getSimpleName(), TealiumConstants.loyalty,TealiumConstants.loyalty_enroll_program_page);

        addMessageTextView();
        addPointsImageView();
        addEnrollButton();

        ((LoyaltyPointsActivity) getActivity()).getNavigationHeader().hideSelectorView();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((LoyaltyPointsActivity) getActivity()).getNavigationHeader().hideSelectorView();
    }

    @Override
    public String getTitle() {
        return LoyaltyLabels.getLoyalty_points_enroll_in_program();
    }

    private void addEnrollButton() {
        VodafoneButton enrollInProgramBtn;

        enrollInProgramBtn = new VodafoneButton(getContext());
        enrollInProgramBtn.setTransformationMethod(null);
        enrollInProgramBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        enrollInProgramBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white_text_color));
        enrollInProgramBtn.setTypeface(Fonts.getVodafoneRG());
        enrollInProgramBtn.setBackgroundResource(R.drawable.selector_button_background_card_primary);
        enrollInProgramBtn.setText(LoyaltyLabels.getLoyalty_enroll_now());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(ScreenMeasure.dpToPx(12), 0, ScreenMeasure.dpToPx(12), ScreenMeasure.dpToPx(12));
        enrollInProgramBtn.setLayoutParams(params);

        enrollInProgramBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tealium track event
                Map<String, Object> tealiumMapEvent = new HashMap(6);
                tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.loyalty_enroll_program_page);
                tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.loyalty_enroll_program_signup);
                if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
                    tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);

                enrollInProgram();
            }
        });
        card.addViewToBottom(enrollInProgramBtn);
    }

    private void addPointsImageView() {
        ImageView pointsImageView;
        pointsImageView = new ImageView(getContext());
        pointsImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.puncte_img));
        pointsImageView.setAdjustViewBounds(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(ScreenMeasure.dpToPx(12), ScreenMeasure.dpToPx(12), ScreenMeasure.dpToPx(12), ScreenMeasure.dpToPx(20));
        pointsImageView.setLayoutParams(params);
        card.addViewToBottom(pointsImageView);
    }

    private void addMessageTextView() {
        VodafoneTextView messageTextView = new VodafoneTextView(getContext());
        messageTextView.setText(LoyaltyLabels.getLoyalty_enroll_message());
        messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        messageTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray_text_color));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(ScreenMeasure.dpToPx(12), ScreenMeasure.dpToPx(16), ScreenMeasure.dpToPx(12), 0);
        messageTextView.setLayoutParams(params);
        card.addViewToBottom(messageTextView);
    }

    private void enrollInProgram() {
        if (activity != null) {
            Log.d("", "enrollInProgram: " + shopToken);
            new ShopService(getContext()).postEnrollLoyaltyProgram(UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan(), shopToken)
                    .subscribe(new RequestSessionObserver<GeneralResponse<OptInSuccess>>() {
                        @Override
                        public void onNext(GeneralResponse<OptInSuccess> optInSuccessGeneralResponse) {
                            if (optInSuccessGeneralResponse.getTransactionStatus() == 0) {
                                activity.getBanState();
                                FragmentManager fm = getFragmentManager();
                                fm.popBackStack();
                                fm.beginTransaction().commit();
                                String enrolledBan = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
                                new CustomToast.Builder(getContext()).message(String.format(LoyaltyLabels.getLoyaltySuccessEnroll(), enrolledBan)).success(true).show();
//                                CustomToast toast = new CustomToast(getActivity(), getContext(), String.format(getResources().getString(R.string.loyalty_success_enroll), enrolledBan), true);
//                                toast.show();
                            } else {
                                activity.showError(errorClickListener);
                            }
                        }

                        @Override
                        public void onCompleted() {
                            super.onCompleted();
                            TealiumHelper.tealiumTrackView(getClass().getSimpleName(), TealiumConstants.loyalty,TealiumConstants.loyalty_enroll_program_page);

                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            TealiumHelper.tealiumTrackView(getClass().getSimpleName(), TealiumConstants.loyalty,TealiumConstants.loyalty_enroll_program_page);
                            activity.showError(errorClickListener);
                        }
                    });

        }
    }

    View.OnClickListener errorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getFragmentManager().beginTransaction()
                    .detach(FragmentUtils.getVisibleFragment((AppCompatActivity) getActivity(), false))
                    .attach(FragmentUtils.getVisibleFragment((AppCompatActivity) getActivity(), false))
                    .commit();
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        ((LoyaltyPointsActivity) getActivity()).setTitle();
    }
}

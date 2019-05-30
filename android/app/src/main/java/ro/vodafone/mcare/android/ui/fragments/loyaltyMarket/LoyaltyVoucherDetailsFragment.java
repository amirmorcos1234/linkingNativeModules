package ro.vodafone.mcare.android.ui.fragments.loyaltyMarket;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;


import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneGenericCard;
import ro.vodafone.mcare.android.card.loyaltyMarket.LoyaltyVoucherDescriptionCard;
import ro.vodafone.mcare.android.card.loyaltyMarket.LoyaltyVoucherHeaderCard;
import ro.vodafone.mcare.android.card.offers.GeneralCardsWithTitleBodyAndTwoButtons;
import ro.vodafone.mcare.android.card.offers.ServiceCard;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltySegmentSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherReserveSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.Promotion;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.interfaces.fragment.base.OnScrollViewCreatedListener;
import ro.vodafone.mcare.android.interfaces.fragment.loyalty.LoyaltyVoucherCommunicationListener;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.rest.requests.ReserveVoucherRequest;
import ro.vodafone.mcare.android.service.LoyaltyMarketService;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.loyalty.LoyaltyMarketActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.TextUtils;
import ro.vodafone.mcare.android.ui.utils.ViewUtils;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Serban Radulescu on 8/25/2017.
 */

public class LoyaltyVoucherDetailsFragment extends BaseFragment {

    private static final String TAG = LoyaltyVoucherDetailsFragment.class.getSimpleName();

    private static final String PROMOTION_ID_KEY = "promotionId";

    @BindView(R.id.navigation_header)
    NavigationHeader navigationHeader;

    @BindView(R.id.voucher_details_container)
    LinearLayout viewGroup;

    @BindView(R.id.scroll_view)
    ScrollView scrollView;

    private Realm realm;
    private LoyaltyVoucherCommunicationListener loyaltyVoucherCommunicationListener;
    private LoyaltyMarketService loyaltyMarketService;

    private String voucherId;
    private Promotion promotionVoucher;

    private GeneralCardsWithTitleBodyAndTwoButtons reserveCard;

    OnScrollViewCreatedListener scrollViewCreatedListener;

    public LoyaltyVoucherDetailsFragment() {
    }

    public static LoyaltyVoucherDetailsFragment newInstance() {

        Bundle args = new Bundle();

        LoyaltyVoucherDetailsFragment fragment = new LoyaltyVoucherDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loyalty_voucher_details, null);
        ButterKnife.bind(this,view);

        TealiumHelper.tealiumTrackView(getClass().getSimpleName(), TealiumConstants.loyalty,TealiumConstants.loyalty_vouchers_details);
        realm = Realm.getDefaultInstance();
        loyaltyMarketService = new LoyaltyMarketService(getContext());
        showLoadingDialog();
        if(voucherId == null){
            try {
                voucherId = IntentActionName.LOYALTY_MARKET_VOUCHER_DETAILS.getOneUsageSerializedData();
                Log.d(TAG, "Voucher id: " + voucherId);
            } catch (Exception e){
                e.printStackTrace();
            }

        }
        if(areRequestFinished()) {
            loadVoucherFromRealm(false);
        } else {
            navigationHeader.displayDefaultHeader();
        }

		VodafoneMarketOfferTrackingEvent event = new VodafoneMarketOfferTrackingEvent(voucherId);
		VodafoneController.getInstance().getTrackingService().track(event);

        triggerNonTelcoQuestionnaire();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scrollViewCreatedListener.onScrollViewCreated(scrollView);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            loyaltyVoucherCommunicationListener = (LoyaltyVoucherCommunicationListener) context;
            scrollViewCreatedListener= (OnScrollViewCreatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement LoyaltyVoucherCommunicationListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    private boolean areRequestFinished() {
        return getActivity() == null ||
                ((LoyaltyMarketActivity) getActivity()).isGetReservedVouchersFinished() &&
                        ((LoyaltyMarketActivity) getActivity()).isGetVouchersFinished();

    }

    public void loadVoucherFromRealm(boolean isFromBanners){
        String imageUrl= "";
        promotionVoucher = (Promotion) RealmManager.getRealmObjectAfterStringField(realm,
                Promotion.class, PROMOTION_ID_KEY, voucherId);

        Promotion promotion = null;
        if(promotionVoucher != null) {
            promotion = realm.copyFromRealm(promotionVoucher);
        }

        if (promotion != null) {
            if(promotion.getBannerId() != null) {
                imageUrl = promotion.getBannerId();
            }
            if (promotion.getCategory() != null) {
                initNavigationHeader(promotion.getCategory(), imageUrl);
            } else {
                initNavigationHeader("", imageUrl);
            }

            loadVoucherLayout(promotion.isReserved(), isFromBanners);

        } else if(getActivity() != null){
            viewGroup.removeAllViews();

            VodafoneGenericCard errorCard = getErrorCard();
            viewGroup.addView(errorCard);
            stopLoadingDialog();

            navigationHeader.setTitle(LoyaltyLabels.getLoyaltyVoucherDetailsPageTitle());
        }
    }

    private VodafoneGenericCard getErrorCard(){
        String errorMessage;
        VodafoneGenericCard errorCard = new VodafoneGenericCard(getActivity());

        if(((LoyaltyMarketActivity) getActivity()).isGetVouchersError()){
            errorMessage = LoyaltyLabels.getErrorServiceTemporarilyUnavailableClickToRetry();
            errorCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getActivity() != null){
                        loyaltyVoucherCommunicationListener.setRefreshVoucherDetails(false);
                        loyaltyVoucherCommunicationListener.reloadVouchers();
                    }
                }
            });
        }
        else {
            errorMessage = LoyaltyLabels.getMarketErrorInvalidVoucher();
        }

        errorCard.showError(true, errorMessage);
        return errorCard;
    }


    private void initNavigationHeader(String voucherCategory, String imageUrl){
        navigationHeader.setTitle(LoyaltyLabels.getLoyaltyVoucherDetailsPageTitle() + " " + voucherCategory);

        View v = (View) View.inflate(getActivity(), R.layout.header_simple_image_view, null);
        ImageView imageView = (ImageView) v.findViewById(R.id.header_image);

        if(imageUrl != null && !imageUrl.equals("")) {
            Glide.with(getContext())
                    .load(imageUrl)
                    .error(R.drawable.vodafone_voucher_mall_placeholder)
                    .into(imageView);
        } else {
            Glide.with(getContext())
                    .load(R.drawable.vodafone_voucher_mall_placeholder)
                    .error(R.drawable.vodafone_voucher_mall_placeholder)
                    .into(imageView);
        }
        navigationHeader.removeViewFromContainer();
        navigationHeader.addViewToContainer(v);
        navigationHeader.showBannerView();
        navigationHeader.showTriangleView();
        setBackgroundViewColor(R.color.transparent);
    }

    private void loadVoucherLayout(boolean isReserved, boolean isFromBanners){

        LoyaltyVoucherHeaderCard loyaltyVoucherHeaderCard = setVoucherHeaderCard(isReserved);

        LoyaltyVoucherDescriptionCard loyaltyVoucherDescriptionCard = setVoucherDescriptionCard();

        ServiceCard voucherCampaignCard = setCampaignCard();

        if(!isReserved) {
            reserveCard = setReserveButtonCard();
        }

        if(isFromBanners && isReserved) {
            String alreadyReservedMessage = LoyaltyLabels.getLoyaltyVoucherAlreadyReserved();
            new CustomToast.Builder(getContext()).message(alreadyReservedMessage).success(true).show();
        }

        try {
            if (getActivity() != null && viewGroup!=null) {
                viewGroup.removeAllViews();
                viewGroup.addView(loyaltyVoucherHeaderCard);
                viewGroup.addView(loyaltyVoucherDescriptionCard);
                viewGroup.addView(voucherCampaignCard);
                if (!isReserved) {
                    viewGroup.addView(reserveCard);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        stopLoadingDialog();
    }

    private LoyaltyVoucherHeaderCard setVoucherHeaderCard(boolean isReserved){
        LoyaltyVoucherHeaderCard loyaltyVoucherHeaderCard = new LoyaltyVoucherHeaderCard(getContext());
        if(promotionVoucher.getDiscountValue() != null) {
            if(promotionVoucher.getDiscountValue().contains("+")) {
                String discountValueText = promotionVoucher.getDiscountValue() + " " + LoyaltyLabels.getLoyaltyVoucherFreeLabel();
                loyaltyVoucherHeaderCard.setDiscountValue(discountValueText);
            } else
            if(promotionVoucher.getUnitMeasure() != null) {
                String discountValueText = promotionVoucher.getDiscountValue() + " " + promotionVoucher.getUnitMeasure() + " " + LoyaltyLabels.getLoyaltyVoucherDiscountLabel();
                loyaltyVoucherHeaderCard.setDiscountValue(discountValueText);
            } else {
                loyaltyVoucherHeaderCard.hideDiscountValue();
            }
        } else {
            loyaltyVoucherHeaderCard.hideDiscountValue();
        }

        if(promotionVoucher.getPartnerName() != null) {
            String discountLabelText = LoyaltyLabels.getLoyaltyOfferAvailable() + " " + promotionVoucher.getPartnerName();
            loyaltyVoucherHeaderCard.setDiscountLabel(discountLabelText);
        }
        else {
            loyaltyVoucherHeaderCard.hideDiscountLabel();
        }

        if(!isReserved) {
            if (promotionVoucher.getCampaignExpiryDate() != null) {
                String campaignExpiryDateParsed = TextUtils.fromMillisToDate(promotionVoucher.getCampaignExpiryDate(), "dd.MM.yyyy");
                if (campaignExpiryDateParsed != null) {
                    String label = LoyaltyLabels.getLoyaltyVoucherOfferAvailabilityLabel() + campaignExpiryDateParsed;
                    SpannableString voucherValidityText = new SpannableString(label);
                    voucherValidityText.setSpan(new StyleSpan(Typeface.BOLD),
                            LoyaltyLabels.getLoyaltyVoucherOfferAvailabilityLabel().length(),
                            label.length(),
                            0);
                    loyaltyVoucherHeaderCard.setVoucherValidity(voucherValidityText);
                } else {
                    loyaltyVoucherHeaderCard.hideVoucherValidity();
                }
            } else {
                loyaltyVoucherHeaderCard.hideVoucherValidity();
            }
        } else {
            if(promotionVoucher.getVoucherCode() != null) {
                String voucherLabelText = LoyaltyLabels.getLoyaltyVoucherCodeLabel() + " " + promotionVoucher.getVoucherCode();
                SpannableString voucherValidityText = new SpannableString(voucherLabelText);
                voucherValidityText.setSpan(new StyleSpan(Typeface.BOLD),
                        0,
                        voucherLabelText.length(),
                        0);
                loyaltyVoucherHeaderCard.setVoucherValidityTextSize(17);
                loyaltyVoucherHeaderCard.setVoucherValidity(voucherValidityText);
            }
            else {
                loyaltyVoucherHeaderCard.hideDiscountLabel();
            }
        }

        if(promotionVoucher.getVoucherExpiryDate() != null && promotionVoucher.getVoucherExpiryDate() != Long.MAX_VALUE) {
            String voucherExpiryDateParsed = TextUtils.fromMillisToDate(promotionVoucher.getVoucherExpiryDate(), "dd.MM.yyyy");
            if(voucherExpiryDateParsed != null){
                String label = LoyaltyLabels.getLoyaltyVoucherAvailabilityLabel() + voucherExpiryDateParsed;
                SpannableString voucherExpirationText = new SpannableString(label);
                voucherExpirationText.setSpan(new StyleSpan(Typeface.BOLD),
                        LoyaltyLabels.getLoyaltyVoucherAvailabilityLabel().length(),
                        label.length(),
                        0);
                loyaltyVoucherHeaderCard.setVoucherExpiration(voucherExpirationText);
            }
            else {
                loyaltyVoucherHeaderCard.hideVoucherExpiration();
            }
        }
        else {
            loyaltyVoucherHeaderCard.hideVoucherExpiration();
        }
        return loyaltyVoucherHeaderCard;
    }

    private LoyaltyVoucherDescriptionCard setVoucherDescriptionCard(){
        LoyaltyVoucherDescriptionCard loyaltyVoucherDescriptionCard = new LoyaltyVoucherDescriptionCard(getContext());
        String voucherDescriptionText = promotionVoucher.getShortDescription();
        loyaltyVoucherDescriptionCard.setVoucherDescription(voucherDescriptionText);
        String voucherDescrTitle = LoyaltyLabels.getLoyaltyCampaignDescription();
        loyaltyVoucherDescriptionCard.setCampaignTitle(voucherDescrTitle);

        if(promotionVoucher.getPartnerHyperlink() != null) {
            loyaltyVoucherDescriptionCard.setVoucherHyperlink(promotionVoucher.getPartnerHyperlink(), voucherId);
        }
        else {
            loyaltyVoucherDescriptionCard.hideVoucherHyperlink();
        }
        return loyaltyVoucherDescriptionCard;
    }

    private ServiceCard setCampaignCard(){
        ServiceCard voucherCampaignCard = new ServiceCard(getContext());
        voucherCampaignCard.setSingleTitleText(LoyaltyLabels.getMarketCampaignTitle(),ScreenMeasure.dpToPx(80))
                .setCardMargins((int) getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal), (int) getResources().getDimensionPixelSize(R.dimen.login_page_vertical_view_margin), (int) getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal), (int) getResources().getDimensionPixelSize(R.dimen.login_page_vertical_view_margin));

        final String url;
        if(promotionVoucher.getTermsAndConditions() != null){
            if(promotionVoucher.getTermsAndConditions().contains(".pdf")){
                url = "http://docs.google.com/gview?embedded=true&url=" + promotionVoucher.getTermsAndConditions();
            }
            else {
                url = promotionVoucher.getTermsAndConditions();
            }
        }
        else {
            url = LoyaltyLabels.getCampaignDetailsActivity();
        }
        voucherCampaignCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackEvent(TealiumConstants.loyalty_vouchers_campaign);
                if(url !=null){
                    IntentActionName.WEBVIEW.setOneUsageSerializedData(url);
                    new NavigationAction(getContext(), IntentActionName.WEBVIEW).startAction();
                }
            }
        });
        return voucherCampaignCard;
    }

    private GeneralCardsWithTitleBodyAndTwoButtons setReserveButtonCard(){
        reserveCard = new GeneralCardsWithTitleBodyAndTwoButtons(getContext());
        reserveCard
                .setPrimaryButtonClickListener(reserveVoucherListener)
                .setPrimaryButtonMessage(LoyaltyLabels.getLoyaltyVoucherReserveLabel());

        reserveCard.removePaddings();
        reserveCard.build();
        reserveCard.setCardViewBackground(R.color.blackNormal);

        ViewCompat.setElevation(reserveCard, 0);
        reserveCard.setBackgroundResource(R.color.blackNormal);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = ScreenMeasure.dpToPx(8);
        layoutParams.setMarginStart(0);
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        layoutParams.setMarginEnd(0);
        reserveCard.setCardMarginsInDp(12, 0, 12, 0);
        reserveCard.setCardPaddingsInDp(0, 0, 0 , 0);
        reserveCard.setPaddings(0, 0, 0, 0);
        reserveCard.setLayoutParams(layoutParams);
        return reserveCard;
    }

    private void trackEvent(String event){
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.loyalty_vouchers_details);
        tealiumMapEvent.put(TealiumConstants.event_name, event);
        if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());

        TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);
    }
    private View.OnClickListener reserveVoucherListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            trackEvent(TealiumConstants.reserve_voucher);
            showLoadingDialog();
            if(getActivity() != null) {
                reserveCard.getPrimaryButton().setEnabled(false);
            }

            long campaignExpireDate = promotionVoucher.getCampaignExpiryDate();

            LoyaltySegmentSuccess loyaltySegmentSuccess = (LoyaltySegmentSuccess) RealmManager.getRealmObject(realm,
                    LoyaltySegmentSuccess.class);

            String treatmentSegment = "";
            if(VodafoneController.getInstance().getUser() instanceof EbuMigrated) {
                treatmentSegment = EbuMigratedIdentityController.getInstance().getSelectedIdentity().getTreatmentSegment();
                Log.d(TAG, "treatmentSegment: " + treatmentSegment);
            }

            if(promotionVoucher.getVoucherName() == null) {
                new CustomToast.Builder(getContext()).message("Sistem momentan indisponibil.").success(false).show();
                stopLoadingDialog();
                return;
            }

            ReserveVoucherRequest reserveVoucherRequest = new ReserveVoucherRequest(promotionVoucher.getVoucherName(),
                    loyaltySegmentSuccess.getLpsSegment(), campaignExpireDate, treatmentSegment);

            loyaltyMarketService.postReservedVoucher(reserveVoucherRequest)
                .subscribe(new RequestSessionObserver<GeneralResponse<LoyaltyVoucherReserveSuccess>>() {

                    @Override
                    public void onNext(GeneralResponse<LoyaltyVoucherReserveSuccess> generalResponse) {

                        stopLoadingDialog();

                        if (generalResponse.getTransactionStatus() == 0 && generalResponse.getTransactionSuccess().getVoucherCode() != null) {
                            LoyaltyMarketVoucherListingsFragment.setLastTabSelectedToMyOffer();
                            String reservedVoucherCode = generalResponse.getTransactionSuccess().getVoucherCode();
                            refreshWithReservedVoucher(true, reservedVoucherCode);
                            if(getActivity() != null){
                                loyaltyVoucherCommunicationListener.setRefreshVoucherDetails(false);
                                loyaltyVoucherCommunicationListener.reloadVouchers();
                            }

                        } else if(generalResponse.getTransactionStatus() == 2 && generalResponse.getTransactionFault().getFaultCode().equals("EC06901")){
                            refreshWithReservedVoucher(false, null);
                            new CustomToast.Builder(getContext()).message(LoyaltyLabels.getLoyaltyVoucherAlreadyReserved()).success(true).show();

                        } else if(generalResponse.getTransactionStatus() == 2 && generalResponse.getTransactionFault().getFaultCode().equals("EC06902")) {

                            new CustomToast.Builder(getContext()).message(LoyaltyLabels.getLoyaltyVoucherNoStock()).success(false).show();
                            if(getActivity() != null) {
                                reserveCard.getPrimaryButton().setEnabled(false);
                            }

                        } else if(generalResponse.getTransactionStatus() == 2){

                            if(getActivity() != null) {
                                reserveCard.getPrimaryButton().setEnabled(true);
                            }
                            new CustomToast.Builder(getContext()).message("Sistem momentan indisponibil.").success(false).show();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        TealiumHelper.tealiumTrackView(getClass().getSimpleName(), TealiumConstants.loyalty,TealiumConstants.loyalty_vouchers_details);

                    }

                    @Override
                    public void onError(Throwable e) {
                        TealiumHelper.tealiumTrackView(getClass().getSimpleName(), TealiumConstants.loyalty,TealiumConstants.loyalty_vouchers_details);

                        stopLoadingDialog();
                        if(getActivity() != null) {
                            reserveCard.getPrimaryButton().setEnabled(true);
                        }
                        new CustomToast.Builder(getContext()).message("Sistem momentan indisponibil.").success(false).show();
                        super.onError(e);
                    }
                });
        }
    };

    private void refreshWithReservedVoucher(boolean isReservedSuccessful, String reservedVoucherCode) {
        viewGroup.removeAllViews();


        RealmManager.startTransaction(realm);
        promotionVoucher.setVoucherCode(reservedVoucherCode);
        RealmManager.update(realm, promotionVoucher);

        loadVoucherLayout(true, false);

        if(isReservedSuccessful && promotionVoucher != null) {
            String successfulToastText = LoyaltyLabels.getLoyaltyVoucherReservedCodeLabel() + " " + reservedVoucherCode + ".";
            new CustomToast.Builder(getContext()).message(successfulToastText).success(true).show();
        } else {
            if(getActivity() != null){
                loyaltyVoucherCommunicationListener.setRefreshVoucherDetails(false);
                loyaltyVoucherCommunicationListener.reloadVouchers();
            }
        }
        stopLoadingDialog();
    }

    private void setBackgroundViewColor(final int color) {
        if (getActivity() != null && navigationHeader != null) {
            ViewTreeObserver vto = navigationHeader.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (navigationHeader.getViewTreeObserver().isAlive()) {
                        // only need to calculate once, so remove listener
                        navigationHeader.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    if(getActivity()==null){
                        return;
                    }
                    if(getActivity() instanceof LoyaltyMarketActivity){
                        boolean present =((LoyaltyMarketActivity)getActivity()).isVoucherCurrentPresent(LoyaltyVoucherDetailsFragment.this);
                        if(!present){
                            return;
                        }
                    }
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (ViewUtils.getWindowHeight() - navigationHeader.getHeight()));
                    lp.height = (ViewUtils.getWindowHeight() - navigationHeader.getHeight());
                    lp.gravity = Gravity.BOTTOM;
                    LinearLayout backgroundView = (LinearLayout) getActivity().findViewById(R.id.background_view);
                    backgroundView.setGravity(Gravity.BOTTOM);
                    backgroundView.setLayoutParams(lp);
                    backgroundView.setBackgroundColor(ContextCompat.getColor(getActivity(), color));
                    D.w();
                }
            });
        }
    }

    private void triggerNonTelcoQuestionnaire(){
        rx.Observable.timer(AppConfiguration.getNonTelcoQuestionnaireTime(), TimeUnit.SECONDS)
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        //add Qualtrics survey
                        TealiumHelper.addQualtricsCommand();
                        //track
                    }
                });
    }

    private String getVoucherId() {
        return voucherId;
    }


    public static class VodafoneMarketOfferTrackingEvent extends TrackingEvent {

    	private String voucherId;

    	public VodafoneMarketOfferTrackingEvent(String voucherId)
		{
			this.voucherId = voucherId;
		}

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "loyalty offer:" + voucherId;
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "loyalty offer:" + voucherId);


            s.channel = "loyalty";
            s.getContextData().put("&&channel", s.channel);
            s.events = "event3";
            s.getContextData().put("event3", s.event3);
            s.events = "event5";
            s.getContextData().put("event5", s.event5);
            s.events = "event6";
            s.getContextData().put("event6", s.event6);
            s.events = "event8";
            s.getContextData().put("event8", s.event8);

            s.prop5 = "content";
            s.getContextData().put("prop5", s.prop5);

            s.eVar5 = "content";
            s.getContextData().put("eVar5", s.eVar5);
            s.eVar73 = "loyalty";
            s.getContextData().put("eVar73", s.eVar73);
        }
    }
}

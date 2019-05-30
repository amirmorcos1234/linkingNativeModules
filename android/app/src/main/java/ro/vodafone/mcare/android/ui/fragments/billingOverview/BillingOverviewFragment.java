package ro.vodafone.mcare.android.ui.fragments.billingOverview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.BaseCardControllerInterface;
import ro.vodafone.mcare.android.card.billingGraphSection.BillingGraphCardController;
import ro.vodafone.mcare.android.client.model.billing.InvoiceDetailsSuccess;
import ro.vodafone.mcare.android.client.model.identity.EntityChildItem;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.billHistory.BillHistorySuccess;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.BillingOverviewLabels;
import ro.vodafone.mcare.android.client.model.realm.system.YourProfileLabels;
import ro.vodafone.mcare.android.interfaces.fragment.base.OnScrollViewCreatedListener;
import ro.vodafone.mcare.android.rest.observers.RequestSaveRealmObserver;
import ro.vodafone.mcare.android.service.BillingServices;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.BillingOverviewActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.fragments.settings.SettingsCardArrow;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.ViewUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.EbuMigratedIdentityController;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Bivol Pavel on 05.04.2017.
 */
public class BillingOverviewFragment extends BaseFragment {

    @BindView(R.id.navigation_header)
    NavigationHeader navigationHeader;

    @BindView(R.id.parent_viewgroup)
    LinearLayout parentViewGroup;

    @BindView(R.id.no_bill_container)
    LinearLayout noBillContainer;

    @BindView(R.id.error_message)
    VodafoneTextView errorMessage;

    @BindView(R.id.payment_agreement)
    SettingsCardArrow paymentAgreement;

    @BindView(R.id.payment_confirmation)
    SettingsCardArrow paymentConfirmation;

    public static String TAG = "BillingOverviewFragment";
    private View view;
    private String ban;

    private List<BaseCardControllerInterface> billHistoryControllerList;
    private boolean isBillHistoryRequested = false;
    private OnScrollViewCreatedListener scrollViewCreatedListener;
    View card;

    public synchronized static BillingOverviewFragment getInstance(FragmentManager fm) {
        return (BillingOverviewFragment) FragmentUtils.getInstance(fm, BillingOverviewFragment.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_billing_overview, container, false);
        ButterKnife.bind(this, view);
        initNavigationFragment();

        setAdobeVariables();

        if (scrollViewCreatedListener != null) {
            scrollViewCreatedListener.onScrollViewCreated((ScrollView) view);
        }
        return view;
    }

    public NavigationHeader getNavigationHeader() {
        return navigationHeader;
    }

    private void initNavigationFragment() {
        navigationHeader.setActivity(getActivity())
                .setTitle(AppLabels.getBilling_overview_page_title())
                .buildBanSelectorHeader()
                .displayDefaultHeader();
    }

    public void requestBillHistoryData(BaseCardControllerInterface controller) {
        if (billHistoryControllerList == null) {
            billHistoryControllerList = new ArrayList<>();
        }
        billHistoryControllerList.add(controller);
        if (!isBillHistoryRequested) {
            getBillHistory();
        }
    }

    private void getBillHistory() {
        isBillHistoryRequested = true;
        BillingServices billingServices = new BillingServices(getContext());

        ban = UserSelectedMsisdnBanController.getInstance().getSelectedNumberBan();
        String ben = UserSelectedMsisdnBanController.getInstance().getSelectedEbuBen();
        String userCid = VodafoneController.getInstance().getUserProfile().getCid();
        EntityChildItem entityChildItem = EbuMigratedIdentityController.getInstance().getSelectedIdentity();
        String vfOdsCid = entityChildItem != null ? entityChildItem.getVfOdsCid() : null;
        String crmtRole = entityChildItem != null ? entityChildItem.getCrmRole() : null;
        billingServices.getBillingHistory(ben, userCid, vfOdsCid, crmtRole, ban, 6).subscribe(new RequestSaveRealmObserver<GeneralResponse<BillHistorySuccess>>() {
            @Override
            public void onNext(GeneralResponse<BillHistorySuccess> billHistorySuccessGeneralResponse) {
                isBillHistoryRequested = false;
                if (billHistorySuccessGeneralResponse.getTransactionSuccess() != null) {
                    super.onNext(billHistorySuccessGeneralResponse);
                    for (BaseCardControllerInterface controller : billHistoryControllerList) {

                        controller.onDataLoaded(RealmManager.getRealmObject(BillHistorySuccess.class));
                    }

                } else {
                    for (BaseCardControllerInterface controller : billHistoryControllerList) {
                        controller.onDataLoaded(billHistorySuccessGeneralResponse.getTransactionFault());
                    }
                }
                billHistoryControllerList.clear();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                isBillHistoryRequested = false;
                for (BaseCardControllerInterface controller : billHistoryControllerList) {
                    controller.onRequestFailed();
                }
                billHistoryControllerList.clear();
            }
        });
    }

    private void updateNavigationHeader() {
        if (getNavigationHeader() != null) {
            if (EbuMigratedIdentityController.isUserVerifiedEbuMigrated()) {
                getNavigationHeader().buildMsisdnSelectorHeader();
            } else {
                getNavigationHeader().showBanSelector();
            }
            getNavigationHeader().setTitle(BillingOverviewLabels.getBilling_overview_page_title());
            getNavigationHeader().removeViewFromContainer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNavigationHeader();
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    void setBackgroundViewColor(final int color) {
        if (getActivity() != null && navigationHeader != null) {
            ViewTreeObserver vto = navigationHeader.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (navigationHeader.getViewTreeObserver().isAlive()) {
                        // only need to calculate once, so remove listener
                        navigationHeader.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    if (getActivity() == null) {
                        return;
                    }
                    if (getActivity() instanceof BillingOverviewActivity) {
                        boolean present = ((BillingOverviewActivity) getActivity()).isFragmentPresent(BillingOverviewFragment.this);
                        if (!present) {
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

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.BILLING_OVERVIEW);
    }

    public static class BillingOverviewTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "billing overview";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "billing overview");

            s.channel = "billing overview";
            s.getContextData().put("&&channel", s.channel);
        }
    }


    public void setNoBillContainer() {

        noBillContainer.setVisibility(View.VISIBLE);
        errorMessage.setText(BillingOverviewLabels.getBilling_overview_no_bill_history_title_message());
    }

    public void hiddenNoBillContainer(){

         noBillContainer.setVisibility(View.GONE);
    }


    public void setAdobeVariables (){

        Log.d(TAG, "onCreateView");
        //Tealium Track View

        TealiumHelper.tealiumTrackView(getClass().getSimpleName(),TealiumConstants.billingOverview,TealiumConstants.billingOverview);

        BillingOverviewTrackingEvent event = new BillingOverviewTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);

    }

    public void setCardProfile() {

        noBillContainer.setVisibility(View.GONE);

            paymentAgreement.setAttributes(YourProfileLabels.getPayBillDelayCardTitle(), YourProfileLabels.getPayBillDelayCardSubText());
            paymentAgreement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trackEvent(TealiumConstants.billingOverview_payment_agreement);
                    try {
                        ((BillingOverviewActivity) getContext()).atachFragment(FragmentUtils.newInstance(PaymentAgreementFragment.class));
                    } catch (Exception e) {
                        Log.e(TAG, "getCard: " + e.getMessage());
                    }
                }
            });

            paymentConfirmation.setAttributes(YourProfileLabels.getPayBillConfirmationCardTitle(), YourProfileLabels.getPayBillConfirmationCardSubTitle());
            paymentConfirmation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trackEvent(TealiumConstants.billingOverview_payment_confirmation);
                    try {
                        ((BillingOverviewActivity) getContext()).atachFragment(FragmentUtils.newInstance(PaymentConfirmationFragment.class));
                    } catch (Exception e) {
                        Log.e(TAG, "getCard: " + e.getMessage());
                    }
                }
            });

    }

    private void trackEvent(String event) {
        //Tealium Track Event
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.billingOverview);
        tealiumMapEvent.put(TealiumConstants.event_name, event);
        if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);

    }


}

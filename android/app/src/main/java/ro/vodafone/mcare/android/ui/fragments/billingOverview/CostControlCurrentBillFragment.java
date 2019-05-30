package ro.vodafone.mcare.android.ui.fragments.billingOverview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.adapters.CurrentBillRVAdapter;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.Subscriber;
import ro.vodafone.mcare.android.client.model.realm.hierarchy.UserProfileHierarchy;
import ro.vodafone.mcare.android.client.model.realm.system.BillingOverviewLabels;
import ro.vodafone.mcare.android.interfaces.factory.InterfaceNavigationLayoutFactory;
import ro.vodafone.mcare.android.interfaces.fragment.base.OnRecycleScrollViewCreatedListener;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.BillingOverviewActivity;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.ViewUtils;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by Bivol Pavel on 13.04.2017.
 */

public class CostControlCurrentBillFragment extends BaseFragment implements InterfaceNavigationLayoutFactory {

    public static String TAG = "CCCurrentBillFragment";


    private RelativeLayout parentViewGroup;

    private RecyclerView recyclerView;

    private LinearLayoutManager mLayoutManager;

    private CurrentBillRVAdapter mAdapter;

    NavigationHeader navigationHeader;

    OnRecycleScrollViewCreatedListener scrollViewCreatedListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentViewGroup = (RelativeLayout) inflater.inflate(R.layout.fragment_cost_control_current_bill, container, false);
        Log.d(TAG, "onCreateView");
        initializeNavigationHeader();
        initRecyclerView();
        setupTelium();
        setBackgroundViewColor(R.color.general_background_light_gray);

        return parentViewGroup;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            scrollViewCreatedListener = (OnRecycleScrollViewCreatedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentLifeCycleListener");
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (scrollViewCreatedListener != null) {
            scrollViewCreatedListener.onRecycleScrollViewCreated(recyclerView);
        }
    }

    private void initRecyclerView() {

        recyclerView = parentViewGroup.findViewById(R.id.bill_recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        List<String> msisdnList = new ArrayList<>();

        UserProfileHierarchy userProfileHierarchy = (UserProfileHierarchy) RealmManager.getRealmObject(UserProfileHierarchy.class);

        if (userProfileHierarchy != null) {
            for (Subscriber subscriber : userProfileHierarchy.getSubscriberList()) {
                msisdnList.add(subscriber.getMsisdn());
            }
        } else {
            msisdnList.add(VodafoneController.getInstance().getUserProfile().getMsisdn());
        }

        mAdapter = new CurrentBillRVAdapter(this, getContext(), msisdnList);
        recyclerView.setAdapter(mAdapter);
        stopLoadingDialog();
    }

    @Override
    public NavigationHeader initializeNavigationHeader() {
        NavigationHeader navigationHeader = new NavigationHeader(getContext());
        navigationHeader.setId(R.id.navigation_header);
        navigationHeader.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        navigationHeader.setTitle(BillingOverviewLabels.getBilling_overview_cost_control_card_title());
        navigationHeader.hideSelectorView();
        navigationHeader.showTriangleView();
        this.navigationHeader = navigationHeader;
        return navigationHeader;
    }

    @Override
    public NavigationHeader getNavigationHeader() {
        return navigationHeader == null ? initializeNavigationHeader() : navigationHeader;
    }

    public static class CostControlCurrentBillTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "cost control";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "cost control");


            s.channel = "billing overview";
            s.getContextData().put("&&channel", s.channel);
            s.eVar18 = "current bill overview";
            s.getContextData().put("eVar18", s.eVar18);
            s.eVar19 = "query";
            s.getContextData().put("eVar19", s.eVar19);
            s.prop21 = "mcare:" + "cost control";
            s.getContextData().put("prop21", s.prop21);
        }
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
                        boolean present = ((BillingOverviewActivity) getActivity()).isFragmentPresent(CostControlCurrentBillFragment.this);
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
                }
            });
        }
    }

    private void setupTelium() {
        //Tealium Track View
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put("screen_name", "cost control");
        tealiumMapView.put("journey_name", "billing overview");
        tealiumMapView.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapView);

        CostControlCurrentBillTrackingEvent event = new CostControlCurrentBillTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        journey.event7 = "event7";
        journey.getContextData().put("event7", journey.event7);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    @Override
    public void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.COST_CONTROL);
    }
}

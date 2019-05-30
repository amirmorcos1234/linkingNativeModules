package ro.vodafone.mcare.android.ui.fragments.billingOverview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.system.BillingOverviewLabels;
import ro.vodafone.mcare.android.interfaces.fragment.base.OnRecycleScrollViewCreatedListener;
import ro.vodafone.mcare.android.interfaces.fragment.base.OnScrollViewCreatedListener;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.views.CardErrorLayout;

/**
 * Created by Bivol Pawel on 20.04.2017.
 */

public class InstructionalMessageFragment extends BaseFragment {



    @BindView(R.id.navigation_header)
    NavigationHeader navigationHeader;

    private static final String MESSAGE_PARAMETER_KEY = "message";

    private View view;

    private String message;

    private LinearLayout mContainer;
    private OnScrollViewCreatedListener scrollViewCreatedListener;

    public InstructionalMessageFragment(){
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
    public static InstructionalMessageFragment newInstance(String message) {
        InstructionalMessageFragment fragment = new InstructionalMessageFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_PARAMETER_KEY, message);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_instructional_message, container, false);
        ButterKnife.bind(this,view);
        this.message = getArguments().getString(MESSAGE_PARAMETER_KEY);

        mContainer = (LinearLayout) view.findViewById(R.id.container);
        setNavigationHeader();
        atachErrorContainer();
        scrollViewCreatedListener.onScrollViewCreated(null);

        return view;
    }

    private void atachErrorContainer(){
        CardErrorLayout billSummaryErrorView = new CardErrorLayout(getContext());
        billSummaryErrorView.setText(message);
        mContainer.addView(billSummaryErrorView);
    }

    public void setNavigationHeader() {
        navigationHeader.setTitle(BillingOverviewLabels.getBillingOverviewUnpaidTitle());
        navigationHeader.hideSelectorView();
    }

}

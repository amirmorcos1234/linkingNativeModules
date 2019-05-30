package ro.vodafone.mcare.android.ui.fragments.yourProfile.UserRequests;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.realm.system.LoginLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SeamlessLabels;
import ro.vodafone.mcare.android.client.model.realm.system.UserRequestsLabels;
import ro.vodafone.mcare.android.client.model.userRequest.UserRequest;
import ro.vodafone.mcare.android.custom.CustomRecyclerView;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.ui.activities.yourProfile.YourProfileActivity;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.YourProfileBaseFragment;
import ro.vodafone.mcare.android.ui.utils.CustomSnackBar;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.listeners.adapter.item_click.TabAdapterOnItemClickListener;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.widget.TabMenu.TabAdapter;
import ro.vodafone.mcare.android.widget.TabMenu.TabCard;
import ro.vodafone.mcare.android.widget.dialogs.VodafoneFullscreenDialog;

public class UserRequestsFragment extends YourProfileBaseFragment implements UserRequestContract.View
{
    private static final String TAG = UserRequestsFragment.class.getCanonicalName();

    private UserRequestContract.Presenter presenter;

    private AtomicInteger previousTabIndex = new AtomicInteger(0);
    @BindView(R.id.tab_container)
    protected TabCard tabCardView;

    @BindView(R.id.viewError)
    protected ViewGroup viewError;

    @BindView(R.id.viewRequests)
    protected View viewRequests;

    @BindView(R.id.rvPending)
    protected CustomRecyclerView rvPending;
    private PendingRequestsAdapter pendingAdapter;

    @BindView(R.id.rvAccepted)
    protected CustomRecyclerView rvAccepted;

    @BindView(R.id.rvRejected)
    protected CustomRecyclerView rvRejected;

    @BindView(R.id.viewEmptyMessage)
    protected View viewEmptyMessage;

    @BindView(R.id.lblMessage)
    protected TextView lblMessage;

    @BindView(R.id.viewBottomButtons)
    protected View viewBottomButtons;

    //used to determine click after view created
    private int tabcardClick = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (activityInterface != null)
            activityInterface.getNavigationHeader().hideSelectorView();

        UserRequestsTrackingEvent event = new UserRequestsTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        basePresenter = presenter = new UserRequestPresenter(this);

        View v = inflater.inflate(R.layout.fragment_user_requests, container, false);
        unbinder = ButterKnife.bind(this, v);

        TabAdapter adapter = new TabAdapter(getActivity(),
                new ArrayList<>(Arrays.asList(UserRequestsLabels.getUserRequestsPendingRequests(), UserRequestsLabels.getUserRequestsAcceptedRequests(), UserRequestsLabels.getUserRequestsDeniedRequests())),
                true);
        tabCardView.setAdapter(adapter);
        tabCardView.bringToFront();

        ConstraintLayout.LayoutParams lp = ((ConstraintLayout.LayoutParams) viewRequests.getLayoutParams());            // start with bottom buttons view hidden
        ((ConstraintLayout.LayoutParams) viewRequests.getLayoutParams()).bottomMargin = ScreenMeasure.dpToPx(16);
        viewRequests.setLayoutParams(lp);

        //  change tab logic

        tabCardView.setOnItemClickListener(new TabAdapterOnItemClickListener(tabCardView){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                super.onItemClick(parent, view, position, id);
                // don't animate if the user clicks on the same or similar (Accepted and Rejected) tabs
                if (!(previousTabIndex.get() == position || (position == 1 && previousTabIndex.get() == 2) || (position == 2 && previousTabIndex.get() == 1)))
                {
                    if (rvPending.getAdapter().getItemCount() > 0)
                    {
                        viewBottomButtons.setVisibility(View.VISIBLE);

                        ObjectAnimator animator = ObjectAnimator.ofFloat(viewBottomButtons, "translationY", position != 0 ? ScreenMeasure.dpToPx(44) : ScreenMeasure.dpToPx(0));
                        animator.setDuration(200);
                        animator.setInterpolator(new DecelerateInterpolator());
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator)
                            {
                                ConstraintLayout.LayoutParams lp = ((ConstraintLayout.LayoutParams) viewRequests.getLayoutParams());
                                ((ConstraintLayout.LayoutParams) viewRequests.getLayoutParams()).bottomMargin = (int)
                                        (position != 0
                                                ? ScreenMeasure.dpToPx(16) + (ScreenMeasure.dpToPx(60) * (1 - valueAnimator.getAnimatedFraction()))
                                                : (ScreenMeasure.dpToPx(60) * valueAnimator.getAnimatedFraction()));
                                viewRequests.setLayoutParams(lp);
                            }
                        });
                        animator.start();

                        previousTabIndex.set(position);
                    }
                    else if (rvPending.getAdapter().getItemCount() == 0)
                    {
                        ConstraintLayout.LayoutParams lp = ((ConstraintLayout.LayoutParams) viewRequests.getLayoutParams());
                        ((ConstraintLayout.LayoutParams) viewRequests.getLayoutParams()).bottomMargin = ScreenMeasure.dpToPx(16);
                        viewRequests.setLayoutParams(lp);

                        viewBottomButtons.setVisibility(View.GONE);
                    }
                }

                rvPending.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                rvAccepted.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
                rvRejected.setVisibility(position == 2 ? View.VISIBLE : View.GONE);

                lblMessage.setText(UserRequest.Status.values()[position].getEmptyMessageLabel());

                if (tabCardView.getSelectedTabIndex() == UserRequest.Status.PENDING.getValue()) {
                    viewEmptyMessage.setVisibility(rvPending.getAdapter().getItemCount() == 0 ? View.VISIBLE : View.GONE);

                    TealiumHelper.tealiumTrackEvent(UserRequestsFragment.class.getSimpleName(),
                            UserRequestsLabels.getUserRequestsPendingRequests(), TealiumConstants.userRequestsScreenName, "button=");

                    TealiumHelper.tealiumTrackView(UserRequestsFragment.class.getSimpleName(),
                            TealiumConstants.yourProfileJourney, TealiumConstants.pendingUserRequestsScreenName);
                } else if (tabCardView.getSelectedTabIndex() == UserRequest.Status.ACCEPTED.getValue()) {
                    viewEmptyMessage.setVisibility(rvAccepted.getAdapter().getItemCount() == 0 ? View.VISIBLE : View.GONE);

                    TealiumHelper.tealiumTrackEvent(UserRequestsFragment.class.getSimpleName(),
                            UserRequestsLabels.getUserRequestsAcceptedRequests(), TealiumConstants.userRequestsScreenName, "button=");

                    TealiumHelper.tealiumTrackView(UserRequestsFragment.class.getSimpleName(),
                            TealiumConstants.yourProfileJourney, TealiumConstants.acceptedUserRequestsScreenName);
                } else if (tabCardView.getSelectedTabIndex() == UserRequest.Status.REJECTED.getValue()) {
                    viewEmptyMessage.setVisibility(rvRejected.getAdapter().getItemCount() == 0 ? View.VISIBLE : View.GONE);

                    TealiumHelper.tealiumTrackEvent(UserRequestsFragment.class.getSimpleName(),
                            UserRequestsLabels.getUserRequestsDeniedRequests(), TealiumConstants.userRequestsScreenName, "button=");

                    TealiumHelper.tealiumTrackView(UserRequestsFragment.class.getSimpleName(),
                            TealiumConstants.yourProfileJourney, TealiumConstants.refusedUserRequestsScreenName);
                }

                tabcardClick ++;
                if(tabcardClick > tabCardView.getChildCount())
                	setupTabsTrackingEvent(position);
            }
        });         //  /change tab logic


        viewBottomButtons.setVisibility(View.GONE);
        rvAccepted.setVisibility(View.GONE);
        rvRejected.setVisibility(View.GONE);

        // recycler views
        rvPending.setLayoutManager(new LinearLayoutManager(getActivity()));
        pendingAdapter = new PendingRequestsAdapter(presenter.getListPending());
        rvPending.setAdapter(pendingAdapter);

        rvPending.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l)
            {
                pendingAdapter.setIndexSelected(index);
                pendingAdapter.notifyDataSetChanged();
            }
        });

        rvAccepted.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvAccepted.setAdapter(new PendingRequestsAdapter(presenter.getListAccepted()));

        rvRejected.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvRejected.setAdapter(new PendingRequestsAdapter(presenter.getListRejected()));

        tabCardView.setPosition(0);

        viewEmptyMessage.setVisibility(View.GONE);

        // empty view
        ((ImageView) v.findViewById(R.id.imgIcon)).setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getContext(), R.color.green_check_mark_color), PorterDuff.Mode.SRC_IN));

        {       // bottom view
            ColorFilter filterReject = new PorterDuffColorFilter(ContextCompat.getColor(getContext(), R.color.red_button_color), PorterDuff.Mode.SRC_IN);
            ((ImageView) v.findViewById(R.id.imgReject)).setColorFilter(filterReject);

            ColorFilter filterAccept = new PorterDuffColorFilter(ContextCompat.getColor(getContext(), R.color.green_stroke), PorterDuff.Mode.SRC_IN);
            ((ImageView) v.findViewById(R.id.imgAccept)).setColorFilter(filterAccept);

            ((TextView) v.findViewById(R.id.lblAccept)).setText(AppLabels.getAcceptLabel());
            ((TextView) v.findViewById(R.id.lblReject)).setText(AppLabels.getRejectLabel());
        }

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        requestData();
        TealiumHelper.tealiumTrackView(UserRequestsFragment.class.getSimpleName(),
                TealiumConstants.yourProfileJourney,TealiumConstants.userRequestsScreenName);

    }

    private void requestData()
    {
        showLoadingView();
        presenter.getData(-1);
    }

    @Override
    public String getTitle()
    {
        return UserRequestsLabels.getUserRequestsCardTitle();
    }

    @Override
    public void showErrorMessage(String message, int drawableId, boolean allowRetry)
    {
        hideLoadingView();

        viewRequests.setVisibility(View.GONE);
        viewBottomButtons.setVisibility(View.GONE);

        viewError.setVisibility(View.VISIBLE);
        viewError.removeAllViews();

        if (viewError.findViewById(R.id.card_error_container) != null)
            viewError.removeView(viewError.findViewById(R.id.card_error_container));

        View card_error_container = LayoutInflater.from(getActivity()).inflate(R.layout.card_error_layout, viewError, true);
        TextView card_error_message = card_error_container.findViewById(R.id.card_error_message);
        ImageView image = card_error_container.findViewById(R.id.image);

        card_error_message.setText(message);
        image.setImageResource(drawableId);

        image.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getContext(), R.color.widget_warning_icon_color), PorterDuff.Mode.SRC_IN));

        if (allowRetry)
            card_error_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestData();
                }
            });
    }

    @Override
    public void notifyDataChanged(int tabIndexToSwitchTo)
    {
        previousTabIndex.set(-1);

        viewRequests.setVisibility(View.VISIBLE);
        hideLoadingView();

        rvPending.getAdapter().notifyDataSetChanged();
        rvAccepted.getAdapter().notifyDataSetChanged();
        rvRejected.getAdapter().notifyDataSetChanged();
        pendingAdapter.setIndexSelected(0);

        tabCardView.setPosition(UserRequest.Status.PENDING.getValue());

//        if (tabIndexToSwitchTo == -1)
            tabCardView.setPosition(UserRequest.Status.PENDING.getValue());
//        else
//            tabCardView.setPosition(tabIndexToSwitchTo);
    }

    @Override
    public void showToast(String message, boolean isSuccess, boolean isError)
    {
        hideLoadingView();

        new CustomToast.Builder(getActivity()).message(message).success(isSuccess).errorIcon(isError).show();
    }

    private void showLoadingView()
    {
        // show the loading view
//        ViewGroup contentView = (ViewGroup) getActivity().getWindow().getDecorView().getRootView();     //getActivity().findViewById(android.R.id.content);
//        View loadingView = LayoutInflater.from(getActivity()).inflate(R.layout.component_loading, contentView, false);
//        contentView.addView(loadingView);

        showLoadingDialog();
    }

    private void hideLoadingView()
    {
        stopLoadingDialog();
    }

    @OnClick(R.id.btnReject)
    public void rejectRequest()
    {
        promptProcessRequestDialog(R.id.btnReject);
    }

    @OnClick(R.id.btnAccept)
    protected void acceptRequest()
    {
        promptProcessRequestDialog(R.id.btnAccept);
    }

    private void promptProcessRequestDialog(final int buttonPressedId)
    {
        new VodafoneFullscreenDialog.Builder(getActivity())
                .setTitle(buttonPressedId == R.id.btnReject ? UserRequestsLabels.getRejectRequestTitle() : UserRequestsLabels.getApproveRequestTitle())
                .setMessage(buttonPressedId == R.id.btnReject ? UserRequestsLabels.getRejectRequestDescription() : UserRequestsLabels.getApproveRequestDescription())
                .setPositiveButton(LoginLabels.getYesLabel(), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which)
                    {
                        showLoadingView();

                        UserRequest.Status newStatus = buttonPressedId == R.id.btnReject ? UserRequest.Status.REJECTED : UserRequest.Status.ACCEPTED;
                        presenter.processPendingRequest(pendingAdapter.getIndexSelected(), newStatus);
                    }
                })
                .setNegativeButton(SeamlessLabels.getRevokeButtonLabel(), null)
                .show();
    }

    @Override
    public void notifyRequestProcessed(UserRequest.Status status)
    {
        hideLoadingView();

        CustomSnackBar.make(getActivity(), UserRequestsLabels.getRequestProcessed(), true).show();
        tabCardView.setPosition(status.getValue());

        pendingAdapter.setIndexSelected(pendingAdapter.getIndexSelected() - 1);
        pendingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        hideLoadingView();

        if (activityInterface != null)
            activityInterface.getNavigationHeader().hideSelectorViewWithoutTriangle();
        ((YourProfileActivity) getActivity()).setTitle();
    }

    private void setupTabsTrackingEvent(int position){
        UserRequestsTrackingEvent event = new UserRequestsTrackingEvent();
        TrackingAppMeasurement journey = new TrackingAppMeasurement();
        switch (position){
            case 0:
                journey.event65 = "waiting";
                journey.eVar82 = "mcare:user request:button:waiting";
                break;
            case 1:
                journey.event65 = "accepted";
                journey.eVar82 = "mcare:user request:button:accepted";
                break;
            case 2:
                journey.event65 = "denied";
                journey.eVar82 = "mcare:user request:button:denied";
                break;
        }
        journey.getContextData().put("eVar82", journey.eVar82);
        journey.getContextData().put("event65", journey.event65);
        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }

    public static class UserRequestsTrackingEvent extends TrackingEvent{
        @Override
        protected void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);
            if (getErrorMessage() != null) {
                s.event11 = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "user request";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "user request");
            s.channel = "user request";
            s.getContextData().put("&&channel", s.channel);
            s.prop21 = "mcare:" + "user request";
            s.getContextData().put("prop21", s.prop21);
            s.eVar5 = "content";
            s.getContextData().put("eVar5", s.eVar5);
        }
    }
}

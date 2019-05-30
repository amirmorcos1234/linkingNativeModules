package ro.vodafone.mcare.android.ui.fragments.dashboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.RectEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import ro.vodafone.mcare.android.BuildConfig;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.application.controllers.GdprController;
import ro.vodafone.mcare.android.client.model.costControl.AdditionalCost;
import ro.vodafone.mcare.android.client.model.costControl.AmountTypeIdEnum;
import ro.vodafone.mcare.android.client.model.costControl.Balance;
import ro.vodafone.mcare.android.client.model.costControl.BalanceShowAndNotShown;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.realm.balance.BalanceSecondarySuccess;
import ro.vodafone.mcare.android.client.model.realm.system.AppLabels;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.ui.activities.DashboardActivity;
import ro.vodafone.mcare.android.application.controllers.TravellingAboardController;
import ro.vodafone.mcare.android.ui.fragments.BaseFragment;
import ro.vodafone.mcare.android.ui.fragments.callDetails.Category;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.ListViewAnimationCallback;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.utils.ViewUtils;
import ro.vodafone.mcare.android.ui.views.ExpandableListAdapter;
import ro.vodafone.mcare.android.ui.views.PagingScrollView;
import ro.vodafone.mcare.android.ui.views.nonscrollable.NonScrollExpandableListView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.Logger;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TrackingException;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.ProfileUtils;
import ro.vodafone.mcare.android.widget.VoiceOfVodafoneWidget;
import ro.vodafone.mcare.android.widget.animators.DashboardAnimator;
import ro.vodafone.mcare.android.widget.avatar.DashboardMenuAvatar;
import ro.vodafone.mcare.android.widget.balancecards.BalanceCardDetail;
import ro.vodafone.mcare.android.widget.balancecards.BalanceCardHeader;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Victor Radulescu on 3/1/2017.
 */

public abstract class BaseDashboardFragment extends BaseFragment implements ListViewAnimationCallback, ExpandableListAdapter.OnGroupExpandedListener {

    public static final String EXTRA_PARAMETER_BUNDLE_KEY = "extraParameter";
    public static Logger LOGGER = Logger.getInstance(BaseDashboardFragment.class);
    protected DashboardActivity dashboardActivity;
    protected CompositeSubscription compositeSubscription = new CompositeSubscription();
    /**
     * Animate elements in dashboard
     */
    protected boolean animateElements = true;
    ExpandableListAdapter listAdapter;
    @BindView(R.id.expandableCardList)
    NonScrollExpandableListView expListView;
    List<BalanceCardHeader> listDataHeader;
    HashMap<BalanceCardHeader, List<BalanceCardDetail>> listDataChild;
    @BindView(R.id.dashboard_vov_widget)
    VoiceOfVodafoneWidget vovWidget;
    @BindView(R.id.dashboard_menu_avatar_widget)
    DashboardMenuAvatar avatarWidget;
    DashboardAnimator dashboardAnimator;
    boolean menuAvatarWasNotInited = true;

    protected DashboardCommunicatorListener dashboardCommunicationListener;

    public Realm realm;

    public BaseDashboardFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupBundle();
    }

    protected void setupBundle() {
        if (getArguments() == null) {
            return;
        }
        animateElements = getArguments().getBoolean(DashboardActivity.ANIMATE_KEY,true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dashboardActivity = (DashboardActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_dashboard_base, null);
        ButterKnife.bind(this, view);
        realm = Realm.getDefaultInstance();

        try {
            vovWidget.initialize();

        } catch (Exception ex) {
            LOGGER.e("Exception initVovs.", ex);
        }
        menuAvatarWasNotInited = false;
        avatarWidget.init(getActivity());
        initExpandableCards(view);
        initExtraWidgets((RelativeLayout) view);

        dashboardAnimator = new DashboardAnimator();
        dashboardAnimator.addVoiceOfVodafoneAnimation(vovWidget);
        dashboardAnimator.addServiceSelectorAnimation(avatarWidget);

        addExtraViewsInAnimator(dashboardAnimator);

        //initialize vov widget
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if(realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
        refresh();
    }

    public void refresh() {
//        D.e("menuAvatarWasNotInited=" + menuAvatarWasNotInited);
        refreshVoiceOfVodafoneWidget();
        bundleReceivedFromFragments();
        //if (menuAvatarWasNotInited)
        avatarWidget.refresh();

    }

    private void refreshVoiceOfVodafoneWidget() {
        vovWidget.refreshIfNeeded();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.invalidate();
        endListViewAnimation();
        //animateElements();

        startGroupAnimation();
    }

    //shows message for block/unblock in VoV and Payment Agreement
    //and toast
    public void bundleReceivedFromFragments() {
        String oneUsageValue = IntentActionName.DASHBOARD.getOneUsageSerializedData();
        if(oneUsageValue != null) {
            String[] splitString = oneUsageValue.split("/");
            new CustomToast.Builder(getContext()).message(splitString[1]).success(true).show();
        }

        if (getActivity().getIntent() != null) {
            Log.d("dashboard", "extra intent here");
            if (getActivity().getIntent().getExtras() != null) {
                Log.d("dashboard", "extra here");
                String value = getActivity().getIntent().getExtras().getString(EXTRA_PARAMETER_BUNDLE_KEY);
                if (value != null) {

                    if (value.equals("show_success_block_sim")) {
//                        new CustomToast.Builder(getContext()).message("Cererea se procesează.").success(true).show();
                        getActivity().getIntent().removeExtra(EXTRA_PARAMETER_BUNDLE_KEY);
                    } else if (value.equals("payment_agreement_success")) {
                        new CustomToast.Builder(getContext()).message("Ai amânat plata cu succes.").success(true).show();
                        getActivity().getIntent().removeExtra(EXTRA_PARAMETER_BUNDLE_KEY);
                    } else if (value.equals("getPermissions")) {
                        GdprController.getPermissionsAfterLogin(true);
                        getActivity().getIntent().removeExtra(EXTRA_PARAMETER_BUNDLE_KEY);
                    } else if (value.equals("updateProfile")) {
                        TravellingAboardController.getInstance().refreshProfile(getContext(), getProgressDialog());
                        getActivity().getIntent().removeExtra(EXTRA_PARAMETER_BUNDLE_KEY);
                    }
                }
            }
        }
    }


    protected abstract void initExtraWidgets(RelativeLayout baseContent);

    protected abstract void addExtraViewsInAnimator(DashboardAnimator dashboardAnimator);

    @Override
    public void endListViewAnimation() {
        ((DashboardActivity) getActivity()).fragmentAnimationFinishedListener();
    }


    protected abstract void setCardsLayoutParams(NonScrollExpandableListView expandableListView);

    protected void initExpandableCards(View view) {

        try {
            setCardsLayoutParams(expListView);
            // preparing list data

            prepareListData(null);

            listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild, this);

            // setting list adapter
            expListView.setAdapter(listAdapter);

            expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                // Keep track of previous expanded parent
                int previousGroup = -1;

                @Override
                public void onGroupExpand(int groupPosition) {
                    if (expListView.getChildCount() != 0) {

                        // Collapse previous parent if expanded.
                        if ((previousGroup != -1) && (groupPosition != previousGroup)) {
                            expListView.collapseGroup(previousGroup);
                        }

                        previousGroup = groupPosition;
                        expListView.setSelectedGroup(groupPosition);

                    }
                }

            });

            expListView.setOnGroupClickListener(new NonScrollExpandableListView.OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    LOGGER.d("Parent children count " + parent.getChildCount());
                    if (parent.getChildCount() == 0) {
                        Toast.makeText(getActivity(), "Redirect to Secondary Balance", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            });

        } catch (Exception ex) {
            LOGGER.e("Exception on initExpandableCards", ex);
        }

    }

    public void setDashboardCommunicatorListener(DashboardCommunicatorListener dashboardCommunicationListener)
    {
        this.dashboardCommunicationListener = dashboardCommunicationListener;
    }

    protected void startGroupAnimation() {
        /**
         * @Serban
         * Stops the loadingDialog from the {@link ro.vodafone.mcare.android.service.LoginService} login flow
         * possible solution for a delay on Dashboard loading
         */
        dashboardCommunicationListener.stopSwipeRefreshAnimation();

        stopLoadingDialog();
        if (animateElements)
            dashboardAnimator.animateGroup();
    }

    /*
   * Preparing the list data
   */
    protected void prepareListData(@Nullable CostControl newCostControl) {
        if (getActivity() == null) {
            return;
        }
        listDataHeader = listDataHeader == null ? new ArrayList<BalanceCardHeader>() : listDataHeader;
        listDataChild = listDataChild == null ? new HashMap<BalanceCardHeader, List<BalanceCardDetail>>() : listDataChild;

        if (listDataHeader != null) {
            listDataHeader.clear();
        }
        if (listDataChild != null) {
            listDataChild.clear();
        }

        BalanceSecondarySuccess secondarybalance = (BalanceSecondarySuccess) RealmManager.getRealmObject(realm, BalanceSecondarySuccess.class);
        CostControl costControl = newCostControl != null ? newCostControl : (CostControl) RealmManager.getRealmObject(realm, CostControl.class);
        if (costControl == null) {
            return;
        }
        Timestamp timestamp = costControl.getLastUpdateDate() != null ?
                new Timestamp(costControl.getLastUpdateDate())
                : null;

        String time = null;
        if (timestamp != null) {
            //time = dashboardActivity.getResources().getString(R.string.text_high_postpaid_balance_card,  dayFormat.format(day) + " " + month);
            time = dashboardActivity.getResources().getString(R.string.text_high_postpaid_balance_card, ProfileUtils.getLastBillCycleDate());
        }
        LOGGER.d("Secondary balance is " + secondarybalance);

        if (costControl.getAdditionalCost() != null && costControl.getAdditionalCost().getTotalCost() >= 0f) {
            String textHigh = timestamp != null ? time : "";
            BalanceCardHeader balanceCardHeader =getBalanceCardHeaderWithData(costControl.getAdditionalCost(), textHigh, "Costuri suplimentare");
            if(VodafoneController.getInstance().getUser() instanceof PostPaidUser){
                balanceCardHeader.setShowSecondaryFooterButton(((PostPaidUser) VodafoneController.getInstance().getUser()).showSecondaryButtonInSuplimentaryCost());
            }
            setTextLowWithDynamicIncrease(balanceCardHeader,costControl);

            addOverspentCard(costControl.getAdditionalCost(),balanceCardHeader);
        }
    }
    private void setTextLowWithDynamicIncrease(BalanceCardHeader balanceCardHeader, @NonNull CostControl costControl) {
        BalanceShowAndNotShown balance = costControl.getCurrentExtraoptions().getExtendedBalanceList().where().equalTo(Balance.AMOUNT_TYPE_ID_STRING_KEY, AmountTypeIdEnum.chunk.toString())
                .equalTo(Balance.CHUNK_MODEL_KEY,1).findFirst();
        if(balance!=null && balance.isValid()){
            Double chunktotalcharge = balance.getChunktotalcharge();
            Integer chunkType = balance.getChunkmodel();
            if (chunktotalcharge!=null && chunktotalcharge>0 &&
                    chunkType!=null && chunkType==1) {
                String chunkInString = NumbersUtils.twoDigitsAfterDecimal(chunktotalcharge).replaceAll(",", ".");
                SpannableString sb = setSpannableStringForDynamicRaise(chunkInString);
                balanceCardHeader.setTextLow(sb);
            }
        }
    }

    @NonNull
    protected SpannableString setSpannableStringForDynamicRaise(String chunkInString) {
        String textLow = getActivity().getResources().getString(
                R.string.text_low_postpaid_balance_card, chunkInString);
        int indexOfSpan = textLow.indexOf(chunkInString);
        SpannableString sb = new SpannableString(textLow);
        //sb.setSpan(new StyleSpan(Typeface.BOLD), indexOfSpan, textLow.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new ForegroundColorSpan(Color.WHITE), indexOfSpan, textLow.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }


    @Override
    public void onPause() {
        super.onPause();
        menuAvatarWasNotInited = true;
        realm.close();
    }

    protected BalanceCardHeader getBalanceCardHeaderWithData(AdditionalCost additionalCost, String textHigh, String headerTitle){
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String totalCost = decimalFormat.format(additionalCost.getTotalCost());
        if (totalCost != null && !totalCost.isEmpty()) {
            //create postpaid cost control overspent card data
            String textMiddle = getActivity().getResources().getString(R.string.text_amount_balance_card, totalCost);

           return new BalanceCardHeader(headerTitle, textHigh, textMiddle.replaceAll(",", "."), null);
        }
        return null;
    }

    protected void addOverspentCard(AdditionalCost additionalCost,BalanceCardHeader costControlCardOverspent) {
        List<BalanceCardDetail> ControlCardcardDetailsList = null;
        //BalanceCardHeader costControlCardOverspent = null;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        String totalCost = decimalFormat.format(additionalCost.getTotalCost());
        if (totalCost != null && !totalCost.isEmpty()) {
            //create postpaid cost control overspent card data
            ControlCardcardDetailsList = new ArrayList<BalanceCardDetail>();
            if (additionalCost.getVoiceCost() != null && additionalCost.getVoiceCost() >= 0f) {
                String textLeft = getActivity().getResources().getString(R.string.voice_prepaid_text_left);
                String textRight = getActivity().getResources().getString(R.string.text_amount_balance_card, NumbersUtils.twoDigitsAfterDecimal(additionalCost.getVoiceCost()));
                BalanceCardDetail voiceDetail = new BalanceCardDetail(textLeft, R.drawable.landline_or_call, textRight, Category.VOCE);
                ControlCardcardDetailsList.add(voiceDetail);
            }
            if (additionalCost.getDataCost() != null && additionalCost.getVoiceCost() >= 0f) {
                String textLeft = getActivity().getResources().getString(R.string.data_prepaid_text_left);
                String textRight = getActivity().getResources().getString(R.string.text_amount_balance_card, NumbersUtils.twoDigitsAfterDecimal(additionalCost.getDataCost()));
                BalanceCardDetail dataDetail = new BalanceCardDetail(textLeft, R.drawable.ic_mobile_data, textRight, Category.DATE);
                ControlCardcardDetailsList.add(dataDetail);
            }
            if (additionalCost.getSmsCost() != null && additionalCost.getVoiceCost() >= 0f) {
                String textLeft = getActivity().getResources().getString(R.string.sms_prepaid_text_left);
                String textRight = getActivity().getResources().getString(R.string.text_amount_balance_card, NumbersUtils.twoDigitsAfterDecimal(additionalCost.getSmsCost()));
                BalanceCardDetail smsDetail = new BalanceCardDetail(textLeft, R.drawable.text, textRight, Category.SMS);
                ControlCardcardDetailsList.add(smsDetail);
            }
            if (additionalCost.getOtherCost() != null && additionalCost.getVoiceCost() >= 0f) {
                String textLeft = getActivity().getResources().getString(R.string.other_postpaid_text_left);
                //String textLeft = "";
                String textRight = getActivity().getResources().getString(R.string.text_amount_balance_card, NumbersUtils.twoDigitsAfterDecimal(additionalCost.getOtherCost()));
                BalanceCardDetail smsDetail = new BalanceCardDetail(textLeft, R.drawable.view_grid_white_48, textRight, Category.OTHER);
                ControlCardcardDetailsList.add(smsDetail);
            }
        }
        if (costControlCardOverspent != null) {
            listDataHeader.add(costControlCardOverspent);
            listDataChild.put(costControlCardOverspent, ControlCardcardDetailsList);
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    protected void addErrorCard() {
        if (getView() == null) {
            if (BuildConfig.USE_CRASHLYTICS)
                Crashlytics.logException(new TrackingException(TrackingException.CHANGE_DETACHED_FRAGMENT_VIEWS));
            return;
        }

        LinearLayout errorCard = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.card_dashboard_error_layout, null, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout relativeLayout = (RelativeLayout) expListView.getParent();

        layoutParams.addRule(RelativeLayout.BELOW, relativeLayout.getChildAt(
                relativeLayout.getChildCount()-1).getId());
        layoutParams.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.general_margin);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        errorCard.setId(ViewUtils.generateViewId());

        if (relativeLayout != null) {
            relativeLayout.addView(errorCard, layoutParams);
        }
    }

    protected void showToastErrorMessage() {
        try {
            new CustomToast.Builder(getActivity().getApplicationContext()).message(AppLabels.getToastErrorSomeInfoNotLoaded()).success(false).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void syncronizeCard() {
        listAdapter.notifyDataSetChanged();
    }




    @Override
    public void OnExpanded(int groupPosition, View arrowView) {
        scrollTo(groupPosition);
    }

    @Override
    public void OnCollapse(int groupPosition, View arrowView) {
        scrollTo(groupPosition);

    }

    protected void scrollTo(final int groupPosition) {
        int scrollTo = expListView.getTop() + (expListView.getChildAt(0).getPaddingTop());
        if (groupPosition > 0) {
            int firstChildHeight = expListView.getChildAt(0).getHeight() + (expListView.getChildAt(0).getPaddingTop());
            scrollTo += firstChildHeight;
        }

        expListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom - top > oldBottom - oldTop) {
                    expListView.removeOnLayoutChangeListener(this);

                    final Rect to = new Rect();
                    to.right = right - left;
                    to.bottom = bottom - top;
                    final Rect from = new Rect(to);
                    from.bottom = from.top + oldBottom - oldTop;

                    ObjectAnimator clipAnimator = ObjectAnimator.ofObject(expListView, "clipBounds", new RectEvaluator(), from, to);
                    clipAnimator.setDuration(300);
                    clipAnimator.addListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            onEnd();
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            onEnd();
                        }

                        private void onEnd() {
                            expListView.setClipBounds(null);
                        }
                    });
                    clipAnimator.start();
                }
            }
        });

        final PagingScrollView scrollView = ((DashboardActivity) getActivity()).getMenuScrollView();
        ValueAnimator animToBottom = ValueAnimator.ofInt(0, scrollTo);
        animToBottom.setDuration(300);
        animToBottom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                D.d("scrollTo "+animation.getAnimatedValue());
                scrollView.scrollTo(0, (Integer) animation.getAnimatedValue());
            }
        });
        animToBottom.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearSubcriptions();
        D.w("onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        D.w("onDestroyView");
    }

    public void clearSubcriptions() {
        compositeSubscription.clear();
    }
    protected boolean addViewToGroup(View view, RelativeLayout.LayoutParams layoutParams) {
        if (vovWidget == null || view == null || vovWidget.getParent() == null || !(vovWidget.getParent()instanceof RelativeLayout)) {
            return false;
        } else {
            RelativeLayout relativeLayout = (RelativeLayout) vovWidget.getParent();
            if(layoutParams!=null){
                relativeLayout.addView(view,layoutParams);
            }else{
                relativeLayout.addView(view);
            }
            return true;
        }
    }
}

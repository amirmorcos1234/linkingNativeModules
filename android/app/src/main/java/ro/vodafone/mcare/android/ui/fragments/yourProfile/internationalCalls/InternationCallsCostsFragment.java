package ro.vodafone.mcare.android.ui.fragments.yourProfile.internationalCalls;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.system.InternationalCallsLabels;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.ui.fragments.yourProfile.YourProfileBaseFragment;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;

public class InternationCallsCostsFragment extends YourProfileBaseFragment
{
    public static final String EXTRA_CALLER_PHONE   = "EXTRA_CALLER_PHONE";
    public static final String EXTRA_CALLED_PHONE   = "EXTRA_CALLED_PHONE";
    public static final String EXTRA_COST_RATE      = "EXTRA_COST_RATE";
    public static final String EXTRA_IS_OWN      = "EXTRA_IS_OWN";

    @BindView(R.id.lblCaller)
    protected TextView lblCaller;

    @BindView(R.id.lblCallerNumber)
    protected TextView lblCallerNumber;

    @BindView(R.id.lblReceiver)
    protected TextView lblReceiver;

    @BindView(R.id.lblReceiverNumber)
    protected TextView lblReceiverNumber;

    @BindView(R.id.lblCost)
    protected TextView lblCost;

    @BindView(R.id.btnBack)
    protected TextView btnBack;

    @BindView(R.id.lblDescription)
    protected TextView lblDescription;

    public static InternationCallsCostsFragment newInstance(String callerPhone, String calledPhone, Float rate, boolean isOwn)
    {
        Bundle args = new Bundle();
        args.putString(EXTRA_CALLER_PHONE, callerPhone);
        args.putString(EXTRA_CALLED_PHONE, calledPhone);
        args.putFloat(EXTRA_COST_RATE, rate);
        args.putBoolean(EXTRA_IS_OWN, isOwn);

        InternationCallsCostsFragment fragment = new InternationCallsCostsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (activityInterface != null)
            activityInterface.getNavigationHeader().hideSelectorView();
    }

    @Override
    public String getTitle()
    {
        return InternationalCallsLabels.internationalCallsCardTitle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_international_calls_costs, container, false);
        unbinder = ButterKnife.bind(this, v);

        btnBack.setText(SettingsLabels.getBack());

        lblDescription.setText(InternationalCallsLabels.feeExplanation());

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        if (args != null)
        {
            lblCaller.setVisibility(View.GONE);
            StringBuilder caller = new StringBuilder();
            if (UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber() != null && UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getAlias() != null && args.getBoolean(EXTRA_IS_OWN))
                caller.append(UserSelectedMsisdnBanController.getInstance().getSelectedSubscriber().getAlias() + "\n");

            String callerMSISDN = args.getString(EXTRA_CALLER_PHONE);
            callerMSISDN =  callerMSISDN.startsWith("4") ? callerMSISDN.substring(1) : callerMSISDN;
            caller.append(callerMSISDN);
            lblCallerNumber.setText(caller.toString());

            lblReceiver.setText(InternationalCallsLabels.recipient());
            lblReceiverNumber.setText(args.getString(EXTRA_CALLED_PHONE));

            {
                String costStr = InternationalCallsLabels.getFee();
                String amount = String.format(new Locale("RO", "RO"), "%.2f€", args.getFloat(EXTRA_COST_RATE));
                String perMinuteStr = "/" + InternationalCallsLabels.getMinute();
                String message = String.format(new Locale("RO", "RO"),"%s\n%s%s", costStr, amount, perMinuteStr);

                SpannableString spannable = new SpannableString(message);
                spannable.setSpan(new StyleSpan(Typeface.BOLD), message.indexOf(amount), message.indexOf(amount) + amount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new RelativeSizeSpan(0.6f), message.indexOf(perMinuteStr), message.indexOf(perMinuteStr) + perMinuteStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                lblCost.setText(spannable);
            }
        }
    }

    @OnClick(R.id.btnBack)
    public void backPressed()
    {
        getActivity().onBackPressed();
    }
}

package ro.vodafone.mcare.android.card.billSummary.termTotAmount;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.RealmResults;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.realm.billSummary.ServiceDetails;
import ro.vodafone.mcare.android.client.model.realm.system.BillingOverviewLabels;
import ro.vodafone.mcare.android.ui.utils.DrawableUtils;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Bivol Pavel on 14.06.2017.
 */
public class TermTotAmountCard extends VodafoneAbstractCard {

    private static final String TAG = "TermTotAmountCard";

    @BindView(R.id.tab_icon)
    CircleImageView icon;

    @BindView(R.id.arrow)
    ImageView arrow;

    @BindView(R.id.tab_name)
    VodafoneTextView tabName;

    @BindView(R.id.aditional_text)
    VodafoneTextView aditionalTextView;

    @BindView(R.id.cost)
    VodafoneTextView costView;

    @BindView(R.id.term_expand_section)
    LinearLayout billSummaryDataSection;

    TermTotAmountController termTotAmountController;

    private String name;
    private String aditionalText;
    private String cost;
    private Long billClosedDate;

    private boolean isDisplayedExpandSection = false;

    public TermTotAmountCard(Context context) {
        super(context);
        init(null);
    }

    public TermTotAmountCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TermTotAmountCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected int setContent() {
        return R.layout.card_term_tot_amount;
    }

    private void init(AttributeSet attrs) {
        ButterKnife.bind(this);
    }

    public TermTotAmountCard setData(String name, String aditionalText, String cost, long billClosedDate) {
        this.name = name;
        this.aditionalText = aditionalText;
        this.cost = cost;
        this.billClosedDate = billClosedDate;

        setAttributes();

        return this;
    }

    public TermTotAmountCard requestBillSummaryTermData() {
        Log.d(TAG, "requestBillSummaryTermData()");
        termTotAmountController = new TermTotAmountController(this);
        termTotAmountController.getTermBillSummary(billClosedDate);
        return this;
    }

    public void onResponse(RealmResults<ServiceDetails> billSummaryList) {
        Log.d(TAG, "onResponse()");

        buildList(billSummaryList);
    }

    private void buildList(RealmResults<ServiceDetails> subTermAmountList) {
        Log.d(TAG, "buildList()");
        if (subTermAmountList != null && subTermAmountList.isValid() && !subTermAmountList.isEmpty()) {

            billSummaryDataSection.removeAllViews();
            for (ServiceDetails subsTermAmountDetails : subTermAmountList) {
                billSummaryDataSection.addView(makeView(billSummaryDataSection, subsTermAmountDetails));
            }

            if (billSummaryDataSection.getChildCount() != 0) {
                arrow.setVisibility(VISIBLE);
                arrow.setColorFilter(Color.parseColor("#E60000"));
                initClickListner();
                if(isDisplayedExpandSection) {
                    arrow.setRotation(180);
                    //DrawableUtils.rotateArrow(arrow, isDisplayedExpandSection);
                }
            }
        } else {
            Log.d("TermTotAmountCard", "null list");
        }
    }

    private View makeView(ViewGroup group, ServiceDetails subsTermAmountDetails) {
        final View view = ((LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.term_tot_amount_item, group, false);
        VodafoneTextView serviceName = (VodafoneTextView) view.findViewById(R.id.service_name);
        VodafoneTextView serviceCost = (VodafoneTextView) view.findViewById(R.id.service_cost);

        serviceName.setText(subsTermAmountDetails.getServiceDesc());
        serviceCost.setText(NumbersUtils.twoDigitsAfterDecimal(subsTermAmountDetails.getBillingAmount()) + " RON");

        return view;
    }

    private void initClickListner() {
        Log.d(TAG, "initClickListner()");
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                arrow.setRotation(0);
                DrawableUtils.rotateArrow(arrow, !isDisplayedExpandSection);
                changeBillSummarySectionVisibility();
            }
        });
    }

    private void changeBillSummarySectionVisibility() {
        Log.d(TAG, "changeBillSummarySectionVisibility()");
        if (!isDisplayedExpandSection) {
            displayBillSummarySection();
        } else {
            hideBillSummarySection();
        }
        isDisplayedExpandSection = !isDisplayedExpandSection;
    }

    private void displayBillSummarySection() {
        Log.d(TAG, "displayBillSummarySection()");
        billSummaryDataSection.setVisibility(VISIBLE);
    }

    private void hideBillSummarySection() {
        Log.d(TAG, "hideBillSummarySection()");
        billSummaryDataSection.setVisibility(GONE);
    }

    public void setAttributes() {
        Log.d(TAG, "setAttributes()");
        this.tabName.setText(name);
        this.costView.setText(cost);
        this.costView.setFont(VodafoneTextView.TextStyle.VODAFONE_RGBD);

        if (aditionalText != null) {
            this.aditionalTextView.setVisibility(VISIBLE);
            this.aditionalTextView.setText(aditionalText);
        }
        icon.setColorFilter(Color.parseColor("#ffffff"));
    }
}

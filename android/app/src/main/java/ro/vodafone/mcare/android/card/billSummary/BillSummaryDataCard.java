package ro.vodafone.mcare.android.card.billSummary;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.realm.system.BillingOverviewLabels;
import ro.vodafone.mcare.android.ui.utils.NumbersUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by user on 18.04.2017.
 */

public class BillSummaryDataCard extends VodafoneAbstractCard{

    @BindView(R.id.tab_icon)
    CircleImageView icon;

    @BindView(R.id.arrow)
    ImageView arrow;

    @BindView(R.id.tab_name)
    VodafoneTextView tabName;

    @BindView(R.id.aditional_text)
    VodafoneTextView aditionalText;

    @BindView(R.id.cost)
    VodafoneTextView cost;

    public BillSummaryDataCard(Context context) {
        super(context);
        init(null);
    }

    public BillSummaryDataCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        ButterKnife.bind(this);
        setCardMargins();
    }

    private void setCardMargins(){
        setCardMargins((int)getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal),
                (int)getResources().getDimensionPixelSize(R.dimen.default_margin_card_vertical) ,
                (int)getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal),
                (int)getResources().getDimensionPixelSize(R.dimen.default_margin_card_vertical) );
    }

    @Override
    protected int setContent() {
        return R.layout.card_bill_summary_data;
    }

    public BillSummaryDataCard setAttributes(String name, String aditionalText, String cost, boolean isDisplayedArrow){

        this.tabName.setText(name);
        this.cost.setText(cost);
        this.cost.setFont(VodafoneTextView.TextStyle.VODAFONE_RGBD);

        if(aditionalText != null){
            this.aditionalText.setVisibility(VISIBLE);
            this.aditionalText.setText(aditionalText);
        }

        if(isDisplayedArrow){
            arrow.setVisibility(VISIBLE);
            arrow.setColorFilter(Color.parseColor("#E60000"));
        }

        icon.setColorFilter(Color.parseColor("#ffffff"));

        return this;
    }
}

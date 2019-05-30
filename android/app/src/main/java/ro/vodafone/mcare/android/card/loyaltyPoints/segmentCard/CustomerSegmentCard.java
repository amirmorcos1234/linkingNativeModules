package ro.vodafone.mcare.android.card.loyaltyPoints.segmentCard;

import android.content.Context;
import android.util.ArrayMap;
import android.util.AttributeSet;

import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.ui.utils.TextUtils;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by User on 21.04.2017.
 */

public class CustomerSegmentCard extends VodafoneAbstractCard {
    private Context mContext;

    public CustomerSegmentCard(Context context) {
        super(context);
        this.mContext = context;
        init(null);
    }

    public CustomerSegmentCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    public CustomerSegmentCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }

    @Override
    protected int setContent() {
        return R.layout.card_customer_segment;
    }

    private void init(AttributeSet attrs) {
        setCardPaddingsInDp(12, 16, 12, 16);
        setupController();
    }

    private void setupController() {
        CustomerSegmentCardController.getInstance().setup(this).requestData();
    }

    public void buildCard(String segment) {
        VodafoneTextView segmentTitle = (VodafoneTextView) findViewById(R.id.loyalty_segment_text);
        segmentTitle.setText(String.format(LoyaltyLabels.getLoyalty_segment_title(), segment.toUpperCase()));
        VodafoneTextView segmentDescription = (VodafoneTextView) findViewById(R.id.loyalty_segment_description);
        if (segment.equals("Silver")) {
            segmentDescription.setText(LoyaltyLabels.getLoyalty_segment_description_silver());
        } else {
                segmentDescription.setText(TextUtils.fromHtml(String.format(LoyaltyLabels.getLoyalty_segment_description(), getSegmentBonus(segment))));
        }
    }

    private String getSegmentBonus(String segment) {
        Map<String, String> bonus = new ArrayMap<>();
        bonus.put("Gold", "50%");
        bonus.put("Platinum", "50%");
        bonus.put("Titanium", "100%");
        bonus.put("Diamond", "200%");

        return bonus.get(segment);
    }
}

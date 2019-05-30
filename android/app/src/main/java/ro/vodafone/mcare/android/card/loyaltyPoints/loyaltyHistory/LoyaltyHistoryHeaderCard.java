package ro.vodafone.mcare.android.card.loyaltyPoints.loyaltyHistory;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import org.apache.commons.lang3.text.WordUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by User on 25.04.2017.
 */

public class LoyaltyHistoryHeaderCard extends VodafoneAbstractCard {
    @BindView(R.id.section_total_points)
    VodafoneTextView totalPoints;

    @BindView(R.id.section_date)
    VodafoneTextView date;


    public LoyaltyHistoryHeaderCard(Context context) {
        super(context);
        initCard(null);
    }

    public LoyaltyHistoryHeaderCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCard(attrs);
    }

    public LoyaltyHistoryHeaderCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCard(attrs);
    }

    @Override
    protected int setContent() {
        return R.layout.card_header_loyalty_history;
    }

    private void initCard(AttributeSet attrs) {
        ButterKnife.bind(this);

        setupController();
        setTransparentBackground();
    }

    private void setupController() {
        LoyaltyHistoryHeaderController.getInstance().setup(this);
    }

    private void setTransparentBackground() {
        setPadding(0, 0, 0, 0);
        this.setBackgroundColor(Color.TRANSPARENT);
        getCardView().setBackgroundColor(Color.TRANSPARENT);
    }

    public void buildCard(String balance, String date) {
        VodafoneTextView points = (VodafoneTextView) findViewById(R.id.section_total_points);
        VodafoneTextView lastUpdateView = (VodafoneTextView) findViewById(R.id.section_date);
        points.setText(String.format(LoyaltyLabels.getLoyaltyTotalPoints(), balance));
        try {
            lastUpdateView.setText(String.format(LoyaltyLabels.getLoyaltyLastUpdate(), changeDateFormat(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String changeDateFormat(String date) throws ParseException {
        DateFormat fromFormat = new SimpleDateFormat("dd.MM.yyyy", new Locale("RO", "RO"));
        DateFormat toFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("RO", "RO"));
        Date formattedDate = fromFormat.parse(date);


        return WordUtils.capitalize(toFormat.format(formattedDate));
    }
}

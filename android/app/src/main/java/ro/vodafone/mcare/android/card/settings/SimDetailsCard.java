package ro.vodafone.mcare.android.card.settings;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;


/**
 * Created by user2 on 4/14/2017.
 */

public class SimDetailsCard extends VodafoneAbstractCard {
    @BindView(R.id.title_tv)
    VodafoneTextView titleTextView;
    @BindView(R.id.details_tv)
    VodafoneTextView detailsTextView;

    Context mContext;

    String details = "";
    private View extraView;

    public SimDetailsCard(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public SimDetailsCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    public SimDetailsCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        ButterKnife.bind(this);
        setStartPadding(0);
        setAttributes(attributeSet);
    }

    private void setAttributes(AttributeSet attributeSet) {

    }


    @Override
    protected int setContent() {
        return R.layout.card_simple_detail;
    }

    public SimDetailsCard setTitle(String title) {
        titleTextView.setText(title);
        return this;
    }

    public SimDetailsCard setDetails(String detail) {
        details = detail;
        detailsTextView.setText(details);
        return this;
    }

    public SimDetailsCard setDetailsSpannable(Spannable detail) {
        details = detail.toString();
        detailsTextView.setMovementMethod(LinkMovementMethod.getInstance());
        detailsTextView.setText(detail);
        detailsTextView.setLinkTextColor(ContextCompat.getColor(mContext, R.color.black_opacity_90));
        return this;
    }

    private LinearLayout getContentGroupView() {
        return (LinearLayout) findViewById(R.id.text_sim_group);
    }

    public void addExtraTextGroup(String title, String detail) {
        if (extraView == null)
            extraView = LayoutInflater.from(getContext()).inflate(R.layout.simple_extra_detail_card, null);
        VodafoneTextView vodafoneTitleView = (VodafoneTextView) extraView.findViewById(R.id.extra_title_tv);
        vodafoneTitleView.setText(title);

        VodafoneTextView vodafoneDetailView = (VodafoneTextView) extraView.findViewById(R.id.extra_details_tv);
        vodafoneDetailView.setText(detail);

        if (!extraView.isAttachedToWindow())
            getContentGroupView().addView(extraView);
    }

    public void removeExtraTextGroupIfDisplayed(){
        if(extraView != null && extraView.getVisibility() == VISIBLE){
            getContentGroupView().removeView(extraView);
        }
    }

    public void hideLoading() {
        super.hideLoading();
    }

    public void showError() {
        super.showError(true);
    }

    @Override
    public SimDetailsCard showLoading(boolean hideContent) {
        super.showLoading(true);
        return this;
    }
}

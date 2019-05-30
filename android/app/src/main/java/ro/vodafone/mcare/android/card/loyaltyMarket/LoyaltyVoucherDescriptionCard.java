package ro.vodafone.mcare.android.card.loyaltyMarket;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.StyleableRes;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.ui.fragments.loyaltyMarket.LoyaltyVoucherDetailsFragment;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Serban Radulescu on 8/28/2017.
 */

public class LoyaltyVoucherDescriptionCard extends VodafoneAbstractCard {

    @BindView(R.id.voucher_campaign_title_textview)
    VodafoneTextView campaignTitle;
    @BindView(R.id.voucher_description_textview)
    VodafoneTextView voucherDescriptionTv;
    @BindView(R.id.partner_link_text)
    VodafoneTextView partnerLink;

    Context context;

    public LoyaltyVoucherDescriptionCard(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    public LoyaltyVoucherDescriptionCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public LoyaltyVoucherDescriptionCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attributeSet){
        ButterKnife.bind(this);
        setStartPadding(0);
        setAttributes(attributeSet);
        setCardMargins((int) getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal), (int) getResources().getDimensionPixelSize(R.dimen.login_page_vertical_view_margin), (int) getResources().getDimensionPixelSize(R.dimen.default_margin_horizontal), (int) getResources().getDimensionPixelSize(R.dimen.login_page_vertical_view_margin));
    }

    private void setAttributes(AttributeSet attributeSet) {
    }

    @Override
    protected int setContent() {
        return R.layout.card_loyalty_voucher_description;
    }


    public LoyaltyVoucherDescriptionCard setCampaignTitle(String campaignTitleString) {
        campaignTitle.setText(campaignTitleString);
        return this;
    }

    public LoyaltyVoucherDescriptionCard setVoucherDescription(String voucherDescription){
        voucherDescriptionTv.setText(voucherDescription);
        return this;
    }

    public LoyaltyVoucherDescriptionCard setVoucherHyperlink(String voucherLink, final String voucherId){
        String label = "Vezi produsele la ofertă";


        SpannableString content = new SpannableString(label);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        partnerLink.setText(content);

        if (!voucherLink.startsWith("http://") && !voucherLink.startsWith("https://")) {
            voucherLink = "http://" + voucherLink;
        }

        final String url = voucherLink;

        partnerLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    setupUrlClickTrackingEvent(url, voucherId);

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                } catch (Exception e){
                    Log.e(TAG, url + "is not a valid URI");
                    e.printStackTrace();
                }

            }
        });
        return this;
    }

    public LoyaltyVoucherDescriptionCard hideVoucherHyperlink(){
        partnerLink.setVisibility(GONE);
        return this;
    }

    private void setupUrlClickTrackingEvent(String url, String voucherId) {
        LoyaltyVoucherDetailsFragment.VodafoneMarketOfferTrackingEvent event = new LoyaltyVoucherDetailsFragment.VodafoneMarketOfferTrackingEvent(voucherId);
        TrackingAppMeasurement journey = new TrackingAppMeasurement();

        journey.prop29 = url;
        journey.getContextData().put("prop29", journey.prop29);

        journey.event65 = "click on URL";
        journey.getContextData().put("event65", journey.event65);

        journey.eVar82 = "mcare:loyalty program:button:click on " + url;
        journey.getContextData().put("eVar82", journey.eVar82);

        event.defineTrackingProperties(journey);
        VodafoneController.getInstance().getTrackingService().trackCustom(event);
    }
}

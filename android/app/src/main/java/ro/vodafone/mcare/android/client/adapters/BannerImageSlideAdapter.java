package ro.vodafone.mcare.android.client.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.bumptech.glide.Glide;

import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.beo.OfferRowInterface;
import ro.vodafone.mcare.android.client.model.beo.postpaid.eligibleOffers.PostpaidOfferRow;
import ro.vodafone.mcare.android.client.model.beo.prepaid.eligibleOffers.PrepaidOfferRow;
import ro.vodafone.mcare.android.client.model.realm.offers.BannerOffer;
import ro.vodafone.mcare.android.client.model.realm.offers.BannerOfferType;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by Bivol Pavel on 14.03.2017.
 */
public class BannerImageSlideAdapter extends PagerAdapter {

    private static final String TAG = "BannerImageSlider";
    private Context context;

    private View.OnClickListener offerBannerWebviewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            loadWevView(view);
        }
    };

    private View.OnClickListener offerBannerDeepLinkClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            loadBeoDetailedFragment(view);
        }
    };

    private OfferRowInterface getOfferRowByOfferId(String offerId){
        Log.d(TAG, "getOfferRowByOfferId with id :" + offerId);

        OfferRowInterface offerRowInterface = null;
        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
            Long idOfferRow = Long.valueOf(offerId);
            offerRowInterface = (PrepaidOfferRow) RealmManager.getRealmObjectAfterLongField(PrepaidOfferRow.class,PrepaidOfferRow.OFFER_ID,idOfferRow);
        }else if(VodafoneController.getInstance().getUser() instanceof PostPaidUser){
            String idOfferRow = offerId;
            offerRowInterface = (PostpaidOfferRow) RealmManager.getRealmObjectAfterStringField(PostpaidOfferRow.class,PostpaidOfferRow.MATRIX_ID,idOfferRow);
        }
        return  offerRowInterface;
    }

    private List<BannerOffer> offerList;
    private LayoutInflater layoutInflater;

    public BannerImageSlideAdapter(Context context, List<BannerOffer> offerList) {
        this.context = context;
        this.offerList = offerList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.offer_banner_item, container, false);

        BannerOffer currentOffer = offerList.get(position);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.banner_image);
        VodafoneTextView bannerTitle = (VodafoneTextView) itemView.findViewById(R.id.banner_title);
        VodafoneTextView bannerDescription = (VodafoneTextView) itemView.findViewById(R.id.banner_description);


        bannerTitle.setText(currentOffer.getBannerTitle());
        bannerDescription.setText(currentOffer.getBannerDescription());

        Glide.with(context)
                .load(currentOffer.getBannerImg())
//                .placeholder(R.drawable.promo_banner)
                .into(imageView);

        if (BannerOfferType.WEBVIEW.getType().equalsIgnoreCase(currentOffer.getOfferType())
                || BannerOfferType.WEBVIEW_CAMPAIGN.getType().equalsIgnoreCase(currentOffer.getOfferType())) {
            imageView.setTag(currentOffer.getLink());
            imageView.setOnClickListener(offerBannerWebviewClickListener);
        } else {
            imageView.setTag(currentOffer.getOfferId());
            imageView.setOnClickListener(offerBannerDeepLinkClickListener);
        }

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((RelativeLayout) object);
    }

    @Override

    public int getCount() {
        return offerList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private void loadBeoDetailedFragment(View view) {
        Log.d(TAG, "loadBeoDetailedFragment with offerId:" + view.getTag());
        new NavigationAction(context,IntentActionName.OFFERS_BEO_DETAILS).setOneUsageSerializedData(view.getTag().toString()).startAction();
    }

    private void loadWevView(View view){
        Log.d(TAG, "loadWevView with url:" + view.getTag());
        if(view.getTag() != null){
            Intent intent = new Intent(context, WebviewActivity.class);
            intent.putExtra(WebviewActivity.KEY_URL, String.valueOf(view.getTag()));
            context.startActivity(intent);
        }
    }
}

package ro.vodafone.mcare.android.client.adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.realm.appconfig.AppConfiguration;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.Promotion;
import ro.vodafone.mcare.android.ui.activities.loyalty.LoyaltyActivity;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by Serban Radulescu on 9/6/2017.
 */

public class VoucherBannerSliderAdapter extends PagerAdapter {

    private static final String TAG = VoucherBannerSliderAdapter.class.getCanonicalName();
    private Context context;
    private List<Promotion> voucherList;
    private LayoutInflater layoutInflater;

    private static final String DEFAULT_PROMOTION_ID = "default_banner";

    private boolean redirectToNewVoucherList = false;
    private boolean redirectToReservedVoucher = false;

    public VoucherBannerSliderAdapter(Context context, List<Promotion> voucherList, boolean redirectToNewVoucherListFlag, boolean redirectToOwnVoucherListingFlag) {
        this.context = context;
        this.voucherList = voucherList;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        redirectToNewVoucherList = redirectToNewVoucherListFlag;
        redirectToReservedVoucher = redirectToOwnVoucherListingFlag;
    }

    private View.OnClickListener voucherDetailsRedirect = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Map<String, Object> tealiumMapEvent = new HashMap(6);
            tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.loyalty);
            tealiumMapEvent.put(TealiumConstants.event_name, TealiumConstants.loyalty_vouchers_banner_click);
            if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
                tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());

            TealiumHelper.trackEvent(getClass().getSimpleName(), tealiumMapEvent);

            String redirectTarget = view.getTag().toString();
            Log.d(TAG, "voucherDetailsredirect view.getTag()" + redirectTarget);
            if (redirectTarget.contains(DEFAULT_PROMOTION_ID)) {
                if(redirectToNewVoucherList) {
                    new NavigationAction(context, IntentActionName.LOYALTY_MARKET)
                            .setOneUsageSerializedData("new_voucher_list").startAction();
                }
                else
                if(redirectToReservedVoucher){
                    new NavigationAction(context, IntentActionName.LOYALTY_MARKET)
                            .setOneUsageSerializedData("reserved_voucher_list").startAction();
                }
            }
            else {
                new NavigationAction(context ,IntentActionName.LOYALTY_MARKET_VOUCHER_DETAILS)
                        .setOneUsageSerializedData(redirectTarget).startAction();
            }
            ((LoyaltyActivity)context).stopSlider();
        }
    };

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d(TAG, "load banner: " + voucherList.get(position));

        View itemView = layoutInflater.inflate(R.layout.voucher_banner_item, container, false);

        Promotion voucherItem = voucherList.get(position);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.voucher_image);

        String defaultBannerUrl = "";
        defaultBannerUrl = AppConfiguration.getNonTelcoDefaultBannerUrl();

        if(voucherItem != null && voucherItem.isValid()) {
            if (voucherItem.getPromotionId().equals(DEFAULT_PROMOTION_ID)) {
                Glide.with(context)
                        .load(defaultBannerUrl)
                        .placeholder(R.drawable.vodafone_voucher_mall_placeholder)
                        .error(R.drawable.vodafone_voucher_mall_placeholder)
                        .into(imageView);
            } else {
                Glide.with(context)
                        .load(voucherItem.getBannerId())
                        .placeholder(R.drawable.vodafone_voucher_mall_placeholder)
                        .error(R.drawable.vodafone_voucher_mall_placeholder)
                        .into(imageView);
            }

            String redirectTarget = voucherItem.getPromotionId();
            imageView.setTag(redirectTarget);
            imageView.setOnClickListener(voucherDetailsRedirect);

            container.addView(itemView);
            return itemView;
        }

        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((ConstraintLayout) object);
    }

    @Override
    public int getCount() {
        return voucherList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}

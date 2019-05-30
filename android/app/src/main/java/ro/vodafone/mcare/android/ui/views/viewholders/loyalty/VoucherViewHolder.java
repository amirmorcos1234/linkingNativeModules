package ro.vodafone.mcare.android.ui.views.viewholders.loyalty;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.TealiumConstants;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.loyaltyMarket.LoyaltyVoucherCard;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherReservedSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.LoyaltyVoucherSuccess;
import ro.vodafone.mcare.android.client.model.realm.loyalty.market.Promotion;
import ro.vodafone.mcare.android.client.model.realm.system.LoyaltyLabels;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.ui.activities.loyalty.LoyaltyActivity;
import ro.vodafone.mcare.android.ui.views.viewholders.general.DynamicViewHolder;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;

/**
 * Created by Victor Radulescu on 8/30/2017.
 */

public class VoucherViewHolder extends DynamicViewHolder<RealmObject, LoyaltyVoucherCard> {

    private Long serverSysDate;

    public VoucherViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void setupWithData(final Activity activity, final RealmObject element) {
        if (element instanceof Promotion) {
            if(element.isValid()) {
                setupServerDate();
                setData((Promotion) element);
            }
        } else {
            return;
        }

        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoyaltyActivity.LoyaltyProgramTrackingEvent event = new LoyaltyActivity.LoyaltyProgramTrackingEvent();
                TrackingAppMeasurement journey = new TrackingAppMeasurement();
                journey.event65 = "Banner click";
                journey.getContextData().put("event65", journey.event65);

                journey.eVar82 = "mcare:loyalty program:button:banner click";
                journey.getContextData().put("eVar82", journey.eVar82);

                event.defineTrackingProperties(journey);
                VodafoneController.getInstance().getTrackingService().trackCustom(event);

                trackEvent(TealiumConstants.voucher_details);

                if (element instanceof Promotion) {
                    if (((Promotion) element).isReserved()) {
                        if(!isVoucherExpired((Promotion)element)) {

                            String promotionId = ((Promotion) element).getPromotionId();
                            new NavigationAction(activity, IntentActionName.LOYALTY_MARKET_VOUCHER_DETAILS)
                                    .setOneUsageSerializedData(promotionId)
                                    .startAction();
                        }
                    }
                    else {
                        if(!isCampaignExpired((Promotion) element)) {

                            String promotionId = ((Promotion) element).getPromotionId();
                            new NavigationAction(activity, IntentActionName.LOYALTY_MARKET_VOUCHER_DETAILS)
                                    .setOneUsageSerializedData(promotionId)
                                    .startAction();
                        }
                    }
                }
            }
        });
    }
    private void trackEvent(String event){
        Map<String, Object> tealiumMapEvent = new HashMap(6);
        tealiumMapEvent.put(TealiumConstants.screen_name, TealiumConstants.loyalty_market_vouchers);
        tealiumMapEvent.put(TealiumConstants.event_name, event);
        if(VodafoneController.getInstance().getUserProfile() != null && VodafoneController.getInstance().getUserProfile().getUserRole()!=null)
            tealiumMapEvent.put(TealiumConstants.user_type, VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());

        TealiumHelper.trackEvent(TealiumConstants.loyalty_market_voucher_listings_fragment, tealiumMapEvent);

    }
    private void setData(Promotion element) {
        try {
            String decodedVoucherName = URLDecoder.decode(element.getVoucherName(), "UTF-8");
            getView().setVoucherName(decodedVoucherName)
                    .loadProductImage(element.getBannerId())
                    .setVoucherShortDescription(element.getPartnerName());

            if (element.isReserved()) {
                if(isVoucherExpired(element)){
                    getView().setVoucherExpirationWarningText(element.getVoucherExpiryDate(), serverSysDate)
                            .setImagesGrayed()
                            .setDiscountContainerColor(R.color.gray_text_color)
                            .hideArrowIndicator();
                }
                else {
                    getView().setVoucherExpirationWarningText(element.getVoucherExpiryDate(), serverSysDate)
                            .setDiscountContainerColor(R.color.blue_chart_top_color)
                            .showArrowIndicator()
                            .setImagesColored();
                }
            } else {
                if(isCampaignExpired(element)){
                    getView().setVoucherExpirationWarningText(element.getCampaignExpiryDate(), serverSysDate)
                            .setImagesGrayed()
                            .setDiscountContainerColor(R.color.gray_text_color)
                            .hideArrowIndicator();
                }
                else {
                    getView().setVoucherExpirationWarningText(element.getCampaignExpiryDate(), serverSysDate)
                            .setDiscountContainerColor(R.color.blue_chart_top_color)
                            .showArrowIndicator()
                            .setImagesColored();
                }
            }

            if (element.getDiscountValue() != null) {
                if(element.getDiscountValue().contains("+")){
                    getView().setVoucherDiscountLabel(LoyaltyLabels.getLoyaltyVoucherFreeLabel());
                    getView().setVoucherDiscount(element.getDiscountValue());
                }
                else
                if (element.getUnitMeasure() != null) {
                    getView().setVoucherDiscountLabel(LoyaltyLabels.getLoyaltyVoucherDiscountLabel());
                    getView().setVoucherDiscount(element.getDiscountValue() + " " + element.getUnitMeasure());
                } else {
                    getView().colapseDiscountContainer();
                }
            } else {
                getView().colapseDiscountContainer();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isCampaignExpired(Promotion promotion) {
        try {
            return promotion.getCampaignExpiryDate() < serverSysDate;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean isVoucherExpired(Promotion promotion) {
        try {
            return promotion.getVoucherExpiryDate() < serverSysDate;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void setupServerDate() {
        LoyaltyVoucherReservedSuccess loyaltyVoucherReservedSuccess = (LoyaltyVoucherReservedSuccess) RealmManager.getRealmObject(LoyaltyVoucherReservedSuccess.class);
        LoyaltyVoucherSuccess loyaltyVoucherSuccess = (LoyaltyVoucherSuccess) RealmManager.getRealmObject(LoyaltyVoucherSuccess.class);

        if(loyaltyVoucherReservedSuccess != null){
            serverSysDate = loyaltyVoucherReservedSuccess.getSysdate();
        }
        else if(loyaltyVoucherSuccess != null){
            serverSysDate = loyaltyVoucherSuccess.getSysdate();
        }
        else {
            serverSysDate = System.currentTimeMillis();
        }
    }
}

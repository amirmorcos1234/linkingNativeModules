package ro.vodafone.mcare.android.client.model.payBill;

import ro.vodafone.mcare.android.utils.firebaseAnalytics.FirebaseAnalyticsItem;

/**
 * Created by Bivol Pavel on 29.06.2017.
 */

public class BillingWebViewModel extends WebViewBaseModel {

    private String successMessage;
    private String activityIdentifier;
    private boolean isServices;
    private String offerName;
    private FirebaseAnalyticsItem analyticsValue;

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getActivityIdentifier() {
        return activityIdentifier;
    }

    public void setActivityIdentifier(String activityIdentifier) {
        this.activityIdentifier = activityIdentifier;
    }

    public boolean isServices() {
        return isServices;
    }

    public void setServices(boolean services) {
        isServices = services;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public FirebaseAnalyticsItem getAnalyticsValue() {
        return analyticsValue;
    }

    public void setAnalyticsValue(FirebaseAnalyticsItem analyticsValue) {
        this.analyticsValue = analyticsValue;
    }
}

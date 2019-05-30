package ro.vodafone.mcare.android.client.model.vodafoneTv;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class TvHierarchy extends RealmObject {

    @SerializedName("serviceType")
    String serviceType;
    @SerializedName("serviceId")
    String serviceId;
    @SerializedName("ban")
    String ban;
    @SerializedName("infoSource")
    String infoSource;
    @SerializedName("customerId")
    String customerId;
    @SerializedName("subscriberId")
    String subscriberId;
    @SerializedName("creationDate")
    String creationDate;
    @SerializedName("avatarURL")
    String avatarURL;
    @SerializedName("alias")
    String alias;

    public TvHierarchy() {
    }

    public TvHierarchy(String serviceType, String serviceId, String ban, String infoSource, String customerId, String subscriberId, String creationDate, String avatarURL, String alias) {
        this.serviceType = serviceType;
        this.serviceId = serviceId;
        this.ban = ban;
        this.infoSource = infoSource;
        this.customerId = customerId;
        this.subscriberId = subscriberId;
        this.creationDate = creationDate;
        this.avatarURL = avatarURL;
        this.alias = alias;
    }

    public String getAvatarUrl() {
        return avatarURL;
    }

    public void setAvatarUrl(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getBan() {
        return ban;
    }

    public void setBan(String ban) {
        this.ban = ban;
    }

    public String getInfoSource() {
        return infoSource;
    }

    public void setInfoSource(String infoSource) {
        this.infoSource = infoSource;
    }

    public String getCustomerID() {
        return customerId;
    }

    public void setCustomerID(String customerId) {
        this.customerId = customerId;
    }

    public String getSubscriberID() {
        return subscriberId;
    }

    public void setSubscriberID(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}

package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bogdan.marica on 3/2/2017.
 */

public class NotificationRequest extends BaseRequest {


    @SerializedName("notificationsFlag")
    private boolean notificationsFlag;

    @SerializedName("vouchersFlag")
    private boolean vouchersFlag;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("os")
    private String os;

    @SerializedName("deviceId")
    private String deviceId;

    public NotificationRequest(boolean notificationsFlag, boolean vouchersFlag, String createdBy, String os, String deviceId) {
        this.notificationsFlag = notificationsFlag;
        this.createdBy = createdBy;
        this.os = os;
        this.deviceId = deviceId;
        this.vouchersFlag = vouchersFlag;
    }

    public boolean isNotificationsFlag() {
        return notificationsFlag;
    }

    public void setNotificationsFlag(boolean notificationsFlag) {
        this.notificationsFlag = notificationsFlag;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isVouchersFlag() {
        return vouchersFlag;
    }

    public void setVouchersFlag(boolean vouchersFlag) {
        this.vouchersFlag = vouchersFlag;
    }
}

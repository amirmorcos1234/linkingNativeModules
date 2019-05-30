package ro.vodafone.mcare.android.client.model.realm.notifications;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by User1 on 3/2/2017.
 */

public class NotificationFlag extends RealmObject {

    @SerializedName("notificationsFlag")
    private boolean notificationsFlag;

    @SerializedName("vouchersFlag")
    private boolean vouchersFlag;

    @PrimaryKey
    private int id_notificationFlag;

    public NotificationFlag() {
        id_notificationFlag = 1;
    }

    public int getId_initialToken() {
        return id_notificationFlag;
    }

    public boolean getNotificationsFlag() {
        return notificationsFlag;
    }

    public void setNotificationsFlag(boolean notificationsFlag) {
        this.notificationsFlag = notificationsFlag;
    }

    public boolean getVouchersFlag() {
        return vouchersFlag;
    }

    public void setVouchersFlag(boolean vouchersFlag) {
        this.vouchersFlag = vouchersFlag;
    }
}

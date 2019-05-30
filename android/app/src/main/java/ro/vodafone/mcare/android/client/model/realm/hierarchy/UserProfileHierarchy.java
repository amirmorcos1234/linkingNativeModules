package ro.vodafone.mcare.android.client.model.realm.hierarchy;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bivol Pavel on 05.02.2017.
 */
public class UserProfileHierarchy extends RealmObject{

    @PrimaryKey
    int id_userProfileHierarchy;

    @SerializedName("banList")
    RealmList<Ban> banList;

    @SerializedName("subscriberList")
    RealmList<Subscriber> subscriberList;

    @SerializedName("ban")
    private Ban ban = null;

    @SerializedName("errorDetectedFlag")
    private int errorDetectedFlag;

    public UserProfileHierarchy() {
        this.id_userProfileHierarchy = 1;
    }

    public int getId_userProfileHierarchy() {
        return id_userProfileHierarchy;
    }

    public RealmList<Ban> getBanList() {
        return banList;
    }

    public void setBanList(RealmList<Ban> banList) {
        this.banList = banList;
    }

    public RealmList<Subscriber> getSubscriberList() {
        return subscriberList;
    }

    public void setSubscriberList(RealmList<Subscriber> subscriberList) {
        this.subscriberList = subscriberList;
    }
    /*
    * Gen Ban, for almost all Ebu Migrated user(ebu users calling api 19)
    * */
    public Ban getBan() {
        return ban;
    }

    public void setBan(Ban ban) {
        this.ban = ban;
    }

    public int getErrorDetectedFlag() {
        return errorDetectedFlag;
    }

    public void setErrorDetectedFlag(int errorDetectedFlag) {
        this.errorDetectedFlag = errorDetectedFlag;
    }

    public boolean ifApi19CallTimeout() {
        return errorDetectedFlag == 2;
    }

    public boolean ifApi19CallFailed() {
        return errorDetectedFlag == 1;
    }
}

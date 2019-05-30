package ro.vodafone.mcare.android.client.model.realm.hierarchy;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bivol Pavel on 05.02.2017.
 */
public class Ban extends RealmObject{

    public static String BAN_KEY = "number";

    @PrimaryKey
    @SerializedName("number")
    String number;

    @SerializedName("subscriberList")
    RealmList<Subscriber> subscriberList;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public RealmList<Subscriber> getSubscriberList() {
        return subscriberList;
    }

    public void setSubscriberList(RealmList<Subscriber> subscriberList) {
        this.subscriberList = subscriberList;
    }

    public static Comparator<Ban> getBanComparator(){
        return  new Comparator<Ban>() {
            @Override
            public int compare(Ban ban1, Ban ban2) {
                return ban1.getNumber().compareTo(ban2.getNumber());
            }
        };
    }
}

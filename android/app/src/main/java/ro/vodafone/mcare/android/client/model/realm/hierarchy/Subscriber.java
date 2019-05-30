package ro.vodafone.mcare.android.client.model.realm.hierarchy;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Comparator;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ro.vodafone.mcare.android.utils.RealmManager;

/**
 * Created by Bivol Pavel on 05.02.2017.
 */
public class Subscriber extends RealmObject{

    public static final String MSISDN_KEY = "msisdn";
    private static String GEO_NUMBER = "GEONUM";

    public final static String AVATART_URL_KEY = "avatarUrl";
    public final static String ALIAS_KEY = "alias";
    public final static String SID_KEY = "sid";
    public final static String RESOURCE_TYPE_KEY = "resourceType";

    @SerializedName(AVATART_URL_KEY)
    String avatarUrl;

    @SerializedName(ALIAS_KEY)
    String alias;

    @PrimaryKey
    @SerializedName(MSISDN_KEY)
    String msisdn;

    @SerializedName(SID_KEY)
    String sid;

    @SerializedName(RESOURCE_TYPE_KEY)
    String resourceType;

    public Subscriber() {
    }

    public Subscriber(String avatarUrl, String alias, String msisdn, String sid, String resourceType) {
        this.avatarUrl = avatarUrl;
        this.alias = alias;
        this.msisdn = msisdn;
        this.sid = sid;
        this.resourceType = resourceType;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        RealmManager.startTransaction();
        this.avatarUrl = avatarUrl;
        RealmManager.commitTransaction();
    }

    public String getAlias() {
        return alias;
    }

    public String getAliasWithout4Prefix() {
        if(alias!=null && NumberUtils.isNumber(alias))
            return getNumberWithout4Prefix(alias);
        return alias;
    }

    public void setAlias(String alias) {
        RealmManager.startTransaction();
        this.alias = alias;
        RealmManager.commitTransaction();
    }

    public String getMsisdn() {
        return msisdn;
    }
    public String getMsisdnWithout4Prefix() {
        return getNumberWithout4Prefix(msisdn);
    }

    public String getMsisdnWith0PrefixIfNeeded() {
        if (msisdn!=null && msisdn.length() == 9)
            return  "0" + msisdn;
        return msisdn;
    }

    public String getNumberWithout4PrefixOneMsisdn() {
        String msisdn = getMsisdnWith0PrefixIfNeeded();
        if (msisdn.length() == 11) {
            return getNumberWithout4Prefix(msisdn);
        }
        return msisdn;
    }

    private String getNumberWithout4Prefix(String number) {
        if(number!=null && number.length()>=2){
            if ( number.substring(0, 1).equals("4")) {
                return number.substring(1);
            }
        }
        return number;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public boolean isGeoNumber(){
        return resourceType!=null && resourceType.equals("GEONUM");
    }


    public static Comparator<Subscriber> getSubscriberComparator(){
        return new Comparator<Subscriber>() {
            @Override
            public int compare(Subscriber subscriber1, Subscriber subscriber2) {

                if (subscriber1.getAlias() != null && subscriber2.getAlias() != null)
                    return subscriber1.getAlias().compareTo(subscriber2.getAlias());
                else if (subscriber1.getAlias() == null && subscriber2.getAlias() != null)
                    return -1;
                else if (subscriber1.getAlias() != null && subscriber2.getAlias() == null)
                    return 1;
                else
                    return subscriber1.getMsisdn().compareTo(subscriber2.getMsisdn());
            }
        };
    }
}

package ro.vodafone.mcare.android.client.model.travellingAboard;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Alex on 4/28/2017.
 */

public class CountryList extends RealmObject {

    public static final String COUNTRY_CODE_ISO = "countryCodeISO";
    public static final String COUNTRY_NAME = "countryName";

    @PrimaryKey
    @SerializedName("countryCodeISO")
    private String countryCodeISO;

    @SerializedName("countryName")
    private String countryName;

    @SerializedName("flagURL")
    private String flagURL;

    @SerializedName("messageRo")
    private String messageRo;

    @SerializedName("messageLocal")
    private String messageLocal;

    @SerializedName("operator")
    private String operator;

    @SerializedName("zoneId")
    private int zoneId;

    @SerializedName("RelatedOffers")
    private RealmList<PrepaidRealmLong> RelatedOffers;


    public CountryList() {
    }

    public CountryList(String countryCodeISO, String countryName, String flagURL, String messageRo, String messageLocal, String operator, int zoneId, RealmList<PrepaidRealmLong> relatedOffers) {
        this.countryCodeISO = countryCodeISO;
        this.countryName = countryName;
        this.flagURL = flagURL;
        this.messageRo = messageRo;
        this.messageLocal = messageLocal;
        this.operator = operator;
        this.zoneId = zoneId;
        this.RelatedOffers = relatedOffers;
    }

    public String getCountryCodeISO() {
        return countryCodeISO;
    }

    public void setCountryCodeISO(String countryCodeISO) {
        this.countryCodeISO = countryCodeISO;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getFlagURL() {
        return flagURL;
    }

    public void setFlagURL(String flagURL) {
        this.flagURL = flagURL;
    }

    public String getMessageRo() {
        return messageRo;
    }

    public void setMessageRo(String messageRo) {
        this.messageRo = messageRo;
    }

    public String getMessageLocal() {
        return messageLocal;
    }

    public void setMessageLocal(String messageLocal) {
        this.messageLocal = messageLocal;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public RealmList<PrepaidRealmLong> getRelatedOffers() {
        return RelatedOffers;
    }

    public void setRelatedOffers(RealmList<PrepaidRealmLong> relatedOffers) {
        RelatedOffers = relatedOffers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class CountryList {\n");

        sb.append("  countryCodeISO: ").append(countryCodeISO).append("\n");
        sb.append("  countryName: ").append(countryName).append("\n");
        sb.append("  flagURL: ").append(flagURL).append("\n");
        sb.append("  messageRo: ").append(messageRo).append("\n");
        sb.append("  operator: ").append(operator).append("\n");
        sb.append("  zoneId: ").append(zoneId).append("\n");
        sb.append("  RelatedOffers: ").append(RelatedOffers).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}

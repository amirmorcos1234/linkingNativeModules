package ro.vodafone.mcare.android.client.model.realm.profile;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.math.NumberUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by user on 10.02.2017.
 */
public class Profile extends RealmObject {

    @SerializedName("homeMsisdn")
    String homeMsisdn;
    @SerializedName("avatarUrl")
    String avatarUrl;
    @SerializedName("alias")
    String alias;
    @SerializedName("isIndygen")
    String isIndygen;
    @SerializedName("isGreenCard")
    boolean isGreenCard;
    @SerializedName("isHybrid")
    boolean isHybrid;
    @SerializedName("isTobe")
    boolean isTobe;
    @SerializedName("isVMB")
    boolean isVMB;
    @SerializedName("billCycleDate")
    Integer billCycleDate;
    @SerializedName("subscriberEffectiveDate")
    Long subscriberEffectiveDate;
    @SerializedName("activationDate")
    Long activationDate;
    @SerializedName("customerSegment")
    String customerSegment;
    @PrimaryKey
    private long id_profile;
    @SerializedName("isRoaming")
    private Boolean isRoaming = null;

    @SerializedName("isEmailBlacklisted")
    boolean isEmailBlacklisted;

    public Profile() {
        this.id_profile = 1;
    }

    public String getHomeMsisdn() {
        return homeMsisdn;
    }

    public String getHomeMsisdnWithout4Prefix() {
        if(homeMsisdn!=null && NumberUtils.isNumber(homeMsisdn))
            return getNumberWithout4Prefix(homeMsisdn);
        return homeMsisdn;
    }

    public void setHomeMsisdn(String homeMsisdn) {
        this.homeMsisdn = homeMsisdn;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAlias() {
        return alias;
    }
    public String getAliasWithout4Prefix() {
        if(alias!=null && NumberUtils.isNumber(alias))
            return getNumberWithout4Prefix(alias);
        return alias;
    }
    private String getNumberWithout4Prefix(String number){
        if(number!=null && number.length()>=2){
            if ( number.substring(0, 1).equals("4")) {
                return number.substring(1);
            }
        }
        return number;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getIsIndygen() {
        return isIndygen;
    }

    public void setIsIndygen(String isIndygen) {
        this.isIndygen = isIndygen;
    }

    public boolean isGreenCard() {
        return isGreenCard;
    }

    public void setGreenCard(boolean greenCard) {
        isGreenCard = greenCard;
    }

    public boolean isEmailBlacklisted() {
        return isEmailBlacklisted;
    }

    public void setIsEmailBlacklisted(boolean isEmailBlacklisted) {
        this.isEmailBlacklisted = isEmailBlacklisted;
    }

    public boolean isHybrid() {
        return isHybrid;
    }

    public void setHybrid(boolean hybrid) {
        isHybrid = hybrid;
    }

    public boolean isTobe() {
        return isTobe;
    }

    public void setTobe(boolean tobe) {
        isTobe = tobe;
    }

    public boolean isVMB() {
        return isVMB;
    }

    public void setVMB(boolean vmb) {
        isVMB = vmb;
    }

    public Integer getBillCycleDate() {
        return billCycleDate;
    }

    public void setBillCycleDate(Integer billCycleDate) {
        this.billCycleDate = billCycleDate;
    }

    public String getCustomerSegment() {
        return customerSegment;
    }

    public void setCustomerSegment(String customerSegment) {
        this.customerSegment = customerSegment;
    }

    public Boolean getRoaming() {
        return isRoaming;
    }

    public void setRoaming(Boolean roaming) {
        isRoaming = roaming;
    }

    public Long getSubscriberEffectiveDate() {
        return subscriberEffectiveDate;
    }

    public void setSubscriberEffectiveDate(Long subscriberEffectiveDate) {
        this.subscriberEffectiveDate = subscriberEffectiveDate;
    }

    public Long getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Long activationDate) {
        this.activationDate = activationDate;
    }
}

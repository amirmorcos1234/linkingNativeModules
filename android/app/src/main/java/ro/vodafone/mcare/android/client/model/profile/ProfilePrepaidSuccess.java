package ro.vodafone.mcare.android.client.model.profile;

/**
 * Created by bogdan marica on 4/29/2017.
 */

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "")public class ProfilePrepaidSuccess extends ProfileSuccess {

    @SerializedName("homeMsisdn")
    private String homeMsisdn = null;
    @SerializedName("avatarUrl")
    private String avatarUrl = null;
    @SerializedName("alias")
    private String alias = null;
    @SerializedName("isIndygen")
    private Boolean isIndygen = null;
    @SerializedName("isGreenCard")
    private Boolean isGreenCard = null;
    @SerializedName("isHybrid")
    private Boolean isHybrid = null;
    @SerializedName("isTobe")
    private Boolean isTobe = null;
    @SerializedName("isRoaming")
    private Boolean isRoaming = null;

    /**
     * default msisdn shown on landing. \"null\" value if no msisdn is set
     **/
    @ApiModelProperty(value = "default msisdn shown on landing. \"null\" value if no msisdn is set")
    public String getHomeMsisdn() {
        return homeMsisdn;
    }
    public void setHomeMsisdn(String homeMsisdn) {
        this.homeMsisdn = homeMsisdn;
    }

    /**
     * CMS url for subscriber avatar. \"null\" value if no avatar is set
     **/
    @ApiModelProperty(value = "CMS url for subscriber avatar. \"null\" value if no avatar is set")
    public String getAvatarUrl() {
        return avatarUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * subscriber nickname/alias. \"null\" value if no alias is set
     **/
    @ApiModelProperty(value = "subscriber nickname/alias. \"null\" value if no alias is set")
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * if user contains srv_id=1006, user is indygen
     **/
    @ApiModelProperty(value = "if user contains srv_id=1006, user is indygen")
    public Boolean getIsIndygen() {
        return isIndygen;
    }
    public void setIsIndygen(Boolean isIndygen) {
        this.isIndygen = isIndygen;
    }

    /**
     * if user contains srv_id=79, user has green card
     **/
    @ApiModelProperty(value = "if user contains srv_id=79, user has green card")
    public Boolean getIsGreenCard() {
        return isGreenCard;
    }
    public void setIsGreenCard(Boolean isGreenCard) {
        this.isGreenCard = isGreenCard;
    }

    /**
     * if vfCustomerType=hybrid, use is hybrid
     **/
    @ApiModelProperty(value = "if vfCustomerType=hybrid, use is hybrid")
    public Boolean getIsHybrid() {
        return isHybrid;
    }
    public void setIsHybrid(Boolean isHybrid) {
        this.isHybrid = isHybrid;
    }

    /**
     * if isTobe=true, use is TOBE
     **/
    @ApiModelProperty(value = "if isTobe=true, use is TOBE")
    public Boolean getIsTobe() {
        return isTobe;
    }
    public void setIsTobe(Boolean isTobe) {
        this.isTobe = isTobe;
    }

    /**
     * if isRoaming=true, user has Roaming access type
     **/
    @ApiModelProperty(value = "if isRoaming=true, user has Roaming access type")
    public Boolean getIsRoaming() {
        return isRoaming;
    }
    public void setIsRoaming(Boolean isRoaming) {
        this.isRoaming = isRoaming;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProfilePrepaidSuccess profilePrepaidSuccess = (ProfilePrepaidSuccess) o;
        return (this.homeMsisdn == null ? profilePrepaidSuccess.homeMsisdn == null : this.homeMsisdn.equals(profilePrepaidSuccess.homeMsisdn)) &&
                (this.avatarUrl == null ? profilePrepaidSuccess.avatarUrl == null : this.avatarUrl.equals(profilePrepaidSuccess.avatarUrl)) &&
                (this.alias == null ? profilePrepaidSuccess.alias == null : this.alias.equals(profilePrepaidSuccess.alias)) &&
                (this.isIndygen == null ? profilePrepaidSuccess.isIndygen == null : this.isIndygen.equals(profilePrepaidSuccess.isIndygen)) &&
                (this.isGreenCard == null ? profilePrepaidSuccess.isGreenCard == null : this.isGreenCard.equals(profilePrepaidSuccess.isGreenCard)) &&
                (this.isHybrid == null ? profilePrepaidSuccess.isHybrid == null : this.isHybrid.equals(profilePrepaidSuccess.isHybrid)) &&
                (this.isTobe == null ? profilePrepaidSuccess.isTobe == null : this.isTobe.equals(profilePrepaidSuccess.isTobe)) &&
                (this.isRoaming == null ? profilePrepaidSuccess.isRoaming == null : this.isRoaming.equals(profilePrepaidSuccess.isRoaming));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.homeMsisdn == null ? 0: this.homeMsisdn.hashCode());
        result = 31 * result + (this.avatarUrl == null ? 0: this.avatarUrl.hashCode());
        result = 31 * result + (this.alias == null ? 0: this.alias.hashCode());
        result = 31 * result + (this.isIndygen == null ? 0: this.isIndygen.hashCode());
        result = 31 * result + (this.isGreenCard == null ? 0: this.isGreenCard.hashCode());
        result = 31 * result + (this.isHybrid == null ? 0: this.isHybrid.hashCode());
        result = 31 * result + (this.isTobe == null ? 0: this.isTobe.hashCode());
        result = 31 * result + (this.isRoaming == null ? 0: this.isRoaming.hashCode());
        return result;
    }

    @Override
    public String toString()  {
        StringBuilder sb = new StringBuilder();
        sb.append("class ProfilePrepaidSuccess {\n");
        sb.append("  " + super.toString()).append("\n");
        sb.append("  homeMsisdn: ").append(homeMsisdn).append("\n");
        sb.append("  avatarUrl: ").append(avatarUrl).append("\n");
        sb.append("  alias: ").append(alias).append("\n");
        sb.append("  isIndygen: ").append(isIndygen).append("\n");
        sb.append("  isGreenCard: ").append(isGreenCard).append("\n");
        sb.append("  isHybrid: ").append(isHybrid).append("\n");
        sb.append("  isTobe: ").append(isTobe).append("\n");
        sb.append("  isRoaming: ").append(isRoaming).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}

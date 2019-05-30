package ro.vodafone.mcare.android.client.model.profile;

/**
 * Created by bogdan marica on 4/29/2017.
 */

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "object containing data associated with successful transaction")
public class ProfileSuccess {

    @SerializedName("homeMsisdn")
    private String homeMsisdn = null;
    @SerializedName("avatarUrl")
    private String avatarUrl = null;
    @SerializedName("alias")
    private String alias = null;

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


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProfileSuccess profileSuccess = (ProfileSuccess) o;
        return (this.homeMsisdn == null ? profileSuccess.homeMsisdn == null : this.homeMsisdn.equals(profileSuccess.homeMsisdn)) &&
                (this.avatarUrl == null ? profileSuccess.avatarUrl == null : this.avatarUrl.equals(profileSuccess.avatarUrl)) &&
                (this.alias == null ? profileSuccess.alias == null : this.alias.equals(profileSuccess.alias));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.homeMsisdn == null ? 0: this.homeMsisdn.hashCode());
        result = 31 * result + (this.avatarUrl == null ? 0: this.avatarUrl.hashCode());
        result = 31 * result + (this.alias == null ? 0: this.alias.hashCode());
        return result;
    }

    @Override
    public String toString()  {
        StringBuilder sb = new StringBuilder();
        sb.append("class ProfileSuccess {\n");

        sb.append("  homeMsisdn: ").append(homeMsisdn).append("\n");
        sb.append("  avatarUrl: ").append(avatarUrl).append("\n");
        sb.append("  alias: ").append(alias).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}

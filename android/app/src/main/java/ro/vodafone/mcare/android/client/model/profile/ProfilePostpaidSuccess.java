package ro.vodafone.mcare.android.client.model.profile;

/**
 * Created by bogdan marica on 4/29/2017.
 */

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class ProfilePostpaidSuccess extends ProfileSuccess {

    @SerializedName("homeMsisdn")
    private String homeMsisdn = null;
    @SerializedName("avatarUrl")
    private String avatarUrl = null;
    @SerializedName("alias")
    private String alias = null;
    @SerializedName("billCycleDate")
    private Integer billCycleDate = null;
    @SerializedName("customerSegment")
    private String customerSegment = null;

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
     * day in month when invoice issue
     **/
    @ApiModelProperty(value = "day in month when invoice issue")
    public Integer getBillCycleDate() {
        return billCycleDate;
    }
    public void setBillCycleDate(Integer billCycleDate) {
        this.billCycleDate = billCycleDate;
    }

    /**
     * used to target users based on segmentation done having as criteria the number of subscribers under that customer.  Read from header \"vfCustomerSegment\"
     **/
    @ApiModelProperty(value = "used to target users based on segmentation done having as criteria the number of subscribers under that customer.  Read from header \"vfCustomerSegment\"")
    public String getCustomerSegment() {
        return customerSegment;
    }
    public void setCustomerSegment(String customerSegment) {
        this.customerSegment = customerSegment;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProfilePostpaidSuccess profilePostpaidSuccess = (ProfilePostpaidSuccess) o;
        return (this.homeMsisdn == null ? profilePostpaidSuccess.homeMsisdn == null : this.homeMsisdn.equals(profilePostpaidSuccess.homeMsisdn)) &&
                (this.avatarUrl == null ? profilePostpaidSuccess.avatarUrl == null : this.avatarUrl.equals(profilePostpaidSuccess.avatarUrl)) &&
                (this.alias == null ? profilePostpaidSuccess.alias == null : this.alias.equals(profilePostpaidSuccess.alias)) &&
                (this.billCycleDate == null ? profilePostpaidSuccess.billCycleDate == null : this.billCycleDate.equals(profilePostpaidSuccess.billCycleDate)) &&
                (this.customerSegment == null ? profilePostpaidSuccess.customerSegment == null : this.customerSegment.equals(profilePostpaidSuccess.customerSegment));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.homeMsisdn == null ? 0: this.homeMsisdn.hashCode());
        result = 31 * result + (this.avatarUrl == null ? 0: this.avatarUrl.hashCode());
        result = 31 * result + (this.alias == null ? 0: this.alias.hashCode());
        result = 31 * result + (this.billCycleDate == null ? 0: this.billCycleDate.hashCode());
        result = 31 * result + (this.customerSegment == null ? 0: this.customerSegment.hashCode());
        return result;
    }

    @Override
    public String toString()  {
        StringBuilder sb = new StringBuilder();
        sb.append("class ProfilePostpaidSuccess {\n");
        sb.append("  " + super.toString()).append("\n");
        sb.append("  homeMsisdn: ").append(homeMsisdn).append("\n");
        sb.append("  avatarUrl: ").append(avatarUrl).append("\n");
        sb.append("  alias: ").append(alias).append("\n");
        sb.append("  billCycleDate: ").append(billCycleDate).append("\n");
        sb.append("  customerSegment: ").append(customerSegment).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}

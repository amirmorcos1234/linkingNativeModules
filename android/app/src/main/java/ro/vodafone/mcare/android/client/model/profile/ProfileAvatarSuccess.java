package ro.vodafone.mcare.android.client.model.profile;

import com.google.gson.annotations.SerializedName;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by User1 on 5/3/2017.
 */

public class ProfileAvatarSuccess {

    @SerializedName("avatarUrl")
    private String avatarUrl = null;

    /**
     * CMS url for subscriber avatar.
     **/
    @ApiModelProperty(value = "CMS url for subscriber avatar.")
    public String getAvatarUrl() {
        return avatarUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProfileAvatarSuccess profileAvatarSuccess = (ProfileAvatarSuccess) o;
        return (this.avatarUrl == null ? profileAvatarSuccess.avatarUrl == null : this.avatarUrl.equals(profileAvatarSuccess.avatarUrl));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.avatarUrl == null ? 0: this.avatarUrl.hashCode());
        return result;
    }

    @Override
    public String toString()  {
        StringBuilder sb = new StringBuilder();
        sb.append("class ProfileAvatarSuccess {\n");

        sb.append("  avatarUrl: ").append(avatarUrl).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}


package ro.vodafone.mcare.android.client.model.realm.system.mentenance;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.swagger.annotations.ApiModelProperty;

public class Maintenance extends RealmObject {
    @SerializedName("isServerDown")
    private Boolean isServerDown;
    @SerializedName("title")
    private String title;
    @SerializedName("summary")
    private String summary;
    @SerializedName("primaryButtonLabel")
    private String primaryButtonLabel;
    @SerializedName("upTime")
    private Long upTime;

    @ApiModelProperty(value = "specificUrl read from configuration file")
    public Boolean getIsServerDown() {
        return isServerDown;
    }

    @ApiModelProperty(value = "specificUrl read from configuration file")
    public void setIsServerDown(Boolean isServerDown) {
        this.isServerDown = isServerDown;
    }

    public Maintenance withIsServerDown(Boolean isServerDown) {
        this.isServerDown = isServerDown;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Maintenance withTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Maintenance withDescription(String summary) {
        this.summary = summary;
        return this;
    }

    public String getPrimaryButtonLabel() {
        return primaryButtonLabel;
    }

    public void setPrimaryButtonLabel(String primaryButtonLabel) {
        this.primaryButtonLabel = primaryButtonLabel;
    }

    public Maintenance withPrimaryButtonLabel(String primaryButtonLabel) {
        this.primaryButtonLabel = primaryButtonLabel;
        return this;
    }

    public Long getUpTime() {
        return upTime;
    }

    public void setUpTime(Long upTime) {
        this.upTime = upTime;
    }

    public Maintenance withUpTime(Long upTime) {
        this.upTime = upTime;
        return this;
    }

    @Override
    public String toString()  {
        StringBuilder sb = new StringBuilder();
        sb.append("class Maintenance {\n");

        sb.append("  isServerDown: ").append(isServerDown).append("\n");
        sb.append("  title: ").append(title).append("\n");
        sb.append("  summary: ").append(summary).append("\n");
        sb.append("  primaryButtonLabel: ").append(primaryButtonLabel).append("\n");
        sb.append("  upTime: ").append(upTime).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}

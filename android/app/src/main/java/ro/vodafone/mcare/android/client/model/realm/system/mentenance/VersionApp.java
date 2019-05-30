
package ro.vodafone.mcare.android.client.model.realm.system.mentenance;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class VersionApp extends RealmObject {
    @SerializedName("version")
    private String version;
    @SerializedName("isRequired")
    private Boolean isRequired;
    @SerializedName("storeURL")
    private String storeURL;
    @SerializedName("title")
    private String title;
    @SerializedName("summary")
    private String summary;
    @SerializedName("primaryButtonLabel")
    private String primaryButtonLabel;
    @SerializedName("secondaryButtonLabel")
    private String secondaryButtonLabel;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public VersionApp withVersion(String version) {
        this.version = version;
        return this;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public VersionApp withIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
        return this;
    }

    public String getStoreURL() {
        return storeURL;
    }

    public void setStoreURL(String storeURL) {
        this.storeURL = storeURL;
    }

    public VersionApp withStoreURL(String storeURL) {
        this.storeURL = storeURL;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public VersionApp withTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return summary;
    }

    public void setDescription(String description) {
        this.summary = description;
    }

    public VersionApp withDescription(String description) {
        this.summary = description;
        return this;
    }

    public String getPrimaryButtonLabel() {
        return primaryButtonLabel;
    }

    public void setPrimaryButtonLabel(String primaryButtonLabel) {
        this.primaryButtonLabel = primaryButtonLabel;
    }

    public VersionApp withPrimaryButtonLabel(String primaryButtonLabel) {
        this.primaryButtonLabel = primaryButtonLabel;
        return this;
    }

    public String getSecondaryButtonLabel() {
        return secondaryButtonLabel;
    }

    public void setSecondaryButtonLabel(String secondaryButtonLabel) {
        this.secondaryButtonLabel = secondaryButtonLabel;
    }

    public VersionApp withSecondaryButtonLabel(String secondaryButtonLabel) {
        this.secondaryButtonLabel = secondaryButtonLabel;
        return this;
    }

    public int compareWithCurrentAppVersion(String currentAppVersion) {
        if(version == null) {
            return 1;
        }

        String[] requiredVersion = version.split("\\.");
        String[] currentVersion = currentAppVersion.split("\\.");

        int counter = 0;
        int requiredVersionLength = requiredVersion.length;
        int currentVersionLength = currentVersion.length;

        if(requiredVersion.length > currentVersion.length){
            return 1;
        } else
        if(requiredVersion.length < currentVersion.length){
            return -1;
        }

        for(String v:requiredVersion) {

            if (requiredVersionLength > 0 && currentVersionLength > 0 && v.compareTo(currentVersion[counter]) > 0) {
                return 1;
            } else {
                if (requiredVersionLength > 0 && currentVersionLength > 0 && v.compareTo(currentVersion[counter]) < 0) {
                    return -1;
                } else {
                    currentVersionLength--;
                    requiredVersionLength--;
                    counter++;
                }

            }
        }

        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AncomOffer {\n");

        sb.append("  version: ").append(version).append("\n");
        sb.append("  isRequired: ").append(isRequired).append("\n");
        sb.append("  storeURL: ").append(storeURL).append("\n");
        sb.append("  title: ").append(title).append("\n");
        sb.append("  summary: ").append(summary).append("\n");
        sb.append("  primaryButtonLabel: ").append(primaryButtonLabel).append("\n");
        sb.append("  secondaryButtonLabel: ").append(secondaryButtonLabel).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
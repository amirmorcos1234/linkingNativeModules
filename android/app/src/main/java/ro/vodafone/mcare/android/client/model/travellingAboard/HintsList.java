
package ro.vodafone.mcare.android.client.model.travellingAboard;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class HintsList extends RealmObject {

    @SerializedName("summary")
    private String summary;
    @SerializedName("webviewURL")
    private String webviewURL;
    @SerializedName("details")
    private String details;

    /**
     * No args constructor for use in serialization
     * 
     */
    public HintsList() {
    }

    /**
     * 
     * @param summary
     * @param details
     * @param webviewURL
     */
    public HintsList(String summary, String webviewURL, String details) {
        super();
        this.summary = summary;
        this.webviewURL = webviewURL;
        this.details = details;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getWebviewURL() {
        return webviewURL;
    }

    public void setWebviewURL(String webviewURL) {
        this.webviewURL = webviewURL;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

}

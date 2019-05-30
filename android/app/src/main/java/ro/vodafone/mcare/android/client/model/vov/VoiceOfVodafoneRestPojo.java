package ro.vodafone.mcare.android.client.model.vov;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Victor Radulescu on 2/2/2018.
 */
public class VoiceOfVodafoneRestPojo {

    @SerializedName("priority")
    @Expose
    private int priority;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("leftButton")
    @Expose
    private VovLeftButtonAction leftButton;
    @SerializedName("rightButton")
    @Expose
    private VovRightButtonAction rightButton;
    @SerializedName("layout")
    @Expose
    private String layout;
    @SerializedName("loginType")
    @Expose
    private List<String> loginType = null;
    @SerializedName("userRole")
    @Expose
    private List<String> userRole = null;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public VovLeftButtonAction getVovLeftButtonAction() {
        return leftButton;
    }

    public void setVoVLeftButtonAction(VovLeftButtonAction leftButton) {
        this.leftButton = leftButton;
    }

    public VovRightButtonAction getVovRightButtonAction() {
        return rightButton;
    }

    public void setVovRightButtonAction(VovRightButtonAction rightButton) {
        this.rightButton = rightButton;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public List<String> getLoginType() {
        return loginType;
    }

    public void setLoginType(List<String> loginType) {
        this.loginType = loginType;
    }

    public List<String> getUserRole() {
        return userRole;
    }

    public void setUserRole(List<String> userRole) {
        this.userRole = userRole;
    }

}

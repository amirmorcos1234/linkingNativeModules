package ro.vodafone.mcare.android.client.model.vov;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Victor Radulescu on 2/2/2018.
 */
public class VovRightButtonAction {

@SerializedName("enabled")
@Expose
private boolean enabled;
@SerializedName("title")
@Expose
private String title;
@SerializedName("action")
@Expose
private String action;
@SerializedName("actionParameter")
@Expose
private String actionParameter;

public boolean isEnabled() {
return enabled;
}

public void setEnabled(boolean enabled) {
this.enabled = enabled;
}

public String getTitle() {
return title;
}

public void setTitle(String title) {
this.title = title;
}

public String getAction() {
return action;
}

public void setAction(String action) {
this.action = action;
}

public String getActionParameter() {
return actionParameter;
}

public void setActionParameter(String actionParameter) {
this.actionParameter = actionParameter;
}

}

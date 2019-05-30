package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alex on 1/19/2017.
 */

public class AccountActivation extends BaseRequest {


    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("activationCode")
    private String activationCode;

    @SerializedName("password")
    private String password;

    @SerializedName("confirmPassword")
    private String confirmPassword;


    public AccountActivation(String username, String email, String activationCode, String password, String confirmPassword) {
        this.setUsername(username);
        this.setEmail(email);
        this.setActivationCode(activationCode);
        this.setPassword(password);
        this.setConfirmPassword(confirmPassword);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}

package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bivol Pavel on 18.01.2017.
 */
public class CompleteProfileRequest extends BaseRequest{

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("confirmPassword")
    private String confirmPassword;

    @SerializedName("acceptTerm")
    private boolean acceptTerm;

    public CompleteProfileRequest(String email, String password, String confirmPassword, boolean acceptTerm) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.acceptTerm = acceptTerm;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isAcceptTerm() {
        return acceptTerm;
    }

    public void setAcceptTerm(boolean acceptTerm) {
        this.acceptTerm = acceptTerm;
    }
}

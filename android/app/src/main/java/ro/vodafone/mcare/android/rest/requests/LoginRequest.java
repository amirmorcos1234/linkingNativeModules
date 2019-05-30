package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Victor Radulescu on 1/12/2017.
 */

public class LoginRequest extends BaseRequest {


    @SerializedName("username")
    private String username ;

    @SerializedName("password")
    private String password ;

//{"requesterId":"mcare","requesterPassword":"mcare","username":"alinrescorp","password":"12345678aA"}


    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

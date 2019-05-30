package ro.vodafone.mcare.android.ui.activities.support;

import ro.vodafone.mcare.android.DontObfuscate;

/**
 * Created by bogdan marica on 4/8/2017.
 */
@DontObfuscate
public class StartChatRequest{

    private String firstName;
    private String lastName;
    private String email;
    private String categoryName;
    private String categoryCode;

    public StartChatRequest() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }


}

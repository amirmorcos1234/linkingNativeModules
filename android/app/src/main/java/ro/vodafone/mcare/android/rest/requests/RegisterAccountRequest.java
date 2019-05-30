package ro.vodafone.mcare.android.rest.requests;

import com.google.gson.annotations.SerializedName;



/**
 * Created by Alex on 1/18/2017.
 */

public class RegisterAccountRequest extends BaseRequest {


    @SerializedName("username")
    private String username;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("email")
    private String email;

    @SerializedName("contactPhone")
    private String contactPhone;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("acceptTerm")
    private boolean acceptTerm;

    @SerializedName("newsletter")
    private boolean newsletters;

    @SerializedName("customerType")
    private String customerType;

    @SerializedName("salutation")
    private String salutation;



//{"requesterId":"mcare","requesterPassword":"mcare","username":"alinrescorp","password":"12345678aA"}


    public RegisterAccountRequest(String phoneNumber, String customerType, String username, String firstName, String lastName, String email,
                                  String contactPhone, boolean acceptTerm, boolean newsletters, String salutation) {
        this.setPhoneNumber(phoneNumber);
        this.setUsername(username);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setContactPhone(contactPhone);
        this.setAcceptTerm(acceptTerm);
        this.setNewsletters(newsletters);
        this.setCustomerType(customerType);
        this.setSalutation(salutation);
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public boolean isAcceptTerm() {
        return acceptTerm;
    }

    public void setAcceptTerm(boolean acceptTerm) {
        this.acceptTerm = acceptTerm;
    }

    public boolean isNewsletters() {
        return newsletters;
    }

    public void setNewsletters(boolean newsletters) {
        this.newsletters = newsletters;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String isCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }
}

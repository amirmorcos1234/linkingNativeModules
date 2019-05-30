package ro.vodafone.mcare.android.client.model.realm;


import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import ro.vodafone.mcare.android.client.model.UserRole;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.preferences.CredentialUtils;

/**
 * Models a user profile of the application.
 * <p>
 * Todo serialize all fields
 */
public class UserProfile extends RealmObject implements Serializable {

    public static final String SSO_TOKEN_ID = "ssoTokenId";
    public static final String SESSION_EXPIRATION_DATE = "sessionExpirationDate";
    public static final String USER_NAME = "userName";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "email";
    public static final String IDENTITY_NAME = "identityName";
    public static final String MSISDN = "msisdn";
    public static final String PASSWORD = "password";
    public static final String SID = "sid";
    public static final String CID = "cid";
    public static final String SSO_ID = "ssoId";
    public static final String USER_ROLE = "userRole";
    public static final String CUSTOMER_TYPE = "customerType";
    public static final String IS_MIGRATED = "isMigrated";
    public static final String CONTACT_ID = "contactId";
    public static final String USER_STATUS = "userStatus";
    public static final String IS_SEAMLESS_LOGGED_IN = "isSeamlessLoggedIn";
    public static final String IS_KEEP_ME_LOGGED_IN = "isKeepMeLoggedIn";
    public static final String SELECTED_MSISDN_NUMBER = "selectedMsisdnNumber";
    public static final String SELECTED_BAN_NUMBER = "selectedBanNumber";
    @Ignore
    transient boolean isSsoTokenEncoded = false;
    @PrimaryKey
    private long id_userProfile;
    @SerializedName("ssoTokenId")
    @Expose
    private String ssoTokenId;
    @SerializedName("sessionExpirationDate")
    private int sessionExpirationDate;
    @SerializedName("userName")
    private String userName;
    private String password;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("identityName")
    private String identityName;
    @SerializedName("msisdn")
    private String msisdn;
    @SerializedName("sid")
    private String sid;
    @SerializedName("cid")
    private String cid;
    @SerializedName("ssoId")
    private String ssoId;
    @SerializedName("userRole")
    @Expose
    private String userRoleString;
    @Ignore
    private transient UserRole userRole;
    @SerializedName("customerType")
    private String customerType;
    @SerializedName("isMigrated")
    private boolean isMigrated;
    @SerializedName("contactId")
    private String contactId;
    @SerializedName("subscriberType")
    private String subscriberType;
    @SerializedName("vfActivationToken")
    private String vfActivationToken;
    @SerializedName("vfIsEmailValidated")
    private boolean vfIsEmailValidated;
    @SerializedName("vfIsUsernameValidated")
    private boolean vfIsUsernameValidated;
    @SerializedName("vfFixedBan")
    private String vfFixedBan;
    @SerializedName("vfFixedCid")
    private String vfFixedCid;
    @SerializedName("vfIsPhoneValidated")
    private boolean vfIsPhoneValidated;

    private String selectedMsisdnNumber;
    private String selectedBanNumber;
    /**
     * Used for redirecting to different pages ( activities, fragment) and other unusual scenarios.
     * “pending”, “inconsistent” “active”“pending”, “inconsistent” and “active”
     */
    @SerializedName("userStatus")
    private String userStatus;

    public UserProfile() {
        id_userProfile = 1;
    }

    public long getId_userProfile() {
        return id_userProfile;
    }

    public UserRole getUserRole() {
        if(userRole!=null){
            return userRole;
        }
        return User.roleFromProfile(this);
    }

    public void setUserRole(UserRole userRole) {
        RealmManager.startTransaction();
        this.userRole = userRole;
        RealmManager.commitTransaction();
    }

    public int getSessionExpirationDate() {
        return sessionExpirationDate;
    }

    public void setSessionExpirationDate(int sessionExpirationDate) {
        this.sessionExpirationDate = sessionExpirationDate;
    }

    public String getUserName() {
        return userName!=null? userName : CredentialUtils.getUsername();
    }

    public void setUserName(String userName) {
        this.userName = userName;
        CredentialUtils.saveUsername(userName);
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

    public String getIdentityName() {
        return identityName;
    }

    public void setIdentityName(String identityName) {
        this.identityName = identityName;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getSsoId() {
        return ssoId;
    }

    public void setSsoId(String ssoId) {
        this.ssoId = ssoId;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public boolean isMigrated() {
        return isMigrated;
    }

    public void setMigrated(boolean migrated) {
        isMigrated = migrated;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getVfActivationToken() {
        return vfActivationToken;
    }

    public void setVfActivationToken(String vfActivationToken) {
        this.vfActivationToken = vfActivationToken;
    }

    public boolean isVfIsEmailValidated() {
        return vfIsEmailValidated;
    }

    public void setVfIsEmailValidated(boolean vfIsEmailValidated) {
        this.vfIsEmailValidated = vfIsEmailValidated;
    }

    public Boolean getVfIsUsernameValidated() {
        return vfIsUsernameValidated;
    }

    public void setVfIsUsernameValidated(Boolean vfIsUsernameValidated) {
        this.vfIsUsernameValidated = vfIsUsernameValidated;
    }

    public String getVfFixedBan() {
        return vfFixedBan;
    }

    public void setVfFixedBan(String vfFixedBan) {
        this.vfFixedBan = vfFixedBan;
    }

    public String isVfFixedCid() {
        return vfFixedCid;
    }

    public void setVfFixedCid(String vfFixedCid) {
        this.vfFixedCid = vfFixedCid;
    }

    public Boolean getVfIsPhoneValidated() {
        return vfIsPhoneValidated;
    }

    public void setVfIsPhoneValidated(Boolean vfIsPhoneValidated) {
        this.vfIsPhoneValidated = vfIsPhoneValidated;
    }

    public String getPassword() {
        return password !=null? password:CredentialUtils.getPassword();
    }

    public void setPassword(String password) {
        RealmManager.startTransaction();
//        D.w("password = "+password);
        this.password = password;
        RealmManager.update(this);
        CredentialUtils.savePassword(password);

    }

    public String getUserRoleString() {
        return userRoleString;
    }

    public void setUserRoleString(String userRoleString) {
        this.userRoleString = userRoleString;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getSubscriberType() {
        return subscriberType;
    }

    public void setSubscriberType(String subscriberType) {
        this.subscriberType = subscriberType;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    private String decodeSsoToken(String encodedSSOToken) throws UnsupportedEncodingException {
        return java.net.URLDecoder.decode(ssoTokenId, "UTF-8");
    }

    public String getSsoTokenId() {
        return ssoTokenId;
       /* String decodedId="";
        RealmManager.startTransaction();
            if(this.ssoTokenId != null){
                try {
                    decodedId = decodeSsoToken(ssoTokenId);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        RealmManager.commitTransaction();
        return decodedId;*/
    }

    public void setSsoTokenId(String ssoTokenId) {
        this.ssoTokenId = ssoTokenId;
    }

    public String getSelectedMsisdnNumber() {
        return selectedMsisdnNumber;
    }

    public void setSelectedMsisdnNumber(String selectedMsisdnNumber) {
        RealmManager.startTransaction();
        this.selectedMsisdnNumber = selectedMsisdnNumber;
        RealmManager.update(this);
    }

    public String getSelectedBanNumber() {
        return selectedBanNumber;
    }

    public void setSelectedBanNumber(String selectedBanNumber) {
        RealmManager.startTransaction();
        this.selectedBanNumber = selectedBanNumber;
        RealmManager.update(this);
    }
}

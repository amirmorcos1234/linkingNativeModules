package ro.vodafone.mcare.android.client.model.gdpr;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import ro.vodafone.mcare.android.ui.fragments.settings.PermissionsFragment;

/**
 * Created by cosmin deliu on 2/6/2018.
 */

public class GdprPermissions extends RealmObject {

    public static final String MINOR = "minor";
    public static final String GUARDIAN = "guardian";
    public static final String CHECKED = "yes";
    public static final String UNCHECKED = "no";
    public static final String SPECIFY = "specify";

    @SerializedName("vfSid")
    protected String vfSid;

    @SerializedName("channel")
    protected String channel;

    @SerializedName("sessionId")
    protected String sessionId;

    @SerializedName("vfEmail")
    protected String vfEmail;

    @SerializedName("vfPost")
    protected String vfPost;

    @SerializedName("vfSmsMmsPush")
    protected String vfSmsMmsPush;

    @SerializedName("vfOutboundCall")
    protected String vfOutboundCall;

    @SerializedName("vfBasicProfileCustServiceData")
    protected String vfBasicProfileCustServiceData;

    @SerializedName("vfAdvancedProfileNetworkData")
    protected String vfAdvancedProfileNetworkData;

    @SerializedName("vfAdvancedProfileOnlineData")
    protected String vfAdvancedProfileOnlineData;

    @SerializedName("vfSurveyCategory")
    protected String vfSurveyCategory;

    @SerializedName("extEmail")
    protected String extEmail;

    @SerializedName("extPost")
    protected String extPost;

    @SerializedName("extSmsMmsPush")
    protected String extSmsMmsPush;

    @SerializedName("extOutboundCall")
    protected String extOutboundCall;

    @SerializedName("extBasicProfileCustServiceData")
    protected String extBasicProfileCustServiceData;

    @SerializedName("extAdvancedProfileNetworkData")
    protected String extAdvancedProfileNetworkData;

    @SerializedName("extAdvancedProfileOnlineData")
    protected String extAdvancedProfileOnlineData;

    @SerializedName("extSurveyCategory")
    protected String extSurveyCategory;

    @SerializedName("minorStatus")
    protected String minorStatus;

    @SerializedName("minorBirthDate")
    protected String minorBirthDate;

    @SerializedName("guardianContactPhone")
    protected String guardianContactPhone;

    @SerializedName("guardianEmailAddress")
    protected String guardianEmailAddress;

    public String getVfSid() {
        return vfSid;
    }
    public void setVfSid(String vfSid) {
        this.vfSid = vfSid;
    }

    public String getChannel() {
        return channel;
    }
    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getVfEmail() {
        return vfEmail;
    }
    public void setVfEmail(String vfEmail) {
        this.vfEmail = vfEmail;
    }

    public String getVfPost() {
        return vfPost;
    }
    public void setVfPost(String vfPost) {
        this.vfPost = vfPost;
    }

    public String getVfSmsMmsPush() {
        return vfSmsMmsPush;
    }
    public void setVfSmsMmsPush(String vfSmsMmsPush) {
        this.vfSmsMmsPush = vfSmsMmsPush;
    }

    public String getVfOutboundCall() {
        return vfOutboundCall;
    }
    public void setVfOutboundCall(String vfOutboundCall) {
        this.vfOutboundCall = vfOutboundCall;
    }

    public String getVfBasicProfileCustServiceData() {
        return vfBasicProfileCustServiceData;
    }
    public void setVfBasicProfileCustServiceData(String vfBasicProfileCustServiceData) {
        this.vfBasicProfileCustServiceData = vfBasicProfileCustServiceData;
    }

    public String getVfAdvancedProfileNetworkData() {
        return vfAdvancedProfileNetworkData;
    }
    public void setVfAdvancedProfileNetworkData(String vfAdvancedProfileNetworkData) {
        this.vfAdvancedProfileNetworkData = vfAdvancedProfileNetworkData;
    }

    public String getVfAdvancedProfileOnlineData() {
        return vfAdvancedProfileOnlineData;
    }
    public void setVfAdvancedProfileOnlineData(String vfAdvancedProfileOnlineData) {
        this.vfAdvancedProfileOnlineData = vfAdvancedProfileOnlineData;
    }

    public String getVfSurveyCategory() {
        return vfSurveyCategory;
    }
    public void setVfSurveyCategory(String vfSurveyCategory) {
        this.vfSurveyCategory = vfSurveyCategory;
    }

    public String getExtEmail() {
        return extEmail;
    }
    public void setExtEmail(String extEmail) {
        this.extEmail = extEmail;
    }

    public String getExtPost() {
        return extPost;
    }
    public void setExtPost(String extPost) {
        this.extPost = extPost;
    }

    public String getExtSmsMmsPush() {
        return extSmsMmsPush;
    }
    public void setExtSmsMmsPush(String extSmsMmsPush) {
        this.extSmsMmsPush = extSmsMmsPush;
    }

    public String getExtOutboundCall() {
        return extOutboundCall;
    }
    public void setExtOutboundCall(String extOutboundCall) {
        this.extOutboundCall = extOutboundCall;
    }

    public String getExtBasicProfileCustServiceData() {
        return extBasicProfileCustServiceData;
    }
    public void setExtBasicProfileCustServiceData(String extBasicProfileCustServiceData) {
        this.extBasicProfileCustServiceData = extBasicProfileCustServiceData;
    }

    public String getExtAdvancedProfileNetworkData() {
        return extAdvancedProfileNetworkData;
    }
    public void setExtAdvancedProfileNetworkData(String extAdvancedProfileNetworkData) {
        this.extAdvancedProfileNetworkData = extAdvancedProfileNetworkData;
    }

    public String getExtAdvancedProfileOnlineData() {
        return extAdvancedProfileOnlineData;
    }
    public void setExtAdvancedProfileOnlineData(String extAdvancedProfileOnlineData) {
        this.extAdvancedProfileOnlineData = extAdvancedProfileOnlineData;
    }

    public String getExtSurveyCategory() {
        return extSurveyCategory;
    }
    public void setExtSurveyCategory(String extSurveyCategory) {
        this.extSurveyCategory = extSurveyCategory;
    }

    public String getMinorStatus() {
        return minorStatus;
    }
    public void setMinorStatus(String minorStatus) {
        this.minorStatus = minorStatus;
    }

    public boolean isMinor() {
        return minorStatus != null && minorStatus.equalsIgnoreCase(MINOR);
    }

    public String getMinorBirthDate() {
        return minorBirthDate;
    }
    public void setMinorBirthDate(String minorBirthDate) {
        this.minorBirthDate = minorBirthDate;
    }

    public String getGuardianContactPhone() {
        return guardianContactPhone;
    }
    public void setGuardianContactPhone(String guardianContactPhone) {
        this.guardianContactPhone = guardianContactPhone;
    }

    public String getGuardianEmailAddress() {
        return guardianEmailAddress;
    }
    public void setGuardianEmailAddress(String guardianEmailAddress) {
        this.guardianEmailAddress = guardianEmailAddress;
    }

    public void setToSendMinorAccount(String permission) {
        minorStatus = permission;
        guardianEmailAddress = permission;
        guardianContactPhone = permission;
        minorBirthDate = permission;
    }

    public void setToSendVodafonePermissions(String permission) {
        extEmail = permission;
        extPost = permission;
        extSmsMmsPush = permission;
        extOutboundCall = permission;
        extBasicProfileCustServiceData = permission;
        extAdvancedProfileNetworkData = permission;
        extAdvancedProfileOnlineData = permission;
        extSurveyCategory = permission;
    }

    public void setToSendVodafonePartenersPermissions(String permission) {
        vfEmail = permission;
        vfPost = permission;
        vfSmsMmsPush = permission;
        vfOutboundCall = permission;
        vfBasicProfileCustServiceData = permission;
        vfAdvancedProfileNetworkData = permission;
        vfAdvancedProfileOnlineData = permission;
        vfSurveyCategory = permission;
    }

    public void setToSendMinorAccountSave() {
        setToSendVodafonePermissions(null);
        setToSendVodafonePartenersPermissions(null);
    }

    public void setToSendMinorAccountActivate() {
        setToSendVodafonePermissions("NO");
        setToSendVodafonePartenersPermissions("NO");
    }

    public void setToSendMinorAccountDeactivate() {
        guardianEmailAddress = null;
        guardianContactPhone = null;
        minorBirthDate = null;
        setToSendVodafonePermissions(null);
        setToSendVodafonePartenersPermissions(null);
    }

    public boolean checkIfSpecify(String type, boolean disabledCommercial, boolean disabledSurvey,
                                  boolean disabledCreateProfile,
                                  boolean disabledOnlineData, boolean disabledNetworkData) {
        switch (type) {
            case PermissionsFragment.VODAFONE_PERMISSIONS_CARD:
                return checkIfSpecifyForCommercialVdfPermissions(disabledCommercial) ||
                        checkIfSpecifyForCreateProfileVdfPermissions(disabledCreateProfile) ||
                        checkIfSpecifyForOnlineDataVdfPermissions(disabledOnlineData) ||
                        checkIfSpecifyForNetworkDataVdfPermissions(disabledNetworkData) ||
                        checkIfSpecifyForSurveyVdfPermissions(disabledSurvey);
            case PermissionsFragment.VODAFONE_PARTENERS_CARD:
                return checkIfSpecifyForCommercialParteners(disabledCommercial) ||
                        checkIfSpecifyForCreateProfileParteners(disabledCreateProfile) ||
                        checkIfSpecifyForOnlineDataParteners(disabledOnlineData) ||
                        checkIfSpecifyForNetworkDataParteners(disabledNetworkData) ||
                        checkIfSpecifyForSurveyParteners(disabledSurvey);
            default:
                return false;
        }
    }

    protected boolean checkIfSpecifyForCommercialVdfPermissions(boolean disabledCommercial) {
        return !disabledCommercial && ((this.vfEmail == null || this.vfEmail.equalsIgnoreCase("specify")) ||
                (this.vfPost == null || this.vfPost.equalsIgnoreCase("specify")) ||
                (this.vfSmsMmsPush == null || this.vfSmsMmsPush.equalsIgnoreCase("specify")) ||
                (this.vfOutboundCall == null || this.vfOutboundCall.equalsIgnoreCase("specify")));
    }

    protected boolean checkIfSpecifyForCommercialParteners(boolean disabledCommercial) {
        return !disabledCommercial && ((this.extEmail == null || this.extEmail.equalsIgnoreCase("specify")) ||
                (this.extPost == null || this.extPost.equalsIgnoreCase("specify")) ||
                (this.extSmsMmsPush == null || this.extSmsMmsPush.equalsIgnoreCase("specify")) ||
                (this.extOutboundCall == null || this.extOutboundCall.equalsIgnoreCase("specify")));
    }

    protected boolean checkIfSpecifyForCreateProfileVdfPermissions(boolean disabledCreateProfile) {
        return !disabledCreateProfile && (this.vfBasicProfileCustServiceData == null || this.vfBasicProfileCustServiceData.equalsIgnoreCase("specify"));
    }

    protected boolean checkIfSpecifyForCreateProfileParteners(boolean disabledCreateProfile) {
        return !disabledCreateProfile && (this.extBasicProfileCustServiceData == null || this.extBasicProfileCustServiceData.equalsIgnoreCase("specify"));
    }

    protected boolean checkIfSpecifyForOnlineDataVdfPermissions(boolean disabledOnlineData) {
        return !disabledOnlineData && (this.vfAdvancedProfileOnlineData == null || this.vfAdvancedProfileOnlineData.equalsIgnoreCase("specify"));
    }

    protected boolean checkIfSpecifyForOnlineDataParteners(boolean disabledOnlineData) {
        return !disabledOnlineData && (this.extAdvancedProfileOnlineData == null || this.extAdvancedProfileOnlineData.equalsIgnoreCase("specify"));
    }

    protected boolean checkIfSpecifyForNetworkDataVdfPermissions(boolean disabledNetworkData) {
        return !disabledNetworkData && (this.vfAdvancedProfileNetworkData == null || this.vfAdvancedProfileNetworkData.equalsIgnoreCase("specify"));
    }

    protected boolean checkIfSpecifyForNetworkDataParteners(boolean disabledNetworkData) {
        return !disabledNetworkData && (this.extAdvancedProfileNetworkData == null || this.extAdvancedProfileNetworkData.equalsIgnoreCase("specify"));
    }

    protected boolean checkIfSpecifyForSurveyVdfPermissions(boolean disabledSurvey) {
        return !disabledSurvey && ((this.vfSurveyCategory == null || this.vfSurveyCategory.equalsIgnoreCase("specify")));
    }

    protected boolean checkIfSpecifyForSurveyParteners(boolean disabledSurvey) {
        return !disabledSurvey && (this.extSurveyCategory == null || this.extSurveyCategory.equalsIgnoreCase("specify"));
    }

    public static GdprPermissions copyObject(GdprPermissions gdprPermissions) {
        GdprPermissions newGdprPermissions = new GdprPermissions();

        newGdprPermissions.vfSid = gdprPermissions.getVfSid();
        newGdprPermissions.channel = gdprPermissions.getChannel();
        newGdprPermissions.vfEmail = gdprPermissions.getVfEmail();
        newGdprPermissions.vfPost = gdprPermissions.getVfPost();
        newGdprPermissions.vfSmsMmsPush = gdprPermissions.getVfSmsMmsPush();
        newGdprPermissions.vfOutboundCall = gdprPermissions.getVfOutboundCall();
        newGdprPermissions.vfBasicProfileCustServiceData = gdprPermissions.getVfBasicProfileCustServiceData();
        newGdprPermissions.vfAdvancedProfileNetworkData = gdprPermissions.getVfAdvancedProfileNetworkData();
        newGdprPermissions.vfAdvancedProfileOnlineData = gdprPermissions.getVfAdvancedProfileOnlineData();
        newGdprPermissions.vfSurveyCategory = gdprPermissions.getVfSurveyCategory();
        newGdprPermissions.extEmail = gdprPermissions.getExtEmail();
        newGdprPermissions.extPost = gdprPermissions.getExtPost();
        newGdprPermissions.extSmsMmsPush = gdprPermissions.getExtSmsMmsPush();
        newGdprPermissions.extOutboundCall = gdprPermissions.getExtOutboundCall();
        newGdprPermissions.extBasicProfileCustServiceData = gdprPermissions.getExtBasicProfileCustServiceData();
        newGdprPermissions.extAdvancedProfileNetworkData = gdprPermissions.getExtAdvancedProfileNetworkData();
        newGdprPermissions.extAdvancedProfileOnlineData = gdprPermissions.getExtAdvancedProfileOnlineData();
        newGdprPermissions.extSurveyCategory = gdprPermissions.getExtSurveyCategory();
        newGdprPermissions.minorStatus = gdprPermissions.getMinorStatus();
        newGdprPermissions.minorBirthDate = gdprPermissions.getMinorBirthDate();
        newGdprPermissions.guardianContactPhone = gdprPermissions.getGuardianContactPhone();
        newGdprPermissions.guardianEmailAddress = gdprPermissions.getGuardianEmailAddress();

        return newGdprPermissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        GdprPermissions gdprPermissions = (GdprPermissions) o;

        return (this.vfSid == null ? gdprPermissions.vfSid == null : this.vfSid.equals(gdprPermissions.vfSid)) &&
                (this.channel == null ? gdprPermissions.channel == null : this.channel.equals(gdprPermissions.channel)) &&
                (this.vfEmail == null ? gdprPermissions.vfEmail == null : this.vfEmail.equals(gdprPermissions.vfEmail)) &&
                (this.vfPost == null ? gdprPermissions.vfPost == null : this.vfPost.equals(gdprPermissions.vfPost)) &&
                (this.vfSmsMmsPush == null ? gdprPermissions.vfSmsMmsPush == null : this.vfSmsMmsPush.equals(gdprPermissions.vfSmsMmsPush)) &&
                (this.vfOutboundCall == null ? gdprPermissions.vfOutboundCall == null : this.vfOutboundCall.equals(gdprPermissions.vfOutboundCall)) &&
                (this.vfBasicProfileCustServiceData == null ? gdprPermissions.vfBasicProfileCustServiceData == null : this.vfBasicProfileCustServiceData.equals(gdprPermissions.vfBasicProfileCustServiceData)) &&
                (this.vfAdvancedProfileOnlineData == null ? gdprPermissions.vfAdvancedProfileOnlineData == null : this.vfAdvancedProfileOnlineData.equals(gdprPermissions.vfAdvancedProfileOnlineData)) &&
                (this.vfAdvancedProfileNetworkData == null ? gdprPermissions.vfAdvancedProfileNetworkData == null : this.vfAdvancedProfileNetworkData.equals(gdprPermissions.vfAdvancedProfileNetworkData)) &&
                (this.vfSurveyCategory == null ? gdprPermissions.vfSurveyCategory == null : this.vfSurveyCategory.equals(gdprPermissions.vfSurveyCategory)) &&
                (this.extEmail == null ? gdprPermissions.extEmail == null : this.extEmail.equals(gdprPermissions.extEmail)) &&
                (this.extPost == null ? gdprPermissions.extPost == null : this.extPost.equals(gdprPermissions.extPost)) &&
                (this.extSmsMmsPush == null ? gdprPermissions.extSmsMmsPush == null : this.extSmsMmsPush.equals(gdprPermissions.extSmsMmsPush)) &&
                (this.extOutboundCall == null ? gdprPermissions.extOutboundCall == null : this.extOutboundCall.equals(gdprPermissions.extOutboundCall)) &&
                (this.extBasicProfileCustServiceData == null ? gdprPermissions.extBasicProfileCustServiceData == null : this.extBasicProfileCustServiceData.equals(gdprPermissions.extBasicProfileCustServiceData)) &&
                (this.extAdvancedProfileNetworkData == null ? gdprPermissions.extAdvancedProfileNetworkData == null : this.extAdvancedProfileNetworkData.equals(gdprPermissions.extAdvancedProfileNetworkData)) &&
                (this.extAdvancedProfileOnlineData == null ? gdprPermissions.extAdvancedProfileOnlineData == null : this.extAdvancedProfileOnlineData.equals(gdprPermissions.extAdvancedProfileOnlineData)) &&
                (this.extSurveyCategory == null ? gdprPermissions.extSurveyCategory == null : this.extSurveyCategory.equals(gdprPermissions.extSurveyCategory)) &&
                (this.minorStatus == null ? gdprPermissions.minorStatus == null : this.minorStatus.equals(gdprPermissions.minorStatus)) &&
                (this.minorBirthDate == null ? gdprPermissions.minorBirthDate == null : this.minorBirthDate.equals(gdprPermissions.minorBirthDate)) &&
                (this.guardianContactPhone == null ? gdprPermissions.guardianContactPhone == null : this.guardianContactPhone.equals(gdprPermissions.guardianContactPhone)) &&
                (this.guardianEmailAddress == null ? gdprPermissions.guardianEmailAddress == null : this.guardianEmailAddress.equals(gdprPermissions.guardianEmailAddress));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.vfSid == null ? 0: this.vfSid.hashCode());
        result = 31 * result + (this.channel == null ? 0: this.channel.hashCode());
        result = 31 * result + (this.vfEmail == null ? 0: this.vfEmail.hashCode());
        result = 31 * result + (this.vfPost == null ? 0: this.vfPost.hashCode());
        result = 31 * result + (this.vfSmsMmsPush == null ? 0: this.vfSmsMmsPush.hashCode());
        result = 31 * result + (this.vfOutboundCall == null ? 0: this.vfOutboundCall.hashCode());
        result = 31 * result + (this.vfBasicProfileCustServiceData == null ? 0: this.vfBasicProfileCustServiceData.hashCode());
        result = 31 * result + (this.vfAdvancedProfileNetworkData == null ? 0: this.vfAdvancedProfileNetworkData.hashCode());
        result = 31 * result + (this.vfAdvancedProfileOnlineData == null ? 0: this.vfAdvancedProfileOnlineData.hashCode());
        result = 31 * result + (this.vfSurveyCategory == null ? 0: this.vfSurveyCategory.hashCode());
        result = 31 * result + (this.extEmail == null ? 0: this.extEmail.hashCode());
        result = 31 * result + (this.extPost == null ? 0: this.extPost.hashCode());
        result = 31 * result + (this.extSmsMmsPush == null ? 0: this.extSmsMmsPush.hashCode());
        result = 31 * result + (this.extOutboundCall == null ? 0: this.extOutboundCall.hashCode());
        result = 31 * result + (this.extBasicProfileCustServiceData == null ? 0: this.extBasicProfileCustServiceData.hashCode());
        result = 31 * result + (this.extAdvancedProfileNetworkData == null ? 0: this.extAdvancedProfileNetworkData.hashCode());
        result = 31 * result + (this.extAdvancedProfileOnlineData == null ? 0: this.extAdvancedProfileOnlineData.hashCode());
        result = 31 * result + (this.extSurveyCategory == null ? 0: this.extSurveyCategory.hashCode());
        result = 31 * result + (this.minorStatus == null ? 0: this.minorStatus.hashCode());
        result = 31 * result + (this.minorBirthDate == null ? 0: this.minorBirthDate.hashCode());
        result = 31 * result + (this.guardianContactPhone == null ? 0: this.guardianContactPhone.hashCode());
        result = 31 * result + (this.guardianEmailAddress == null ? 0: this.guardianEmailAddress.hashCode());
        return result;
    }

    @Override
    public String toString()  {
        StringBuilder sb = new StringBuilder();
        sb.append("class GdprPermissions {\n");

        sb.append("  vfSid: ").append(vfSid).append("\n");
        sb.append("  channel: ").append(channel).append("\n");
        sb.append("  vfEmail: ").append(vfEmail).append("\n");
        sb.append("  vfPost: ").append(vfPost).append("\n");
        sb.append("  vfSmsMmsPush: ").append(vfSmsMmsPush).append("\n");
        sb.append("  vfOutboundCall: ").append(vfOutboundCall).append("\n");
        sb.append("  vfBasicProfileCustServiceData: ").append(vfBasicProfileCustServiceData).append("\n");
        sb.append("  vfAdvancedProfileNetworkData: ").append(vfAdvancedProfileNetworkData).append("\n");
        sb.append("  vfAdvancedProfileOnlineData: ").append(vfAdvancedProfileOnlineData).append("\n");
        sb.append("  vfSurveyCategory: ").append(vfSurveyCategory).append("\n");
        sb.append("  extEmail: ").append(extEmail).append("\n");
        sb.append("  extPost: ").append(extPost).append("\n");
        sb.append("  extSmsMmsPush: ").append(extSmsMmsPush).append("\n");
        sb.append("  extOutboundCall: ").append(extOutboundCall).append("\n");
        sb.append("  extBasicProfileCustServiceData: ").append(extBasicProfileCustServiceData).append("\n");
        sb.append("  extAdvancedProfileNetworkData: ").append(extAdvancedProfileNetworkData).append("\n");
        sb.append("  extAdvancedProfileOnlineData: ").append(extAdvancedProfileOnlineData).append("\n");
        sb.append("  extSurveyCategory: ").append(extSurveyCategory).append("\n");
        sb.append("  minorStatus: ").append(minorStatus).append("\n");
        sb.append("  minorBirthDate: ").append(minorBirthDate).append("\n");
        sb.append("  guardianContactPhone: ").append(guardianContactPhone).append("\n");
        sb.append("  guardianEmailAddress: ").append(guardianEmailAddress).append("\n");
        sb.append("}\n");
        return sb.toString();
    }

}

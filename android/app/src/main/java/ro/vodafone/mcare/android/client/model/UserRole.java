package ro.vodafone.mcare.android.client.model;

import com.google.gson.annotations.SerializedName;

/**
 * Defines possible values for the SSO user roles.
 * 
 * @author maria.gaspar
 * 
 */
public enum UserRole {

        @SerializedName("PrivateUser")
        PRIVATE_USER("PrivateUser"),
        @SerializedName("PrepaidUser")
        PREPAID("PrepaidUser"),
        @SerializedName("ResCorp")
        RES_CORP("ResCorp"),
        @SerializedName("ResSub")
        RES_SUB("ResSub"),

        @SerializedName("SeamlessHighAccess")
        SEAMLESS_HIGH_ACCESS("SeamlessHighAccess"),
        @SerializedName("SeamlessLowAccess")
        SEAMLESS_LOW_ACCESS("SeamlessLowAccess"),
        @SerializedName("SeamlessPrepaidAccess")
        SEAMLESS_PREPAID_USER("SeamlessPrepaidAccess"),
        @SerializedName("SeamlessHybridAccess")
        SEAMLESS_HYBRID("SeamlessHybridAccess"),
        @SerializedName("SeamlessEbuUser")
        SEAMLESS_EBU_USER("SeamlessEbuUser"),

        @SerializedName("BasicUser")
        BASIC_USER("BasicUser"),
        @SerializedName("EBUMigrated")
        EBU_Migrated("EBUMigrated"),
        @SerializedName("Hybrid")
        HYBRID("Hybrid"),
        @SerializedName("CorpSubUser")
        CORP_SUB_USER("CorpSubUser"),
        @SerializedName("CorpUser")
        Corp_User("CorpUser"),
        @SerializedName("SubUser")
        SUB_USER("SubUser"),

        @SerializedName("nonVFUser")
        NON_VF_USER("nonVFUser"),

        @SerializedName("UnitUser")
        UNIT_USER("UnitUser"),
        @SerializedName("ISPAdmin")
        ISP_ADMIN("ISPAdmin"),
        @SerializedName("CSR")
        CSR("CSR"),
        @SerializedName("Root")
        ROOT("Root"),
        @SerializedName("BizAdmin")
        BIZ_ADMIN("BizAdmin"),

        @SerializedName("PowerUser")
        POWER_USER("PowerUser"),
        @SerializedName("Chooser")
        CHOOSER("Chooser"),
        @SerializedName("DelegatedChooser")
        DELEGATED_CHOOSER("DelegatedChooser"),
        @SerializedName("AuthorisedPerson")
        AUTHORISED_PERSON("AuthorisedPerson"),
       // This is migrated
        @SerializedName("SubUserCRMRole")
        SUB_USER_CRMROLE("SubUserCRMRole");



  /**
   * Initializes the instance with the actual description.
   *
   */
   UserRole(String description) {
    this.description = description;
  }

  private String description;

  /**
   * @return the description of this instance.
   */
  public final String getDescription() {
    return description;
  }

  /*public static UserRole getSSOUserRoleByDescription(String description) {
    for (UserRole userRole : values()) {
      if (userRole.getDescription().equalsIgnoreCase(description)) {
        return userRole;
      }
    }
    return ANONYMOUS;
  }*/
}

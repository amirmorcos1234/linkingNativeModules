package ro.vodafone.mcare.android.client.model.identity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Serban Radulescu on 7/17/2017.
 */

public class EntityChildItem extends RealmObject {

    public static final String ENTITY_TYPE_KEY = "entityType";

    @SerializedName("displayName")
    private String displayName = null;

    @PrimaryKey
    @SerializedName("entityId")
    private String entityId = null;

    @SerializedName("entityType")
    private String entityType = null;

    @SerializedName("crmRole")
    private String crmRole = null;

    //AccountCui
    @SerializedName("vfOdsCui")
    private String vfOdsCui = null;
    @SerializedName("vfOdsSsn")
    private String vfOdsSsn = null;
    @SerializedName("vfOdsSsnType")
    private String vfOdsSsnType = null;

    //SubscriberEntity
    @SerializedName("vfOdsPhoneNumber")
    private String vfOdsPhoneNumber = null;
    @SerializedName("vfOdsSid")
    private String vfOdsSid = null;
    @SerializedName("vfOdsResourceType")
    private String vfOdsResourceType = null;


    //BillingCustomer
    @SerializedName("vfOdsCid")
    private String vfOdsCid = null;
    @SerializedName("treatmentSegment")
    private String treatmentSegment = null;
    @SerializedName("microSegment")
    private String microSegment = null;

    //FinancialAccount
    @SerializedName("vfOdsBan")
    private String vfOdsBan = null;
    @SerializedName("vfOdsBen")
    private String vfOdsBen = null;
    @SerializedName("cuiName")
    private String cuiName = null;

    @SerializedName("childList")
    private RealmList<EntityChildItem> childList = null;

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getCrmRole() {
        return crmRole;
    }

    public void setCrmRole(String crmRole) {
        this.crmRole = crmRole;
    }

    public String getVfOdsCui() {
        return vfOdsCui;
    }

    public void setVfOdsCui(String vfOdsCui) {
        this.vfOdsCui = vfOdsCui;
    }

    public String getVfOdsSsn() {
        return vfOdsSsn;
    }

    public void setVfOdsSsn(String vfOdsSsn) {
        this.vfOdsSsn = vfOdsSsn;
    }

    public String getVfOdsSsnType() {
        return vfOdsSsnType;
    }

    public void setVfOdsSsnType(String vfOdsSsnType) {
        this.vfOdsSsnType = vfOdsSsnType;
    }

    public String getVfOdsPhoneNumber() {
        return vfOdsPhoneNumber;
    }

    public void setVfOdsPhoneNumber(String vfOdsPhoneNumber) {
        this.vfOdsPhoneNumber = vfOdsPhoneNumber;
    }

    public String getVfOdsSid() {
        return vfOdsSid;
    }

    public void setVfOdsSid(String vfOdsSid) {
        this.vfOdsSid = vfOdsSid;
    }

    public String getVfOdsResourceType() {
        return vfOdsResourceType;
    }

    public void setVfOdsResourceType(String vfOdsResourceType) {
        this.vfOdsResourceType = vfOdsResourceType;
    }

    public String getVfOdsCid() {
        return vfOdsCid;
    }

    public void setVfOdsCid(String vfOdsCid) {
        this.vfOdsCid = vfOdsCid;
    }

    public String getTreatmentSegment() {
        return treatmentSegment;
    }

    public void setTreatmentSegment(String treatmentSegment) {
        this.treatmentSegment = treatmentSegment;
    }

    public String getMicroSegment() {
        return microSegment;
    }

    public void setMicroSegment(String microSegment) {
        this.microSegment = microSegment;
    }

    public String getVfOdsBan() {
        return vfOdsBan;
    }

    public void setVfOdsBan(String vfOdsBan) {
        this.vfOdsBan = vfOdsBan;
    }

    public String getVfOdsBen() {
        return vfOdsBen;
    }

    public void setVfOdsBen(String vfOdsBen) {
        this.vfOdsBen = vfOdsBen;
    }

    public String getCuiName() {
        return cuiName;
    }

    public void setCuiName(String cuiName) {
        this.cuiName = cuiName;
    }

    public RealmList<EntityChildItem> getChildList() {
        return childList;
    }

    public void setChildList(RealmList<EntityChildItem> childList) {
        this.childList = childList;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        EntityChildItem entityChildItem = (EntityChildItem) obj;
        return (this.displayName == null ? entityChildItem.displayName == null : this.displayName.equals(entityChildItem.displayName)) &&
                (this.entityId == null ? entityChildItem.entityId == null : this.entityId.equals(entityChildItem.entityId)) &&
                (this.crmRole == null ? entityChildItem.crmRole == null : this.crmRole.equals(entityChildItem.crmRole)) &&
                (this.vfOdsCui == null ? entityChildItem.vfOdsCui == null : this.vfOdsCui.equals(entityChildItem.vfOdsCui)) &&
                (this.vfOdsSsn == null ? entityChildItem.vfOdsSsn == null : this.vfOdsSsn.equals(entityChildItem.vfOdsSsn)) &&
                (this.vfOdsSsnType == null ? entityChildItem.vfOdsSsnType == null : this.vfOdsSsnType.equals(entityChildItem.vfOdsSsnType));
    }
}

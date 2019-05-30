package ro.vodafone.mcare.android.service.tracking.adobe.target;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ro.vodafone.mcare.android.utils.RealmManager;

/**
 * Created by Bivol Pavel on 20.04.2018.
 */

public class AdobeCampaignDetails extends RealmObject{

    public static final String ELEMENT_ID = "elementID";

    @PrimaryKey
    private String elementID;
    private int recurrenceCounter;
    private long recurrenceTTL;

    public AdobeCampaignDetails() {
    }

    public AdobeCampaignDetails(String elementID, int recurrence, long recurrenceTTL) {
        this.elementID = elementID;
        this.recurrenceCounter = recurrence;
        this.recurrenceTTL = recurrenceTTL;
    }

    public String getElementID() {
        return elementID;
    }

    public void setElementID(String elementID) {
        this.elementID = elementID;
    }

    public int getRecurrenceCounter() {
        return recurrenceCounter;
    }

    public void setRecurrenceCounter(int recurrenceCounter) {
        this.recurrenceCounter = recurrenceCounter;
    }

    public void incrementByOneRecurrenceCounter(){
        RealmManager.startTransaction();
        this.recurrenceCounter ++;
        RealmManager.commitTransaction();
        Realm realm = Realm.getDefaultInstance();
        if(!realm.isClosed())
            realm.close();
    }

    public long getRecurrenceTTL() {
        return recurrenceTTL;
    }

    public void setRecurrenceTTL(long recurrenceTTL) {
        this.recurrenceTTL = recurrenceTTL;
    }
}

package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by user1 on 8/11/2017.
 */

public class AccountSpecialistLabels extends AppLabels {

    public static String getDataAccountLabelSetText(){
        return getLabelWithPrimaryKey("data_account_label_set_text","Serviciul Relații Clienți: *222");
    }

    public static String getAccountSpecialistCardTitle(){
        return getLabelWithPrimaryKey("account_specialist_card_title","Specialistul tău de cont");
    }

    public static String getAccountSpecialistCardSubText(){
        return getLabelWithPrimaryKey("account_specialist_card_subtext","Lore ispum dolor sit amet adipiscing elit");
    }

    public static String getYourProfileTitle(){
        return getLabelWithPrimaryKey("your_profile_title","Profilul tău");
    }

    public static String getAccountSpecialistTitle(){
        return getLabelWithPrimaryKey("account_specialist_title","Specialistul tău de cont");
    }
}

package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by user1 on 02-Feb-18.
 */

public class WebViewCampaignLabels extends AppLabels {

    public static String getShareTitle() {
        return getLabelWithPrimaryKey("webview_campaign_share_title","Distribuie promoție");
    }

    public static String getOverlay_Gamification_contacts_permission_context() {
        return getLabelWithPrimaryKey("gamification_permission_first", "Trimite Mystery Box-ul fără să tastezi numărul de telefon al prietenilor tăi, permițând aplicației My Vodafone să acceseze lista de contacte.\n" +
                "Nu vom stoca contactele tale în nici unul dintre sistemele noastre.");
    }

    public static String getOverlay_Gamification_contacts_permission_context_denied() {
        return getLabelWithPrimaryKey("gamification_permission_flag_off", "Pentru a putea trimite Mystery Box-ul fără să tastezi numărul de telefon al prietenilor tăi, trebuie să permiți accesul aplicației MyVodafone la lista de contacte din setările telefonului.\n" +
				"Nu vom stoca contactele tale în nici unul dintre sistemele noastre.");
    }
}

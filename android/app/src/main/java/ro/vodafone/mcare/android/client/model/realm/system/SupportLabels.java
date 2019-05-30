package ro.vodafone.mcare.android.client.model.realm.system;

/**
 * Created by user1 on 22-Jan-18.
 */

public class SupportLabels extends AppLabels {
    public static String getFAQSearchResults(){
        return  getLabelWithPrimaryKey("support_faq_search_results_no_of_articles_android","Rezultatele căutării – %s rezultate");
    }

    public static String getFAQSearchResultsNoneForKeyword(){
        return  getLabelWithPrimaryKey("support_faq_search_results_none_for_keyword","Ne pare rău, nu am găsit rezultatele pentru:");
    }

    public static String getFAQSearchResultsNoneForKeywordHint(){
        return  getLabelWithPrimaryKey("support_faq_search_results_none_for_keyword_hint","Iată nişte rezultate pentru căutările cele mai frecvente ce credem că te-ar putea interesa:");
    }

    public static String getFAQSearchFieldHint(){
        return  getLabelWithPrimaryKey("support_faq_search_field_hint","Caută");
    }
}

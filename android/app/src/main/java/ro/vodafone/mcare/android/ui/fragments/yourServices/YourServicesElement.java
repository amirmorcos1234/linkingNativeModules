package ro.vodafone.mcare.android.ui.fragments.yourServices;

/**
 * Created by Eliza Deaconescu on 08/03/2017.
 */

@Deprecated
public class YourServicesElement {

    String title;
    String subtext;
    int type;


    YourServicesElement(String title, String subtext, int type) {
        this.title = title;
        this.subtext = subtext;
        this.type = type;
    }
}

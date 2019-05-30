package ro.vodafone.mcare.android.widget.gauge.formats;


/**
 * Created by Victor Radulescu on 2/16/2017.
 */

public class CVTFormat extends VASFormat {


    private String formatRemainString = "Mai ai \n %1$S  \n ce vrei tu";

    private String unlimitedString ="Ai ce vrei tu \n nelimitat";

    private String formatWithTotalString = "Ai\n %1$S\n din %2$S\n Min/SMS";

    @Override
    public String getUnlimitedString() {
        return unlimitedString;
    }
    @Override
    protected String getFormatWithTotalString() {
        return this.formatWithTotalString;
    }

    @Override
    protected String getFomatRemainString() {
        return this.formatRemainString;
    }

}

package ro.vodafone.mcare.android.widget.gauge.formats;

/**
 * Created by Victor Radulescu on 2/16/2017.
 */

public class SMSFormat extends VASFormat {

    private String formatWithTotalString ="SMS naţionale \n %1$S \n din %2$s \n disponibil";

    private String formatRemainString = "Mai ai \n %1$S  \n SMS nationale";

    String unlimitedString ="SMS naţionale \n nelimitat";

    @Override
    protected String getFormatWithTotalString() {
        return this.formatWithTotalString;
    }

    @Override
    protected String getFomatRemainString() {
        return this.formatRemainString;
    }

    @Override
    public String getUnlimitedString() {
        return unlimitedString;
    }
}

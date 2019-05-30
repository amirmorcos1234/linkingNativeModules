package ro.vodafone.mcare.android.widget.gauge.formats;

import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import java.util.Formatter;

import ro.vodafone.mcare.android.client.model.costControl.AmountUnitEnum;
import ro.vodafone.mcare.android.client.model.costControl.Balance;

/**
 * Created by Victor Radulescu on 2/16/2017.
 */

public class VASFormat extends Format{

    private String formatWithTotalString ="Min/SMS \n %1$S \n din %2$s \n disponibile";

    private String formatRemainString = "Mai ai \n %1$S  \n min/SMS nationale";

    private String unlimitedString ="Min/SMS nationale \n nelimitat";


    @Override
    public SpannableStringBuilder formatWithTotal(double remain, double total) {
        Formatter gaugeFormatter = new Formatter();

        String stringRemain = String.valueOf( (int) remain );
        String stringTotal = String.valueOf( (int) total );

        String gaugeText;

        gaugeText =gaugeFormatter.format(getFormatWithTotalString(),
                stringRemain,
                stringTotal
        ).toString();

        return getStringBuilderFromFormatText(gaugeText,stringRemain,0);
    }

    @Override
    public SpannableStringBuilder formatRemain(double remain) {
        Formatter gaugeFormatter = new Formatter();

        String stringRemain = String.valueOf( (int) remain );

        String gaugeText;

        gaugeText =gaugeFormatter.format(getFomatRemainString(),
                stringRemain).toString();

        final SpannableStringBuilder sb = new SpannableStringBuilder(gaugeText);
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        int indexRemainValue = gaugeText.indexOf(stringRemain);

        sb.setSpan(bss, indexRemainValue, indexRemainValue + stringRemain.length(), 0);
        sb.setSpan(new RelativeSizeSpan(2f), indexRemainValue, indexRemainValue + stringRemain.length(), 0);

        return sb;
    }

    @Override
    public void setFormatedValues(Balance balance) {

        double remain =   balance.getRemainingAmount()!=null?balance.getRemainingAmount():0;
        double total = balance.getTotalAmount()!=null?balance.getTotalAmount():1;
        
        double minutesRemain = getAmountUnitEnum()== AmountUnitEnum.sec? remain / 60:remain;
        double minutesTotal = getAmountUnitEnum()==AmountUnitEnum.sec? total / 60 : total;

        setFormatedRemainCost(minutesRemain);
        setFormattedTotalCost(minutesTotal);
    }

    @Override
    public String getUnlimitedString() {
        return unlimitedString;
    }

    protected String getFormatWithTotalString() {
        return formatWithTotalString;
    }

    protected String getFomatRemainString() {
        return formatRemainString;
    }


}

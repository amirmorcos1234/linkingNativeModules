package ro.vodafone.mcare.android.widget.gauge.formats;

import android.text.SpannableStringBuilder;

import java.util.Formatter;
import java.util.Objects;

import ro.vodafone.mcare.android.client.model.costControl.Balance;

/**
 * Created by Victor Radulescu on 2/16/2017.
 */

public class ChunkFormat extends DataFormat {


    private String formatMultipleChunkString ="Mai ai \n %1$S \n din %2$s, %3$s pachete \n consumate";
    private String formatOneChunkString ="Mai ai \n %1$S \n din %2$s, %3$s pachet \n consumat";


    private int amountOfChunks = 0;


    @Override
    public SpannableStringBuilder formatWithTotal(double remain, double total) {
        Formatter gaugeFormatter = new Formatter();

        String stringRemain;
        String stringTotal;
        if(Objects.equals(getUnitTotal(), GB) && total!=0){
            stringTotal = String.format("%.2f", total);
        }else{
            stringTotal =  String.valueOf((int)total);
        }
        if(Objects.equals(getUnitRemain(), GB) && remain!=0){
            stringRemain = String.format("%.2f", remain);
        }else{
            stringRemain = String.valueOf((int)remain);
        }
        String gaugeText;

        String formatChunkString = formatMultipleChunkString;
        if(amountOfChunks == 1) {
            formatChunkString = formatOneChunkString;
        }

        gaugeText = gaugeFormatter.format(formatChunkString,
                stringRemain + getRemainUnitToDisplay(),
                stringTotal+ " " + getUnitTotal(),
                amountOfChunks
        ).toString();


        return getStringBuilderFromFormatText(gaugeText,stringRemain,getUnitRemain().length()+1);
    }

    @Override
    public SpannableStringBuilder formatRemain(double remain) {
        return null;
    }

    @Override
    public void setFormatedValues(Balance balance) {
        super.setFormatedValues(balance);
        /*   double remain =   balance.getRemainingAmount()!=null?balance.getRemainingAmount():0;
        double total = balance.getTotalAmount()!=null?balance.getTotalAmount():1;

        double m = ((remain / 1024) );

        double mTotal = ((total / 1024) );
        double gTotal = (((total / 1024) / 1024) );

        double totalFormat = setValue(gTotal,mTotal);

        String totalUnit = setUnit(gTotal);

        setUnitRemain(MB);
        setUnitTotal(totalUnit);

        setFormatedRemainCost(m);
        setFormattedTotalCost(totalFormat);*/
        amountOfChunks = balance.getGroupSize();
    }
    @Override
    public String getUnlimitedString() {
        return super.getUnlimitedString();
    }
    private String getRemainUnitToDisplay(){
        boolean remainUnitSameWithTotalUnit = getUnitRemain().equalsIgnoreCase(getUnitTotal());
       return remainUnitSameWithTotalUnit ? "" : " "+getUnitRemain();
    }
}

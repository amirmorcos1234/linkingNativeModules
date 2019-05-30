package ro.vodafone.mcare.android.widget.gauge.formats;

import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Formatter;
import java.util.Objects;

import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.costControl.Balance;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.utils.D;

/**
 * Created by Victor Radulescu on 2/16/2017.
 */

public class DataFormat extends Format {

    protected final String GB = "GB";
    protected final String MB = "MB";

    private String formatWithTotalString ="Date naţionale \n %1$S \n %2$s din %3$s %4$S \n disponibil";

    private String formatRemainString = "Mai ai \n %1$S date\nnationale";

    protected String unlimitedString ="Data naţionale \n nelimitat";

    protected String unitRemain;

    protected String unitTotal;

    @Override
    public SpannableStringBuilder formatWithTotal(double remain, double total) {
        Formatter gaugeFormatter = new Formatter();

        String stringRemain="",stringTotal="";
        if("Gb".equalsIgnoreCase(unitTotal)){
            if(unitRemain.equalsIgnoreCase("Mb")){
                remain*=1024;
            }
            stringTotal = String.format("%.2f", total);

        }else{
            stringTotal = String.valueOf((int)total);

        }
        if("Gb".equalsIgnoreCase(unitRemain)){

            stringRemain = String.format("%.2f", remain);        }
        else{
            stringRemain = String.valueOf( (int) remain );
        }

        String gaugeText;

        boolean remainUnitSameWithTotalUnit = getUnitRemain().equalsIgnoreCase(getUnitTotal());

            gaugeText =gaugeFormatter.format(formatWithTotalString,
                    stringRemain,
                    remainUnitSameWithTotalUnit ? "" : getUnitRemain(),
                    stringTotal,
                    getUnitTotal()
            ).toString();

        return getStringBuilderFromFormatText(gaugeText,stringRemain,0);
    }

    @Override
    public SpannableStringBuilder formatRemain(double remain) {
        Formatter gaugeFormatter = new Formatter();

        String stringRemain = String.valueOf( (int) remain );

        String gaugeText =gaugeFormatter.format(formatRemainString,
                stringRemain+"\n"+getUnitRemain()).toString();

        final SpannableStringBuilder sb = new SpannableStringBuilder(gaugeText);
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        int indexRemainValue = gaugeText.indexOf(stringRemain);

        sb.setSpan(bss, indexRemainValue, indexRemainValue + stringRemain.length(), 0);
        sb.setSpan(new RelativeSizeSpan(1.5f), indexRemainValue, indexRemainValue + stringRemain.length(), 0);

        return sb;
    }

    @Override
    public void setFormatedValues(Balance balance) {

        double remain =   balance.getRemainingAmount()!=null?balance.getRemainingAmount():0;
        double total = balance.getTotalAmount()!=null?balance.getTotalAmount():1;

        double m = ((remain / 1024) );
        double g = (((remain / 1024) / 1024));

        double mTotal = ((total / 1024) );
        double gTotal = (((total / 1024) / 1024) );

        double finalFormat = setValue(g,m);
        double totalFormat = setValue(gTotal,mTotal);

        String remainUnit = setUnit(g);
        String totalUnit = setUnit(gTotal);

        setUnitRemain(remainUnit);
        setUnitTotal(totalUnit);

        setFormatedRemainCost(finalFormat);
        setFormattedTotalCost(totalFormat);

    }

    @Override
    public String getUnlimitedString() {
        return unlimitedString;
    }


    protected void setUnitRemain(String remainUnit) {
        this.unitRemain = remainUnit;
    }

    protected String setUnit(double unitGb){
        if(unitGb>1 && VodafoneController.getInstance().getUser() instanceof PostPaidUser){
            return GB;
        } else
            return MB;
    }
    protected double setValue(double unitGb, double unitMb){
        if(unitGb>1 && VodafoneController.getInstance().getUser() instanceof PostPaidUser){
            return unitGb;
        } else
        if(unitMb>1){
            return  unitMb;
            //never return in KB just > MB
        }else{
            return unitMb;
        }
    }

    @Override
    public void setFormatedRemainCost(double formatedRemainCost) {
        if("GB".equalsIgnoreCase(unitRemain)){
            String formatedRemainCostString = getGbDecimalFormatToSet().format(formatedRemainCost).replaceAll(",",".");
            if(formatedRemainCostString.contains(",")){
                formatedRemainCostString = formatedRemainCostString.replaceAll(",",".");
            }
            super.setFormatedRemainCost( Float.valueOf(formatedRemainCostString));
        }else{
            super.setFormatedRemainCost((int)formatedRemainCost);
        }
    }

    @Override
    protected void setFormattedTotalCost(double formattedTotalCost) {
        if("GB".equalsIgnoreCase(unitTotal)){
            String number = getGbDecimalFormatToSet().format(formattedTotalCost);
            if(number.contains(",")){
                number = number.replace(",",".");
            }
            super.setFormattedTotalCost( Double.valueOf(number));
        }else{
            super.setFormattedTotalCost(formattedTotalCost);
        }
    }
    @Override
    public double getTotalValueFromAnimation(){

        if(!Objects.equals(getUnitRemain(), getUnitTotal())){
            return getFormattedTotalCost()*1024;
        }
        return getFormattedTotalCost();

    }


    protected String getUnitRemain() {
        return unitRemain;
    }
    protected void setUnitTotal(String unitTotal) {
        this.unitTotal = unitTotal;
    }

    protected String getUnitTotal() {
        return unitTotal;
    }

    private DecimalFormat getGbDecimalFormatToSet(){

        DecimalFormat gbDecimalFormat= new DecimalFormat("#.##");
        gbDecimalFormat.setRoundingMode(RoundingMode.FLOOR);

        return gbDecimalFormat;
    }

}

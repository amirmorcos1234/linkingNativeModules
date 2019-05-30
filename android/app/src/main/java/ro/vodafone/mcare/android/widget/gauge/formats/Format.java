package ro.vodafone.mcare.android.widget.gauge.formats;

import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import ro.vodafone.mcare.android.client.model.costControl.AmountTypeIdEnum;
import ro.vodafone.mcare.android.client.model.costControl.AmountUnitEnum;
import ro.vodafone.mcare.android.client.model.costControl.Balance;

/**
 * Created by Victor Radulescu on 2/16/2017.
 */

public abstract class Format {

    private double formatedRemainCost = 0.99f;

    private double formattedTotalCost = 1f;

    protected AmountTypeIdEnum amountTypeIdEnum;

    protected AmountUnitEnum amountUnitEnum;

    public abstract SpannableStringBuilder formatWithTotal(double remain, double total);

    public abstract SpannableStringBuilder formatRemain(double remain);


    public abstract void setFormatedValues(Balance balance);

    public AmountTypeIdEnum getAmountTypeIdEnum() {
        return amountTypeIdEnum;
    }

    public void setAmountTypeIdEnum(AmountTypeIdEnum amountTypeIdEnum) {
        this.amountTypeIdEnum = amountTypeIdEnum;
    }

    public AmountUnitEnum getAmountUnitEnum() {
        return amountUnitEnum;
    }

    public void setAmountUnitEnum(AmountUnitEnum amountUnitEnum) {
        this.amountUnitEnum = amountUnitEnum;
    }

    public boolean isUnlimited(){
        return amountUnitEnum == AmountUnitEnum.unl;
    }

    public double getFormatedRemainCost() {
        return formatedRemainCost;
    }

    public void setFormatedRemainCost(double formatedRemainCost) {
        this.formatedRemainCost = formatedRemainCost;
    }

    public double getFormattedTotalCost() {
        return formattedTotalCost;
    }

    protected void setFormattedTotalCost(double formattedTotalCost) {
        this.formattedTotalCost = formattedTotalCost;
    }

public SpannableStringBuilder getFormatedUnlimitedString(){
    return getStringBuilderFromFormatText(getUnlimitedString(),"nelimitat",0);
}
    public abstract String getUnlimitedString();


    protected SpannableStringBuilder getStringBuilderFromFormatText(String formatedText, String stringRemain,int offset){
        final SpannableStringBuilder sb = new SpannableStringBuilder(formatedText);
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        int indexRemainValue = formatedText.indexOf(stringRemain);

        sb.setSpan(bss, indexRemainValue, indexRemainValue + stringRemain.length()+offset, 0);
        sb.setSpan(new RelativeSizeSpan(1.6f), indexRemainValue, indexRemainValue + stringRemain.length()+offset, 0);
        return sb;
    }

    public double getTotalValueFromAnimation() {
        return   getFormattedTotalCost();
    }
}

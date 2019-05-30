package ro.vodafone.mcare.android.client.model.dashboard.gauge;


import android.text.SpannableStringBuilder;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.widget.gauge.formats.CVTFormat;
import ro.vodafone.mcare.android.widget.gauge.formats.ChunkFormat;
import ro.vodafone.mcare.android.widget.gauge.formats.DataFormat;
import ro.vodafone.mcare.android.widget.gauge.formats.Format;
import ro.vodafone.mcare.android.widget.gauge.formats.SMSFormat;
import ro.vodafone.mcare.android.widget.gauge.formats.VASFormat;
import ro.vodafone.mcare.android.widget.gauge.formats.VoiceFormat;

/**
 * Created by Victor Radulescu on 2/9/2017.
 */

public enum GaugeOptionsType {

    MOBILE_DATA(new DataFormat(),
             R.drawable.data_white_bg,R.drawable.ic_mobile_data),
    VOICE(new VoiceFormat()
            ,R.drawable.voice_bg,R.drawable.landline_or_call),
    SMS(new SMSFormat(),
            R.drawable.messages_bg, R.drawable.text),
    CHUNCK(new ChunkFormat()
            ,R.drawable.data_white_bg, R.drawable.ic_mobile_data),
    VAS(new VASFormat(),
            R.drawable.sync_white_bg,R.drawable.ic_sync),
    CVT(new CVTFormat(),
            R.drawable.community_or_foundation_white_background,R.drawable.ic_community_or_foundation);

    Format format;

    private int drawableIdSelected;

    private int drawableIdUnselected;


    private double totalCost = -1;

    private double remainCost = -1;

    GaugeOptionsType(Format format,int drawableIdSelected,int drawableIdUnselected) {
        this.format = format;
        this.drawableIdSelected = drawableIdSelected;
        this.drawableIdUnselected = drawableIdUnselected;
    }

    public Format getFormat() {
        return format;
    }

    public int getDrawableIdSelected() {
        return drawableIdSelected;
    }

    public int getDrawableIdUnselected() {
        return drawableIdUnselected;
    }


    public double getRemainCost() {
        return remainCost;
    }

    public void setRemainCost(float remainCost) {
        this.remainCost = remainCost;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    public double getFormatedRemainCost() {
        return format.getFormatedRemainCost();
    }

    public double getFormattedTotalCost() {
        return format.getFormattedTotalCost();
    }

    public SpannableStringBuilder getUnlimitedString() {
        return format.getFormatedUnlimitedString();
    }

    public boolean isUnlimited(){
        return  format.isUnlimited();
    }

    public double getTotalValueFromAnimation() {
        return   format.getTotalValueFromAnimation();
    }
}

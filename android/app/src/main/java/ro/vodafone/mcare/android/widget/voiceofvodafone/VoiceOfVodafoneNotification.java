package ro.vodafone.mcare.android.widget.voiceofvodafone;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;

/**
 * Created by Victor Radulescu on 11/25/2016.
 */

public class VoiceOfVodafoneNotification extends VoiceOfVodafoneBasic {



    public VoiceOfVodafoneNotification(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VoiceOfVodafoneNotification(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VoiceOfVodafoneNotification(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void inflateLayout() {
        inflate(context,R.layout.voice_of_vodafone_notification_layout,this);
    }
    @Override
    protected void initExtra(){
    }

    @Override
    protected void setExtraAtrributes(TypedArray attrs) {
    }

    public void setMessage(String text) {
        try {
            SpannableString span = new SpannableString(text);
            String userRole="";
            userRole = String.valueOf(VodafoneController.getInstance().getUserProfile().getUserRole());
            if(userRole!=null && text.contains(userRole)){
                span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), text.indexOf(userRole), text.indexOf(userRole) + userRole.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else{
                String boldedText = "My Vodafone";
                span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), text.indexOf(boldedText), text.indexOf(boldedText) + boldedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

            super.setMessage(span);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
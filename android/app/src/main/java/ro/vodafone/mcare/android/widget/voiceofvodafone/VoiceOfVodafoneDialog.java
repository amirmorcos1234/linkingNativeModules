package ro.vodafone.mcare.android.widget.voiceofvodafone;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import ro.vodafone.mcare.android.R;

/**
 * Created by Victor Radulescu on 1/25/2017.
 */

public class VoiceOfVodafoneDialog extends VoiceOfVodafoneBasic {

    protected Button leftBtn;
    protected Button rightBtn;

    public VoiceOfVodafoneDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VoiceOfVodafoneDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VoiceOfVodafoneDialog(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void inflateLayout() {
        inflate(context,R.layout.voice_of_vodafone_dialog_layout,this);
    }

    @Override
    protected void initExtra(){
        leftBtn = (Button) findViewById(R.id.bubble_left_btn);
        rightBtn= (Button) findViewById(R.id.bubble_right_btn);
    }

    @Override
    /**
     * Initialize extra attributes
     */
    protected void setExtraAtrributes(TypedArray attributes) {
        if(attributes.getBoolean(R.styleable.VoiceOfVodafoneNotification_showLeftButton,false)){
            leftBtn.setText(attributes.getString(R.styleable.VoiceOfVodafoneNotification_leftButtonText));
            leftBtn.setTextColor(textColor);
            leftBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    bubbleNotification.setVisibility(GONE);
                }
            });
        }else{
            leftBtn.setVisibility(GONE);
        }
        if(attributes.getBoolean(R.styleable.VoiceOfVodafoneNotification_showRightButton,false)){
            rightBtn.setText(attributes.getString(R.styleable.VoiceOfVodafoneNotification_rightButtonText));
            rightBtn.setTextColor(textColor);
        }else{
            rightBtn.setVisibility(GONE);
        }
        setDefaultColor();
    }

    public void setOnClickLeftButtonListener(OnClickListener clickListener){
        try{
            leftBtn.setOnClickListener(clickListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void setOnClickRightButtonListener(OnClickListener clickListener){
        try{
            rightBtn.setOnClickListener(clickListener);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setButtonLeftText(String text){
        leftBtn.setText(text);
    }

    public String getButtonLeftText(){
        return leftBtn!=null?leftBtn.getText().toString():"";

    }
    public void setButtonRighttText(String text){
        rightBtn.setText(text);
    }

    public String getButtonRightText(){
        return rightBtn!=null?rightBtn.getText().toString():"";

    }
    public void showLeftButton(boolean visible){
        leftBtn.setVisibility(visible ? VISIBLE:INVISIBLE);
    }
    public void showRightButton(boolean visible){
        rightBtn.setVisibility(visible ? VISIBLE:INVISIBLE);
    }

    public void setTextColor(@ColorInt int color){
        if(leftBtn!=null){
            leftBtn.setTextColor(color);
        }
        if(rightBtn!=null){
            rightBtn.setTextColor(color);
        }
        if(headlineTv!=null){
            headlineTv.setTextColor(color);
        }
        if(messageTv!=null){
            messageTv.setTextColor(color);
        }
    }

    public void setDefaultColor(){
       setTextColor(ContextCompat.getColor(context,R.color.blackNormal));
    }

    public void setMessage(String text) {
        Spannable sb = new SpannableString( text );
        super.setMessage(sb);
    }
}

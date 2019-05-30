package ro.vodafone.mcare.android.widget.voiceofvodafone;

/**
 * Created by Victor Radulescu on 1/25/2017.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;

public abstract class VoiceOfVodafoneBasic extends RelativeLayout{


    protected Context context;

    protected TextView headlineTv;
    protected TextView messageTv;

    protected View bubbleNotification;

    protected int textColor;

    VoiceOfVodafone voiceOfVodafone;

    public VoiceOfVodafoneBasic(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setAttribrutes(attrs);

    }

    public VoiceOfVodafoneBasic(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setAttribrutes(attrs);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VoiceOfVodafoneBasic(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        setAttribrutes(attrs);
    }
    protected void init(TypedArray attrs) {

        bubbleNotification = this;
        inflateLayout();

        headlineTv = (TextView) findViewById(R.id.bc_headline_textview);
        messageTv= (TextView) findViewById(R.id.bc_message_textView);

        initExtra();

    }
    protected abstract void initExtra();

    /**
     * Must contain bc_headline_textview, bc_message_textView
     */
    protected abstract void inflateLayout();

    @CallSuper
    protected void setAttribrutes(AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs,
                R.styleable.VoiceOfVodafoneNotification);
        try {
            init(attributes);
            textColor = attributes.getColor(
                    R.styleable.VoiceOfVodafoneNotification_textColor,
                    ContextCompat.getColor(context, R.color.whiteNormalTextColor));

            String headlineString =attributes.getString(
                R.styleable.VoiceOfVodafoneNotification_headline);
            headlineTv.setText(headlineString);

            String messageString = attributes.getString(
                    R.styleable.VoiceOfVodafoneNotification_message);
            messageTv.setText(messageString);

            headlineTv.setTextColor(textColor);
            messageTv.setTextColor(textColor);
          setExtraAtrributes(attributes);

     /*       nameTv.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, attributes.getDimension(
                            R.styleable.CategoryCardView_textSize,
                            15));*/
        } finally {
            attributes.recycle();
        }
    }
    private void hideTextIfNullOrEmpty(TextView textView, String text){
        if(textView!=null && (text==null || text.isEmpty())){
            //textView.setVisibility(View.GONE);
        }
    }
    protected abstract void setExtraAtrributes(TypedArray atrributes);

    public void setMessage(Spannable text){
        messageTv.setText(text);
    }
    public String getMessage(){
        return messageTv.getText().toString();
    }
    public void setHeadline(String text){
        headlineTv.setText(text);
        hideTextIfNullOrEmpty(headlineTv,text);
    }
    public String getHeadline(){
        return headlineTv.getText().toString();


    }
    public void hideHeadline(){
        headlineTv.setVisibility(GONE);
    }
    public void showHeadline(){
        headlineTv.setVisibility(VISIBLE);
    }
    public void hideMessage(){
        messageTv.setVisibility(GONE);
    }
    public void showMessage(){
        messageTv.setVisibility(VISIBLE);
    }

    public VoiceOfVodafone getVoiceOfVodafone() {
        return voiceOfVodafone;
    }

    public void setVoiceOfVodafone(VoiceOfVodafone voiceOfVodafone) {
        this.voiceOfVodafone = voiceOfVodafone;

    }
}

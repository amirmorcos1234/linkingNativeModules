package ro.vodafone.mcare.android.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Bivol Pavel on 17.03.2017.
 */
public class CardErrorLayout extends RelativeLayout{

    private Context mContext;
    private static final String DEFAULT_ERROR_MESSAGE = "Serviciu momentan indisponibil. Apasă pentru a reîncerca";

    public CardErrorLayout(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public CardErrorLayout(Context context, String errorText) {
        super(context);
        mContext = context;
        init(null, errorText);
    }

    public CardErrorLayout(Context context, String errorText, Drawable errorDrawable) {
        super(context);
        mContext = context;
        init(null, errorText, errorDrawable);
    }

    public CardErrorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    public CardErrorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CardErrorLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs){
        inflate(mContext, R.layout.card_error_layout, this);
        setText(DEFAULT_ERROR_MESSAGE);
    }

    private void init(AttributeSet attrs, String errorText){
        inflate(mContext, R.layout.card_error_layout, this);
        setText(errorText);
    }

    private void init(AttributeSet attrs, String errorText, Drawable errorDrawable){
        inflate(mContext, R.layout.card_error_layout, this);
        setText(errorText);
        setIcon(errorDrawable);
    }

    public void setText(String message){
        ((VodafoneTextView)findViewById(R.id.card_error_message)).setText(message);
    }

    public void setIcon(Drawable errorDrawable){
        ((ImageView)findViewById(R.id.image)).setImageDrawable(errorDrawable);
    }

    public CardErrorLayout setMargins(int left, int top, int right, int bottom){
        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(ScreenMeasure.dpToPx(left), ScreenMeasure.dpToPx(top), ScreenMeasure.dpToPx(right), ScreenMeasure.dpToPx(bottom));
        this.getChildAt(0).setLayoutParams(params);
        return this;
    }
}

package ro.vodafone.mcare.android.widget.gauge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import de.hdodenhof.circleimageview.CircleImageView;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.dashboard.gauge.GaugeOptionsType;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;

/**
 * Created by Victor Radulescu on 11/28/2016.
 */

public class OptionsToggleImageButton extends CircleImageView {

    private boolean isPressed = false;
    private GaugeOptionsType type;


    private final int  OPTIONS_BUTTON_BACKGROUND_ID_DRAWABLE = R.drawable.option_costcontrol_selector;

    private final int OPTIONS_BUTTON_SIZE_IN_DP = 50;

    private final int ADDED_BORDER_SIZE = ScreenMeasure.dpToPx(2);
    private final int PADDING_SIZE = ScreenMeasure.dpToPx(5);

    public OptionsToggleImageButton(Context context,GaugeOptionsType type) {
        super(context);
        this.type = type;
        setupView();
    }

    public OptionsToggleImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OptionsToggleImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    private void setupView(){
        setBackground(ContextCompat.getDrawable(getContext(),OPTIONS_BUTTON_BACKGROUND_ID_DRAWABLE));
        setLayoutParams(new RelativeLayout.LayoutParams(
                ScreenMeasure.dpToPx(OPTIONS_BUTTON_SIZE_IN_DP),
                ScreenMeasure.dpToPx(OPTIONS_BUTTON_SIZE_IN_DP)));

        setImageDrawable(false);

    }


    public GaugeOptionsType getType() {
        return type;
    }

    public void setType(GaugeOptionsType type) {
        this.type = type;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        setImageDrawable(selected);
    }

    private void setImageDrawable(boolean selected){
        if(selected){
            setPadding(0,0,0,0);
            setImageDrawable(addBorder(ContextCompat.getDrawable(getContext(),type.getDrawableIdSelected()), Color.WHITE, ADDED_BORDER_SIZE + PADDING_SIZE));
        }else{
            setPadding(PADDING_SIZE,PADDING_SIZE,PADDING_SIZE,PADDING_SIZE);
            setImageDrawable(addBorder(ContextCompat.getDrawable(getContext(),type.getDrawableIdUnselected()), Color.TRANSPARENT, ADDED_BORDER_SIZE));
        }
    }

    private Drawable addBorder(Drawable drawable, int borderColor, int borderWidth) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth() + 2 * borderWidth, drawable.getIntrinsicHeight() + 2 * borderWidth, Bitmap.Config.ARGB_8888);

        Bitmap image = drawableToBitmap(drawable);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawARGB(Color.alpha(borderColor), Color.red(borderColor), Color.green(borderColor), Color.blue(borderColor));

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(borderColor == Color.TRANSPARENT ? PorterDuff.Mode.SRC_OUT : PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(image, borderWidth, borderWidth, paint);

        return new BitmapDrawable(getResources(), bitmap);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public boolean isPressed() {
        return isPressed;
    }

    @Override
    public void setPressed(boolean pressed) {

        isPressed = pressed;
    }

    public boolean toggle(){

        this.isPressed = ! isPressed;
        this.setSelected(isPressed);
        return this.isPressed;
    }

}

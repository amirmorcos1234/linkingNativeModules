package ro.vodafone.mcare.android.custom;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.ui.activities.settings.SettingsActivity;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.widget.SwitchButton.ColorUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by bogdan.marica on 3/13/2017.
 */

public class CustomEditTextCompat extends android.support.v7.widget.AppCompatEditText {

    final int DRAWABLE_RIGHT = 2;
    boolean displayBorder = true;
    boolean notError = true;
    float startX;
    float startY;
    OnErrorIconClickListener onErrorIconClickListener;
    //private int crtCursorPos = 0;
    boolean isErrorIconTap = false;
    private boolean errorListenerSetDynamicly = false;

    public void setOnErrorIconClickListener(OnErrorIconClickListener errorIconClickListener){
        this.onErrorIconClickListener = errorIconClickListener;
        errorListenerSetDynamicly = true;
    }

    private OnTouchListener revealPasswordButtonListner = new OnTouchListener() {
        float initialX = 0, initialY = 0;
        float currentX = 0, currentY = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //final int DRAWABLE_LEFT = 0;
            //final int DRAWABLE_TOP = 1;

            System.out.println("onTouch");
            int crtCursorPos = 0;
            //After calling removeIconFromEditText() method , compoundDrawables is null , and we need to check it for null or set TouchListner only when reveal password icon is displayed.
            Drawable drawable = getCompoundDrawables()[DRAWABLE_RIGHT];

            if (drawable != null) {
                if (event.getX() >= (v.getRight() - 2 * v.getPaddingRight() - drawable.getBounds().width())
                        && (event.getX() <= v.getRight())
                        && (event.getY() >= 0)
                        && (event.getY() <= v.getBottom())) {

                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:
                            crtCursorPos = CustomEditTextCompat.this.getSelectionStart();
                            CustomEditTextCompat.this.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            //customEditText.setSelection(customEditText.length());
                            try {
                                CustomEditTextCompat.this.setSelection(crtCursorPos);
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                            }
                            return true;

                        case MotionEvent.ACTION_UP:
                            crtCursorPos = CustomEditTextCompat.this.getSelectionStart();
                            CustomEditTextCompat.this.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            //customEditText.setSelection(customEditText.length());
                            try {
                                CustomEditTextCompat.this.setSelection(crtCursorPos);
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                            }
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            if (initialX == 0 && initialY == 0) {
                                initialX = event.getX();
                                initialY = event.getY();
                            }
                            currentX = event.getX();
                            currentY = event.getY();
                            return true;
                    }
                }
            }
            crtCursorPos = CustomEditTextCompat.this.getSelectionStart();
            CustomEditTextCompat.this.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            try {
                CustomEditTextCompat.this.setSelection(crtCursorPos);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }


            return CustomEditTextCompat.super.onTouchEvent(event);
        }
    };
    private int CLICK_ACTION_THRESHHOLD = ScreenMeasure.dpToPx(5);
    private long CLICK_ACTION_THRESHHOLD_TIMER = 200;
    private long lastTouchDown;
    TextWatcher customEditTeextwatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            int inputType = getInputType();
            if (CustomEditTextCompat.this.getText().toString().length() == 0)

                //  if (inputType == InputType.TYPE_CLASS_TEXT || inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD || inputType == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD))
                removeIconFromEditText();

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            D.w("beforeTextChanged");
        }

        @Override
        public void afterTextChanged(Editable s) {

            int inputType = getInputType();

            if (CustomEditTextCompat.this.getText().toString().length() > 0)
                if (inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD || inputType == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    addFieldIcon(R.drawable.reveal_password_icon);
                }
        }
    };

    public CustomEditTextCompat(Context context) {
        super(context);
        init();
    }

    public CustomEditTextCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditTextCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        addTextChangedListener(customEditTeextwatcher);
//        setGravity(Gravity.BOTTOM);

        setFocusable(true);
        setFocusableInTouchMode(true);


        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(this, R.drawable.custom_cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int inputType = getInputType();

        if (CustomEditTextCompat.this.getText().toString().length() > 0)
            if (inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD || inputType == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                addFieldIcon(R.drawable.reveal_password_icon);
            }

        ColorUtils.setColorHandles(this, ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));
    }

    @Override
    public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        int inputType = getInputType();
        if (displayBorder)
            if (focused)
                this.setBackgroundResource(R.drawable.onfocus_input_border);
            else if (notError) {
                this.setBackgroundResource(R.drawable.gray_input_border);
            } else {
                if(inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                    CustomEditTextCompat.this.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    //CustomEditTextCompat.this.setSelection(crtCursorPos);
                    this.addFieldIcon(R.drawable.error_icon);
                }
                this.setBackgroundResource(R.drawable.red_error_input_border);

            }


    }

    public void setDisplayBorder(boolean displayBorder) {
        this.displayBorder = displayBorder;
    }

    public void setBackgroundresourceAndFieldIcon(@DrawableRes int bgrId, @DrawableRes int icnId) {
        setBackgroundResourceCheckError(bgrId);
        addFieldIcon(icnId);
    }

    public void setBackgroundresourceAndFieldIcon(@DrawableRes int bgrId) {
        setBackgroundResourceCheckError(bgrId);
        removeIconFromEditText();
    }

    public void setBackgroundResourceCheckError(@DrawableRes int bgrId) {
        notError = bgrId != R.drawable.red_error_input_border;
        this.setBackgroundResource(bgrId);
    }

    public void setFieldIcon(@DrawableRes int icnId){
        addFieldIcon(icnId);
    }

    public void removeIconFromEditText() {
        this.setCompoundDrawables(null, null, null, null);
        setOnClickListener(null);

        int inputType = getInputType();

        if (CustomEditTextCompat.this.getText().toString().length() > 0)
            if (inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD || inputType == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                addFieldIcon(R.drawable.reveal_password_icon);
            }

    }

    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return !(differenceX > CLICK_ACTION_THRESHHOLD || differenceY > CLICK_ACTION_THRESHHOLD);
    }

    private void addFieldIcon(int icnId) {


        Drawable image = getResources().getDrawable(icnId);

        int w = image.getMinimumWidth() / 2;
        int h = image.getMinimumHeight() / 2;


        image.setBounds(0, 0, w, h);
        this.setCompoundDrawables(null, null, image, null);
        switch (icnId) {
            case (R.drawable.reveal_password_icon):
                setOnTouchListener(revealPasswordButtonListner);
                if (this.getText().toString().length() == 0)
                    removeIconFromEditText();
                break;
            case (R.drawable.error_icon):
            case (R.drawable.close_48):


                setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        if (CustomEditTextCompat.this.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
                            if (event.getRawX() >= (CustomEditTextCompat.this.getRight() - CustomEditTextCompat.this.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        startX = event.getX();
                                        startY = event.getY();
                                        lastTouchDown = System.currentTimeMillis();
                                        return true;
                                    case MotionEvent.ACTION_UP:
                                        float endX = event.getX();
                                        float endY = event.getY();
                                        if (isAClick(startX, endX, startY, endY) && (System.currentTimeMillis() - lastTouchDown < CLICK_ACTION_THRESHHOLD_TIMER)){
                                            setText("");
                                            removeIconFromEditText();
                                            isErrorIconTap = true;
                                            if(onErrorIconClickListener != null)
                                                onErrorIconClickListener.onErrorIconClickListener();
                                            isErrorIconTap = false;
                                        }
                                        return true;
                                }
                            }
                        }
                        return CustomEditTextCompat.super.onTouchEvent(event);
                    }

                });


                break;
            default:
                break;
        }

    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            this.clearFocus();

            if (getContext() instanceof SettingsActivity && ((SettingsActivity) getContext()) != null)

                Observable.timer(500, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                ((SettingsActivity) getContext()).getToolbar().showToolBar();
                            }
                        });


        }
        return super.onKeyPreIme(keyCode, event);
    }

    public void onFocusChange(View view, boolean hasFocus) {

    }

    public boolean isErrorIconTap(){
        return isErrorIconTap;
    }


}

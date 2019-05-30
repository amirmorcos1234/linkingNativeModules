package ro.vodafone.mcare.android.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.utils.FragmentUtils;
import ro.vodafone.mcare.android.utils.Validator;
import ro.vodafone.mcare.android.widget.SwitchButton.ColorUtils;

/**
 * TODO replaced by CustomEditTextCompat
 * Use CustomEditTextCompat instead, this is a deprecated class and will be removed in the future.
 * Please replace its usages with the specified class.
 */
@Deprecated
public class CustomEditText extends AppCompatEditText implements View.OnFocusChangeListener {

    public static String TAG = CustomEditText.class.getCanonicalName();

    public static final int TRANSPARENT_BORDER = 0;
    public static final int GRAY_BORDER = 1;
    public static final int EMPTY_FIELD_STATUS = 0;
    public static final int VALIDE_FIELD_STATUS = 1;
    public static final int INVALID_FORMAT_FIELD_STATUS = 2;
    public static final int INVALID_FORMAT_FIELD_SECOND_STATUS = 3;

    private int inputType;
    int defaultBorderColor;
    private CustomEditText newPasswordInput;
    private CustomEditText customEditText;
    private Validator validator;
    //private int crtCursorPos =0;

    private boolean touchedOnce = false;

    public InputEventsListenerInterface eventsListener;

    int response = EMPTY_FIELD_STATUS;

    private boolean isHighlighted = false;
    private boolean isEmpty = true;
    private boolean isFromChildFragment;

    float startX;
    float startY;
    private int CLICK_ACTION_THRESHHOLD = ScreenMeasure.dpToPx(5);
    private long CLICK_ACTION_THRESHHOLD_TIMER = 200;
    private long lastTouchDown;

    public boolean isTouchedOnce() {
        return touchedOnce;
    }

    public int isValide() {
        return response;
    }

    public void setValide(int valide) {
        response = valide;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public CustomEditText getNewPasswordInput() {
        return newPasswordInput;
    }

    public void setNewPasswordInput(CustomEditText newPasswordInput) {
        this.newPasswordInput = newPasswordInput;
    }


    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            TypedArray ta = null;
            try {
                customEditText = this;
                validator = new Validator();

                try {
                    Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
                    f.setAccessible(true);
                    f.set(customEditText, R.drawable.custom_cursor);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ColorUtils.setColorHandles(this, ContextCompat.getColor(context, android.R.color.holo_blue_light));

                ta = context.obtainStyledAttributes(attrs, R.styleable.CustomEditTextType);
                inputType = ta.getInt(R.styleable.CustomEditTextType_type, 0);
                defaultBorderColor = ta.getInt(R.styleable.CustomEditTextType_defaultBorderColor, 0);

                this.setHintTextColor(getResources().getColor(R.color.custom_input_hint_color));
                showDefaultBorderStyle();

                isFromChildFragment = ta.getBoolean(R.styleable.CustomEditTextType_fromChildFragment, false);

                AppCompatActivity activity = (AppCompatActivity) context;

                Fragment currentFragment = FragmentUtils.getVisibleFragment(activity, isFromChildFragment);

                Log.d(TAG, "Current Fragment is " + currentFragment);

                eventsListener = (InputEventsListenerInterface) currentFragment;

                addTextChangedListener(customEditTeextwatcher);
                setOnFocusChangeListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (ta != null) {
                    ta.recycle();
                }

            }
        }

        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //Adding code here if need use custom view from XML layout files
    }


    @Override
    public void onFocusChange(View view, boolean hasFocus) {

        if (newPasswordInput != null) {
            System.out.println("newPassword value" + newPasswordInput.getText().toString());
        }
        System.out.println("onFocusChange");
        if (hasFocus) {
            touchedOnce = true;
            if (!isHighlighted()) {
                System.out.println("isHighlighted :" + isHighlighted());
                showOnfocusInputStyle();
                if (InputTypes.fromId(inputType) == InputTypes.PASSWORD || InputTypes.fromId(inputType) == InputTypes.NEW_PASSWORD || InputTypes.fromId(inputType) == InputTypes.CONFIRM_PASSWORD) {
                    //addReavealPasswordIcon();
                    //setOnTouchListener(revealPasswordButtonListner);
                }
            }

            if (isValide() == VALIDE_FIELD_STATUS) {
                if (eventsListener != null)
                    eventsListener.activateButton();
            }

        } else {
            validateCustomEditText();
        }
    }


    TextWatcher customEditTeextwatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (InputTypes.fromId(inputType) == InputTypes.USERNAME) {
                validateCustomEditText();
            }

            //For password input display reveal password button
            if (InputTypes.fromId(inputType) == InputTypes.PASSWORD || InputTypes.fromId(inputType) == InputTypes.NEW_PASSWORD || InputTypes.fromId(inputType) == InputTypes.CONFIRM_PASSWORD) {
                //addReavealPasswordIcon();
                //
            }

            // If user includes at least one caracter in all mandatory input fields, then button is enabled
            if (customEditText != null && customEditText.length() != 0) {

                if (inputType == 2 || inputType == 3 || inputType == 4) {
                    addReavealPasswordIcon();
                }

                customEditText.setEmpty(false);
                if (eventsListener != null) {
                    eventsListener.hideErrorMessage();
                    eventsListener.activateButton();
                }
            } else {
                System.out.println("empty field");
                if (!isHighlighted()) {
                    System.out.println("not highlited");
                    removeIconFromEditText();
                }
                customEditText.setEmpty(true);

                if (eventsListener != null)
                    eventsListener.inactivateButton();
            }

            //Validate input after text changed if it is highlighted.
            if (isHighlighted()) {
                validateCustomEditText();
            }
        }
    };

    public boolean validateCustomEditText() {
        Log.d(TAG, "validateCustomEditText()");
        int validationResult = EMPTY_FIELD_STATUS;

        if (InputTypes.fromId(inputType) == InputTypes.CONFIRM_PASSWORD && newPasswordInput != null) {
            validationResult = validator.validateConfirmPassword(newPasswordInput.getText().toString(), this.getText().toString());
        } else {
            validationResult = validator.isInputValid(inputType, this.getText().toString(), false);
        }

        System.out.println("validationResult = " + validationResult);
        setValide(validationResult);
        if (validationResult == VALIDE_FIELD_STATUS || !canBeHighlighted()) {
            if (customEditText.hasFocus()) {
                setValide(VALIDE_FIELD_STATUS);
                showOnfocusInputStyle();
            } else {
                setValide(VALIDE_FIELD_STATUS);
                showDefaultInputStyle();
            }

            if (eventsListener != null)
                eventsListener.activateButton();

            return true;
        } else {
            Log.d(TAG, "can Be Highlighted");
            setValide(validationResult);
            if (InputTypes.fromId(inputType) == InputTypes.CONTACT_PHONE && validationResult == 0) {
                System.out.println("IS CONTACT PHONE and validationResult = " + validationResult);
                showDefaultInputStyle();
            } else {
                Log.d(TAG, "show error() -" + validationResult);
                showErrorInputStyle(validationResult, true);
            }

            return false;
        }

    }

    public void showOnfocusInputStyle() {
        Log.d(TAG, "showOnfocusInputStyle()");
        setHighlighted(false);
        this.setBackgroundResource(R.drawable.onfocus_input_border);
        this.requestFocus();
        if (eventsListener != null) {
            eventsListener.hideErrorMessage();
        }
        removeIconFromEditText();
       /* if (customEditText.getId() == R.id.telephone_number_input || customEditText.getId() == R.id.email_address_input){
            addClearIconInsideEditText();
        }*/
    }

    public void showErrorInputStyle(int validationResult) {
        Log.d(TAG, "showErrorInputStyle()");
        setValide(validationResult);
        if (eventsListener != null) {
            eventsListener.inactivateButton();
            if (validationResult == EMPTY_FIELD_STATUS) {
                eventsListener.hideErrorMessage();
            } else {
                eventsListener.displayErrorMessage();
            }
        }
    }

    public void showErrorInputStyle(int validationResult, boolean canBeshow) {
        Log.d(TAG, "showErrorInputStyle() , validationResul = " + validationResult + ", canBeShow = " + canBeshow);
        setValide(validationResult);
        if (canBeshow) {
            if (eventsListener != null) {
                eventsListener.inactivateButton();
                // if (validationResult == EMPTY_FIELD_STATUS) {
                //     eventsListener.hideErrorMessage();
                //   } else if (!customEditText.hasFocus()){
                eventsListener.displayErrorMessage();
                // }
            }
        }


        if (canBeHighlighted()) {
            setHighlighted(true);
            setBackgroundResource(R.drawable.red_error_input_border);
            addErrorIconInsideEditText();
            setOnTouchListener(errorIconButtonListner);
        }
    }

    public void showDefaultInputStyle() {
        System.out.println("showDefaultInputStyle");
        setHighlighted(false);
        showDefaultBorderStyle();

        if (eventsListener != null) {
            eventsListener.hideErrorMessage();
        }
        removeIconFromEditText();
    }

    public void showDefaultBorderStyle() {
        switch (defaultBorderColor) {
            case TRANSPARENT_BORDER:
                this.setBackgroundResource(R.drawable.default_input_border);
                break;
            case GRAY_BORDER:
                this.setBackgroundResource(R.drawable.gray_default_input_border);
                break;
            default:
                this.setBackgroundResource(R.drawable.gray_default_input_border);
                break;
        }
    }

    public void removeHighlight() {
        if (customEditText.hasFocus()) {
            showOnfocusInputStyle();
        } else {
            showDefaultInputStyle();
        }
    }

    private void removeIconFromEditText() {
        //Check if an input is type password
        if (this.getText().length() != 0) {
            if (inputType == 2 || inputType == 3 || inputType == 4) {
                addReavealPasswordIcon();
            } else {
                this.setCompoundDrawables(null, null, null, null);
                setOnTouchListener(null);
            }
        } else {
            this.setCompoundDrawables(null, null, null, null);
            setOnTouchListener(null);
        }
    }

    private void addErrorIconInsideEditText() {
        Drawable image = getResources().getDrawable(R.drawable.error_icon);
        int h = 40;
        int w = 40;
        int textFieldHeight = this.getHeight();
        if (textFieldHeight <= h ) {
            image.setBounds( 0, 0, Math.round(w/2)  , Math.round(h/2));
        }
        else {
            image.setBounds( 0, 0, w , h );
        }
        this.setCompoundDrawables( null, null, image, null );
    }

    private void addReavealPasswordIcon() {
        Drawable image = getResources().getDrawable(R.drawable.reveal_password_icon);
        int h = image.getMinimumHeight() /2;
        int w = image.getMinimumWidth() /2;
        int textFieldHeight = this.getHeight();
        if (textFieldHeight <= h) {
            image.setBounds(0, 0, Math.round(w / 2), Math.round(h / 2));
        } else {
            image.setBounds(0, 0, w, h);
        }
        this.setCompoundDrawables(null, null, image, null);
        setOnTouchListener(revealPasswordButtonListner);
    }

    private void addClearIconInsideEditText() {
        Drawable image = getResources().getDrawable(R.drawable.clear_icon);
        int h = 40;
        int w = 40;
        int textFieldHeight = this.getHeight();
        if (textFieldHeight <= h ) {
            image.setBounds( 0, 0, Math.round(w/2)  , Math.round(h/2));
        }
        else {
            image.setBounds( 0, 0, w , h );
        }
        this.setCompoundDrawables(null, null, image, null);
        setOnTouchListener(clearIconButtonListner);
    }


    private OnTouchListener revealPasswordButtonListner = new OnTouchListener() {
        float initialX = 0, initialY = 0;
        float currentX = 0, currentY = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //final int DRAWABLE_LEFT = 0;
            //final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            //final int DRAWABLE_BOTTOM = 3;

            int crtCursorPos = 0;
            Drawable drawable = getCompoundDrawables()[DRAWABLE_RIGHT];

            System.out.println("onTouch");
            if (event.getX() >= (v.getRight() - 2 * v.getPaddingRight() - drawable.getBounds().width())
                    && (event.getX() <= v.getRight())
                    && (event.getY() >= 0)
                    && (event.getY() <= v.getBottom())) {


                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        crtCursorPos = customEditText.getSelectionStart();
                        customEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        //customEditText.setSelection(customEditText.length());
                        try {
                            customEditText.setSelection(crtCursorPos);
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        crtCursorPos = customEditText.getSelectionStart();
                        customEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        //customEditText.setSelection(customEditText.length());
                        try {
                            customEditText.setSelection(crtCursorPos);
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }

                        return true;

                    case MotionEvent.ACTION_MOVE:

                        Log.d(TAG, "action move");

                        if (initialX == 0 && initialY == 0) {
                            initialX = event.getX();
                            initialY = event.getY();
                        }

                        currentX = event.getX();
                        currentY = event.getY();

                        return true;
                }
            }
            crtCursorPos = customEditText.getSelectionStart();
            customEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            try {
                customEditText.setSelection(crtCursorPos);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            return CustomEditText.super.onTouchEvent(event);
        }
    };


    private OnTouchListener errorIconButtonListner = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //final int DRAWABLE_LEFT = 0;
            //final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            //final int DRAWABLE_BOTTOM = 3;
            System.out.println("Error on touch ");
            if (null != customEditText.getCompoundDrawables()[DRAWABLE_RIGHT]) {

                if (event.getRawX() >= (customEditText.getRight() - customEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    System.out.println("Error Icon Ontouch");
                    switch (event.getAction()) {

                        case MotionEvent.ACTION_UP:
                            float endX = event.getX();
                            float endY = event.getY();
                            if (isAClick(startX, endX, startY, endY) && (System.currentTimeMillis() - lastTouchDown < CLICK_ACTION_THRESHHOLD_TIMER)) {
                                customEditText.getText().clear();
                                setValide(EMPTY_FIELD_STATUS);
                                //  showOnfocusInputStyle();
                                if (CustomEditText.this.hasFocus())
                                    showOnfocusInputStyle();
                                else
                                    showDefaultInputStyle();

                                if (eventsListener != null)
                                    eventsListener.hideErrorMessage();
                            }
                            return true;

                        case MotionEvent.ACTION_DOWN:
                            startX = event.getX();
                            startY = event.getY();
                            lastTouchDown = System.currentTimeMillis();
                            return true;
                    }
                }

/*                if (isValide() != VALIDE_FIELD_STATUS) {

                } else {
                    System.out.println("Field on touch");
                }*/
            }
            return CustomEditText.super.onTouchEvent(event);
        }
    };

    private OnTouchListener clearIconButtonListner = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int DRAWABLE_RIGHT = 2;

            if (null != customEditText.getCompoundDrawables()[DRAWABLE_RIGHT]) {

                if (event.getRawX() >= (customEditText.getRight() - customEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                    switch (event.getAction()) {

                        case MotionEvent.ACTION_UP:
                            setValide(EMPTY_FIELD_STATUS);
                            customEditText.getText().clear();
                            showOnfocusInputStyle();

                            return false;
                    }
                }
            }
            return CustomEditText.super.onTouchEvent(event);
        }
    };

    private boolean canBeHighlighted() {
        Log.d(TAG, "canBeHighlighted");
        boolean result = true;

        if (InputTypes.fromId(inputType) == InputTypes.PASSWORD || InputTypes.fromId(inputType) == InputTypes.LOGIN) {
            result = false;
        }
        return result;
    }

    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return !(differenceX > CLICK_ACTION_THRESHHOLD || differenceY > CLICK_ACTION_THRESHHOLD);
    }

}


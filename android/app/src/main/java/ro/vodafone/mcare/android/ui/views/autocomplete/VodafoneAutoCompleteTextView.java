package ro.vodafone.mcare.android.ui.views.autocomplete;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.textwatcher.AutoCompleteTextWatcher;
import ro.vodafone.mcare.android.utils.D;

/**
 * Created by Victor Radulescu on 2/7/2018.
 */

public class VodafoneAutoCompleteTextView extends RelativeLayout {

    @BindView(R.id.search_input)
    CustomEditTextCompat autoCompleteEditText;

    public VodafoneAutoCompleteTextView(Context context) {
        super(context);
        init();
    }

    public VodafoneAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VodafoneAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        inflate(getContext(),R.layout.autocomplete_layout,this);
        ButterKnife.bind(this);
        autoCompleteEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                D.w
                        ("Focus changed: "+hasFocus);
            }
        });
        autoCompleteEditText.clearFocus();
        //requestFocus();
    }

    public void setHint(String hint) {
        autoCompleteEditText.setHint(hint);
    }

    public void setText(String text) {
        autoCompleteEditText.setText(text);
    }

    public void addTextChangedListener(AutoCompleteTextWatcher textChangedListener) {
        autoCompleteEditText.addTextChangedListener(textChangedListener);
    }
    /*public void setMinChar(){
        autoCompleteEditText.se
    }*/

    public void displayClearTextIcon(String text) {
        if (text != null && text.length() == 1) {
            addCloseButtonIconInsideEditText(autoCompleteEditText);
        } else if (text == null || text.isEmpty()) {
            removeButtonIconInsideEditText(autoCompleteEditText);
        }
    }
    public void setInputType(int typeClassNumber){
        if(autoCompleteEditText!=null){
            autoCompleteEditText.setInputType(typeClassNumber);
        }
    }

    public void addCloseButtonIconInsideEditText(CustomEditTextCompat searchField) {
        if (searchField == null) {
            return;
        }
        setDrawableToEditText(R.drawable.close_48);
        /*Drawable image = getResources().getDrawable(R.drawable.close_icon);
        int h = ScreenMeasure.dpToPx(24);
        int w = ScreenMeasure.dpToPx(24);
        image.setBounds(0, 0, w, h);
        searchField.setCompoundDrawables(null, null, image, null);*/
        searchField.setOnTouchListener(clearIconButtonListner);
    }

    private void removeButtonIconInsideEditText(CustomEditTextCompat searchField) {
        if (searchField == null) {
            return;
        }
        searchField.setCompoundDrawables(null, null, null, null);
        searchField.setOnTouchListener(null);
    }

    private View.OnTouchListener clearIconButtonListner = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            EditText searchField = (EditText) v;
            final int DRAWABLE_RIGHT = 2;
            if (null != searchField.getCompoundDrawables()[DRAWABLE_RIGHT]) {

                if (event.getRawX() >= (searchField.getRight() - searchField.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            searchField.getText().clear();
                            return false;
                    }
                }
            }
            return onTouchEvent(event);
        }
    };

    public CustomEditTextCompat getAutoCompleteEditText() {
        return autoCompleteEditText;
    }

    public void setDrawableToEditText(@DrawableRes int drawableResource) {
        getAutoCompleteEditText().setFieldIcon(drawableResource);
    }
}
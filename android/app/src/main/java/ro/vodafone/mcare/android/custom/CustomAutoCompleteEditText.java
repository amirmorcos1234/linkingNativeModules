package ro.vodafone.mcare.android.custom;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.widget.SwitchButton.ColorUtils;

/**
 * Created by Ionut Neagu on 10/06/2017.
 */

public class CustomAutoCompleteEditText extends android.support.v7.widget.AppCompatAutoCompleteTextView {

    public CustomAutoCompleteEditText(Context context) {
        super(context);
        init();
    }

    public CustomAutoCompleteEditText(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        init();
    }

    public CustomAutoCompleteEditText(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
        init();
    }

    private void init() {
        ColorUtils.setColorHandles(this, ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if (getWindowVisibility() != View.VISIBLE) {
            Log.d("InstantAutoComplete", "Window not visible, will not show drop down");
            return;
        }

        if (focused) {
            if(getText() != null && !getText().toString().equals("")) {
                try {
                    performFiltering(getText(), 0);
                    showDropDown();
                }catch (NullPointerException npe){
                    Log.e("onFocusChanged", "error is: " + npe);
                }
            }
        }
    }
}

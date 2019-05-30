package ro.vodafone.mcare.android.ui.views.buttons.checkboxes;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.widget.CompoundButton;

public class GetterCheckedListenerAppCompatCheckBox extends AppCompatCheckBox {
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
        public GetterCheckedListenerAppCompatCheckBox(Context context) {
            super(context);
        }

        public GetterCheckedListenerAppCompatCheckBox(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public GetterCheckedListenerAppCompatCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }
        public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener){
            super.setOnCheckedChangeListener(onCheckedChangeListener);
            this.onCheckedChangeListener = onCheckedChangeListener;
        }

        public OnCheckedChangeListener getOnCheckedChangeListener() {
            return onCheckedChangeListener;
        }
    }

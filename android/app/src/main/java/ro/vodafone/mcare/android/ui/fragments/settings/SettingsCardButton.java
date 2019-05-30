package ro.vodafone.mcare.android.ui.fragments.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.widget.SwitchButton.SwitchButton;

/**
 * Created by User1 on 2/23/2017.
 */

public class SettingsCardButton extends SettingsCard {

    public SwitchButton button;

    public SettingsCardButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingsCardButton(Context context) {
        super(context);
    }

    public SettingsCardButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SettingsCardButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void inflateLayout() {
        inflate(context, R.layout.settings_card_button,this);

    }

    @Override
    protected void setExtraAtrributes(TypedArray atrributes) {


    }

    @Override
    protected void initExtra() {
        button = (SwitchButton) findViewById(R.id.settingsCardButton);

    }
}

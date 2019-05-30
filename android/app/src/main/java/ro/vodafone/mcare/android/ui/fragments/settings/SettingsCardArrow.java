package ro.vodafone.mcare.android.ui.fragments.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.AutoResizeTextView;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by bogdan.marica on 2/23/2017.
 */


public class SettingsCardArrow extends SettingsCard {

    @BindView(R.id.cardTitle)
    VodafoneTextView cardTitle;

    @BindView(R.id.cardSubtext)
    AutoResizeTextView cardSubtext;

    public SettingsCardArrow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SettingsCardArrow(Context context) {
        super(context);
        init();
    }

    public SettingsCardArrow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SettingsCardArrow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void inflateLayout() {
        inflate(context, R.layout.settings_card_arrow,this);

    }
    @Override
    protected void setExtraAtrributes(TypedArray atrributes) {

    }

    @Override
    public void initExtra() {

    }

    public void init(){
        inflateLayout();
        ButterKnife.bind(this);
    }

    public void setAttributes(String title, String subtitle){

        cardTitle.setText(title);
        cardSubtext.setText(subtitle);

    }
}

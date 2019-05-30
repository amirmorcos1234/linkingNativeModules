package ro.vodafone.mcare.android.ui.fragments.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.views.textviews.AutoResizeTextView;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;


/**
 * Created by Bogdan Marica on 2/23/2017.
 */

public abstract class SettingsCard extends LinearLayout {

    protected Context context;

    protected VodafoneTextView title;
    protected AutoResizeTextView subtitle;

    public SettingsCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setAttribrutes(attrs);
    }

    public SettingsCard(Context context) {
        super(context);
        this.context = context;
    }

    public SettingsCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setAttribrutes(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SettingsCard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    protected void init(TypedArray attrs) {

        inflateLayout();

        title = (VodafoneTextView) findViewById(R.id.cardTitle);
        subtitle = (AutoResizeTextView) findViewById(R.id.cardSubtext);

        initExtra();

    }

    protected abstract void inflateLayout();


    protected abstract void setExtraAtrributes(TypedArray atrributes);

    protected abstract void initExtra();

    @CallSuper
    protected void setAttribrutes(AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs,
                R.styleable.SettingsCustomView);

        try {
            init(attributes);

            title.setText(attributes.getString(
                    R.styleable.SettingsCustomView_title));
            subtitle.setText(attributes.getString(
                    R.styleable.SettingsCustomView_subtext));

            setExtraAtrributes(attributes);

        } finally {
            attributes.recycle();
        }
    }
}
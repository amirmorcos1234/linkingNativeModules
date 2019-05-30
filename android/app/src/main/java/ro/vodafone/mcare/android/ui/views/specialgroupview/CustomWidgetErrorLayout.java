package ro.vodafone.mcare.android.ui.views.specialgroupview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

import static android.widget.RelativeLayout.CENTER_IN_PARENT;
import static android.widget.RelativeLayout.TRUE;

/**
 * Created by user on 09.02.2017.
 */
public class CustomWidgetErrorLayout extends LinearLayout{

    private LinearLayout errorLayout;
    private static Context mContext;

    private final int LAYOUT_SIZE_IN_DP = 48;

    ViewGroupParamsEnum viewGroupParamsEnum= ViewGroupParamsEnum.non;

    private int viewWidth = RelativeLayout.LayoutParams.WRAP_CONTENT;
    private int viewHeigth = RelativeLayout.LayoutParams.WRAP_CONTENT;

    private boolean isShown = false;

    private int textColor = R.color.whiteNormalTextColor;

    public CustomWidgetErrorLayout(Context context) {
        super(context);
        this.mContext = context;
    }

    public CustomWidgetErrorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public CustomWidgetErrorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomWidgetErrorLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
    }

    public CustomWidgetErrorLayout build(ViewGroup layout, int gravity, ViewGroupParamsEnum params){
        this.viewGroupParamsEnum = params;
        if(!isBuilded()){
            init(layout,gravity);
        }
        return this;
    }
    public CustomWidgetErrorLayout build(ViewGroup layout, int gravity, ViewGroupParamsEnum params, boolean reset){
        this.viewGroupParamsEnum = params;
        if(reset || !isBuilded()){
            init(layout,gravity);
        }
        return this;
    }

    private boolean isBuilded(){
        return this.errorLayout!=null;
    }

    public CustomWidgetErrorLayout setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
        return this;
    }

    public CustomWidgetErrorLayout setViewHeigth(int viewHeigth) {
        this.viewHeigth = viewHeigth;
        return this;
    }

    public CustomWidgetErrorLayout setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    private LinearLayout init(ViewGroup layout, int gravity){

        //Continer Layout
        errorLayout = new LinearLayout(mContext);
        errorLayout.setOrientation(HORIZONTAL);
        errorLayout.setGravity(gravity);
        LayoutParams containerParams = new
                LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        errorLayout.setLayoutParams(containerParams);


        //ImageView
        ImageView warning = new ImageView(mContext);
        warning.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.warning));
        LayoutParams imageParams = new LayoutParams(ScreenMeasure.pxToDp(LAYOUT_SIZE_IN_DP), ScreenMeasure.pxToDp(LAYOUT_SIZE_IN_DP));

        ColorFilter filter = new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.widget_warning_icon_color), PorterDuff.Mode.SRC_IN);
        warning.setColorFilter(filter);
        warning.setLayoutParams(imageParams);
        warning.setPadding(0,0,ScreenMeasure.dpToPx(20),0);

        //TextView
        VodafoneTextView warningMessage = new VodafoneTextView(mContext);
        warningMessage.setText(getResources().getString(R.string.widget_warning_message));
        warningMessage.setTextSize(15);
        warningMessage.setTextColor(ContextCompat.getColor(getContext(),textColor));
        warningMessage.setLines(2);

        errorLayout.addView(warning);
        errorLayout.addView(warningMessage);
        errorLayout.setVisibility(GONE);

        setViewGroupParams(errorLayout,viewGroupParamsEnum);

        layout.addView(errorLayout);

        return errorLayout;
    }

    public void show() {
        if(errorLayout!=null){
            errorLayout.setVisibility(View.VISIBLE);
            isShown =true;
        }
    }

    public void hide() {
        if(errorLayout!=null){
            errorLayout.setVisibility(View.GONE);
            isShown = false;

        }
    }
    private void setViewGroupParams(View view,ViewGroupParamsEnum params){
        switch (params){
            case relative_center:
                RelativeLayout.LayoutParams  viewParams = new RelativeLayout.LayoutParams(viewWidth, viewHeigth);
                viewParams.addRule(CENTER_IN_PARENT,TRUE);
                view.setLayoutParams(viewParams);
                view.setPadding(0,0,0,ScreenMeasure.pxToDp(LAYOUT_SIZE_IN_DP)/2);
        }
    }

    public boolean isVisible() {
        return isShown;
    }
}

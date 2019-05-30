package ro.vodafone.mcare.android.ui.views.specialgroupview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;

import static android.view.Gravity.CENTER;
import static android.widget.RelativeLayout.CENTER_IN_PARENT;
import static android.widget.RelativeLayout.TRUE;

/**
 * Created by user on 07.02.2017.
 */
public class CustomWidgetLoadingLayout extends ProgressBar {

    private ProgressBar hybridLoading;
    private ViewGroup layout;

    private final int LAYOUT_SIZE_IN_DP = 35;
    private final int INSIDE_CARD_SIZE_IN_DP = 50;
    private final int CARD_ERROR_SIZE_IN_DP = 74;

    ViewGroupParamsEnum viewGroupParamsEnum = ViewGroupParamsEnum.non;

    private int viewWidth = RelativeLayout.LayoutParams.WRAP_CONTENT;
    private int viewHeigth = RelativeLayout.LayoutParams.WRAP_CONTENT;

    private boolean isShown = false;

    public CustomWidgetLoadingLayout(Context context) {
        super(context);
    }

    public CustomWidgetLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomWidgetLoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomWidgetLoadingLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustomWidgetLoadingLayout build(ViewGroup layout, int color, ViewGroupParamsEnum params){
        this.viewGroupParamsEnum = params;
        this.layout = layout;
        if(!isBuilded()){
            init(color);
        }
        return this;
    }
    public CustomWidgetLoadingLayout build(ViewGroup layout, int color, ViewGroupParamsEnum params, boolean reset){
        this.viewGroupParamsEnum = params;
        this.layout = layout;
        if(reset || !isBuilded()){
            init(color);
        }
        return this;
    }

    public CustomWidgetLoadingLayout setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
        return this;
    }

    public CustomWidgetLoadingLayout setViewHeigth(int viewHeigth) {
        this.viewHeigth = viewHeigth;
        return this;
    }


    private boolean isBuilded(){
        return this.hybridLoading!=null;
    }

    public ProgressBar init(int color){

        LinearLayout layoutContainer = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutContainerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        layoutContainer.setLayoutParams(layoutContainerParams);
        layoutContainer.setGravity(CENTER);
        
        hybridLoading = new ProgressBar(getContext());
        hybridLoading.setIndeterminate(true);

        LinearLayout.LayoutParams progressBarrSizeParams = new
                LinearLayout.LayoutParams(ScreenMeasure.pxToDp(LAYOUT_SIZE_IN_DP), ScreenMeasure.pxToDp(LAYOUT_SIZE_IN_DP));
        hybridLoading.setLayoutParams(progressBarrSizeParams);

        hybridLoading.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        setViewGroupParams(hybridLoading,viewGroupParamsEnum);

        layout.addView(hybridLoading);
            //hybridLoading.setPadding(0,0,0,hybridLoading.getHeight()/2);
            //layoutContainer.addView(hybridLoading);
        //layout.addView(layoutContainer);

        return this;
    }

    public void show() {
        if(hybridLoading!=null){
            hybridLoading.setVisibility(View.VISIBLE);
            isShown = true;
        }
    }

    public void hide() {
        if(hybridLoading!=null){
            hybridLoading.setVisibility(View.GONE);
            isShown = false;
        }
    }

    public void removeFromParrent(){
        layout.removeView(hybridLoading);
    }

    private void setViewGroupParams(View view,ViewGroupParamsEnum params){
        switch (params){
            case relative_center:
                RelativeLayout.LayoutParams  viewParams = new RelativeLayout.LayoutParams(viewWidth, viewHeigth);
                viewParams.addRule(CENTER_IN_PARENT,TRUE);
                view.setLayoutParams(viewParams);
                view.setPadding(0,0,0,ScreenMeasure.pxToDp(LAYOUT_SIZE_IN_DP)/2);
                break;
            case card_params:
                LinearLayout.LayoutParams progressBarrSizeParams = new
                        LinearLayout.LayoutParams(ScreenMeasure.pxToDp(INSIDE_CARD_SIZE_IN_DP), ScreenMeasure.pxToDp(INSIDE_CARD_SIZE_IN_DP));
                progressBarrSizeParams.gravity = Gravity.CENTER;
                progressBarrSizeParams.setMargins(0, ScreenMeasure.pxToDp((CARD_ERROR_SIZE_IN_DP - INSIDE_CARD_SIZE_IN_DP)/2),
                        0, ScreenMeasure.pxToDp((CARD_ERROR_SIZE_IN_DP - INSIDE_CARD_SIZE_IN_DP)/2));
                view.setLayoutParams(progressBarrSizeParams);
        }
    }

    public boolean isVisible() {
        return isShown;
    }
}

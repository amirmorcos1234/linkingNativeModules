package ro.vodafone.mcare.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.costControl.CostControl;
import ro.vodafone.mcare.android.client.model.dashboard.gauge.GaugeOptionsType;
import ro.vodafone.mcare.android.client.model.users.User;
import ro.vodafone.mcare.android.client.model.users.postpaid.PostPaidUser;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
import ro.vodafone.mcare.android.ui.utils.CompatView;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.specialgroupview.CustomWidgetErrorLayout;
import ro.vodafone.mcare.android.ui.views.specialgroupview.CustomWidgetLoadingLayout;
import ro.vodafone.mcare.android.ui.views.specialgroupview.ViewGroupParamsEnum;
import ro.vodafone.mcare.android.ui.views.textviews.AutoResizeTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.gauge.CostControlWidgetController;
import ro.vodafone.mcare.android.widget.gauge.ExtraOptionsController;
import ro.vodafone.mcare.android.widget.gauge.OptionsController;
import ro.vodafone.mcare.android.widget.gauge.SeriesController;
import ro.vodafone.mcare.android.widget.gauge.SeriesUpdate;

/**
 * Created by Victor Radulescu on 11/28/2016.
 */

public abstract class CostControlWidget extends RelativeLayout implements SeriesUpdate, OptionsController.OptionsChangedListener {

    protected static String TAG = "CostControl";

    private static final float TRESHOULD_FOR_CREDITPLUS_DETACH_ANIMATION = 0.25f;
    Context context;

    DecoView arcView;

    AutoResizeTextView availableResourceTv;

    CostControl costControlInfo;

    float default_min_value = 0.99f;
    float default_max_value = 1f;

    ExtraOptionsController extraOptionsController;

    OptionsController optionsController;

    SeriesController seriesController;

    ImageView whiteCircle,plusCircle;

    boolean isArcDrawen = false;

    protected CustomWidgetErrorLayout errorView;
    protected CustomWidgetLoadingLayout loadingView;

    @BindView(R.id.gauge_white_circle)
    ImageView gaugeWhiteLayer;
    /**
     * Current option selected from Data, minutes and messages
     */
    GaugeOptionsType optionTypeSelected = null;

    private final static int WHITE_CIRCLE_SIZE_IN_DP = 60;


    private boolean allowExtraOptionsAnimation = true;

    @OnClick(R.id.dynamicArcView)
    public void redirectToCallDetailsActivity(){
        new NavigationAction(getContext()).startAction(IntentActionName.SERVICES_PRODUCTS);
    }



    public CostControlWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setAttribrutes(attrs);
    }

    public CostControlWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setAttribrutes(attrs);
    }


    public void startAnimation(){
        if(!isGaugeVisible()){
            showGaugeTurometer();
        }
        drawArcViewBoundaries(arcView,default_min_value);
        float total = (float) optionTypeSelected.getTotalValueFromAnimation();
        float remain = (float) optionTypeSelected.getFormatedRemainCost();
        Log.d("CostControlWidget","total " +total);
        Log.d("CostControlWidget","remain " +remain);

        float percentFinalPosition = (total>0 && remain>-1) ? getProcentFromRealValue(total,remain):0.99f;
        seriesController.animateSeries(percentFinalPosition,default_max_value);
    }

    protected void setAttribrutes(AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs,
                R.styleable.VoiceOfVodafoneNotification);
        try {
            init(attributes);
        } finally {
            attributes.recycle();
        }
    }


    public void drawArcViewBoundaries(DecoView arcView, float finalPostion ){
        //clear elements from the DecoView element
        arcView.executeReset();
        arcView.deleteAll();
       // Color color = Color.WHITE;

        final SeriesItem seriesItem= new SeriesItem.Builder(Color.argb(76, 255, 255, 255))
                .setRange(0, default_max_value, default_max_value)
                .setInitialVisibility(false)
                .setLineWidth(ScreenMeasure.dpToPx(SeriesController.CIRCLE_LINE_WIDTH_IN_DP))
                .build();
        int grayIndex = arcView.addSeries(seriesItem);
        DecoEvent.Builder builder = new DecoEvent.Builder(finalPostion).setIndex(grayIndex);
        arcView.addEvent(builder.build());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(arcView.getHeight()>0 && !isArcDrawen){
            initializeWidgets();
            Log.d("CostControlWidget","on measure");
        }
    }

    public DecoView getArcView() {
        return arcView;
    }

    @Override
    public void onSeriesUpdate(float percent) {
        try {
            float total = (float) optionTypeSelected.getFormattedTotalCost();
            float realValue = (float) getRealValueFromProcent(total, percent);
            setGaugeFromOptionText(realValue, total);

            if(percent < TRESHOULD_FOR_CREDITPLUS_DETACH_ANIMATION){
                animateDetachCreditPlusFromCostControlWidget();
            }

           // Log.d("CostControlWidget","onSeriesUpdate total "+total+" remain "+realValue+" percent "+realValue);
        }catch(Exception ex){
            Log.e("CostControl", "Unknown exception while setting up Gauge.", ex);
        }
        /*if(percent==0){
            hideGaugeTurometer();
        }*/
    }

    private void animateDetachCreditPlusFromCostControlWidget(){
        if(whiteCircle!=null && plusCircle!=null && whiteCircle.getAnimation()==null){
            float translation = ScreenMeasure.dpToPx(3);
            TranslateAnimation translateAnimationWhiteCircle = new TranslateAnimation(
                    0,
                    translation,
                    0,
                    -translation);
            translateAnimationWhiteCircle.setFillEnabled(true);
            translateAnimationWhiteCircle.setFillAfter(true);
            translateAnimationWhiteCircle.setDuration(20);

            TranslateAnimation translateAnimationPlusCircle = new TranslateAnimation(
                    0,
                    translation,
                    0,
                     -translation);
            translateAnimationPlusCircle.setFillEnabled(true);
            translateAnimationPlusCircle.setFillAfter(true);
            translateAnimationPlusCircle.setDuration(20);
            translateAnimationPlusCircle.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    ExtraOptionsController.EXTRA_RADIOS_IN_PX += ScreenMeasure.dpToPx(3);
                    allowExtraOptionsAnimation = false;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    allowExtraOptionsAnimation=true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            whiteCircle.startAnimation(translateAnimationWhiteCircle);
            plusCircle.startAnimation(translateAnimationPlusCircle);
        }
    }
    private void resetTranslationForCreditPlus(){
        if(whiteCircle!=null && plusCircle!=null){
            whiteCircle.clearAnimation();
            ExtraOptionsController.EXTRA_RADIOS_IN_PX = ScreenMeasure.dpToPx(28);
            plusCircle.clearAnimation();
        }
    }

    @Override
    public void OnOptionsChanged(GaugeOptionsType optionType) {
        seriesController.clearAnimations();
        this.optionTypeSelected = optionType;
        setupGauge();
    }

    public void onExtraOptionsRequestCompleted(){
        extraOptionsController.clearListViews();
        extraOptionsController.setViewListToAnimated(generateExtraOptionsButtons());
    }

    public void onCostControlRequestCompleted(CostControl costControl){
        this.optionTypeSelected = null;
        if(optionsController!=null){
            optionsController.cleanOptions();
        }
        this.costControlInfo = costControl;
        updateOptions();
        setupGauge();
    }

    public void onCostControlRequestFailed(String message){
        Log.d("CostControlWidget","Error on request"+message);
        showError();
    }

    protected void setupGauge(){

        if(CostControlWidgetController.getInstance().isBadData()){
            showError();
            return;
        }

        if(!CostControlWidgetController.getInstance().isDataCompleted()){
            showLoading();
            return;
        }

        try {
            if(!optionsController.areOptionDrawn()){
                optionsController.setup();
            }
            optionTypeSelected = optionTypeSelected == null ? optionsController.getDefaultOption() : optionTypeSelected;

        }catch (Exception ex){
            Log.e(TAG, "Unknown ex", ex);
        }

        if(optionTypeSelected==null){
            showError();
            return;
        }else{
            hideError();
            hideLoading();
        }
        resetTranslationForCreditPlus();
        if(optionTypeSelected.isUnlimited() ) {
            setUnlimitedText();
        }else if(optionTypeSelected == GaugeOptionsType.CHUNCK) {
            showChunkText();
        } else if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
            setPrepaidText();
        }
        else {
            availableResourceTv.setLines(4);
            startAnimation();
        }
    }
    protected void setupController(){
        CostControlWidgetController.getInstance().setup().requestData();
    }

    protected void init(TypedArray attributes) {
        inflate(context,R.layout.cost_control_widget,this);

        this.setOnClickListener(null);

        ButterKnife.bind(this);

        gaugeWhiteLayer.setVisibility(INVISIBLE);
        initArcView();

        extraOptionsController = new ExtraOptionsController(context,arcView,generateExtraOptionsButtons());

        optionsController = new OptionsController(context,this,this);

        seriesController = new SeriesController(context,this,this);

        drawArcViewBoundaries(arcView, default_min_value);

        showLoading();

        setupController();
        availableResourceTv.setMinTextSize(10);
    }

    protected void setupDrawInArc(){
        whiteCircle = createImageView(R.drawable.white_circle,WHITE_CIRCLE_SIZE_IN_DP,WHITE_CIRCLE_SIZE_IN_DP);
        whiteCircle.setOnClickListener(null);
        extraOptionsController.drawInArc(whiteCircle,WHITE_CIRCLE_SIZE_IN_DP,WHITE_CIRCLE_SIZE_IN_DP);
        plusCircle = createImageView(R.drawable.usage_gauge_plus,WHITE_CIRCLE_SIZE_IN_DP/2,WHITE_CIRCLE_SIZE_IN_DP/2);
        extraOptionsController.drawInArc(plusCircle,WHITE_CIRCLE_SIZE_IN_DP/2,WHITE_CIRCLE_SIZE_IN_DP/2);
        extraOptionsController.setRotateView(plusCircle);


        plusCircle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allowExtraOptionsAnimation){
                    extraOptionsController.toggle();
                }

            }
        });
    }
     abstract List<View> generateExtraOptionsButtons();


    protected ImageView createImageView(@DrawableRes int resId, int dpWidth, int dpHeight){
        ImageView newImageView = new ImageView(context);
        newImageView.setImageResource(resId);
        newImageView.setLayoutParams(new RelativeLayout.LayoutParams(ScreenMeasure.dpToPx(dpWidth),ScreenMeasure.dpToPx(dpHeight)));
        this.addView(newImageView);

        return newImageView;
    }
    protected Button createDialViewButton(int resId,String text,int textColor){
        Button newButton = new Button(context);
        newButton.setBackgroundResource(resId);
        newButton.setText(text);
        newButton.setTextColor(textColor);
        newButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,10F);
        newButton.setTransformationMethod(null);
        newButton.setLayoutParams(new LayoutParams(ScreenMeasure.dpToPx(42),ScreenMeasure.dpToPx(42)));
        //Log.d(TAG, String.valueOf(getChildCount()));
        this.addView(newButton,1);
        newButton.setVisibility(INVISIBLE);
        return newButton;
    }
    protected FrameLayout createDialViewImageButton(int resId, @DrawableRes int imageDrawable, @ColorRes int imageColor){
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new LayoutParams(ScreenMeasure.dpToPx(42),ScreenMeasure.dpToPx(42)));
        frameLayout.setBackgroundResource(resId);

        DynamicColorImageView newButton = new DynamicColorImageView(context);
        int margins = ScreenMeasure.dpToPx(3);
        newButton.setImageDrawable(ContextCompat.getDrawable(context,imageDrawable));
        newButton.setDrawableColor(imageColor);
        FrameLayout.LayoutParams frameLayout1 =new FrameLayout.LayoutParams(ScreenMeasure.dpToPx(36),ScreenMeasure.dpToPx(36));
        frameLayout1.setMargins(margins,margins,margins,margins);
        newButton.setLayoutParams(frameLayout1);
        frameLayout1.gravity=CENTER_IN_PARENT;
        //Log.d(TAG, String.valueOf(getChildCount()));
        frameLayout.addView(newButton);

        this.addView(frameLayout,1);
        frameLayout.setVisibility(INVISIBLE);
        return frameLayout;
    }

    protected void initArcView(){
        if(arcView == null || availableResourceTv == null) {
            arcView = (DecoView) findViewById(R.id.dynamicArcView);
            availableResourceTv = (AutoResizeTextView) findViewById(R.id.availableResourceTv);
        }
    }


    protected void initializeWidgets(){
        isArcDrawen = true;
        setupDrawInArc();
        brintToFrontPlusButton();

        seriesController.setup();

        optionsController.setup();

        if(!isGaugeVisible()){
            hideGaugeTurometer();
        }

    }

    protected void brintToFrontPlusButton(){
        if(whiteCircle!=null && plusCircle!=null){
            CompatView.bringToFront(whiteCircle);
            CompatView.bringToFront(plusCircle);
        }

    }
    protected void hideGaugeWhiteLayer(){
        gaugeWhiteLayer.setVisibility(INVISIBLE);
    }
    protected void showGaugeWhiteLayer(){
        gaugeWhiteLayer.setVisibility(VISIBLE);
        resizeCostTextView(getResources().getDimensionPixelSize(R.dimen.cost_control_widget_white_layer_textview_size));
        enableClickListenerOnGauge(true);
        if(getArcView()!=null){
            getArcView().setOnClickListener(null);
        }
    }
    protected void resizeCostTextView(int size){
        if(availableResourceTv==null){
            return;
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) availableResourceTv.getLayoutParams();
        layoutParams.width = size;
        layoutParams.height = size;
        availableResourceTv.setLayoutParams(layoutParams);
    }

    protected boolean isGaugeVisible(){
        return arcView.getVisibility()==VISIBLE;
    }

    protected void showGaugeTurometer(){
        seriesController.showGauge();
        hideGaugeWhiteLayer();
        enableClickListenerOnGauge(true);
        resizeCostTextView(getResources().getDimensionPixelSize(R.dimen.cost_control_widget_gauge_textview_size));

    }

    protected void hideGaugeTurometer(){
        seriesController.hideGauge();
        showGaugeWhiteLayer();
    }
    protected void setUnlimitedText() {
        if(VodafoneController.getInstance().getUser() instanceof PostPaidUser){
            showGaugeTurometer();
            seriesController.drawWhiteSeriesNoAnimation();
        }else{
            hideGaugeTurometer();
        }
        SpannableStringBuilder unlimitetValueMessage =  optionTypeSelected.getUnlimitedString();
        //add empty string for a good autoresize ( hack)
        availableResourceTv.setText(unlimitetValueMessage);
    }



    protected void setGaugeFromOptionText( float value, float totalValue){
        D.w("CostControl value"+value);
        SpannableStringBuilder stringBuilder = optionTypeSelected.getFormat().formatWithTotal(value,totalValue);
        availableResourceTv.setText(stringBuilder);
        // availableResourceTv.setText(optionTypeSelected.getUnlimitedString());
        //availableResourceTv.invalidate();
        availableResourceTv.requestLayout();
    }
    protected void showChunkText() {
        hideGaugeTurometer();
        float remain = (float) optionTypeSelected.getFormatedRemainCost();
        float total = (float) optionTypeSelected.getFormattedTotalCost();
        SpannableStringBuilder stringBuilder = optionTypeSelected.getFormat().formatWithTotal(remain,total);
        availableResourceTv.setLines(4);
        availableResourceTv.setText(stringBuilder);
        availableResourceTv.requestLayout();
    }

    protected void setPrepaidText() {
        hideGaugeTurometer();
        float remain = (float) optionTypeSelected.getFormatedRemainCost();
        SpannableStringBuilder stringBuilder = optionTypeSelected.getFormat().formatRemain(remain);
        setTextSizeForWhiteGauge();
        availableResourceTv.setLines(4);
        availableResourceTv.setText(stringBuilder);
        availableResourceTv.requestLayout();
    }

    protected void showError() {
        hideGaugeTurometer();
        errorView =errorView != null ?
                errorView: new CustomWidgetErrorLayout(context).setViewWidth(
                        ScreenMeasure.dpToPx(140)).build(
                        this,
                        Gravity.CENTER,
                        ViewGroupParamsEnum.relative_center);
        errorView.show();
        hideLoading();
    }

    protected void showLoading(){
        hideGaugeTurometer();
        if(loadingView==null)
            loadingView = new CustomWidgetLoadingLayout(context).build(
                    this,
                    Color.RED,
                    ViewGroupParamsEnum.relative_center);
        loadingView.show();
        hideError();
        //enableClickListenerOnGauge(false);
    }
    protected void hideError(){
        if(errorView!=null && errorView.isVisible()){
            errorView.hide();
        }
    }

    protected void hideLoading(){
        if(loadingView!=null && loadingView.isVisible()){
            loadingView.hide();
        }
    }

    protected void enableClickListenerOnGauge(boolean enable){
        findViewById(R.id.gauge_click_view).setOnClickListener(enable?getGaugeClickListener():null);
    }

    protected float getProcentFromRealValue(float total,float realValue){
        return (float)(realValue/total);
    }

    protected float getRealValueFromProcent(float total,float percent){
        return total*percent;
    }


    protected void updateOptions(){
        if(optionsController==null|| costControlInfo==null){
            return;
        }
        optionsController.updateWithBalanceList(costControlInfo.getCurrentExtraoptions());
    }
    protected boolean areOptionsReady(){
        return optionsController.isDataReady();
    }
    protected boolean isCorrectUserForAnimation(){
        User user =VodafoneController.getInstance().getUser();
        if(user instanceof PostPaidUser){
            return true;
        }
        return false;
    }
    protected boolean isGaugeValid(){
        return optionTypeSelected!=null;
    }

    protected abstract View.OnClickListener getGaugeClickListener();


    public int getPlusButtonHeight() {
        return ScreenMeasure.dpToPx(WHITE_CIRCLE_SIZE_IN_DP);
    }

    public void setTextSizeForGauge(){
        setTextViewSize(getResources().getDimensionPixelSize(R.dimen.cost_control_widget_gauge_textview_size));
    }
    public void setTextSizeForWhiteGauge(){
        setTextViewSize(getResources().getDimensionPixelSize(R.dimen.cost_control_widget_gauge_textview_size));
    }
    public void setTextViewSize(int size){
        if(availableResourceTv!=null){
            RelativeLayout.LayoutParams layoutParams = (LayoutParams) availableResourceTv.getLayoutParams();
            layoutParams.height = size;
            layoutParams.width = size;
            availableResourceTv.setLayoutParams(layoutParams);
        }
    }

}

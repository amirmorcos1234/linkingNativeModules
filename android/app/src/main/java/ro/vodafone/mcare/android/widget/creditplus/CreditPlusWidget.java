package ro.vodafone.mcare.android.widget.creditplus;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.users.NonVodafoneUser;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.migrated.EbuMigrated;
import ro.vodafone.mcare.android.client.model.users.postpaid.ebu.nonMigrated.EbuNonMigrated;
import ro.vodafone.mcare.android.client.model.users.prepaid.PrepaidUser;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import ro.vodafone.mcare.android.widget.animators.interpolators.Interpolators;

/**
 * Created by Daniel Slavic
 */

public class CreditPlusWidget extends RelativeLayout {

    protected static String TAG = "CreditPlusWidget";
    Context context;

    public LinearLayout textLayout;
    public TextView textLabel;
    public TextView textValue;

    public FrameLayout imageLayout;

    boolean isAnimating = false;

    protected final long ANIMATION_DURATION = 670;

    protected final int RIGHT_MARGIN_IN_DP = 5;

    protected boolean singleText = false;

    protected float componentsSize;
    protected static final int COMPONENTS_SIZE_DEFAULT_RESOURCE = R.dimen.credit_plus_component_width;
    protected static final int IMAGE_INSIDE_MARGIN_DEFAULT_RESOURCE = R.dimen.credit_plus_component_credit_plus_image_margin;

    protected static final float balanceCreditLimitRedirectToTopUp =3;


    protected ProgressBar loadingView;

    int textLeftPadding;


    @Optional
    @OnClick({R.id.credit_plus_image_layout,R.id.credit_plus_image})
    public void redirect(View view){
        float balanceCredit = CreditPlusWidgetController.getInstance().getBalanceCredit();

        if(VodafoneController.getInstance().getUser() instanceof PrepaidUser){
            if(balanceCredit <= balanceCreditLimitRedirectToTopUp){
                new NavigationAction(getContext()).startAction(IntentActionName.TOP_UP);
            }else{
                new NavigationAction(getContext()).startAction(IntentActionName.OFFERS_BEO_WITH_SEAMLESS);
            }
        }else{
            new NavigationAction(getContext()).startAction(IntentActionName.TOP_UP_ANONYMOUS);
        }
    }

    public CreditPlusWidget(Context context, AttributeSet attrs) {

        super(context, attrs);
        this.context = context;
        setAttribrutes(attrs);
    }

    public CreditPlusWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setAttribrutes(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CreditPlusWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        setAttribrutes(attrs);

    }
    protected void setAttribrutes(AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs,
                R.styleable.CreditPlusWidget);
        try {
            singleText = attributes.getBoolean(
                    R.styleable.CreditPlusWidget_singleText,
                    false);
            componentsSize = attributes.getDimension(R.styleable.CreditPlusWidget_components_size,
                    getResources().getDimension(COMPONENTS_SIZE_DEFAULT_RESOURCE));
            int imageInsideMargin = (int) attributes.getDimension(R.styleable.CreditPlusWidget_image_plus_margin,
                    getResources().getDimension(IMAGE_INSIDE_MARGIN_DEFAULT_RESOURCE));
             textLeftPadding = (int) attributes.getDimension(R.styleable.CreditPlusWidget_text_left_padding, 0);
            init(imageInsideMargin);


        } finally {
            attributes.recycle();
        }
    }

    protected void init(int imageInsideMargin) {

        inflate(context, R.layout.credit_plus_widget, this);

        ButterKnife.bind(this);

        textLabel = (TextView) this.findViewById(R.id.credit_plus_text_label);
        textValue = (TextView) this.findViewById(R.id.credit_plus_text_value);
        TextView textError = (TextView) this.findViewById(R.id.credit_plus_text_error);
        //textValue.setPadding(textLeftPadding,textLabel.getPaddingTop(),textLabel.getPaddingRight(),textLabel.getPaddingBottom());
       // textLabel.setPadding(textLeftPadding,textLabel.getPaddingTop(),textLabel.getPaddingRight(),textLabel.getPaddingBottom());


        if(componentsSize!=getResources().getDimension(COMPONENTS_SIZE_DEFAULT_RESOURCE)){
            setSizes();
        }
        if(imageInsideMargin!=getResources().getDimension(IMAGE_INSIDE_MARGIN_DEFAULT_RESOURCE)){
            setMarginsForCreditPlusImageLayout(imageInsideMargin);
        }
    }

    protected void setSizes(){
        imageLayout = (FrameLayout) this.findViewById(R.id.credit_plus_image_layout);
        imageLayout.getLayoutParams().width = (int) componentsSize;
        imageLayout.getLayoutParams().height = (int) componentsSize;


        textLayout = (LinearLayout) this.findViewById(R.id.credit_plus_text_layout);
        textLayout.getLayoutParams().width = (int) componentsSize;
        textLayout.getLayoutParams().height = (int) componentsSize;

         RelativeLayout errorLayout = (RelativeLayout) this.findViewById(R.id.credit_plus_error_layout);
        errorLayout.getLayoutParams().width = (int) componentsSize;
        errorLayout.getLayoutParams().height = (int) componentsSize;


        FrameLayout loadingLayout = (FrameLayout) this.findViewById(R.id.credit_plus_loading_layout);
        loadingLayout.getLayoutParams().width = (int) componentsSize;
        loadingLayout.getLayoutParams().height = (int) componentsSize;



    }
    protected void setMarginsForCreditPlusImageLayout(int imageInsideMargin){
        ImageView imagePlus = (ImageView) this.findViewById(R.id.credit_plus_image);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imagePlus.getLayoutParams();
        layoutParams.setMargins(imageInsideMargin,imageInsideMargin,imageInsideMargin,imageInsideMargin);
        imagePlus.setLayoutParams(layoutParams);

    }

    public void startAnimation(boolean expandWithoutAnimation){
        if(isAnimating){
            return;
        }
        isAnimating= true;
        imageLayout = (FrameLayout) this.findViewById(R.id.credit_plus_image_layout);
        moveImageView(imageLayout,expandWithoutAnimation);


        textLayout = (LinearLayout) this.findViewById(R.id.credit_plus_text_layout);
        expandTextView(textLayout,expandWithoutAnimation);

        RelativeLayout errorLayout = (RelativeLayout) this.findViewById(R.id.credit_plus_error_layout);
        expandTextView(errorLayout, expandWithoutAnimation);

        FrameLayout loadingLayout = (FrameLayout) this.findViewById(R.id.credit_plus_loading_layout);
        expandTextView(loadingLayout, expandWithoutAnimation);

        setupController();

    }

    protected void setupController(){
        if(!(VodafoneController.getInstance().getUser() instanceof NonVodafoneUser
                || VodafoneController.getInstance().getUser() instanceof EbuNonMigrated
                || VodafoneController.getInstance().getUser() instanceof EbuMigrated)){
            CreditPlusWidgetController.getInstance().setup(context,this).load();
        }
    }

    protected void moveImageView(final FrameLayout v,boolean expandWithoutAnimation){

        final float deltaWidth = getFinalTranslatePosition(v) ;

        ObjectAnimator translateX = ObjectAnimator.ofFloat(v, "translationX", deltaWidth);

        translateX.setInterpolator(Interpolators.OUT_EXPO);
        translateX.setDuration(expandWithoutAnimation? 0:ANIMATION_DURATION);
        setViewInFront(imageLayout);
        translateX.start();
    }
    protected float getFinalTranslatePosition(View v){
        ScreenMeasure screenMeasure = new ScreenMeasure(context);
        int screenWidth = screenMeasure.getWidth();
        int locationOnScrenen[]= new int[2];
        v.getLocationOnScreen(locationOnScrenen);
        if(componentsSize * 3 + ScreenMeasure.dpToPx(RIGHT_MARGIN_IN_DP) + locationOnScrenen[0] > screenWidth){
            return  Math.abs(screenWidth - locationOnScrenen[0] -componentsSize - ScreenMeasure.dpToPx(RIGHT_MARGIN_IN_DP) );
        }else{
            return  (componentsSize*2);
        }
    }

    protected void expandTextView(final View v, boolean expandWithoutAnimation) {

        final int prevWidth = (int) componentsSize;

        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevWidth, prevWidth*3);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().width = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                isAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        valueAnimator.setInterpolator(Interpolators.OUT_EXPO);
        valueAnimator.setDuration(expandWithoutAnimation?0:ANIMATION_DURATION);
        valueAnimator.start();
    }

    protected void setViewInFront(View view){
        ViewCompat.setZ(view,10000);
    }
    public float getComponentsSize() {
        return componentsSize;
    }

    public void showTextValue(boolean showTextValue){
        if(showTextValue){
            textValue.setVisibility(VISIBLE);
        }else{
            textValue.setVisibility(INVISIBLE);
        }
    }
    public void showError(boolean visibile){
        try {
            if (visibile) {
                RelativeLayout errorLayout = (RelativeLayout) this.findViewById(R.id.credit_plus_error_layout);
                textLayout.setVisibility(INVISIBLE);
                errorLayout.setVisibility(VISIBLE);
            } else {
                RelativeLayout errorLayout = (RelativeLayout) this.findViewById(R.id.credit_plus_error_layout);
                errorLayout.setVisibility(INVISIBLE);
                textLayout.setVisibility(VISIBLE);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    protected void showLoading(boolean visible) {
        try {
            if (visible) {
                textLayout.setVisibility(INVISIBLE);

                this.findViewById(R.id.credit_plus_loading_layout).setVisibility(VISIBLE);
                loadingView = (ProgressBar) this.findViewById(R.id.credit_plus_loading_progressbar);
                loadingView.setIndeterminate(true);
                loadingView.getIndeterminateDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                loadingView.setVisibility(VISIBLE);

            } else {
                if (loadingView != null) {
                    loadingView.setVisibility(INVISIBLE);
                    this.findViewById(R.id.credit_plus_loading_layout).setVisibility(INVISIBLE);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        }
}

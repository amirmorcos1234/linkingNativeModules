package ro.vodafone.mcare.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
/**
 * Created by Victor Radulescu on 12/8/2016.
 */

/**
 * Menu button having a textView in form of a bubble used in DashboardActivty
 */
public class BubbleMenuButton extends RelativeLayout {

    DynamicColorImageView imageButton;

    OnClickListener imageBtnClickListener;

    public BubbleMenuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttribrutes(attrs);
    }

    public BubbleMenuButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttribrutes(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BubbleMenuButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setAttribrutes(attrs);
    }

    private void init(){
        getContext();
        inflate(getContext(), R.layout.bubble_menu_button,this);
        imageButton = (DynamicColorImageView) findViewById(R.id.menu_btn_imageView);
        imageButton.setOnClickListener(imageBtnClickListener);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        imageBtnClickListener = l;
        if(imageButton!=null){
            imageButton.setOnClickListener(l);
        }
    }


    protected void setAttribrutes(AttributeSet attrs) {

        TypedArray attributes = getContext().obtainStyledAttributes(attrs,
                R.styleable.BubbleMenuButton);
        try {
            init();


        } finally {
            attributes.recycle();
        }
    }

}

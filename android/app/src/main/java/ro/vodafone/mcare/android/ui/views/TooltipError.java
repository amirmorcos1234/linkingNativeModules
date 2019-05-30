package ro.vodafone.mcare.android.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;

/**
 * Created by Bogdan Marica on 6/18/2017.
 */


public class TooltipError extends LinearLayout {

    Context context;

    public TooltipError(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public TooltipError(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public TooltipError(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TooltipError(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init(attrs);
    }

    void init() {
        inflate(context, R.layout.tooltip_error, this);
        setVisibility(GONE);
    }

    void init(AttributeSet attributeSet) {
        inflate(context, R.layout.tooltip_error, this);

        setAttributes1(attributeSet);

        setVisibility(GONE);
    }

    public void setText(int stringResource) {

        ((TextView) findViewById(R.id.errorTextMessage)).setText(stringResource);
        ((DynamicColorImageView) findViewById(R.id.triangle)).setDrawableColor(R.color.snackBarBackgroundColor);
        findViewById(R.id.tooltip_container).setBackgroundColor(getResources().getColor(R.color.snackBarBackgroundColor));

    }

    public void setText(String stringResource) {

        ((TextView) findViewById(R.id.errorTextMessage)).setText(stringResource);
        ((DynamicColorImageView) findViewById(R.id.triangle)).setVisibility(VISIBLE);
        ((DynamicColorImageView) findViewById(R.id.triangle)).setDrawableColor(R.color.snackBarBackgroundColor);
        findViewById(R.id.tooltip_container).setBackgroundColor(getResources().getColor(R.color.snackBarBackgroundColor));

    }

    public void addSpanableAfterText(final String text, final OnClickListener clickAction) {
//TODO XOXOXOXOXO

        TextView textView = findViewById(R.id.errorTextMessage);

        SpannableString hashText = new SpannableString(textView.getText().toString() + text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                clickAction.onClick(widget);
            }
        };

        hashText.setSpan(new SpannableString(textView.getText().toString()), 0, textView.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        hashText.setSpan(clickableSpan, textView.getText().toString().length(), textView.getText().toString().length() + text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        textView.setText(hashText);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);

    }


    private void setAttributes1(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TooltipError);

        String s = a.getString(R.styleable.TooltipError_errorTextDefaultMessage);
        ((TextView) findViewById(R.id.errorTextMessage)).setText(s);

        a.recycle();
    }

    public void setColorBackground(int colorId) {
        ((DynamicColorImageView) findViewById(R.id.triangle)).setDrawableColor(colorId);
        findViewById(R.id.tooltip_container).setBackgroundColor(getResources().getColor(colorId));
    }


}
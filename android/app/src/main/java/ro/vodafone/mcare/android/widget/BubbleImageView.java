package ro.vodafone.mcare.android.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;

/**
 * Created by bogdan marica on 4/6/2017.
 */

public class BubbleImageView extends RelativeLayout {
    View chatBubbleView;
    TextView bubbleNumberTextView;
    ImageView imageView;
    int drawableRes = -1;


    public BubbleImageView(Context context) {
        super(context);
        init();
    }

    public ImageView getImageView() {
        return imageView;
    }

    public BubbleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BubbleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BubbleImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    int messagesCount;

    public void init() {
        inflate(getContext(), R.layout.bubble_image_view, this);
        bubbleNumberTextView = (TextView) findViewById(R.id.bubble_text);
        imageView = (ImageView) findViewById(R.id.bubble_image);
        chatBubbleView = findViewById(R.id.chat_bubble_layout);

        if (drawableRes != -1)
            imageView.setImageResource(drawableRes);
        if (bubbleNumberTextView != null)
            bubbleNumberTextView.setVisibility(GONE);
    }

    //
    public void setCount(int count) {
        messagesCount = count;
        if (count > 0) {
            showBubble(true);
            bubbleNumberTextView.setText(String.valueOf(count));
        }
        else
            showBubble(false);
    }

    public int getCount() {
      return messagesCount;
    }

    public void setImageResource(@DrawableRes int resId) {
//        D.w("3");
        imageView.setImageResource(resId);
    }

    public void showBubble(boolean show) {
        if (!show)
            bubbleNumberTextView.setVisibility(GONE);
        else
            bubbleNumberTextView.setVisibility(VISIBLE);

    }

    public ImageView getImageview() {
        return imageView;
    }


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        chatBubbleView.setVisibility(visibility);
    }
}

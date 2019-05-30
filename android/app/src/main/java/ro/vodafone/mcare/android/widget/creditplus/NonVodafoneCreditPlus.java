package ro.vodafone.mcare.android.widget.creditplus;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Victor Radulescu on 7/7/2017.
 */

public class NonVodafoneCreditPlus extends CreditPlusWidget{
    public NonVodafoneCreditPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonVodafoneCreditPlus(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NonVodafoneCreditPlus(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void init(int imageInsideMargin) {
        super.init(imageInsideMargin);
        textValue.setVisibility(GONE);
        textValue.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        textValue.setPadding(textLeftPadding,textLabel.getPaddingTop(),textLabel.getPaddingRight(),textLabel.getPaddingBottom());
        textLabel.setPadding(textLeftPadding,textLabel.getPaddingTop(),textLabel.getPaddingRight(),textLabel.getPaddingBottom());

        textLabel.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        textLabel.getLayoutParams().height = (int) (2*componentsSize/3);


        LinearLayout.LayoutParams lpLabel = (LinearLayout.LayoutParams) textLabel.getLayoutParams();
        lpLabel.setMargins(lpLabel.leftMargin,lpLabel.topMargin, (int) componentsSize/5,lpLabel.bottomMargin);
        LinearLayout.LayoutParams lpValue = (LinearLayout.LayoutParams) textLabel.getLayoutParams();
        lpValue.setMargins(lpValue.leftMargin,lpValue.topMargin, (int) componentsSize/5,lpValue.bottomMargin);

        textLabel.setText("Reîncarcă o cartelă \n Vodafone");
        textLabel.setLines(2);
    }
}

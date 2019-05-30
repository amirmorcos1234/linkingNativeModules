package ro.vodafone.mcare.android.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

public class PrefixCustomAutoCompleteEditText extends CustomAutoCompleteEditText {

//    float mOriginalLeftPadding = -1;

    public PrefixCustomAutoCompleteEditText(Context context) {
        super(context);
    }

    public PrefixCustomAutoCompleteEditText(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public PrefixCustomAutoCompleteEditText(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }


//    @Override
//    protected void onMeasure(int widthMeasureSpec,
//                             int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        calculatePrefix();
//    }
//
//    private void calculatePrefix() {
//        if (mOriginalLeftPadding == -1) {
//            String prefix = (String) getTag();
//            float[] widths = new float[prefix.length()];
//            getPaint().getTextWidths(prefix, widths);
//            float textWidth = 0;
//            for (float w : widths) {
//                textWidth += w;
//            }
//            mOriginalLeftPadding = getCompoundPaddingLeft();
//            setPadding((int) (textWidth + mOriginalLeftPadding),
//                    getPaddingRight(), getPaddingTop(),
//                    getPaddingBottom());
//        }
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        String prefix = (String) getTag();
//        canvas.drawText(prefix, mOriginalLeftPadding,
//                getLineBounds(0, null), getPaint());
//    }

    private String mPrefix = "+"; // can be hardcoded for demo purposes
    private Rect mPrefixRect = new Rect(); // actual prefix size

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        getPaint().getTextBounds(mPrefix, 0, mPrefix.length(), mPrefixRect);
        mPrefixRect.right += getPaint().measureText(""); // add some offset
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(mPrefix, super.getCompoundPaddingLeft(), getBaseline(), getPaint());
    }

    @Override
    public int getCompoundPaddingLeft() {
        return super.getCompoundPaddingLeft() + mPrefixRect.width();
    }

}

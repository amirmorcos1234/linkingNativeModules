package ro.vodafone.mcare.android.ui.views.expandabales;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.DynamicColorImageView;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Victor Radulescu on 3/27/2017.
 */

public class ExpandableTextViewGroup extends ExpandableViewGroup {


    @BindView(R.id.currentServiceTextView)
    VodafoneTextView currentServiceTextView;
    @BindView(R.id.nameTextView)
    VodafoneTextView nameTextView;
    @BindView(R.id.detailsTextView)
    VodafoneTextView detailsTextView;
    @BindView(R.id.arrow_indicator)
    DynamicColorImageView arrowImageView;

    ExpandableTextViewGroupListener expandableTextViewGroupListener;
    String dot ="\u2022";
    String end ="\n";

    private static final String TAG = "ExpandableTvGroup";

    @Override
    @OnClick(NOT_EXPANDED_GROUP_ID)
    public void toggle(View view){
      super.toggle(view);
    }

    SpannableStringBuilder details= new SpannableStringBuilder("");

    public ExpandableTextViewGroup(Context context) {
        super(context);
    }

    public ExpandableTextViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableTextViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void setContent(){
        //Html.ImageGetter
        inflate(getContext(), R.layout.expandable_textview_group,this);
        ButterKnife.bind(this);
        hideExpandedView();
    }

    @Override
    protected View getArrowView() {
        return arrowImageView;
    }

    @Override
    protected View getExpandedView() {
        return detailsTextView;
    }

    public void build(ExpandableTextViewGroupListener expandableTextViewGroupListener){
        this.expandableTextViewGroupListener =expandableTextViewGroupListener;
        unExpand(0);
    }


    public void setCurrentService(String currentService) {
        currentServiceTextView.setText(currentService);
    }
    public void addToDetails(String name, String amount, String unit, boolean showDot){
        if(amount==null || name==null || unit == null){
            return;
        }

        String finalText;
        if(showDot) {
            finalText = (details.toString().isEmpty() ? "" : end) + dot + " " + amount + " " + unit+ " " + name;
            SpannableStringBuilder sb = new SpannableStringBuilder(finalText);

            StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD);
            Log.d(TAG, finalText.indexOf(dot) + " - " + (finalText.indexOf(dot) + (dot+" "+amount + " " + unit).length()));
            //sb.setSpan(b, 0, finalText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(b, finalText.indexOf(dot), finalText.indexOf(dot) + (dot+" "+amount + " " + unit).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        else
        {
            finalText = (details.toString().isEmpty() ? "" : end) + " " + amount + " " + unit + " " + name;
        }

        details.append(finalText);
        detailsTextView.setText(details);

    }

    public void setNameTextView(String name){
        nameTextView.setText(name);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(getHeight()>0){
            expandableTextViewGroupListener.onExpandableTextViewGroupMeasured(getHeight());
        }

    }

    public interface ExpandableTextViewGroupListener {
        void onExpandableTextViewGroupMeasured(int height);
    }
}

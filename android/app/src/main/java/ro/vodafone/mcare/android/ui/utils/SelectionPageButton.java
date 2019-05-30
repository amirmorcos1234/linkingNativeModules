package ro.vodafone.mcare.android.ui.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.activities.TopUpActivity;
import ro.vodafone.mcare.android.ui.activities.loyalty.LoyaltyActivity;
import ro.vodafone.mcare.android.ui.activities.offers.OffersActivity;
import ro.vodafone.mcare.android.ui.fragments.offers.OffersSelectionPageFragment;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.FragmentUtils;

/**
 * Created by Bivol Pavel on 23.02.2017.
 */
public class SelectionPageButton extends LinearLayout {

    private static final String TAG = "SelectionPageButton";
    private Context context;

    private ImageView buttonIcon;
    private View buttonLine;
    private VodafoneTextView buttonName;
    private VodafoneTextView buttonDescription;

    private View view;

    private Object target;

    private String buttonTittle;

    public SelectionPageButton(Context context, int buttonLineColor, String buttonName, String buttonDescription, Object target, Drawable buttonIcon) {
        super(context);
        this.context = context;
        init(buttonLineColor, buttonName, buttonDescription, target, buttonIcon);
    }

    private void init(int buttonLineColor, String buttonName, String buttonDescription, Object target, Drawable buttonIcon) {
        view = inflate(context, R.layout.selection_page_button, null);

        this.buttonLine = view.findViewById(R.id.button_line);
        this.buttonName = (VodafoneTextView) view.findViewById(R.id.button_name);
        this.buttonDescription = (VodafoneTextView) view.findViewById(R.id.button_description);
        this.buttonIcon = (ImageView) view.findViewById(R.id.button_icon);

        this.buttonLine.setBackgroundColor(buttonLineColor);
        this.buttonName.setText(buttonName);

        if (context instanceof OffersActivity) {
            if (FragmentUtils.getVisibleFragment((OffersActivity) context, false) instanceof OffersSelectionPageFragment) {
                this.buttonName.setTypeface(Fonts.getVodafoneRGBD());
            }
        }

        if(context instanceof LoyaltyActivity){
            this.buttonName.setTypeface(null, Typeface.BOLD);
        }


        if (buttonDescription != null) {
            if (context instanceof OffersActivity) {
               this.buttonDescription.setTypeface(null, Typeface.BOLD);
            }
            String finalText;
//            if (target instanceof BeoFragment && VodafoneController.getInstance().getUser() instanceof PrepaidUser) {
//                finalText = buttonDescription;
//            } else {
                finalText = buttonDescription;
//            }

            if(!(context instanceof TopUpActivity) && !(context instanceof LoyaltyActivity)){
                SpannableStringBuilder sb = new SpannableStringBuilder(finalText);

                StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD);
                Log.d(TAG, finalText.indexOf(buttonDescription) + " - " + (finalText.indexOf(buttonDescription) + buttonDescription.length()));
                sb.setSpan(b, finalText.indexOf(buttonDescription), finalText.indexOf(buttonDescription) + buttonDescription.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                this.buttonDescription.setText(sb);
            } else {
                this.buttonDescription.setText(finalText);
            }

            this.buttonDescription.setVisibility(VISIBLE);

        }

        if (buttonIcon != null) {
            this.buttonIcon.setVisibility(VISIBLE);
            this.buttonIcon.setImageDrawable(buttonIcon);
        }

        setButtonTittle(buttonName);
        setTarget(target);


    }

    public void addButton(ViewGroup viewGroup) {
        viewGroup.addView(view);
    }

    public View getLayoutView() {
        return view;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public String getButtonTittle() {
        return buttonTittle;
    }

    public void setButtonTittle(String buttonTittle) {
        this.buttonTittle = buttonTittle;
    }
}

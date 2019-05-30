package ro.vodafone.mcare.android.ui.activities.support;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.viewholders.general.DynamicViewHolder;

/**
 * Created by Bogdan Marica on 10/11/2017.
 */

public class ChatElementViewHolder extends DynamicViewHolder<JsonItem, View> {

    public ChatElementViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setupWithData(Activity activity, JsonItem ji) {

        if (ji != null) {

            ((TextView) getView().findViewById(R.id.optionName)).setText(ji.getTitle());
            final LinearLayout contentContainer = ((LinearLayout) getView().findViewById(R.id.contentContainer));

            int defaultPadding = ScreenMeasure.dpToPx(10);
            contentContainer.setPadding(defaultPadding, 0, defaultPadding, defaultPadding);

            final ImageView expandButton = ((ImageView) getView().findViewById(R.id.expandButton));
            RelativeLayout clickReceiver = ((RelativeLayout) getView().findViewById(R.id.recycler_item_click_Receiver));

            contentContainer.addView(getViewWithText(ji.getContent()));
            expandButton.setImageDrawable(ContextCompat.getDrawable(VodafoneController.currentActivity(), R.drawable.chevron_down_48_red));
            expandButton.getDrawable().mutate();

            clickReceiver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animateArrow(contentContainer, expandButton);
                }
            });

        }
    }

    private void animateArrow(LinearLayout contentContainer, ImageView arrowView){
        int start = contentContainer.getVisibility()==View.VISIBLE ? 180 : 0;
        int end = contentContainer.getVisibility() == View.VISIBLE ? 0 : 180;
        ObjectAnimator animator = ObjectAnimator.ofFloat(arrowView, "rotation", start, end);
        animator.setDuration(120);
        animator.start();

        contentContainer.setVisibility(contentContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private TextView getViewWithText(String text) {
        TextView tv = new TextView(VodafoneController.currentActivity());
        tv.setText(text);
        return tv;
    }

}

package ro.vodafone.mcare.android.ui.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ro.vodafone.mcare.android.R;

/**
 * Created by User on 06.12.2017.
 */

public class CustomSnackBar extends BaseTransientBottomBar<CustomSnackBar> {
    /**
     * Constructor for the transient bottom bar.
     *
     * @param parent              The parent for this transient bottom bar.
     * @param content             The content view for this transient bottom bar.
     * @param contentViewCallback The content view callback for this transient bottom bar.
     */
    protected CustomSnackBar(@NonNull ViewGroup parent, @NonNull View content, @NonNull ContentViewCallback contentViewCallback) {
        super(parent, content, contentViewCallback);
    }

    public static CustomSnackBar make(@NonNull Activity activity, CharSequence message, boolean success) {
        ViewGroup parent = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content));
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View content = inflater.inflate(R.layout.custom_snack_bar, parent, false);
        final ContentViewCallback viewCallback = new ContentViewCallback(content);
        final CustomSnackBar customSnackBar = new CustomSnackBar(parent, content, viewCallback);
        customSnackBar.setDuration(3500);
        customSnackBar.setBackgroundColor();
        customSnackBar.setText(message);
        customSnackBar.setSuccess(success);
        return customSnackBar;
    }

    private void setText(CharSequence text) {
        TextView textView = getView().findViewById(R.id.message);
        textView.setText(text);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(ContextCompat.getColor(getContext(), R.color.transparent));
    }

    private void setSuccess(boolean success) {
        if (success)
            getView().findViewById(R.id.succes_icon).setVisibility(View.VISIBLE);
        else
            getView().findViewById(R.id.error_icon).setVisibility(View.VISIBLE);
    }

    private void setBackgroundColor() {
        getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blacktransparent));
    }

    private static class ContentViewCallback implements BaseTransientBottomBar.ContentViewCallback {
        private View content;

        ContentViewCallback(View content) {
            this.content = content;
        }

        @Override
        public void animateContentIn(int delay, int duration) {
//            content.getRootView().setAlpha(0f);
            ViewCompat.animate(content.getRootView())
                    .alpha(1f)
                    .setDuration(1000)
                    .setStartDelay(delay);
        }

        @Override
        public void animateContentOut(int delay, int duration) {

        }
    }
}

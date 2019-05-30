package ro.vodafone.mcare.android.ui.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.utils.D;

/**
 * Created by Cristian Neagu on 17/06/2017.
 * .
 */

public class CustomToast extends Toast {
    private static CustomToast currentToast;
    private static final int TOAST_LENGTH_DELAY = 3500;

    private boolean dummy = false;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SuppressLint("InflateParams")
    public CustomToast(Context context, String message, boolean success, boolean errorIcon) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);

        TextView textView = (TextView) view.findViewById(R.id.message);
        setMessage(message);
        textView.setText(getMessage());


        if (errorIcon) {
            ImageView errorIconView = (ImageView) view.findViewById(R.id.error_icon);
            errorIconView.setImageResource(R.drawable.toast_warning);
        }

        if (success) {
            view.findViewById(R.id.succes_icon).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.error_icon).setVisibility(View.VISIBLE);
        }

        setView(view);
        setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL | Gravity.FILL_VERTICAL, 0, 0);

    }

    private CustomToast(String message) {
        super(VodafoneController.getInstance());
        dummy = true;
    }

    @Override
    public void show() {
        if (dummy)
            return;
        else
            super.show();
    }

    public static class Builder {
        private Context context;
        private String message;
        private boolean errorIcon;
        private boolean success;
        private int duration = LENGTH_LONG;


        public Builder(@NonNull Context context) {
            this.context = context;
        }


        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder message(@StringRes int messageRes) {
            message = VodafoneController.getInstance().getString(messageRes);
            return this;
        }

        public Builder errorIcon(boolean errorIcon) {
            this.errorIcon = errorIcon;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public CustomToast build() {
            if (context == null)
                return new CustomToast(message);
            CustomToast toast = new CustomToast(context, message, success, errorIcon);
            toast.setDuration(duration);

            return toast;
        }

        public CustomToast show() {
            try {
                if (currentToast != null)
                    if(currentToast.getMessage() != null && message != null && currentToast.getMessage().equals(message)){
                        currentToast.cancel();
                    }
            } catch (Exception e) {
                //Crashlytics.logException(e);
                D.e("Exception on show() CustomToast");
            }
            final CustomToast toast = build();
            if (toast.getDuration() > 0)
                VodafoneController.getInstance().handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (currentToast == toast)
                            currentToast = null;
                    }
                }, TOAST_LENGTH_DELAY);
            currentToast = toast;
            toast.show();
            return toast;
        }
    }

    public static void resetCurrentToast() {
        if(currentToast!=null){
            currentToast.cancel();
        }
        CustomToast.currentToast = null;
    }
}

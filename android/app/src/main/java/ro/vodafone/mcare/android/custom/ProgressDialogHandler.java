package ro.vodafone.mcare.android.custom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.widget.ProgressBar;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

public class ProgressDialogHandler extends ProgressDialog {

    private static final String TAG = "ProgressDialogHandler";
    private Context mContext;
    private VodafoneTextView loadingTextView;
    private boolean isVisible = false;
    private String loadingMessage;
    private static int theme = R.style.TransparentTheme;

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public ProgressDialogHandler(Context context, int theme, String loadingMessage) {
        super(context, theme);
        mContext = context;
        this.loadingMessage = loadingMessage;
        setProgressStyle(android.R.attr.progressBarStyleLarge);
    }

    public ProgressDialogHandler(Context context, String loadingMessage) {
        super(context);
        mContext = context;
        this.loadingMessage = loadingMessage;
        setProgressStyle(android.R.attr.progressBarStyleLarge);
    }

    @Override
    public void show() {
        super.show();
        setContentView(R.layout.custom_progressbar_dialog);
        loadingTextView = (VodafoneTextView) findViewById(R.id.loading_textview);
        loadingTextView.setText(loadingMessage);
        ((ProgressBar) findViewById(R.id.loading_spinner)).getIndeterminateDrawable().setColorFilter(Color.WHITE,PorterDuff.Mode.MULTIPLY);
    }

    public void hide() {
        dismiss();
    }

//    public static ProgressDialog progressDialogConstructor(final Context context, int loadingMessage) {
//        ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(context, loadingMessage);
//        progressDialogHandler.setIndeterminate(true);
//        progressDialogHandler.setCancelable(true);
//        progressDialogHandler.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                Log.d(TAG, "onCancel");
//                if(context != null && context instanceof Activity){
//                    ((Activity) context).onBackPressed();
//                }
//            }
//        });
//        return progressDialogHandler;
//    }

    public static ProgressDialog progressDialogConstructor(final Context context, String loadingMessage) {

        ProgressDialogHandler progressDialogHandler = new ProgressDialogHandler(context, theme, loadingMessage);
        progressDialogHandler.setIndeterminate(true);
        progressDialogHandler.setCancelable(true);
        progressDialogHandler.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d(TAG, "onCancel");
                if(context != null && context instanceof Activity){
                    ((Activity) context).onBackPressed();
                }
            }
        });
        return progressDialogHandler;
    }

    public void setTheme(int newTheme){
        theme = newTheme;
    }

}
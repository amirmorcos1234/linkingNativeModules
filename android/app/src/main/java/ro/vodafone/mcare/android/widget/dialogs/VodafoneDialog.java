package ro.vodafone.mcare.android.widget.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ro.vodafone.mcare.android.R;

/**
 * Created with the build logic pattern in mind.
 * Created by Victor Radulescu on 12/16/2016.
 */

public class VodafoneDialog extends Dialog  {

    public Activity activity;

    private boolean isColorInverted = false;

    /**
     * Default action for positive action.
     */
    @OnClick(R.id.action_positive)
    public void positiveAction(){
        dismiss();
    }


    /**
     * Default action for negative action.
     */
    @OnClick(R.id.action_negative)
    public void negativeAction(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && activity!=null) {
            activity.finishAndRemoveTask ();
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN  && activity!=null) {
            activity.finishAffinity();
        }
        else if(activity != null){
            activity.finish();
            System.exit(0);
        }else{
            System.exit(0);
        }
    }

    @BindView(R.id.message)
    TextView messageTextView;

    @BindView(R.id.action_positive)
    Button actionPositiveButton;

    @BindView(R.id.action_negative)
    Button actionNegativeButton;


    private View.OnClickListener negativeAction,positiveAction;

    private String message,negativeMessage,positiveMessage;

    private boolean showNegativeActionButton =true,showPositiveActionButton = true;

    public VodafoneDialog(Activity context) {
        super(context);
        this.activity = context;
    }

    public VodafoneDialog(Activity context,String message) {
        super(context);
        this.activity = context;

        this.message = message;

    }
    public VodafoneDialog(Activity context,String message,String negativeMessage,String positiveMessage) {
        super(context);
        this.activity = context;

        this.message = message;
        this.negativeMessage= negativeMessage;
        this.positiveMessage = positiveMessage;

    }

    public VodafoneDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected VodafoneDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.general_dialog);

        ButterKnife.bind(this);

        initViews();
    }

    public VodafoneDialog setNegativeAction(View.OnClickListener clickListener){

        this.negativeAction = clickListener;

        if(actionNegativeButton!=null){
            actionNegativeButton.setOnClickListener(negativeAction);
        }
        return this;
    }
    public VodafoneDialog setPositiveAction(View.OnClickListener clickListener) {

        this.positiveAction = clickListener;

        if(actionPositiveButton!=null){
            actionPositiveButton.setOnClickListener(positiveAction);
        }
        return this;
    }

    /**
     *Sets just one button instead of two, positive message theme.
     * @param message of the positive button
     * @return this dialog object
     */
    public VodafoneDialog setOnePositiveButton(String message){

        this.positiveMessage = message;

        showNegativeActionButton = false;
        showPositiveActionButton = true;

        if(actionNegativeButton!=null){
            actionNegativeButton.setVisibility(View.GONE);
        }
        if(actionPositiveButton!=null){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f);
            actionPositiveButton.setLayoutParams(params);
            actionPositiveButton.setText(positiveMessage);
        }
        return this;
    }

    /**
     *Sets just one button instead of two, negative message theme.
     * @param message of the negative button
     * @return this dialog object
     */
    public VodafoneDialog setOneNegativeButton(String message){

        this.negativeMessage = message;

        showNegativeActionButton = true;
        showPositiveActionButton = false;

        if(actionPositiveButton!=null){
            actionPositiveButton.setVisibility(View.GONE);
        }
        if(actionNegativeButton!=null){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f);
            actionNegativeButton.setLayoutParams(params);
            actionNegativeButton.setText(negativeMessage);
        }
        return this;
    }
    /**
     *Sets message of the negative action button
     * @param message of the negative button
     * @return this dialog object
     */
    public VodafoneDialog setNegativeMessage(String message){

        this.negativeMessage = message;

        if(actionNegativeButton!=null){
            actionNegativeButton.setText(message);
        }
        return  this;
    }
    /**
     *Sets message of the positive action button
     * @param message of the negative button
     * @return this dialog object
     */
    public VodafoneDialog setPositiveMessage(String message){

        this.positiveMessage = message;

        if(actionPositiveButton!=null){
            actionPositiveButton.setText(message);
        }
        return  this;
    }

    private void  initViews(){

        messageTextView.setText(message);

        if(positiveAction!=null){
            setPositiveAction(positiveAction);
        }
        if(negativeAction!=null){
            setNegativeAction(negativeAction);
        }

        if(showPositiveActionButton && !showNegativeActionButton)
        {
            setOnePositiveButton(positiveMessage);
        }
        else if (!showPositiveActionButton && showNegativeActionButton)
        {
            setOneNegativeButton(negativeMessage);
        }
        else
        {
            setPositiveMessage(positiveMessage);
            setNegativeMessage(negativeMessage);
        }

    }
    public VodafoneDialog setDismissActionOnNegative(){
        setNegativeAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return this;
    }
    public VodafoneDialog setDismissActionOnPositive() {
        setPositiveAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return this;
    }


/*    public VodafoneDialog setDarkGraySecondaryButtonTextColor(){
        if(actionNegativeButton!=null)
            actionNegativeButton.setTextColor(ContextCompat.getColor(getContext(),R.color.dark_gray_text_color));
        return this;
    }

    public VodafoneDialog setNudgeBackgroundSecondaryButton(){
        actionNegativeButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selector_button_background_nudge_secondary));
        return this;
    }*/

}

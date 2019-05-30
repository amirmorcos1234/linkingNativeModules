package ro.vodafone.mcare.android.card;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

public class SaveCreditCard extends VodafoneAbstractCard {
    private boolean isSelected = false;
    @BindView(R.id.save_linear)
    LinearLayout save_linear;
    @BindView(R.id.pay_with_alt_card)
    RadioButton pay_with_alt_card;
    @BindView(R.id.check_box_linear)
    LinearLayout check_box_linear;
    @BindView(R.id.checkbox)
    CheckBox checkBox;
    @BindView(R.id.checkBoxText)
    VodafoneTextView checkBoxText;
    OnClickListener checkBoxListener=new OnClickListener() {
        @Override
        public void onClick(View v) {

            //if(check_box_linear.isEnabled())
                checkBox.setChecked(!checkBox.isChecked());
        }
    };

    public SaveCreditCard(Context context) {
        super(context);
        init();
    }


    @Override
    protected int setContent() {
        return R.layout.pay_with_new_card;
    }
    private void init() {
        ButterKnife.bind(this);
        setBackGround();
        setContentViewVisibility();
        LayoutParams mCardViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mCardViewParams.setMargins(0,0,0,0);
        getCardView().setLayoutParams(mCardViewParams);
        //check_box_linear.setOnClickListener(checkBoxListener);
        checkBoxText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isEnabled())
                    checkBox.performClick();
            }
        });
        pay_with_alt_card.setClickable(false);
    }
    private SaveCreditCard setBackGround(){
        if(isSelected){
            getCardView().setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.turqoise_border_with_gray_background, null));
        }
        else{
            getCardView().setBackground(ResourcesCompat.getDrawable(getResources(), R.color.white, null));
        }
        return this;
    }
    private void setContentViewVisibility(){
        if(isSelected){
            save_linear.setVisibility(VISIBLE);
        } else{
          save_linear.setVisibility(GONE);
        }
    }
    public void selectRadioButton(boolean flag){
        isSelected=flag;
        pay_with_alt_card.setChecked(isSelected);
        setBackGround();
        setContentViewVisibility();

    }
    public SaveCreditCard enableCheckBox(boolean flag){

        if(flag) {
            check_box_linear.setVisibility(VISIBLE);
        }else {
            check_box_linear.setVisibility(GONE);
        }
        return this;
    }
    public CheckBox getCheckBox(){
        return checkBox;
    }
    public RadioButton getRadioButton(){
        return pay_with_alt_card;
    }


}

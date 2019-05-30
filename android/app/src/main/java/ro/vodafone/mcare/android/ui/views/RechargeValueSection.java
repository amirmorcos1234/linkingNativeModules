package ro.vodafone.mcare.android.ui.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.client.model.realm.system.TopUPLabels;
import ro.vodafone.mcare.android.client.model.recommendedRecharge.RecommendedRecharge;
import ro.vodafone.mcare.android.custom.CustomEditText;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.FragmentUtils;

/**
 * Created by Bivol Pavel on 25.02.2017.
 */
public class RechargeValueSection extends LinearLayout {

    private Context context;
    private LinearLayout firstValueButton;
    private LinearLayout secondValueButton;
    private LinearLayout thirdValueButton;
    private LinearLayout otherButton;
    private LinearLayout otherValueLayout;

    private LinearLayout radioGroupLayout;

    public CustomEditText otherValueInput;

    private VodafoneTextView creditValabilityLabel;

    private LinearLayout otherValueErrorLayout;
    private LinearLayout valueSelectionErrorLayout;

    private int selectedAmount;

    private List<RecommendedRecharge> recommendedRecharges;
    private List<RadioButtonBobble> radioButtonBobbles;
    boolean isTransferCredit;


    public RechargeValueSection(Context context) {
        super(context);
        this.context = context;
        if (!isInEditMode()) {
            init(null);
        }
    }

    public RechargeValueSection(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (!isInEditMode()) {
            init(attrs);
        }
    }

    public RechargeValueSection(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        if (!isInEditMode()) {
            init(attrs);
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RechargeValueSection(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        if (!isInEditMode()) {
            init(attrs);
        }
    }


    private void init(AttributeSet attrs) {
        inflate(context, R.layout.recharge_value_section, this);

        setupLabels();

        radioGroupLayout = (LinearLayout) findViewById(R.id.radio_group_layout);
        otherValueInput = (CustomEditText) findViewById(R.id.other_value_input);
        otherValueLayout = (LinearLayout) findViewById(R.id.other_value_layout);
        otherValueErrorLayout = (LinearLayout) findViewById(R.id.other_value_error_layout);
        valueSelectionErrorLayout = (LinearLayout) findViewById(R.id.recharge_value_section_error);

        creditValabilityLabel = (VodafoneTextView) findViewById(R.id.credit_valability_label);

        setRecommendedRechargeValues(null, true,false);
    }

    public void setMarginsRadioGroupLayout(int l, int t, int r, int b) {
        if (radioGroupLayout.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) radioGroupLayout.getLayoutParams();
            p.setMargins(l, t, r, b);
            radioGroupLayout.requestLayout();
        }
    }

    private void setupLabels() {
        ((VodafoneTextView) findViewById(R.id.recharge_value_label)).setText(TopUPLabels.getTop_up_recharge_value());
    }

    public void setupLabels(String label) {
        ((VodafoneTextView) findViewById(R.id.recharge_value_label)).setText(label);
        isTransferCredit = true;
    }

    private void setValidity(int validity) {

        if (isTransferCredit)
            creditValabilityLabel.setText(TopUPLabels.getTop_up_transfer_credit_availability_value_card().replace("%@", String.valueOf(validity)));
        else
            creditValabilityLabel.setText(TopUPLabels.getTop_up_credit_validity_time() + validity + " " + TopUPLabels.getTop_up_days());

        if (!otherValueInput.isHighlighted() && validity > 0) {
            creditValabilityLabel.setVisibility(VISIBLE);
        } else {
            creditValabilityLabel.setVisibility(GONE);
        }
    }

    public void setRecommendedRechargeValues(List<RecommendedRecharge> recommendedRecharges, boolean isOtherButtonVisible,boolean isDefaultSelectedBobble) {
        if (recommendedRecharges != null) {
            this.recommendedRecharges = recommendedRecharges;
        } else {
            createDefaultRecommendedRecharges();
        }
        buildBobbles(isOtherButtonVisible);
        setCliclListner();
        if(isDefaultSelectedBobble)
        setDefaultSelectedBobble(1);
    }


    public void createDefaultRecommendedRecharges() {

        recommendedRecharges = new ArrayList<>();
        recommendedRecharges.add(new RecommendedRecharge(5, getEnteredValueValidity(String.valueOf(5))));
        recommendedRecharges.add(new RecommendedRecharge(6, getEnteredValueValidity(String.valueOf(6))));
        recommendedRecharges.add(new RecommendedRecharge(7, getEnteredValueValidity(String.valueOf(7))));
    }

    private void buildBobbles(boolean isOtherButtonVisible) {

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);

        radioButtonBobbles = new ArrayList<>();
        radioGroupLayout.removeAllViews();

        for (RecommendedRecharge recommendedRecharge : recommendedRecharges) {

            RadioButtonBobble radioButtonBobble = new RadioButtonBobble(context);

            radioButtonBobble.setLayoutParams(param);
            radioButtonBobble.setAmount(recommendedRecharge.getAmount());
            radioButtonBobble.setValidity(recommendedRecharge.getValidity());


            radioGroupLayout.addView(radioButtonBobble);
            radioButtonBobbles.add(radioButtonBobble);
        }

        if (isOtherButtonVisible) {
            RadioButtonBobble radioButtonBobble = new RadioButtonBobble(context);

            radioButtonBobble.setLayoutParams(param);
            radioButtonBobble.setLabel(TopUPLabels.getTop_up_other_value());

            radioGroupLayout.addView(radioButtonBobble);
            radioButtonBobbles.add(radioButtonBobble);
        }
    }


    public void setDefaultSelectedBobble(int i) {
        Log.d("RechargeValueSection", "setDefaultSelectedBobble");
        changeSelectedButton(radioButtonBobbles.get(i));
        //amountButtonSelected(radioButtonBobbles.get(1));
    }

    private void setCliclListner() {
        for (RadioButtonBobble radioButtonBobble : radioButtonBobbles) {
            radioButtonBobble.setOnClickListener(rechargeValuesListner);
        }
    }

    private View.OnClickListener rechargeValuesListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            KeyboardHelper.hideKeyboard((Activity) getContext());
            try {
                changeSelectedButton((RadioButtonBobble) v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void amountButtonSelected(RadioButtonBobble button) {
        selectedAmount = button.getAmount();

        setValidity(button.getValidity());

        if (FragmentUtils.getVisibleFragment((AppCompatActivity) context, false) instanceof Callback) {
            Callback callback = (Callback) FragmentUtils.getVisibleFragment((AppCompatActivity) context, false);

            if (callback != null) {
                callback.selectElement(selectedAmount);

                if (isVisibleOtherValueLayout()) {
                    callback.hideOtherValueField();
                }
            }
        }

        if (otherValueLayout.getVisibility() == VISIBLE) {
            KeyboardHelper.hideKeyboard((Activity) context);
            otherValueLayout.setVisibility(View.GONE);
            // otherValueInput.setText("");
            otherValueInput.removeHighlight();
        }

        if (otherValueErrorLayout.getVisibility() == VISIBLE) {
            otherValueErrorLayout.setVisibility(GONE);
        }
    }

    private void otherButtonSelected() {
        selectedAmount = 0;

        if (FragmentUtils.getVisibleFragment((AppCompatActivity) context, false) instanceof Callback) {
            Callback callback = (Callback) FragmentUtils.getVisibleFragment((AppCompatActivity) context, false);

            if (callback != null) {
                callback.displayOtherValueField();
            }
        }

        otherValueLayout.setVisibility(View.VISIBLE);
        setValidity(getEnteredValueValidity(otherValueInput.getText().toString()));
        if (otherValueInput.hasFocus()) {
            otherValueInput.showDefaultInputStyle();
        }

        if (otherValueInput.isEmpty() || otherValueInput.isValide() != CustomEditText.VALIDE_FIELD_STATUS) {
            otherValueInput.requestFocus();
            otherValueInput.showErrorInputStyle(CustomEditText.EMPTY_FIELD_STATUS);
        }

        //Prefilled input with default value
    }

    TextWatcher amountTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (otherValueInput.hasFocus()) {
                setValidity(getEnteredValueValidity(otherValueInput.getText().toString()));
            }
        }
    };

    public void changeSelectedButton(RadioButtonBobble button) {
        hideRechargeError();
        Log.d("RechargeValueSection", "changeSelectedButton");
        for (RadioButtonBobble radioButtonBobble : radioButtonBobbles) {
            if (radioButtonBobble.isSelected()) {
                radioButtonBobble.unselect();
            }
        }

        if (button.haveAmount()) {
            amountButtonSelected(button);
        } else {
            otherButtonSelected();
        }

        button.onSelect();
    }

    private int getEnteredValueValidity(String enteredAmount) {
        int validity = 0;
        float amount = 0;

        if (!enteredAmount.equals("")) {
            try {
                amount = Float.valueOf(enteredAmount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(amount > 0 && amount < 2){
            validity = 5;
        }else if(amount >= 2 && amount < 3){
            validity = 7;
        }else if(amount >= 3 && amount < 4){
            validity = 10;
        }else if(amount >= 4 && amount < 7){
            validity = 30;
        }else if(amount >= 7 && amount < 11){
            validity = 60;
        }else if(amount >= 11 && amount < 16){
            validity = 90;
        }else if(amount >= 16 && amount < 26){
            validity = 120;
        }else if(amount >= 26 && amount <50){
            validity = 150;
        } else if (amount >= 50 && amount <= 100) {
            validity = 150;
        }

        return validity;
    }

    public void disableRadioButtonKeepPreviousSelection() {
        hideRechargeError();
        Log.d("RechargeValueSection", "changeSelectedButton");
        for (RadioButtonBobble radioButtonBobble : radioButtonBobbles) {
            if (radioButtonBobble.isSelected() && radioButtonBobble.haveAmount()) {
                amountButtonSelected(radioButtonBobble);
            } else {
                otherButtonSelected();
            }
            radioButtonBobble.setAlpha((float) 0.25);
            radioButtonBobble.setEnabled(false);
        }
        creditValabilityLabel.setVisibility(GONE);
        otherValueLayout.setVisibility(GONE);
    }

    public void activateRadioButtons() {
        for (RadioButtonBobble radioButtonBobble : radioButtonBobbles) {
            if (radioButtonBobble.isSelected() && radioButtonBobble.haveAmount()) {
                amountButtonSelected(radioButtonBobble);
            }
            if (radioButtonBobble.isSelected() && !radioButtonBobble.haveAmount()) {
                otherButtonSelected();
            }
            radioButtonBobble.setAlpha((float) 1);
            radioButtonBobble.setEnabled(true);
        }
    }

    public void showError(String errorMessage) {
        otherValueErrorLayout.setVisibility(VISIBLE);
        ((VodafoneTextView) findViewById(R.id.other_value_error_message)).setText(errorMessage);
    }

    public void hideError() {
        otherValueErrorLayout.setVisibility(GONE);
        ((VodafoneTextView) findViewById(R.id.other_value_error_message)).setText("");
    }

    public void showRechargeError(String errorMessage) {
        valueSelectionErrorLayout.setVisibility(VISIBLE);
        ((VodafoneTextView) findViewById(R.id.recharge_value_section_message)).setText(errorMessage);
    }

    public void hideRechargeError() {
        if (valueSelectionErrorLayout.getVisibility() == VISIBLE) {
            valueSelectionErrorLayout.setVisibility(GONE);
        }
        ((VodafoneTextView) findViewById(R.id.recharge_value_section_message)).setText("");
    }

    public boolean isVisibleOtherValueLayout() {
        boolean isVisible = false;

        if (otherValueLayout.getVisibility() == VISIBLE) {
            isVisible = true;
        }
        return isVisible;
    }

    public interface Callback {
        public void selectElement(int amount);

        public void displayOtherValueField();

        public void hideOtherValueField();
    }

    public void setOtherValueInput(int i) {
        otherValueInput.setText(String.valueOf(radioButtonBobbles.get(i).getAmount()));
        setValidity(getEnteredValueValidity(String.valueOf(radioButtonBobbles.get(i).getAmount())));

        otherValueInput.addTextChangedListener(amountTextWatcher);
    }

    public void setOtherValueInputString(String i) {
        otherValueInput.setText(i);
        setValidity(getEnteredValueValidity(i));

        otherValueInput.addTextChangedListener(amountTextWatcher);
    }

}

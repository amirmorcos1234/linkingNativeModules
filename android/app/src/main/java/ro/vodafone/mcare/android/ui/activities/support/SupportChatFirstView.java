package ro.vodafone.mcare.android.ui.activities.support;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinnerAdapter;
import ro.vodafone.mcare.android.utils.D;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by cristi on 24/06/2017.
 * .
 */

public class SupportChatFirstView extends MyChatView implements SupportWindowSpinner.Callback, OnErrorIconClickListener {

    final SupportWindow window;

    VodafoneButton nextStep;

    CustomEditTextCompat firstName;
    CustomEditTextCompat lastName;
    CustomEditTextCompat email;

    SupportWindowSpinner requestSpinner;

    @BindView(R.id.bad_firstName_layout)
    LinearLayout bad_firstName_layout;
    @BindView(R.id.bad_lastName_layout)
    LinearLayout bad_lastName_layout;
    @BindView(R.id.bad_email_layout)
    LinearLayout bad_email_layout;

    @BindView(R.id.bad_request_layout)
    LinearLayout bad_request_layout;
    View.OnClickListener clickedNextStep = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            try {
                closeKeyboard();
                window.email = email.getText().toString();
                window.firstName = firstName.getText().toString();
                window.lastName = lastName.getText().toString();


                window.startChatRequest.setFirstName(window.firstName);
                window.startChatRequest.setLastName(window.lastName);
                window.startChatRequest.setEmail(window.email);
                window.startChatRequest.setCategoryName(requestSpinner.getText().toString());
            } catch (Exception e) {
                D.e("e = " + e);
            }

            int index = requestSpinner.getSelectedIndex();
            window.currentView = new SupportChatSecondView(window, index);
            window.inflateLayout();
        }
    };

    boolean optionSelected = false;
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            if (!firstName.getText().toString().equals("") && !lastName.getText().toString().equals("") && isValidEmail(email.getText().toString()) && selectedSpinnerOption())
                enableButton(nextStep);
            else
                disableButton(nextStep);
        }
    };
    boolean loadedJson = false;
    boolean shopRequestInProcess = true;
    RequestSessionObserver<List<JsonList>> jsonObserver = new RequestSessionObserver<List<JsonList>>() {

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            D.e("e = " + e);
            stopLoading();
            window.inflateError();
        }

        @Override
        public void onNext(List<JsonList> jsonLists) {
//            D.e();
            loadedJson = true;
            window.mJsonLists = jsonLists;
        }

        @Override
        public void onCompleted() {
            super.onCompleted();
            stopLoading();
            inflateSpinner();
        }
    };

    public SupportChatFirstView(@NonNull SupportWindow w) {
        super(w.getContext());
        window = w;

        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_support_chat_first, this);
        ButterKnife.bind(this, v);

        initViews(v);

        disableButton(nextStep);

        completeEmail();
        completeFirstName();
        completeLastName();

        // onResume
        if (window.mJsonLists == null) {

            Observable.timer(1, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            if (shopRequestInProcess)
                                window.showLoadingDialog();
                        }
                    });


            shopRequestInProcess = true;
            window.getChatService().getChatFaqJsonList().subscribe(jsonObserver);
        } else
            inflateSpinner();

        window.changeMenuItemCheckedStateColor(window.views.navigation);
        window.hideFaqSearchButton();

        firstName.setOnErrorIconClickListener(this);
        lastName.setOnErrorIconClickListener(this);
        email.setOnErrorIconClickListener(this);

    }

    void initViews(View v) {

        window.views.scrollView.setEnableScrolling(true);
        window.views.scrollView.setVerticalScrollBarEnabled(true);
        window.views.scrollView.setEnabled(true);

        lastName = (CustomEditTextCompat) v.findViewById(R.id.lastnameInput);
        firstName = (CustomEditTextCompat) v.findViewById(R.id.firstnameInput);
        email = (CustomEditTextCompat) v.findViewById(R.id.emailInput);
        setNoSuggestionsInputType();

        requestSpinner = (SupportWindowSpinner) v.findViewById(R.id.requestSpinnerInput);

        lastName.addTextChangedListener(textWatcher);
        firstName.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        nextStep = (VodafoneButton) v.findViewById(R.id.nextStep);

        firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
//                    D.w("we got focus");
                    displayEditTextError(firstName, bad_firstName_layout, true);
                } else {
//                    D.e("we lost focus");
                    if (firstName.getText().toString().equals(""))
                        displayEditTextError(firstName, bad_firstName_layout, false);
                    else
                        displayEditTextError(firstName, bad_firstName_layout, true);
                }
            }
        });

        lastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    //              D.w("we got focus");
                    displayEditTextError(lastName, bad_lastName_layout, true);
                } else {
//                    D.e("we lost focus");
                    if (lastName.getText().toString().equals(""))
                        displayEditTextError(lastName, bad_lastName_layout, false);
                    else
                        displayEditTextError(lastName, bad_lastName_layout, true);
                }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
//                    D.w("we got focus");
                    displayEditTextError(email, bad_email_layout, true);
                } else {
//                    D.e("we lost focus");
                    if (email.getText().toString().equals(""))
                        displayEditTextError(email, bad_email_layout, false);
                    else if (!isValidEmail(email.getText().toString()))
                        displayEditTextError(email, bad_email_layout, false);
                    else
                        displayEditTextError(email, bad_email_layout, true);
                }
            }
        });

        firstName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    firstName.clearFocus();
                    closeKeyboard();
                }
                return false;
            }
        });

        email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    email.clearFocus();
                    closeKeyboard();
                }
                return false;
            }
        });
    }

    public void completeEmail() {
        if (window.email != null && !window.email.equals("")) {
            if (!window.isEmailBlackListed()) {
                email.setText(window.email);
//            email.setText("maricab95@yahoo.com");

                if (isValidEmail(window.email)) {
                    email.setEnabled(false);
                    email.setFocusable(false);
                    email.setClickable(false);
                    firstName.setImeOptions(EditorInfo.IME_ACTION_DONE);
                }

                //email.setInputType(InputType.TYPE_NULL);
                email.setBackgroundResource(R.drawable.disabled_input);
            } else {
                email.setHint("nume@exemplu.com");
                email.setHintTextColor(ContextCompat.getColorStateList(getContext(), R.color.gray_button_color));
            }
        }
    }

    public void completeFirstName() {
        if (window.firstName != null && !window.firstName.equals(""))
            firstName.setText(window.firstName);
    }

    public void completeLastName() {
        if (window.lastName != null && !window.lastName.equals(""))
            lastName.setText(window.lastName);
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    private void stopLoading() {
        shopRequestInProcess = false;
        window.stopLoadingDialog();
    }

    public boolean selectedSpinnerOption() {
        return optionSelected;
    }

    public boolean isValidEmail(String emailAdress) {
        return window.isEmailBlackListed() || !TextUtils.isEmpty(emailAdress) &&
                ro.vodafone.mcare.android.ui.utils.TextUtils.EMAIL_PATTERN.matcher(emailAdress).matches() &&
                emailAdress.substring(emailAdress.lastIndexOf(".")).length() > 2;
    }

    public void disableButton(Button button) {
        button.setClickable(false);
        button.setEnabled(false);
    }

    public void enableButton(Button button) {
        button.setEnabled(true);
        button.setClickable(true);
        button.setOnClickListener(clickedNextStep);
    }

    @Override
    public void selectSpinnerElement(Object selectedValue) {
        requestSpinner.setText(selectedValue.toString());
    }

    public void inflateSpinner() {
        ArrayList<String> requestArray = new ArrayList<>();

        for (int i = 0; i < window.mJsonLists.size(); i++) {
            requestArray.add(window.mJsonLists.get(i).getName());
        }

        try {

            requestSpinner.setCallback(this);

            VodafoneSpinnerAdapter<String> requestAdapter = new VodafoneSpinnerAdapter<>(getContext(), requestArray, R.drawable.selector);
            requestSpinner.setAdapter(requestAdapter);

            requestSpinner.setText("Te rugăm să selectezi o opțiune ");
            requestSpinner.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray_text_color));

            requestSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    optionSelected = true;

                    if (!firstName.getText().toString().equals("") && !lastName.getText().toString().equals("") && isValidEmail(email.getText().toString()) && selectedSpinnerOption())
                        enableButton(nextStep);
                    else
                        disableButton(nextStep);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (Exception e) {
            D.e("e = " + e);
        }
    }

    public void displayEditTextError(CustomEditTextCompat target, LinearLayout errorLayout, boolean visibility) {
        errorLayout.setVisibility(visibility ? View.GONE : View.VISIBLE);

        if (!target.hasFocus())
            if (!visibility)
                target.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
            else target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
        else target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
    }

    // FIXME: 18.09.2017 @Lucian & Bogdan please check this. When we introduce a space between words in the input field app crashed with BadTokenException. Relates to VNM-7872.
    private void setNoSuggestionsInputType() {
        firstName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        lastName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    @Override
    public void onErrorIconClickListener() {
        if(firstName.isErrorIconTap())
            displayEditTextError(firstName, bad_firstName_layout, true);
        else if (lastName.isErrorIconTap())
            displayEditTextError(lastName, bad_lastName_layout, true);
        else if (email.isErrorIconTap())
            displayEditTextError(email, bad_email_layout, true);
    }
}

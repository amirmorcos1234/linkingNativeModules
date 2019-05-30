package ro.vodafone.mcare.android.ui.activities.support;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafone;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneAction;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneCategory;
import ro.vodafone.mcare.android.client.model.dashboard.vov.VoiceOfVodafoneController;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.realm.UserProfile;
import ro.vodafone.mcare.android.client.model.shop.EmailRequest;
import ro.vodafone.mcare.android.custom.CustomEditTextCompat;
import ro.vodafone.mcare.android.interfaces.OnErrorIconClickListener;
import ro.vodafone.mcare.android.rest.observers.RequestSessionObserver;
import ro.vodafone.mcare.android.ui.activities.DashboardActivity;
import ro.vodafone.mcare.android.ui.utils.CustomToast;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.utils.rxactivityresult.ActivityResult;
import ro.vodafone.mcare.android.ui.utils.rxactivityresult.RxActivityResult;
import ro.vodafone.mcare.android.ui.views.PagingScrollView;
import ro.vodafone.mcare.android.ui.views.buttons.VodafoneButton;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.ui.views.vodafoneSpinner.VodafoneSpinnerAdapter;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.TealiumHelper;
import ro.vodafone.mcare.android.utils.navigation.IntentActionName;
import ro.vodafone.mcare.android.utils.navigation.NavigationAction;
import rx.functions.Action1;

/**
 * Created by cristi on 24/06/2017.
 */
public class SupportEmailView extends MyChatView implements OnErrorIconClickListener {

    public static final long MAX_FILE_DIM = 3145728;
    private final SupportWindow window;
    VodafoneButton inapoiButton;

    CustomEditTextCompat nameInput;
    CustomEditTextCompat subjectInput;
    CustomEditTextCompat descriptionInput;
    CustomEditTextCompat phoneInput;
    CustomEditTextCompat emailInput;
    VodafoneTextView subjectLabel;
    VodafoneTextView fileName;
    VodafoneSpinnerAdapter<String> requestTypeAdapter;
    ArrayList<String> requestTipe = new ArrayList<>();
    Uri fileUri;
    @BindView(R.id.bad_name_layout)
    LinearLayout bad_name_layout;
    @BindView(R.id.bad_subject_layout)
    LinearLayout bad_subject_layout;
    @BindView(R.id.bad_email_layout)
    LinearLayout bad_email_layout;
    @BindView(R.id.bad_number_layout)
    LinearLayout bad_number_layout;
    @BindView(R.id.bad_description_layout)
    LinearLayout bad_description_layout;
    @BindView(R.id.bad_attachement_layout)
    LinearLayout bad_attachement_layout;
    @BindView(R.id.bad_client_layout)
    LinearLayout bad_client_layout;
    @BindView(R.id.bad_request_layout)
    LinearLayout bad_request_layout;
    MultipartBody.Part messagePart;
    boolean addSuffixToEmail = false;
    boolean selectedClient = false;
    boolean selectedRequest = false;
    private SupportWindowSpinner clientType;
    SupportWindowSpinner.Callback cb1 = new SupportWindowSpinner.Callback() {
        @Override
        public void selectSpinnerElement(Object selectedValue) {
            clientType.setText(selectedValue.toString());
        }
    };
    private SupportWindowSpinner requestType;
    SupportWindowSpinner.Callback cb2 = new SupportWindowSpinner.Callback() {
        @Override
        public void selectSpinnerElement(Object selectedValue) {
            requestType.setText(selectedValue.toString());
        }
    };
    private VodafoneButton sendEmail;
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (validateFieldsNoDropDowns())
                enableButton(sendEmail);
            else
                disableButton(sendEmail);
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };
    AdapterView.OnItemSelectedListener customItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            clientType.setValueChoosen(true);
            requestType.setValueChoosen(false);
            selectedClient = true;
            bad_client_layout.setVisibility(View.GONE);

            if (validateFieldsNoDropDowns())
                enableButton(sendEmail);
            else
                disableButton(sendEmail);

            createRequestTypeArray(i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };
    AdapterView.OnItemSelectedListener customRequestListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            requestType.setValueChoosen(true);
            bad_request_layout.setVisibility(View.GONE);
            selectedRequest = true;
            if (validateFieldsNoDropDowns())
                enableButton(sendEmail);
            else
                disableButton(sendEmail);
//            createRequestTypeArray(i);

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };
    private Activity activity;
    private File file;
    View.OnClickListener openFileChooser = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new RxPermissions(window.activity).request(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean granted) {
                            if (granted) {
                                initChooser();
                            }
                        }
                    });
        }
    };

    public SupportEmailView(@NonNull SupportWindow w) {
        super(w.getContext());
        this.window = w;

        if(getContext() != null) {
            try {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_support_email, this);
                // D.e("top = " + window.views.scrollView.getTop());
                //D.e("bot = " + window.views.scrollView.getBottom());
                VodafoneController.getInstance().handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (window.views != null) {
                            window.views.scrollView.scrollTo(0, 0);
                            D.d("top = " + window.views.scrollView.getTop());
                            D.d("bot = " + window.views.scrollView.getBottom());
                        }
                    }
                });

                if (window.views != null)
                    window.views.scrollView.scrollTo(0, 0);

                ButterKnife.bind(this, v);


                clientType = (SupportWindowSpinner) v.findViewById(R.id.selectClient);
                requestType = (SupportWindowSpinner) v.findViewById(R.id.tipulSolicitarii);
                //disableRequestSpinner(false);
                sendEmail = (VodafoneButton) v.findViewById(R.id.sendEmail);
                inapoiButton = (VodafoneButton) v.findViewById(R.id.inapoiButton);
                fileName = (VodafoneTextView) v.findViewById(R.id.fileName);
                subjectLabel = (VodafoneTextView) v.findViewById(R.id.vodafoneTextView7);

                inapoiButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.inflateFaqLayout();
                    }
                });

                LinearLayout myLayout = (LinearLayout) v.findViewById(R.id.mainLayout);
                myLayout.requestFocus();

                setClientTypeAdapter();
                setRequestTypeAdapter();

                clientType.setOnItemSelectedListener(customItemSelectedListener);
                requestType.setOnItemSelectedListener(customRequestListener);

                nameInput = (CustomEditTextCompat) v.findViewById(R.id.nameInput);
                subjectInput = (CustomEditTextCompat) v.findViewById(R.id.subjectInput);
                descriptionInput = (CustomEditTextCompat) v.findViewById(R.id.descriptionText);
                phoneInput = (CustomEditTextCompat) v.findViewById(R.id.phoneInput);
                emailInput = (CustomEditTextCompat) v.findViewById(R.id.emailInput);
                emailInput.setTextColor(ContextCompat.getColor(getContext(), R.color.blackNormal));
                setNoSuggestionsInputType();

                disableButton(sendEmail);
                if (window.views != null)
                    window.views.minimizeButton.setVisibility(View.GONE);

                // clientType.setText("Te rugăm să selectezi o opţiune");
                //clientType.invalidate();
                //clientType.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray_text_color));

                //requestType.setText("Te rugăm să selectezi o opţiune");
                //requestType.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray_text_color));

                nameInput.addTextChangedListener(textWatcher);
                descriptionInput.addTextChangedListener(textWatcher);
                phoneInput.addTextChangedListener(textWatcher);
                emailInput.addTextChangedListener(textWatcher);
                subjectInput.addTextChangedListener(textWatcher);

                nameInput.setOnErrorIconClickListener(this);
                descriptionInput.setOnErrorIconClickListener(this);
                phoneInput.setOnErrorIconClickListener(this);
                emailInput.setOnErrorIconClickListener(this);
                subjectInput.setOnErrorIconClickListener(this);

                nameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {
//                    D.w("we got focus");
                            displayEditTextError(nameInput, bad_name_layout, true);
                           // scrollBy(-100);
                        } else {
//                    D.e("we lost focus");
                            if (!isValidString(nameInput.getText().toString()))
                                displayEditTextError(nameInput, bad_name_layout, false);
                            else
                                displayEditTextError(nameInput, bad_name_layout, true);
                        }
                    }
                });

                subjectInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {
//                    D.w("we got focus");
                            displayEditTextError(subjectInput, bad_subject_layout, true);
                            //scrollBy(-100);
                        } else {
//                    D.e("we lost focus");
                            if (subjectInput.getText().toString().equals(""))
                                displayEditTextError(subjectInput, bad_subject_layout, false);
                            else
                                displayEditTextError(subjectInput, bad_subject_layout, true);
                        }
                    }
                });

                phoneInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {
//                    D.w("we got focus");
                            displayEditTextError(phoneInput, bad_number_layout, true);
                           // scrollBy(-100);
                        } else {
//                    D.e("we lost focus");
                            if (phoneInput.getText().toString().equals("") || !isValidPhoneNumber(phoneInput.getText().toString()))
                                displayEditTextError(phoneInput, bad_number_layout, false);
                            else
                                displayEditTextError(phoneInput, bad_number_layout, true);
                        }
                    }
                });

                emailInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {

                        if (b) {
//                    D.w("we got focus");
                            displayEditTextError(emailInput, bad_email_layout, true);
                            //scrollBy(-100);
                        } else {
//                    D.e("we lost focus");
                            if (emailInput.getText().toString().equals(""))
                                displayEditTextError(emailInput, bad_email_layout, false);
                            else if (!isValidEmail(emailInput.getText().toString()))
                                displayEditTextError(emailInput, bad_email_layout, false);
                            else
                                displayEditTextError(emailInput, bad_email_layout, true);
                        }
                    }
                });

                descriptionInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {
//                    D.w("we got focus");
                            displayEditTextError(descriptionInput, bad_description_layout, true);
                           // scrollBy(-100);
                        } else {
//                    D.e("we lost focus");
                            if (!isValidString(descriptionInput.getText().toString()))
                                displayEditTextError(descriptionInput, bad_description_layout, false);
                            else
                                displayEditTextError(descriptionInput, bad_description_layout, true);
                        }
                    }
                });


                ImageView attachFile = (ImageView) v.findViewById(R.id.attachFile);
                attachFile.setOnClickListener(openFileChooser);

                UserProfile userProfile = VodafoneController.getInstance().getUserProfile();

                if (userProfile != null) {
                    completeEmail(userProfile);
                    completeName(userProfile);
                    completeNumber(userProfile);
                }

       /* if (clientType.isSelected())
            clientType.setSelectedIndex(window.clientSelection);

        if (requestType.isSelected()) {
            createRequestTypeArray(window.clientSelection);
            requestType.setSelectedIndex(window.requestSelection);
        }*/
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        try {
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return filePath;
    }

    void scrollBy(final int val) {
        VodafoneController.getInstance().handler.post(new Runnable() {
            @Override
            public void run() {
                if (window.views != null)
                    window.views.scrollView.scrollBy(0, ScreenMeasure.dpToPx(val));
            }
        });
//        window.views.scrollView.scrollBy(0, ScreenMeasure.dpToPx(val));

    }

    public void initDefaults(Activity a, String emailSubject) {
        this.activity = a;
        clientType.setHint("Te rugăm să selectezi o opţiune");
        clientType.setHintTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray_text_color));
        clientType.setText(null);
        clientType.resetSelectedIndex();
        clientType.invalidate();
        clientType.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray_text_color));
        clientType.setValueChoosen(false);

        disableRequestSpinner(false);
        requestType.setHint("Te rugăm să selectezi o opţiune");
        requestType.setText(null);
        requestType.invalidate();
        requestType.resetSelectedIndex();
        requestType.setHintTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray_text_color));
        requestType.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray_text_color));
        requestType.setValueChoosen(false);

        if (!emailSubject.equals("clearPrefilledEmailSubject")) {
            addSuffixToEmail = true;
            subjectInput.setText(emailSubject);
//            subjectInput.setEnabled(false);
            subjectInput.setTextColor(ContextCompat.getColor(getContext(), R.color.grey_600));
            subjectInput.setBackground(ContextCompat.getDrawable(VodafoneController.currentActivity(), R.drawable.disabled_input_border));

            inapoiButton.setVisibility(VISIBLE);


        } else {
            subjectInput.setText("");
//            subjectInput.setEnabled(true);
            subjectInput.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray_text_color));
            subjectInput.setBackground(ContextCompat.getDrawable(VodafoneController.currentActivity(), R.drawable.default_input_border));

            inapoiButton.setVisibility(GONE);
        }
        if (window.views != null)
            window.views.minimizeButton.setVisibility(View.GONE);

        String text = "Subiect<font color=red>*</font>:";
        subjectLabel.setText(Html.fromHtml(text));

        selectedClient = false;
        selectedClient = false;

        if (validateFieldsNoDropDowns())
            enableButton(sendEmail);
        else
            disableButton(sendEmail);

        clearAllTooltipErrors();
        if (window.views != null) {
            D.e("top = " + window.views.scrollView.getTop());
            D.e("bot = " + window.views.scrollView.getBottom());
            VodafoneController.getInstance().handler.post(new Runnable() {
                @Override
                public void run() {
                    window.views.scrollView.scrollTo(0, 0);
                    D.i("top = " + window.views.scrollView.getTop());
                    D.i("bot = " + window.views.scrollView.getBottom());
                    if (!subjectInput.getText().toString().equals(""))
                        inapoiButton.setVisibility(VISIBLE);//VNM-8632
                }
            });
            window.views.scrollView.scrollTo(0, 0);
        }
    }

    void clearAllTooltipErrors() {
        displayEditTextError(nameInput, bad_name_layout, true);
        displayEditTextError(subjectInput, bad_subject_layout, true);
        displayEditTextError(phoneInput, bad_number_layout, true);
        displayEditTextError(emailInput, bad_email_layout, true);
        displayEditTextError(descriptionInput, bad_description_layout, true);
    }

    private void setRequestTypeAdapter() {

        //set list defaults
        requestTipe.add("Abonamente \u0219i servicii de voce");
        requestTipe.add("Internet \u0219i date");
        requestTipe.add("MyVodafone \u0219i/sau Magazin online");
        requestTipe.add("Protec\u021bia datelor cu caracter personal");

        requestTypeAdapter = new VodafoneSpinnerAdapter<>(getContext(), requestTipe, R.drawable.selector);
        requestType.setAdapter(requestTypeAdapter);
        requestType.setCallback(cb2);
    }

    private void setClientTypeAdapter() {
        ArrayList<String> clientsList = new ArrayList<>();
        clientsList.add("Abonat persoan\u0103 fizic\u0103");
        clientsList.add("Abonat persoan\u0103 juridic\u0103");
        clientsList.add("Utilizator de cartel\u0103");
        clientsList.add("Nu sunt client Vodafone");


        VodafoneSpinnerAdapter<String> selectAdapter = new VodafoneSpinnerAdapter<>(getContext(), clientsList, R.drawable.selector);
        clientType.setAdapter(selectAdapter);
        clientType.setCallback(cb1);
    }

    public void disableButton(Button button) {
        button.setClickable(false);
        button.setEnabled(false);
    }

    public void enableButton(Button button) {

        button.setClickable(true);
        button.setEnabled(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tealium Track Event
                if (!validateAllFields()) {
                    D.w("DISPLAY ERROR FOR DROPDOWNS");
                    if (!requestType.isValueChoosen())
                        bad_request_layout.setVisibility(View.VISIBLE);

                    if (!clientType.isValueChoosen())
                        bad_client_layout.setVisibility(View.VISIBLE);
                    bad_client_layout.requestFocus();


                } else {


                    Map<String, Object> tealiumMapEvent = new HashMap(6);
                    tealiumMapEvent.put("screen_name", "email");
                    tealiumMapEvent.put("event_name", "mcare:email:button:trimite email");
                    tealiumMapEvent.put("user_type", VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
                    TealiumHelper.trackEvent("event_name", tealiumMapEvent);

                    if (!clientType.isValueChoosen()) {
                        bad_client_layout.setVisibility(View.VISIBLE);

                        if (window.views != null)
                            scrollToView(window.views.scrollView, bad_request_layout);

                    } else {
                        bad_client_layout.setVisibility(View.GONE);

                        if (!requestType.isValueChoosen()) {
                            bad_request_layout.setVisibility(View.VISIBLE);

                            if (window.views != null)
                                scrollToView(window.views.scrollView, bad_request_layout);
                        } else {
                            bad_request_layout.setVisibility(View.GONE);

                            window.showLoadingDialog();

                            EmailRequest emailRequest = new EmailRequest();

                            emailRequest.setClientType(clientType.getText().toString());
                            emailRequest.setDescription(descriptionInput.getText().toString());
                            emailRequest.setPhoneNumber(phoneInput.getText().toString());
                            emailRequest.setEmail(emailInput.getText().toString());
                            emailRequest.setRequestType(requestType.getText().toString());
                            emailRequest.setSenderName(nameInput.getText().toString());

//                            if (!addSuffixToEmail)
//                                emailRequest.setSubject(subjectInput.getText().toString());
//                            else
                                emailRequest.setSubject("mCare:" + subjectInput.getText().toString());

                            window.getChatService().postEmail(emailRequest, messagePart).subscribe(new RequestSessionObserver<GeneralResponse>() {
                                @Override
                                public void onNext(GeneralResponse response) {

                                    if (response.getTransactionFault() != null) {
                                        D.e("fault message = " + response.getTransactionFault().getFaultMessage());
                                        D.e("fault code    = " + response.getTransactionFault().getFaultParam());
                                    }

                                    if (response.getTransactionStatus() == 0) {
                                        window.close(false, false, false);
                                        if (!(VodafoneController.currentActivity() instanceof DashboardActivity))
                                            new NavigationAction(getContext()).finishCurrent(true).startAction(IntentActionName.DASHBOARD);
                                        new CustomToast.Builder(getContext()).message("Email-ul tău a fost trimis cu succes!").success(true).show();
                                        VoiceOfVodafone voiceOfVodafone = new VoiceOfVodafone(8, 20, VoiceOfVodafoneCategory.Chat, null, "Email-ul tău a fost trimis cu succes!", "Ok, am înţeles", "asd",
                                                true, false, VoiceOfVodafoneAction.Dismiss, null);
                                        VoiceOfVodafoneController.getInstance().pushStashToView(voiceOfVodafone.getCategory(), voiceOfVodafone);
                                        VoiceOfVodafoneController.getInstance().refreshVoiceOfVodafoneWidget();
                                    } else {
                                        new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil!").success(false).errorIcon(true).show();
                                    }
                                }

                                @Override
                                public void onCompleted() {
                                    super.onCompleted();
                                    D.w();
                                    window.stopLoadingDialog();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                    window.stopLoadingDialog();
                                    D.e("e = " + e);
                                    new CustomToast.Builder(getContext()).message("Serviciu momentan indisponibil!").success(false).show();
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public void disableRequestSpinner(boolean enabled) {
        requestType.setClickable(enabled);
        requestType.setFocusable(enabled);

        if (!enabled)
            requestType.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.disabled_input_border));
        else
            requestType.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.default_input_border));
    }

    public void displayEditTextError(CustomEditTextCompat target, LinearLayout errorLayout, boolean visibility) {
        if (!visibility)
            errorLayout.setVisibility(View.VISIBLE);
        else
            errorLayout.setVisibility(View.GONE);

        if (!target.hasFocus())
            if (!visibility) {
            if(target.getId() == descriptionInput.getId())
                target.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border);
            else
                target.setBackgroundresourceAndFieldIcon(R.drawable.red_error_input_border, R.drawable.error_icon);
            }
            else
                target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
        else
            target.setBackgroundresourceAndFieldIcon(R.drawable.default_input_border);
    }

    public boolean isValidPhoneNumber(String value) {
        boolean isValid = false;

        if (null != value && !"".equals(value)) {

            if (value.length() == 10) {
                Pattern pattern = Pattern.compile("^0[0-9]{9}");
                Matcher matcher = pattern.matcher(value);

                isValid = matcher.matches() && value.startsWith("0");
            } else
                isValid = false;
        }
        return isValid;
    }

    public boolean isValidString(String value) {
        boolean isValid = false;

        if (null != value && !"".equals(value) && !value.replaceAll(" ", "").equals(""))
            isValid = true;
        return isValid;
    }

    public boolean isValidExtension() {
        D.v();

        String filename = file.getName();
        D.v("filename = " + filename);
        String filenameArray[] = filename.split("\\.");
        String extension = filenameArray[filenameArray.length - 1];
        D.w(extension);
        //.jpeg, Excel, Word, .jpg, .png, .pdf.
        String[] array = {"jpeg", "xls", "doc", "docx", "xlsx", "jpg", "png", "pdf"};

        for (String anArray : array)
            if (extension.equals(anArray))
                return true;

        return false;
    }

    public void createRequestTypeArray(int index) {
        requestTipe.clear();
        disableRequestSpinner(true);

        switch (index) {
            case 0:
                requestTipe.add("Abonamente \u0219i servicii de voce");
                requestTipe.add("Internet \u0219i date");
                requestTipe.add("MyVodafone \u0219i/sau Magazin online");
                requestTipe.add("Protec\u021bia datelor cu caracter personal");
                break;
            case 1:
                requestTipe.add("Abonamente \u0219i servicii de voce");
                requestTipe.add("Internet \u0219i date");
                requestTipe.add("MyVodafone \u0219i/sau Magazin online");
                requestTipe.add("Protec\u021bia datelor cu caracter personal");
                break;
            case 2:
                requestTipe.add("Cartela Vodafone \u0219i servicii de voce");
                requestTipe.add("Internet \u0219i date");
                requestTipe.add("MyVodafone \u0219i/sau Magazin online");
                requestTipe.add("Protec\u021bia datelor cu caracter personal");
                break;
            case 3:
                requestTipe.add("Abonamente \u0219i servicii de voce");
                requestTipe.add("Internet \u0219i date");
                requestTipe.add("MyVodafone \u0219i/sau Magazin online");
                requestTipe.add("Protec\u021bia datelor cu caracter personal");
                break;
            default:
                break;
        }
        VodafoneSpinnerAdapter<String> selectAdapter = new VodafoneSpinnerAdapter<>(getContext(), requestTipe, R.drawable.selector);
        requestType.setAdapter(selectAdapter);
        requestType.setCallback(cb2);
        requestType.setText("Te rugăm să selectezi o opţiune");
    }

    public void completeEmail(UserProfile userProfile) {
        if (userProfile.getEmail() != null && !userProfile.getEmail().equals("")) {
            emailInput.setText(userProfile.getEmail());
        }
    }

    public void completeName(UserProfile userProfile) {
        if (userProfile.getLastName() != null && !userProfile.getLastName().equals(""))
            nameInput.setText(userProfile.getLastName());
        if (userProfile.getFirstName() != null && !userProfile.getFirstName().equals(""))
            nameInput.setText(nameInput.getText().toString() + " " + userProfile.getFirstName());
    }

    public void completeNumber(UserProfile userProfile) {
        if (userProfile.getMsisdn() != null && !userProfile.getMsisdn().equals(""))
            phoneInput.setText(userProfile.getMsisdn().substring(1));
    }

    public boolean isValidEmail(String email) {
        return (!TextUtils.isEmpty(email) && ro.vodafone.mcare.android.ui.utils.TextUtils.EMAIL_PATTERN.matcher(email).matches());
    }

    public void scrollToView(PagingScrollView scroll, View view) {
        scroll.smoothScrollTo(0, view.getTop() + ScreenMeasure.dpToPx(150));
    }

    public boolean validateFieldsNoDropDowns() {
        return isValidString(nameInput.getText().toString()) && !subjectInput.getText().toString().equals("") &&
                !phoneInput.getText().toString().equals("") && isValidPhoneNumber(phoneInput.getText().toString()) &&
                isValidEmail(emailInput.getText().toString()) && isValidString(descriptionInput.getText().toString())
//                && selectedClient && selectedRequest
                ;
    }

    public boolean validateAllFields() {
        return isValidString(nameInput.getText().toString()) && !subjectInput.getText().toString().equals("") &&
                !phoneInput.getText().toString().equals("") && isValidPhoneNumber(phoneInput.getText().toString()) &&
                isValidEmail(emailInput.getText().toString()) && isValidString(descriptionInput.getText().toString())
                && selectedClient && selectedRequest
                ;
    }


    public void initChooser() {
        bad_attachement_layout.setVisibility(View.GONE);

        final File mediaStorageDir = Environment.getRootDirectory();
        final File root = new File(mediaStorageDir, "Attachement");//todo : DO I NEED THIS?
        root.mkdirs();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");      //all files
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            Intent chooser = Intent.createChooser(intent, "Select File to Upload");
            RxActivityResult.startActivityForResult(window.activity, chooser, 10).subscribe(new Action1<ActivityResult>() {
                @Override
                public void call(ActivityResult activityResult) {
                    onActivityResult(activityResult.getRequestCode(), activityResult.getResultCode(), activityResult.getData());
                }
            });
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (file != null)
            if (requestCode == 232 || (data == null && file.exists())) {
                D.w();
                return;
            }

        try {
            D.w();

            if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
                D.w("sTART");
                file = null;
                if (data != null) {
                    D.w(" data = " + data.getData());
                    fileUri = data.getData();
//                    String path = getPath(getContext(), fileUri);
//                    D.w(" fileUri = " + fileUri);
//                    String path2 = fileUri.getPath();
//                    D.w(" path2 = " + path2);
                    String path3 = fileUri.toString();
                    D.w(" path3 = " + path3);
//                    String path4 = getRealPathFromURI_API19(getContext(), fileUri);
//                    D.w(" path4 = " + path4);
                    String path5 = getPath5(getContext(), fileUri);
                    D.w(" path5 = " + path5);


                    if (path5 != null) {
                        file = new File(path5);
                        fileName.setText(file.getName());

                        long sizeInBytes = file.length();

                        if (sizeInBytes > MAX_FILE_DIM) {
                            file = null;
                            displayAttachementTooltipError(" Dimensiunea maximă fișierului trebuie să fie 3 MB. ");
                            fileName.setText("");
                        } else if (!isValidExtension()) {
                            file = null;
                            displayAttachementTooltipError(" Se pot încărca doar următoarele tipuri de fișiere: .jpeg, Excel, Word, .jpg, .png, .pdf.  ");
                            fileName.setText("");
                        }
                        RequestBody requestFile =
                                RequestBody.create(
                                        MediaType.parse(getContext().getContentResolver().getType(fileUri)), file);

                        messagePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                    } else {
                        if (path3 != null) {
                            String filePath = null;
                            if (fileUri != null && "content".equals(fileUri.getScheme())) {
                                Cursor cursor = this.activity.getContentResolver().query(fileUri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
                                cursor.moveToFirst();
                                filePath = cursor.getString(0);
                                cursor.close();
                            } else {
                                filePath = fileUri.getPath();
                            }
                            if (filePath != null) {
                                file = new File(filePath);
                                fileName.setText(file.getName());

                                long sizeInBytes = file.length();

                                if (sizeInBytes > MAX_FILE_DIM) {
                                    file = null;
                                    displayAttachementTooltipError(" Dimensiunea maximă fișierului trebuie să fie 3 MB. ");
                                    fileName.setText("");
                                } else if (!isValidExtension()) {
                                    file = null;
                                    displayAttachementTooltipError(" Se pot încărca doar următoarele tipuri de fișiere: .jpeg, Excel, Word, .jpg, .png, .pdf.  ");
                                    fileName.setText("");
                                }
                                RequestBody requestFile =
                                        RequestBody.create(
                                                MediaType.parse(getContext().getContentResolver().getType(fileUri)), file);

                                messagePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                            } else {
                                File outputDir = this.activity.getCacheDir(); // context being the Activity pointer
                                file = File.createTempFile("tms", "vdfsuppattach", outputDir);
                                copyInputStreamToFile(this.activity.getContentResolver().openInputStream(fileUri), file);
                                fileName.setText(file.getName());

                                long sizeInBytes = file.length();

                                if (sizeInBytes > MAX_FILE_DIM) {
                                    file = null;
                                    displayAttachementTooltipError(" Dimensiunea maximă fișierului trebuie să fie 3 MB. ");
                                    fileName.setText("");
                                } else if (!isValidExtension()) {
                                    file = null;
                                    displayAttachementTooltipError(" Se pot încărca doar următoarele tipuri de fișiere: .jpeg, Excel, Word, .jpg, .png, .pdf.  ");
                                    fileName.setText("");
                                }
                                RequestBody requestFile =
                                        RequestBody.create(
                                                MediaType.parse(getContext().getContentResolver().getType(fileUri)), file);

                                messagePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                            }
                        } else {
                            new CustomToast.Builder(getContext()).message("Fisierul nu s-a putut atasa.").success(false).show();
//                        new CustomToast(getActivity(), getContext(), "Fisierul nu s-a putut atasa.", false).show();
                            fileName.setText("");
                        }
                    }
                }
                D.e("END");
            } else if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
                D.w("start");
                initChooser();
                D.w("end");
            }
        } catch (Exception exception) {
            D.e("okso = " + exception);
            exception.printStackTrace();
        }
    }

    private void copyInputStreamToFile(InputStream in, File file) {
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Ensure that the InputStreams are closed even if there's an exception.
            try {
                if (out != null) {
                    out.close();
                }

                // If you want to close the "in" InputStream yourself then remove this
                // from here but ensure that you close it yourself eventually.
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("NewApi")
    public String getPath5(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception ignored) {
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void displayAttachementTooltipError(String errorMessage) {
        bad_attachement_layout.setVisibility(View.VISIBLE);
        VodafoneTextView attachementErrorMessage = (VodafoneTextView) bad_attachement_layout.findViewById(R.id.attachementErrorMessage);
        attachementErrorMessage.setText(errorMessage);
    }

    // FIXME: 18.09.2017 @Lucian & Bogdan please check this. When we introduce a space between words in the input field app crashed with BadTokenException. Relates to VNM-7872.
    private void setNoSuggestionsInputType() {
        nameInput.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        subjectInput.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        descriptionInput.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        emailInput.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    @Override
    public void onErrorIconClickListener() {
        if(nameInput.isErrorIconTap())
            displayEditTextError(nameInput, bad_name_layout, true);
        else if (subjectInput.isErrorIconTap())
            displayEditTextError(subjectInput, bad_subject_layout, true);
        else if(descriptionInput.isErrorIconTap())
            displayEditTextError(subjectInput, bad_subject_layout, true);
        else if (emailInput.isErrorIconTap())
            displayEditTextError(emailInput, bad_email_layout, true);
        else if(phoneInput.isErrorIconTap())
            displayEditTextError(phoneInput, bad_number_layout, true);

    }
}

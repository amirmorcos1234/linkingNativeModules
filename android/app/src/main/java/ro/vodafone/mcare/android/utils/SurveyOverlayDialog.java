package ro.vodafone.mcare.android.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.service.tracking.adobe.target.AdobeTargetResponseElement;
import ro.vodafone.mcare.android.ui.activities.SplashScreenActivity;
import ro.vodafone.mcare.android.ui.webviews.SurveyWebViewActivity;
import ro.vodafone.mcare.android.ui.webviews.WebviewActivity;
import ro.vodafone.mcare.android.utils.navigation.notification.LinkDispatcherActivity;

/**
 * Created by Andrei DOLTU on 5/8/2017.
 */

public class SurveyOverlayDialog extends AppCompatActivity {

    public static String TAG = "SurveyOverlayDialog";
    ImageView surveyXClose;

    String surveyTitle = "We would love to hear your thoughts";
    String message = "Would you mind answering a couple of quick questions about your experience?";
    String surveyUrl = "http://www.vodafone.ro";

    Button buttonYes;
    Button buttonNo;

    TextView titleView;
    TextView messageView;

    AdobeTargetResponseElement adobeTargetResponseElement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.overlay_survey);
        initViews();

        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            adobeTargetResponseElement = new Gson().fromJson(getIntent().getExtras().getString("adobeResponse"), AdobeTargetResponseElement.class);
        }

        if(adobeTargetResponseElement != null)
            displayAdobeCase();
        else
            displayTealiumCase();
    }

    private void initViews(){
        surveyXClose = (ImageView) findViewById(R.id.survey_x_close);
        titleView = (TextView) findViewById(R.id.survey_title_label);
        titleView.setTextColor(getResources().getColor(R.color.whiteNormalTextColor));
        messageView = (TextView) findViewById(R.id.survey_message_label);
        messageView.setTextColor(getResources().getColor(R.color.whiteNormalTextColor));
        buttonYes = (Button) findViewById(R.id.survey_yes_button);
        buttonYes.setTransformationMethod(null);
        buttonNo = (Button) findViewById(R.id.survey_no_button);
        buttonNo.setTransformationMethod(null);
    }

    private void displayTealiumCase() {
        surveyTitle = getIntent().getExtras().getString("surveyTitle");
        message = getIntent().getExtras().getString("message");
        surveyUrl = getIntent().getExtras().getString("surveyUrl");
        titleView.setText(surveyTitle);
        messageView.setText(message);
        buttonYes.setText("Sigur, vreau să ajut");
        buttonNo.setText("Nu acum");

        surveyXClose.setOnClickListener(closeOverlayListener);
        buttonYes.setOnClickListener(buttonYesClickListener);
        buttonNo.setOnClickListener(closeOverlayListener);
    }

    private void displayAdobeCase() {
        setButtonVisibility(adobeTargetResponseElement.isXButtonDisplayed(), surveyXClose);
        setButtonVisibility(adobeTargetResponseElement.isButton1Displayed(), buttonNo);
        setButtonVisibility(adobeTargetResponseElement.isButton2Displayed(), buttonYes);

        setupButtonStyle(buttonNo, adobeTargetResponseElement.isButton1TypePrimary());
        setupButtonStyle(buttonYes, adobeTargetResponseElement.isButton2TypePrimary());

        titleView.setText(adobeTargetResponseElement.getTitle());
        messageView.setText(adobeTargetResponseElement.getContent());
        buttonNo.setText(adobeTargetResponseElement.getButton1label());
        buttonYes.setText(adobeTargetResponseElement.getButton2label());

        buttonNo.setOnClickListener(createOnClickListener(adobeTargetResponseElement.getButton1urltype(), adobeTargetResponseElement.getButton1action()));
        buttonYes.setOnClickListener(createOnClickListener(adobeTargetResponseElement.getButton2urltype(), adobeTargetResponseElement.getButton2action()));
        surveyXClose.setOnClickListener(closeOverlayListener);
    }

    private void setButtonVisibility(boolean isVisible, View view) {
        if (isVisible)
            view.setVisibility(View.VISIBLE);
        else
            view.setVisibility(View.INVISIBLE);
    }

    private void setupButtonStyle(Button button, boolean isPrimary) {
        if (isPrimary) {
            button.setBackground(ContextCompat.getDrawable(VodafoneController.currentActivity(), R.drawable.selector_button_background_card_primary));
        } else {
            button.setBackground(ContextCompat.getDrawable(VodafoneController.currentActivity(), R.drawable.selector_button_background_card_secondary));
        }
        button.setTextColor(ContextCompat.getColor(VodafoneController.currentActivity(), R.color.white_text_color));
    }

    View.OnClickListener buttonYesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SurveyOverlayDialog.this, SurveyWebViewActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putString("surveyTitle", surveyTitle);
            mBundle.putString("surveyUrl", surveyUrl);
            intent.putExtras(mBundle);
            startActivity(intent);
            finish();
        }
    };

    View.OnClickListener closeOverlayListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };


    private View.OnClickListener createOnClickListener(String urlType, String urlToRedirect) {
        Intent intent = null;
        if (isUrlOrUrlTypeValid(urlType) && isUrlOrUrlTypeValid(urlToRedirect)) {
            switch (urlType) {
                case "intern":
                    intent = new Intent(VodafoneController.currentActivity(), LinkDispatcherActivity.class);
                    intent.setData(Uri.parse(urlToRedirect));
                    break;
                case "extern":
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToRedirect));
                    break;
                case "webview":
                    intent = new Intent(VodafoneController.currentActivity(), WebviewActivity.class);
                    intent.putExtra(WebviewActivity.KEY_URL, urlToRedirect);
                    break;
                case "close":
                default:
                    intent = null;
                    break;
            }
        }

        final Intent finalIntent = intent;
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalIntent != null) {
                    VodafoneController.currentActivity().startActivity(finalIntent);
                }
                finish();
            }
        };

        return onClickListener;
    }

    private boolean isUrlOrUrlTypeValid(String urlTypeOrUrl){
        return urlTypeOrUrl!=null && !urlTypeOrUrl.isEmpty();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
    }
}



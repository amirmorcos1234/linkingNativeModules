package ro.vodafone.mcare.android.ui.activities.other;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.card.settings.PrivacyPolicyCard;
import ro.vodafone.mcare.android.client.model.realm.system.SettingsLabels;
import ro.vodafone.mcare.android.service.tracking.TrackingAppMeasurement;
import ro.vodafone.mcare.android.service.tracking.TrackingEvent;
import ro.vodafone.mcare.android.service.tracking.TrackingVariable;
import ro.vodafone.mcare.android.service.tracking.adobe.target.constants.AdobePageNamesConstants;
import ro.vodafone.mcare.android.ui.activities.MenuActivity;
import ro.vodafone.mcare.android.ui.header.NavigationHeader;
import ro.vodafone.mcare.android.ui.utils.KeyboardHelper;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;
import ro.vodafone.mcare.android.utils.D;
import ro.vodafone.mcare.android.utils.TealiumHelper;

/**
 * Created by user2 on 5/5/2017.
 */

public class PrivacyActivity extends MenuActivity {

    private static final String TAG = "PrivacyActivity";

    NavigationHeader navigationHeader;
    LinearLayout contentLayout;
    PrivacyPolicyCard privacyPolicyCard;
    private VodafoneTextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        contentLayout = (LinearLayout) findViewById(R.id.privacy_container);
        title = (VodafoneTextView) findViewById(R.id.title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);

        setupHeader();
        setupLayout();

        //Tealium Track View
        Map<String, Object> tealiumMapView = new HashMap(6);
        tealiumMapView.put("screen_name", "privacy policy");
        tealiumMapView.put("journey_name", "settings");
        // tealiumMapView.put("user_type",VodafoneController.getInstance().getUserProfile().getUserRole().getDescription());
        TealiumHelper.trackView("screen_name", tealiumMapView);

        PrivacyPolicyTrackingEvent event = new PrivacyPolicyTrackingEvent();
        VodafoneController.getInstance().getTrackingService().track(event);
    }

    @Override
    protected int setContent() {
        return R.layout.activity_privacy;
    }

    private void setupLayout() {
        addGroupPurpleTitle(SettingsLabels.getPrivacyPolicyCollectDataTitle());
        addGroupPurpleTitle(SettingsLabels.getPrivacyPolicyDontCollectDataTitle());
        addGroupPurpleTitle(SettingsLabels.getPrivacyPolicyPurposeTitle());
        addGroupPurpleTitle(SettingsLabels.getPrivacyPolicyDataProtectionTitle());
        //addGroupPurpleTitle(SettingsLabels.getPrivacyPolicyMoreInfoTitle());
    }

    //TODO get rid of hardcoded part
    private void addGroupPurpleTitle(String title) {
        View privacyPurpleTitleView = View.inflate(this, R.layout.purple_text_category_title, null);
        TextView privacyTitle = (TextView) privacyPurpleTitleView.findViewById(R.id.extra_category_tittle);
        privacyTitle.setTextSize(22);
        privacyTitle.setText(title);
        contentLayout.addView(privacyPurpleTitleView);

        privacyPolicyCard = new PrivacyPolicyCard(this);

        //hardcoded part until data is received from server
        //date pe care le colectam
        if (title.equals(SettingsLabels.getPrivacyPolicyCollectDataTitle())) {
            String firstCategory = SettingsLabels.getPrivacyFirstCategory();
            String secondCategory = SettingsLabels.getPrivacySecondCategory();
            String thirdCategory = SettingsLabels.getPrivacyThirdCategory();
            String fourthCategory = SettingsLabels.getPrivacyFourthCategory();
            String fifthCategory = SettingsLabels.getPrivacyFifthCategory();
            String sixthCategory = SettingsLabels.getPrivacySixthCategory();

            addContentGroup(firstCategory, false);
            addContentGroup(secondCategory, false);
            addContentGroup(thirdCategory, false);
            addContentGroup(fourthCategory, false);
            addContentGroup(fifthCategory, false);
            addContentGroup(sixthCategory, true);
        } else if (title.equals(SettingsLabels.getPrivacyPolicyDontCollectDataTitle())) {
            String seventhCategory = SettingsLabels.getPrivacySeventhCategory();
            addContentGroup(seventhCategory, true);
        } else if (title.equals(SettingsLabels.getPrivacyPolicyPurposeTitle())) {
            String eighthCategory = SettingsLabels.getPrivacyEighthCategory();
            String ninthCategory = SettingsLabels.getPrivacyNinthCategory();

            addContentGroup(eighthCategory, false);
            addContentGroup(ninthCategory, true);
        } else if (title.equals(SettingsLabels.getPrivacyPolicyDataProtectionTitle())) {
            String tenthCategory = SettingsLabels.getPrivacyTenthCategory();
            String eleventhCategory = SettingsLabels.getPrivacyEleventhCategory();
            String twelfthCategory = SettingsLabels.getPrivacyTwelfthCategory();

            addContentGroup(tenthCategory, false);
            addContentGroup(eleventhCategory, false);
            addContentGroup(twelfthCategory, true);
        }
//        else
//        if(title.equals(SettingsLabels.getPrivacyPolicyMoreInfoTitle())){
//            String thirteenthCategory = SettingsLabels.getPrivacyThirteenthCategory();
//
//            addContentGroup(thirteenthCategory , true);
//        }


        contentLayout.addView(privacyPolicyCard);
    }

    //TODO get rid of hardcoded part
    private void addContentGroup(String title, boolean last) {
        ArrayList<String> content = new ArrayList<>();
        ArrayList<String> contentType = new ArrayList<>();
        List<String> bulletList = new ArrayList<>();

        if (title.equals(SettingsLabels.getPrivacyFirstCategory())) {
            content.add(SettingsLabels.getPrivacySubtitleOne());
            contentType.add("title");
            content.add(SettingsLabels.getPrivacySimpleOne());
            contentType.add("simple_text");
            content.add(SettingsLabels.getPrivacySubtitleTwo());
            contentType.add("title");
            content.add(SettingsLabels.getPrivacySimpleTwo());
            contentType.add("simple_text");
        } else if (title.equals(SettingsLabels.getPrivacySecondCategory())) {
            content.add(SettingsLabels.getPrivacySimpleThree());
            contentType.add("simple_text");

            bulletList = Arrays.asList(getResources().getStringArray(R.array.first_category_list));
            content.add("");
            contentType.add("list");
        } else if (title.equals(SettingsLabels.getPrivacyThirdCategory())) {
            content.add(SettingsLabels.getThirdCategoryTitle());
            contentType.add("simple_text");

            bulletList = Arrays.asList(getResources().getStringArray(R.array.third_category_list));
            content.add("");
            contentType.add("list");
        } else if (title.equals(SettingsLabels.getPrivacyFourthCategory())) {
            content.add(SettingsLabels.getFourthCategoryTitle());
            contentType.add("simple_text");

            bulletList = Arrays.asList(getResources().getStringArray(R.array.fourth_category_list));
            content.add("");
            contentType.add("list");
        } else if (title.equals(SettingsLabels.getPrivacyFifthCategory())) {
            content.add(SettingsLabels.getFifthCategorySimpleOne());
            contentType.add("simple_text");
            content.add(SettingsLabels.getFifthCategorySimpleTwo());
            contentType.add("simple_text");
        } else if (title.equals(SettingsLabels.getPrivacySixthCategory())) {
            content.add(SettingsLabels.getSixthCategorySimpleOne());
            contentType.add("simple_text");
        } else if (title.equals(SettingsLabels.getPrivacySeventhCategory())) {
            content.add(SettingsLabels.getSeventhCategorySimpleOnePrim());
            contentType.add("simple_text");
            content.add(SettingsLabels.getSeventhCategorySimpleOne());
            contentType.add("simple_text");

            bulletList = Arrays.asList(getResources().getStringArray(R.array.seventh_category_list));
            content.add("");
            contentType.add("list");
        } else if (title.equals(SettingsLabels.getPrivacyEighthCategory())) {
            content.add(SettingsLabels.getEighthCategorySimpleOne());
            contentType.add("simple_text");
            content.add(SettingsLabels.getEighthCategorySimpleTwo());
            contentType.add("simple_text");

            bulletList = Arrays.asList(getResources().getStringArray(R.array.eighth_category_list));
            contentType.add("list");
        } else if (title.equals(SettingsLabels.getPrivacyNinthCategory())) {
            content.add(SettingsLabels.getNinthCategorySimpleOne());
            contentType.add("simple_text");

            bulletList = Arrays.asList(getResources().getStringArray(R.array.ninth_category_list));
            content.add("");
            contentType.add("list");

            content.add(SettingsLabels.getNinthCategorySimpleTwo());
            contentType.add("simple_text");
            content.add(SettingsLabels.getNinthCategorySimpleThree());
            contentType.add("simple_text");
        } else if (title.equals(SettingsLabels.getPrivacyTenthCategory())) {
            content.add(SettingsLabels.getTenthCategorySimpleOne());
            contentType.add("simple_text");

            bulletList = Arrays.asList(getResources().getStringArray(R.array.tenth_category_list));
            content.add("");
            contentType.add("list");

            content.add(SettingsLabels.getTenthCategorySimpleTwo());
            contentType.add("simple_text");
        } else if (title.equals(SettingsLabels.getPrivacyEleventhCategory())) {
            content.add(SettingsLabels.getEleventhCategorySimpleOne());
            contentType.add("simple_text");
        } else if (title.equals(SettingsLabels.getPrivacyTwelfthCategory())) {
            content.add(SettingsLabels.getTwelfthCategorySimpleOne());
            contentType.add("simple_text");
            content.add(SettingsLabels.getTwelfthCategorySimpleTwo());
            contentType.add("simple_text");
            content.add(SettingsLabels.getTwelfthCategorySimpleThree());
            contentType.add("simple_text");
        } else if (title.equals(SettingsLabels.getPrivacyThirteenthCategory())) {

        }


        privacyPolicyCard.addExpandableTextGroup(last, title, content, contentType, bulletList);

    }

    private void setupHeader() {
        navigationHeader = (NavigationHeader) findViewById(R.id.navigation_header);
        navigationHeader.setActivity(this).displayDefaultHeader();
        setTitle();
    }

    public void setTitle() {
        setTitle(SettingsLabels.getPrivacyPageTitle());
    }

    public void setTitle(String text) {
        try {
            title.setText(text);
        } catch (Exception e) {
            Log.e(TAG, "problems with getTitle");

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        KeyboardHelper.hideKeyboard(this);
        try {
            getCurrentFocus().clearFocus();
        } catch (Exception e) {
            D.e("ERROR : " + e);
        }
    }

    @Override
    public void switchFragmentOnCreate(String fragment, String extraParameter) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        callForAdobeTarget(AdobePageNamesConstants.SETTINGS_PRIVACY_POLICY);
    }

    public static class PrivacyPolicyTrackingEvent extends TrackingEvent {

        @Override
        public void defineTrackingProperties(TrackingAppMeasurement s) {
            super.defineTrackingProperties(s);

            if (getErrorMessage() != null) {
                s.events = "event11";
                s.getContextData().put("event11", s.event11);
            }
            s.pageName = s.prop21 + "privacy policy";
            s.getContextData().put(TrackingVariable.P_TRACK_STATE, "mcare:" + "privacy policy");


            s.channel = "settings";
            s.getContextData().put("&&channel", s.channel);
        }
    }
}

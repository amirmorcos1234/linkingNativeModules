package ro.vodafone.mcare.android.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import io.realm.RealmResults;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.model.gdpr.GdprGetResponse;
import ro.vodafone.mcare.android.widget.dialogs.VodafoneDialog;


/**
 * Created by Andrei DOLTU on 4/26/2017.
 */

public class TealiumUtils {

    public static String TAG = "TealiumUtils";

    public VodafoneDialog vodafoneDialog;

    //setting default message texts
    String surveyTitle = "We would love to hear your thoughts";
    String message = "Would you mind answering a couple of quick questions about your experience?";
    String surveyUrl = "http://www.vodafone.ro";

    //setting default buttons texts
    String buttonYesText = "Sigur, vreau sa ajut";
    String buttonNoText = "Nu acum";

    Context context = VodafoneController.getInstance().getApplicationContext();

    public void generateSurveyNotification(JSONObject qualtricsCommand) {
        RealmResults<GdprGetResponse> gdprResponsesOwner = RealmManager.getRealmObjectQuery(GdprGetResponse.class, 1);
        GdprGetResponse gdprGetResponseOwner = (gdprResponsesOwner != null && gdprResponsesOwner.size() > 0) ? gdprResponsesOwner.first() : null;
        if (gdprGetResponseOwner != null && gdprGetResponseOwner.getGdprPermissions() != null
                && gdprGetResponseOwner.getGdprPermissions().getVfSurveyCategory() != null
                && gdprGetResponseOwner.getGdprPermissions().getVfSurveyCategory().equalsIgnoreCase("yes")) {


            Log.d(TAG, "Passing through: generateSurveyNotification");
            try {
                if (qualtricsCommand.get("message").toString() != null &&
                        qualtricsCommand.get("survey_notification_title").toString() != null &&
                        qualtricsCommand.get("survey_url").toString() != null) {

                    //loggin json qualtricsCommand
                    Log.d(TAG, "json qualtricsCommand = " + qualtricsCommand);

                    //create nudge/dialog for survey

                    //extract json fields
                    message = qualtricsCommand.get("message").toString();
                    surveyTitle = qualtricsCommand.get("survey_notification_title").toString();
                    surveyUrl = qualtricsCommand.get("survey_url").toString();


                    Log.d(TAG, "json message = " + message);
                    Log.d(TAG, "json surveyTitle = " + surveyTitle);
                    Log.d(TAG, "json surveyUrl = " + surveyUrl);


                    displaySurveyDialog(message, surveyTitle, surveyUrl);


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void displaySurveyDialog(final String message, final String surveyTitle, final String surveyUrl) {
        Log.d(TAG, "Passing through: displaySurveyDialog");

        Intent intent = new Intent(context, SurveyOverlayDialog.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle mBundle = new Bundle();
        mBundle.putString("message", message);
        mBundle.putString("surveyTitle", surveyTitle);
        mBundle.putString("surveyUrl", surveyUrl);
        intent.putExtras(mBundle);
        context.startActivity(intent);

    }

}

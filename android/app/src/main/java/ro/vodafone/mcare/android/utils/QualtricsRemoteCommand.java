package ro.vodafone.mcare.android.utils;

/**
 * Created by Andrei DOLTU on 4/14/2017.
 */

import android.app.Application;
import android.util.Log;

import com.tealium.internal.tagbridge.RemoteCommand;

import org.json.JSONObject;

public final class QualtricsRemoteCommand extends RemoteCommand {

    public QualtricsRemoteCommand() {
        super("qualtrics", "Qualtrics Remote Command");

    }
    public QualtricsRemoteCommand (Application application) {
        super("qualtrics", "Qualtrics Remote Command");
    }



    public static String TAG = "QualtricsRemoteCmd";

    @Override
    protected void onInvoke (Response response) throws Exception {

        Log.d(TAG, "Passing through: onInvoke");
        JSONObject qualtricsCommand = new JSONObject();

        //list response content
        Log.d(TAG, "response = " + response.toString());

        // get the response from Tealium and add to the JSON object
        // survey_notification_title, message, survey_url
        qualtricsCommand.put("message", response.getRequestPayload().optString("message", null));
        qualtricsCommand.put("survey_notification_title", response.getRequestPayload().optString("survey_notification_title", null));
        qualtricsCommand.put("survey_url", response.getRequestPayload().optString("survey_url", null));
        // calls a fictional method called "generateSurveyNotification" and passes in the JSON config object
        TealiumUtils utils = new TealiumUtils();
        utils.generateSurveyNotification(qualtricsCommand);
        // send a response back to Tealium - required
        response.send();
    }
}
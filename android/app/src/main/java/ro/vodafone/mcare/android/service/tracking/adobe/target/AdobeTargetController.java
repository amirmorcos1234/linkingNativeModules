package ro.vodafone.mcare.android.service.tracking.adobe.target;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

import com.adobe.mobile.Target;
import com.adobe.mobile.TargetLocationRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import ro.vodafone.mcare.android.BuildConfig;
import ro.vodafone.mcare.android.utils.RealmManager;
import ro.vodafone.mcare.android.utils.StringMsisdnCrypt;
import ro.vodafone.mcare.android.widget.avatar.UserSelectedMsisdnBanController;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Bivol Pavel on 18.04.2018.
 */

public class AdobeTargetController {
    private boolean isRequestInProgress = false;

    private LifecycleOwner lifecycleOwner = null;

    private Subscription subscription;

    private  Realm realm;

    public AdobeTargetController trackPage(LifecycleOwner lifecycleOwner, String pageName) {
        if (!isRequestInProgress) {
            String selectedMsisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
            if (selectedMsisdn != null && !selectedMsisdn.isEmpty()) {
                trackPage(lifecycleOwner, pageName, createParamsWithMsisdn(selectedMsisdn));
                Log.d("AdobeTargetController", "Track page: " + pageName);
            } else {
                this.lifecycleOwner = lifecycleOwner;
                this.lifecycleOwner.getLifecycle().addObserver(new LifeCycleObserver());
                Log.d("AdobeTargetController", "Track page: " + pageName);

                runLocationTargeting(createTargetLocationRequest(pageName, null, null));
            }
            isRequestInProgress = true;
        }


        return this;
    }

    public AdobeTargetController trackPage(LifecycleOwner lifecycleOwner, String pageName,
                                           HashMap<String, Object> params) {
        this.lifecycleOwner = lifecycleOwner;
        this.lifecycleOwner.getLifecycle().addObserver(new LifeCycleObserver());

        runLocationTargeting(createTargetLocationRequest(pageName, null, params));

        return this;
    }

    public AdobeTargetController trackPage(LifecycleOwner lifecycleOwner, String pageName,
                                           String defaultContent, Map<String, Object> parameters) {
        this.lifecycleOwner = lifecycleOwner;
        this.lifecycleOwner.getLifecycle().addObserver(new LifeCycleObserver());

        runLocationTargeting(createTargetLocationRequest(pageName, defaultContent, parameters));

        return this;
    }

    private TargetLocationRequest createTargetLocationRequest(String name, String defaultContent,
                                                              Map<String, Object> parameters) {
        /* Adobe Tracking - Target
         *
		 * create a request for the geo targeting location
		 * default is black background and white text
		 */
        return Target.createRequest(name, defaultContent, parameters);
    }

    private void runLocationTargeting(final TargetLocationRequest locationRequest) {
		/* Adobe Tracking - Target
		 *
		 * reset cookies to ensure target gives us a different experience depending on user's location choice
		 * note: we are resetting cookies for this demo so the target server will allow us to reset experiences
		 */
        Target.clearCookies();

		/* Adobe Tracking - Target
		 *
		 * send our location request and callback
		 */
        Target.loadRequest(locationRequest, new Target.TargetCallback<String>() {
            @Override
            public void call(String s) {
                //Todo Uncoment this line when we will receive valid json from Adobe Target.
                List<AdobeTargetResponseElement> listOfResponseElements = getTargetResponseFromString(s);
                if (listOfResponseElements != null && listOfResponseElements.size() > 0) {
                    for (AdobeTargetResponseElement adobeTargetResponseElement : listOfResponseElements)
                        adobeTargetCallBack(adobeTargetResponseElement);
                }

                //Todo Remove this lines when we will receive valid json from Adobe Target.
                // AdobeMockService adobeMockService = new AdobeMockService();
                // for(AdobeTargetResponseElement adobeTargetResponseElement : getTargetResponseFromString(adobeMockService.getTargetResponse()))
                //  adobeTargetCallBack(adobeTargetResponseElement);
            }
        });
    }

    private void adobeTargetCallBack(AdobeTargetResponseElement adobeTargetResponseElement) {
        if (adobeTargetResponseElement == null)
            return;
        Log.d("Target", "adobeTargetCallBack: " + adobeTargetResponseElement.toString());
        openRealmInstance();
        AdobeCampaignDetails adobeCampaignDetails = (AdobeCampaignDetails) RealmManager
                .getRealmObjectAfterStringField(realm, AdobeCampaignDetails.class, AdobeCampaignDetails.ELEMENT_ID, adobeTargetResponseElement.getElementID());

        if (adobeCampaignDetails == null) {
            //First time accesed page with this elementId.
            if (adobeTargetResponseElement.getRecurrenceTTL() != null) {
                AdobeCampaignDetails newAdobeDetails = new AdobeCampaignDetails(adobeTargetResponseElement.getElementID(), 1, parseElementRecurrenceTTL(adobeTargetResponseElement));
                RealmManager.update(realm, newAdobeDetails);
                trigerActionWithDelay(adobeTargetResponseElement);
            }
        } else {
            if (System.currentTimeMillis() < adobeCampaignDetails.getRecurrenceTTL()) {
                //TTL is not expired , check if recurrence limit has not been reached.
                if (adobeTargetResponseElement.getRecurrence() == null || adobeTargetResponseElement.getRecurrence().isEmpty()
                        || adobeCampaignDetails.getRecurrenceCounter() < parseElementRecurrence(adobeTargetResponseElement.getRecurrence())) {
                    trigerActionWithDelay(adobeTargetResponseElement);
                }

                adobeCampaignDetails.incrementByOneRecurrenceCounter();
                RealmManager.update(realm, adobeCampaignDetails);
            } else {
                //TTL is expired , reset countere and TTL.
                RealmManager.update(realm, new AdobeCampaignDetails(adobeTargetResponseElement.getElementID(), 1, parseElementRecurrenceTTL(adobeTargetResponseElement)));
                trigerActionWithDelay(adobeTargetResponseElement);
            }
        }
        closeRealmInstance();
    }

    private Long parseTimeToDisplay(AdobeTargetResponseElement adobeTargetResponseElement) {
        Long timeToDisplay;
        try {
            timeToDisplay = (adobeTargetResponseElement.getTimetodisplay() != null && !adobeTargetResponseElement.getTimetodisplay().isEmpty())
                    ? Long.valueOf(adobeTargetResponseElement.getTimetodisplay()) : 0;
        } catch (Exception e) {
            timeToDisplay = 0L;
            Log.d("", "Error parsing timeToDisplay: " + e.getMessage());
            e.printStackTrace();
        }

        return timeToDisplay;
    }

    private Integer parseElementRecurrence(String recurrence) {
        Integer elementRecurrence;
        try {
            elementRecurrence = Integer.valueOf(recurrence);
        } catch (Exception e) {
            Log.d("", "Error parsing recurrence: " + e.getMessage());
            elementRecurrence = 0;
        }
        return elementRecurrence;
    }

    private Long parseElementRecurrenceTTL(AdobeTargetResponseElement adobeTargetResponseElement) {
        Long expiryTimeTTL;
        try {
            expiryTimeTTL = getTTLExpiryTime(minuteToMillis(!adobeTargetResponseElement.getRecurrenceTTL().isEmpty()
                    ? Long.valueOf(adobeTargetResponseElement.getRecurrenceTTL()) : 0));
        } catch (Exception e) {
            Log.d("", "Error parsing recurrenceTTL:  " + e.getMessage());
            expiryTimeTTL = 0L;
        }

        return expiryTimeTTL;
    }

    private void trigerActionWithDelay(final AdobeTargetResponseElement adobeTargetResponseElement) {
        Log.d("Target: ", "trigerActionWithDelay: " + adobeTargetResponseElement);
        if (adobeTargetResponseElement == null)
            return;
        subscription = Observable.timer(
                secondsToMillis(parseTimeToDisplay(adobeTargetResponseElement)), TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        triggerAction(adobeTargetResponseElement);
                    }
                });
    }

    private void triggerAction(AdobeTargetResponseElement adobeTargetResponseElement) {
        Log.d("Target: ", "triggerAction: ");
        AdobeBaseElement adobeElement = AdobeTargetElementsFactory.getElement(adobeTargetResponseElement);
        isRequestInProgress = false;
        if (adobeElement != null && subscription != null) {
            Log.d("target", "triggerAction: display element: " + adobeElement.toString());
            try {
                adobeElement.display();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class LifeCycleObserver implements LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        public void disconnectListener() {
            /*Adobe Tracking - Target
            *
            * Unsubscriber subscription when user leave current page.
            */
            if (subscription !=
                    null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
            lifecycleOwner = null;
            subscription = null;
            isRequestInProgress = false;
        }
    }

    private List<AdobeTargetResponseElement> getTargetResponseFromString(String response) {
        List<AdobeTargetResponseElement> adobeTargetResponseList = null;
        try {
            adobeTargetResponseList = new Gson().fromJson(response, new TypeToken<List<AdobeTargetResponseElement>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            Log.e("AdobeTargetController",
                    "This json: " + response + " is not a valid representation for an object of "
                            + AdobeTargetResponseElement.class.getName());
        }

        return listOfElementsbyEnvironment(adobeTargetResponseList);
    }

    private List<AdobeTargetResponseElement> listOfElementsbyEnvironment(List<AdobeTargetResponseElement> defaultListFromAdobe) {
        List<AdobeTargetResponseElement> elementsListByEnvironment = new ArrayList<>();
        if (defaultListFromAdobe != null && defaultListFromAdobe.size() > 0) {
            for (AdobeTargetResponseElement adobeResponse : defaultListFromAdobe) {
                if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("release")) {
                    if (!adobeResponse.getElement().toLowerCase().contains("uat")) {
                        elementsListByEnvironment.add(adobeResponse);
                    }
                } else {
                    if (adobeResponse.getElement().toLowerCase().contains("uat")) {
                        elementsListByEnvironment.add(adobeResponse);
                    }
                }
            }

        }

        return elementsListByEnvironment;
    }

    private Long getTTLExpiryTime(Long recurrenceTTL) {
        if (recurrenceTTL == null)
            return null;

        return System.currentTimeMillis() + recurrenceTTL;
    }

    private Long minuteToMillis(Long minutes) {
        if (minutes == null)
            return null;

        return TimeUnit.MINUTES.toMillis(minutes);
    }

    private Long secondsToMillis(Long seconds) {
        if (seconds == null)
            return null;

        return TimeUnit.SECONDS.toMillis(seconds);
    }

    public LifecycleOwner getLifecycleOwner() {
        return lifecycleOwner;
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    private HashMap createParamsWithMsisdn(String selectedMsisdn) {
        StringMsisdnCrypt msisdnCrypt = new StringMsisdnCrypt();
        HashMap<String, Object> contextData = new HashMap<String, Object>();

                /* Encrypt */
        String encryptedMsisdn = null;
        try {
            encryptedMsisdn = StringMsisdnCrypt.bytesToHex(msisdnCrypt.encrypt(selectedMsisdn));
            //Log.d(TAG, "encryptedMsisdn = " + encryptedMsisdn);
        } catch (Exception e) {
            e.printStackTrace();
        }

        contextData.put("msisdn", encryptedMsisdn);

        return contextData;
    }

    public TargetLocationRequest createAdobeRequestForOffers(String pageName) {
        String selectedMsisdn = UserSelectedMsisdnBanController.getInstance().getSelectedMsisdn();
        return Target.createRequest(pageName, null, createParamsWithMsisdn(selectedMsisdn));
    }

    private void openRealmInstance() {
        if(realm == null || realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
    }

    private void closeRealmInstance() {
        realm.close();
    }
}

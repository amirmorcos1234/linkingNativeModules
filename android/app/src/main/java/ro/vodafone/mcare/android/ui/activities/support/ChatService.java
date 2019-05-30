package ro.vodafone.mcare.android.ui.activities.support;

import android.content.Context;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Response;
import ro.vodafone.mcare.android.client.model.realm.GeneralResponse;
import ro.vodafone.mcare.android.client.model.shop.EmailRequest;
import ro.vodafone.mcare.android.rest.RetrofitCall;
import ro.vodafone.mcare.android.service.BaseService;
import ro.vodafone.mcare.android.ui.activities.support.supportModels.FaqPOJO;
import ro.vodafone.mcare.android.utils.D;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by bogdan marica on 4/2/2017.
 */

public class ChatService extends BaseService {

    public ChatService(Context context) {
        super(context);
        this.context = context;
    }

    Observable<List<JsonList>> getChatFaqJsonList() {

        Observable<List<JsonList>> observable =
                RetrofitCall.getInstance().getChatFaqJsonList()
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    Observable<GeneralResponse> postEmail(EmailRequest emailRequest, MultipartBody.Part file) {
        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().postEmail(emailRequest, file)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    Observable<GeneralResponse> getChatEligibility(boolean user, boolean time) {

        Observable<GeneralResponse> observable =
                RetrofitCall.getInstance().getChatEligibility(true, true, true, user, time)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    Observable<ChatStateResponse> getChatState(boolean searchDB, boolean isNewMcare) {

        Observable<Response<ChatStateResponse>> responseObservable =
                RetrofitCall.getInstance().getChatState(searchDB, isNewMcare)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        Observable observable = responseObservable.flatMap(new Func1<Response<ChatStateResponse>, Observable<?>>() {
            @Override
            public Observable<?> call(Response<ChatStateResponse> generalResponseResponse) {
                if (generalResponseResponse != null) {

                    List<String> cookie = generalResponseResponse.headers().toMultimap().get("set-cookie");

                    BaseService.setCookieAfterGetChatStateVdfChat(cookie);
                    return Observable.just(generalResponseResponse.body());
                }
                return null;
            }
        });
        return observable;
    }

    Observable<String> updateChatState(int state) {

        Observable<String> observable =
                RetrofitCall.getInstance().updateChatState(state)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<Response<GeneralResponse<StartChatResponse>>> postStartChat(String msisdn, StartChatRequest startChatRequest) {

        Observable<Response<GeneralResponse<StartChatResponse>>> observable =
                RetrofitCall.getInstance().postStartChat(msisdn, startChatRequest)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    Observable<String> sendTextMessage(String contactId,
                                       String sessionKey,
                                       VodafoneMessage message,
                                       boolean userFromEnroll,
                                       String lastReadTime,
                                       String channel) {
        String myCookie = BaseService.createCookieStatic(context);

        Observable<String> observable =
                RetrofitCall.getInstance().sendTextMessage(myCookie, contactId, sessionKey, message, userFromEnroll, lastReadTime, channel)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    Observable<String> postStartPoll(String contactId, String sessionKey, String userEmail, boolean userFromEnroll, String lastReadTime, String chanel) {

        String myCookie = BaseService.createCookieStatic(context);

        Observable<String> observable =
                RetrofitCall.getInstance().postStartPoll(contactId, sessionKey, userEmail, userFromEnroll, lastReadTime, chanel)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    Observable<String> pollMessages(String contactId, String sessionKey, boolean isTyping, boolean userFromEnroll, String lastReadTime, String channel) {

        Observable<String> observable =
                RetrofitCall.getInstance().pollMessages(contactId, sessionKey, isTyping, userFromEnroll, lastReadTime, channel)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    public Observable<String> logOutChat(String contactId, String sessionKey, String email) {

        ChatBubbleSingleton.getInstance().setMinimized(false, true);
        ChatBubbleSingleton.getInstance().setMessagesCount(0);

        String myCookie = BaseService.createCookieStatic(context);
        myCookie = myCookie + "WLPJSESSIONID=" + ChatBubbleSingleton.getInstance().getWLPJSESSIONID() + ";" + "RPCOOKIE=" + getRpCookie() + ";amlbCookie=" + getAmlbCookie() + ";";

        D.w("myCookie           = " + myCookie);
        D.w("myCookie +rp       = " + myCookie + "RPCOOKIE=" + getRpCookie() + ";");
        D.w("myCookie +rp +amlb = " + myCookie + "RPCOOKIE=" + getRpCookie() + ";amlbCookie=" + getAmlbCookie() + ";");

        Observable<String> observable =
                RetrofitCall.getInstance().logOutChatMyCookie(myCookie, contactId, sessionKey, email, "mCare")
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    public Observable<String> logOutChatWithSurveyCheckWLPJSESSIONID(String contactId, String sessionKey, String email, boolean pSurveyEligible) {
        ChatBubbleSingleton.getInstance().setMinimized(false, true);
        ChatBubbleSingleton.getInstance().setMessagesCount(0);

        String myCookie = BaseService.createCookieStatic(context);
        myCookie = myCookie + "WLPJSESSIONID=" + ChatBubbleSingleton.getInstance().getWLPJSESSIONID() + ";" + "RPCOOKIE=" + getRpCookie() + ";amlbCookie=" + getAmlbCookie() + ";";

        D.w("myCookie           = " + myCookie);
        D.w("myCookie +rp       = " + myCookie + "RPCOOKIE=" + getRpCookie() + ";");
        D.w("myCookie +rp +amlb = " + myCookie + "RPCOOKIE=" + getRpCookie() + ";amlbCookie=" + getAmlbCookie() + ";");

        Observable<String> observable =
                RetrofitCall.getInstance().logOutChatWithSurvey(myCookie, contactId, sessionKey, email, "mCare", pSurveyEligible)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    Observable<String> sendMediaMessage(String contactId,
                                        String sessionKey,
                                        MultipartBody.Part file) {


        String myCookie  = "WLPJSESSIONID=" + ChatBubbleSingleton.getInstance().getWLPJSESSIONID() + ";" + "RPCOOKIE=" + getRpCookie() + ";amlbCookie=" + getAmlbCookie() + ";";


        Observable<String> observable =
                RetrofitCall.getInstance().sendMediaMessage(myCookie, contactId, sessionKey, file)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

    Observable<String> switchToPC(String contactId,
                                  String sessionKey,
                                  String userEmail,
                                  String message) {
        Observable<String> observable =
                RetrofitCall.getInstance().switchToPC(contactId, sessionKey, true, userEmail, message)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }

//    private void resetWLPSESSIONID() {
//        BaseService.setWlpjsessionid(null);
//    }

    Observable<FaqPOJO> getFAQJson() {
        Observable<FaqPOJO> observable =
                RetrofitCall.getInstance().getFAQJson()
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());

        return observable;
    }
}

package ro.vodafone.mcare.android.rest;

/**
 * Created by Victor Radulescu on 9/23/2016.
 */

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import ro.vodafone.mcare.android.BuildConfig;
import ro.vodafone.mcare.android.application.VodafoneController;
import ro.vodafone.mcare.android.client.Hosts;
import ro.vodafone.mcare.android.rest.convertors.LongDeserialize;
import ro.vodafone.mcare.android.rest.security.SSLUtils;
import ro.vodafone.mcare.android.service.BaseService;

//import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Victor on 3/14/2016.
 */
public class ServiceGenerator {

    public static final String UPDATE_REQUIERED_INTENT = "update_requiered";


    private static final String API_BASE_URL = Hosts.getHost();
    private static final String API_AUTO_URL = Hosts.getAutoHost();
    private static final String[] API_CERTIFICATES = Hosts.getHostCertificates();
    private static final String[] API_AUTO_CERTIFICATES = Hosts.getAutoHostCertificates();

    static final String HEADER_REFERER_KEY = "Referer";
    static final String HEADER_REFERER_VALUE = "newMcare";

    static final String MOBILE_APP_KEY = "mobileAPP";
    static final String MOBILE_APP_VALUE = "webview_android";

    static final String MOBILE_APP_VERSION_KEY = "mobileAPPversion";
    static final String MOBILE_APP_VERSION_VALUE = BuildConfig.VERSION_NAME;

    private static final String USER_AGENT_KEY = "User-Agent";
    private static final String USER_AGENT_VALUE = "Android Chrome";

    static final String VERSION_INTERCEPTOR_APP_VERSION_KEY = "clientVersion";
    static final String VERSION_INTERCEPTOR_APP_VERSION_VALUE = BuildConfig.VERSION_NAME;

    private static final String VERSION_INTERCEPTOR_OS_KEY = "clientOS";
    private static final String VERSION_INTERCEPTOR_OS_VALUE = "Android";

    // http://auth-portal-pet-ot.vodafone.ro/
    private static final int MIN_THREADS = 1;
    private static final int MAX_THREADS = 16;

    public static final int VERSION_INTERCEPTOR_ERROR_CODE = 426;

    private static Dispatcher httpDispatcher = new Dispatcher(new ThreadPoolExecutor(MIN_THREADS, MAX_THREADS, 60, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false)));


    public static IRetrofitMethods RETROFIT_SEND_COOKIE_GENERAL_TIMEOUT = createService(true).create(IRetrofitMethods.class);
    public static IRetrofitMethods RETROFIT_SEND_NO_COOKIE_GENERAL_TIMEOUT = createService(false).create(IRetrofitMethods.class);
    public static IRetrofitMethods RETROFIT_AUTO_HOST = createServiceAutoHost(false).create(IRetrofitMethods.class);

    private static <S> Retrofit createService(final boolean sendCookie, long timeOutInSeconds, String url, boolean isAuto) {
        if (url == null || url.isEmpty())
            url = API_BASE_URL;

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(LongDeserialize.gson))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(timeOutInSeconds, TimeUnit.SECONDS).dispatcher(httpDispatcher);
        httpClient.connectTimeout(timeOutInSeconds, TimeUnit.SECONDS).dispatcher(httpDispatcher);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String cookie = BaseService.createCookieStatic(VodafoneController.getInstance());

                Request originalRequest = chain.request();
                Request.Builder requestBuilder = originalRequest.newBuilder();

                if (sendCookie) {
                    requestBuilder.addHeader("Cookie", cookie);
                }

                requestBuilder.header(USER_AGENT_KEY, USER_AGENT_VALUE);
                requestBuilder.addHeader(HEADER_REFERER_KEY, HEADER_REFERER_VALUE);
                requestBuilder.addHeader(MOBILE_APP_KEY, MOBILE_APP_VALUE);
                requestBuilder.addHeader(MOBILE_APP_VERSION_KEY, MOBILE_APP_VERSION_VALUE);

                requestBuilder.removeHeader(VERSION_INTERCEPTOR_APP_VERSION_KEY);
                requestBuilder.removeHeader(VERSION_INTERCEPTOR_OS_KEY);
                requestBuilder.addHeader(VERSION_INTERCEPTOR_APP_VERSION_KEY, VERSION_INTERCEPTOR_APP_VERSION_VALUE);
                requestBuilder.addHeader(VERSION_INTERCEPTOR_OS_KEY, VERSION_INTERCEPTOR_OS_VALUE);


                Response response = chain.proceed(requestBuilder.build());

                if(response.code() == VERSION_INTERCEPTOR_ERROR_CODE)
                {
                    LocalBroadcastManager.getInstance(VodafoneController.getInstance()).sendBroadcast(new Intent(UPDATE_REQUIERED_INTENT));
                }

                return response;
            }
        });

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            //   set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            //   add your other interceptors …

            //    add logging as last interceptor
            httpClient.addInterceptor(logging);  // <-- this is the important line!
        }
        httpClient.retryOnConnectionFailure(true);

        if (isAuto) {
            if (API_AUTO_CERTIFICATES != null && API_AUTO_CERTIFICATES.length != 0)
                SSLUtils.pinCertificates(API_AUTO_URL, httpClient, API_AUTO_CERTIFICATES);
        } else {
            if (API_CERTIFICATES != null && API_CERTIFICATES.length != 0)
                SSLUtils.pinCertificates(API_BASE_URL, httpClient, API_CERTIFICATES);
        }

        retrofitBuilder.client(httpClient.build());

        return retrofitBuilder.build();
    }


    public static <S> Retrofit createService(final boolean sendCookie) {
        return createService(sendCookie, 45, null, false);
    }

    public static <S> Retrofit createService(final boolean sendCookie, long timeOutInSeconds) {
        return createService(sendCookie, timeOutInSeconds, null, false);
    }

    public static <S> Retrofit createServiceCustomUrl(final boolean sendCookie, @NonNull String customUrl) {

        if (customUrl == null || customUrl.isEmpty()) customUrl = "https://www.vodafone.ro";

        return createService(sendCookie, 45, customUrl, false);
    }

    public static <S> Retrofit createServiceAutoHost(final boolean sendCookie) {
        return createService(sendCookie, 45, API_AUTO_URL, true);
    }


}

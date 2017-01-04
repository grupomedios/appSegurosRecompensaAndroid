package com.grupomedios.dclub.segurosrecompensa.notifications;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grupomedios.dclub.segurosrecompensa.R;
import com.grupomedios.dclub.segurosrecompensa.notifications.model.ResponseNotifications;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Query;


public class OneSignalApiClient {
    // Production
    private static String API_URL = "https://onesignal.com/api/v1";

    private static OneSignalApiClientInterface mApiClientInterface;
    private static String KEY_USER_TOKEN = "keyUserToken";

    public static OneSignalApiClientInterface getServiceClient(Context context) {
        if (mApiClientInterface == null) {
            initApiClient(context);
        }

        return mApiClientInterface;
    }

    private static void initApiClient(final Context context) {
        Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy HH:mm:ss")
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Authorization", "Basic "+context.getString(R.string.onesignal_rest_api_key));
            }
        };


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setEndpoint(API_URL)
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.NONE).setLog(new AndroidLog("Retrofit"))
                .build();

        mApiClientInterface = restAdapter.create(OneSignalApiClientInterface.class);
    }

    public interface OneSignalApiClientInterface {
        @GET("/notifications")
        void getNotifications(@Query("app_id") String appId, @Query("limit") Integer limit, @Query("offset") Integer offset, Callback<ResponseNotifications> callback);
    }

}

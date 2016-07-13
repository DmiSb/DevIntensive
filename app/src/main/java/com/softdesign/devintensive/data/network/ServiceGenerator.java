package com.softdesign.devintensive.data.network;

import com.softdesign.devintensive.data.network.interceptors.HeaderInterceptor;
import com.softdesign.devintensive.utils.AppConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 */
public class ServiceGenerator {

    private static OkHttpClient.Builder sHttpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder sBuilder =
            new Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        sHttpClient.addInterceptor(new HeaderInterceptor());
        sHttpClient.addInterceptor(logging);

        Retrofit retrofit = sBuilder
                .client(sHttpClient.build())
                .build();
        return  retrofit.create(serviceClass);
    }

}

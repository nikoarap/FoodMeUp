package com.example.foodmeup.network;

import com.example.foodmeup.utils.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRequestClass {

    private static RequestInterceptor requestInterceptor = new RequestInterceptor();
    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    //creating an OkHttpClient with custom Interceptor for adding headers to requests automatically
    private static OkHttpClient setClientWithInterceptor() {
        httpClient.addInterceptor(requestInterceptor);
        httpClient.addInterceptor(logging);
        logging.level(HttpLoggingInterceptor.Level.BODY);
        return httpClient.build();
    }

    //retrofit instantiation
    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(setClientWithInterceptor())
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static APIHandlingService APIHandlingService = retrofit.create(APIHandlingService.class);

    public static APIHandlingService fetchApi() {
        return APIHandlingService;
    }

}

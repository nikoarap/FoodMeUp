package com.example.foodmeup.network;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

//custom interceptor class for adding headers to requests automatically
public class RequestInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        /*
        chain.request() returns original request that you can work with(modify, rewrite)
        */
        Request originalRequest = chain.request();
        Headers headers = new Headers.Builder()
                .add("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Request newRequest = originalRequest.newBuilder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded") //Adds a header with name and value.
                .cacheControl(CacheControl.FORCE_CACHE) // Sets this request's Cache-Control header, replacing any cache control headers already present.
                .headers(headers) //Removes all headers on this builder and adds headers.
                .build();
        /*
        chain.proceed(request) is the call which will initiate the HTTP work. This call invokes the
        request and returns the response as per the request.
        */
        return chain.proceed(newRequest);
    }
}
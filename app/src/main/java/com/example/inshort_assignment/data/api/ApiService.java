package com.example.inshort_assignment.data.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static final String API_KEY = "5a2ac0dd7c4b8506edc4f94bd81bdce9";
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    private static TmdbApi apiService;

    public static TmdbApi getApiService(){
        if(apiService == null){
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(TmdbApi.class);
        }
        return apiService;
    }
}

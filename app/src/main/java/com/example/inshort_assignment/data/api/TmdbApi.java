package com.example.inshort_assignment.data.api;

import com.example.inshort_assignment.data.database.entities.Movie;
import com.example.inshort_assignment.data.models.MovieResponse;

import retrofit2.Call;
import retrofit2.http.*;


public interface TmdbApi {

    @GET("trending/movie/week")
    Call<MovieResponse> getTrendingMovies(@Query("api_key") String apiKey,@Query("page") int page);

    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlayingMovies(@Query("api_key") String apiKey , @Query("page") int page);

    @GET("search/movie")
    Call<MovieResponse> searchMovies(@Query("api_key") String apiKey , @Query("query") String query , @Query("page") int page);

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

}

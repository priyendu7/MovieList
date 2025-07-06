package com.example.inshort_assignment.data.repository;

import android.util.Log;

import com.example.inshort_assignment.data.api.ApiService;
import com.example.inshort_assignment.data.api.TmdbApi;
import com.example.inshort_assignment.data.database.MovieDao;
import com.example.inshort_assignment.data.database.entities.BookmarkedMovie;
import com.example.inshort_assignment.data.database.entities.Movie;
import com.example.inshort_assignment.data.models.MovieResponse;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;
import retrofit2.Call;

public class MovieRepository {
    private static final String TAG = "MovieRepository";
    private TmdbApi api;
    private MovieDao dao;
    private ExecutorService executor;

    public MovieRepository(TmdbApi api , MovieDao movieDao){
        this.dao = movieDao;
        this.api = api;
        this.executor = Executors.newFixedThreadPool(4);
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(Exception error);
    }

    public void getTrendingMovies(boolean forceRefresh , RepositoryCallback<List<Movie>> callback){
        executor.execute(() -> {
            try{
                if(forceRefresh){
                    Call<MovieResponse> call = api.getTrendingMovies(ApiService.API_KEY , 1);
                    Response<MovieResponse> response = call.execute();

                    if(response.isSuccessful() && response.body() != null){
                        List<Movie> movies = response.body().getResults();
                        for(Movie movie : movies){
                            movie.setCategory("trending");
                            Log.d(TAG, "getTrendingMovies: poster path is "+movie.getPosterPath());
                            Log.d(TAG, "getTrendingMovies: vote average is "+movie.getVoteAverage());
                        }
                        dao.deleteMoviesByCategory("trending");
                        dao.insertMovies(movies);
                        callback.onSuccess(movies);
                    }
                    else{
                        callback.onError(new Exception("Failed to fetch Trending movies"));
                    }
                }
                else{
                    List<Movie> localMovies = dao.getMoviesByCategories("trending");
                    if(localMovies.isEmpty()){
                        getTrendingMovies(true , callback);
                    }
                    else{
                        callback.onSuccess(localMovies);
                    }
                }
            }
            catch (Exception e){
                List<Movie> localMovies = dao.getMoviesByCategories("trending");
                if (!localMovies.isEmpty()) {
                    callback.onSuccess(localMovies);
                } else {
                    callback.onError(e);
                }
            }
        });
    }

    public void getNowPlayingMovies(boolean forceRefresh, RepositoryCallback<List<Movie>> callback) {
        executor.execute(() -> {
            try {
                if (forceRefresh) {
                    Call<MovieResponse> call = api.getNowPlayingMovies(ApiService.API_KEY, 1);
                    Response<MovieResponse> response = call.execute();

                    if (response.isSuccessful() && response.body() != null) {
                        List<Movie> movies = response.body().getResults();
                        for (Movie movie : movies) {
                            movie.setCategory("now_playing");
                        }
                        dao.deleteMoviesByCategory("now_playing");
                        dao.insertMovies(movies);
                        callback.onSuccess(movies);
                    } else {
                        callback.onError(new Exception("Failed to fetch now playing movies"));
                    }
                } else {
                    List<Movie> localMovies = dao.getMoviesByCategories("now_playing");
                    if (localMovies.isEmpty()) {
                        getNowPlayingMovies(true, callback);
                    } else {
                        callback.onSuccess(localMovies);
                    }
                }
            } catch (Exception e) {
                List<Movie> localMovies = dao.getMoviesByCategories("now_playing");
                if (!localMovies.isEmpty()) {
                    callback.onSuccess(localMovies);
                } else {
                    callback.onError(e);
                }
            }
        });
    }

    public void searchMovies(String query, RepositoryCallback<List<Movie>> callback) {
        executor.execute(() -> {
            try {
                Call<MovieResponse> call = api.searchMovies(ApiService.API_KEY, query, 1);
                Response<MovieResponse> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    for (Movie movie : movies) {
                        movie.setCategory("search");
                    }
                    dao.insertMovies(movies);
                    callback.onSuccess(movies);
                } else {
                    callback.onError(new Exception("Search failed"));
                }
            } catch (Exception e) {
                List<Movie> localMovies = dao.searchMovies(query);
                if (!localMovies.isEmpty()) {
                    callback.onSuccess(localMovies);
                } else {
                    callback.onError(e);
                }
            }
        });
    }

    public void getMovieDetails(int movieId , RepositoryCallback<Movie> callback){
        executor.execute(() ->{
            try{
                Call<Movie> call = api.getMovieDetails(movieId,ApiService.API_KEY);
                Response<Movie> response = call.execute();

                if(response.isSuccessful() && response.body() != null){
                    callback.onSuccess(response.body());
                }
                else{
                    callback.onError(new Exception("Failed to fetch movie details"));
                }
            }
            catch (Exception e){
                callback.onError(e);
            }
        });
    }

    public void bookmarkMovie(Movie movie , RepositoryCallback<Void> callback){
        executor.execute(() ->{
            try{
                BookmarkedMovie bookmarkedMovie = new BookmarkedMovie(movie.getId() , movie.getTitle() , movie.getOverview() , movie.getPosterPath() , movie.getBackdropPath() , movie.getReleaseDate() , movie.getVoteAverage());
                dao.bookmarkMovie(bookmarkedMovie);
                callback.onSuccess(null);
            }
            catch (Exception e){
                callback.onError(e);
            }
        });
    }

    public void removeBookmark(int movieId, RepositoryCallback<Void> callback) {
        executor.execute(() -> {
            try {
                dao.removeBookmark(movieId);
                callback.onSuccess(null);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void isBookmarked(int movieId, RepositoryCallback<Boolean> callback) {
        executor.execute(() -> {
            try {
                boolean isBookmarked = dao.isBookmarked(movieId);
                callback.onSuccess(isBookmarked);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void getBookmarkedMovies(RepositoryCallback<List<BookmarkedMovie>> callback) {
        executor.execute(() -> {
            try {
                List<BookmarkedMovie> movies = dao.getBookmarkedMovies();
                callback.onSuccess(movies);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
}

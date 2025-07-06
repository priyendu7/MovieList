package com.example.inshort_assignment.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.inshort_assignment.data.database.entities.Movie;
import com.example.inshort_assignment.data.repository.MovieRepository;

import java.util.List;

public class HomeViewModel  extends ViewModel {
    private MutableLiveData<List<Movie>> trendingMovies;
    private MutableLiveData<List<Movie>> nowPlayingMovies;
    private MutableLiveData<Boolean> loading;
    private MutableLiveData<String> error;
    private MovieRepository repository;

    public HomeViewModel(MovieRepository repository) {
        this.repository = repository;
        this.trendingMovies = new MutableLiveData<>();
        this.nowPlayingMovies = new MutableLiveData<>();
        this.loading = new MutableLiveData<>();
        this.error = new MutableLiveData<>();
    }

    public LiveData<List<Movie>> getTrendingMovies() {
        return trendingMovies;
    }

    public LiveData<List<Movie>> getNowPlayingMovies() {
        return nowPlayingMovies;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadMovies(boolean forceRefresh) {
        loading.setValue(true);

        repository.getTrendingMovies(forceRefresh, new MovieRepository.RepositoryCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> result) {
                trendingMovies.postValue(result);
                loadNowPlayingMovies(forceRefresh);
            }

            @Override
            public void onError(Exception error) {
                HomeViewModel.this.error.postValue(error.getMessage());
                loading.postValue(false);
            }
        });
    }

    private void loadNowPlayingMovies(boolean forceRefresh) {
        repository.getNowPlayingMovies(forceRefresh, new MovieRepository.RepositoryCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> result) {
                nowPlayingMovies.postValue(result);
                loading.postValue(false);
            }

            @Override
            public void onError(Exception error) {
                HomeViewModel.this.error.postValue(error.getMessage());
                loading.postValue(false);
            }
        });
    }
}

package com.example.inshort_assignment.ui.search;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.inshort_assignment.data.database.entities.Movie;
import com.example.inshort_assignment.data.repository.MovieRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {
    private MutableLiveData<List<Movie>> searchResults;
    private MutableLiveData<Boolean> loading;
    private MutableLiveData<String> error;
    private MovieRepository repository;
    private Handler searchHandler;
    private Runnable searchRunnable;

    public SearchViewModel(MovieRepository repository) {
        this.repository = repository;
        this.searchResults = new MutableLiveData<>();
        this.loading = new MutableLiveData<>();
        this.error = new MutableLiveData<>();
        this.searchHandler = new Handler(Looper.getMainLooper());
    }

    public LiveData<List<Movie>> getSearchResults() {
        return searchResults;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void searchMovies(String query) {
        // Cancel previous search
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }

        if (query.trim().isEmpty()) {
            searchResults.setValue(new ArrayList<>());
            return;
        }

        // Create new search runnable with debounce
        searchRunnable = () -> {
            loading.setValue(true);

            repository.searchMovies(query.trim(), new MovieRepository.RepositoryCallback<List<Movie>>() {
                @Override
                public void onSuccess(List<Movie> result) {
                    searchResults.postValue(result);
                    loading.postValue(false);
                }

                @Override
                public void onError(Exception error) {
                    SearchViewModel.this.error.postValue(error.getMessage());
                    loading.postValue(false);
                }
            });
        };

        // Execute search with 500ms delay
        searchHandler.postDelayed(searchRunnable, 500);
    }
}

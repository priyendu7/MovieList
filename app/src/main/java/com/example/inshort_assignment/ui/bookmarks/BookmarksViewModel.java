package com.example.inshort_assignment.ui.bookmarks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.inshort_assignment.data.database.entities.BookmarkedMovie;
import com.example.inshort_assignment.data.repository.MovieRepository;

import java.util.List;

public class BookmarksViewModel extends ViewModel {
    private MutableLiveData<List<BookmarkedMovie>> bookmarkedMovies;
    private MutableLiveData<Boolean> loading;
    private MutableLiveData<String> error;
    private MovieRepository repository;

    public BookmarksViewModel(MovieRepository repository) {
        this.repository = repository;
        this.bookmarkedMovies = new MutableLiveData<>();
        this.loading = new MutableLiveData<>();
        this.error = new MutableLiveData<>();
    }

    public LiveData<List<BookmarkedMovie>> getBookmarkedMovies() {
        return bookmarkedMovies;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadBookmarks() {
        //loading.setValue(true);

        repository.getBookmarkedMovies(new MovieRepository.RepositoryCallback<List<BookmarkedMovie>>() {
            @Override
            public void onSuccess(List<BookmarkedMovie> result) {
                bookmarkedMovies.postValue(result);
                loading.postValue(false);
            }

            @Override
            public void onError(Exception error) {
                BookmarksViewModel.this.error.postValue(error.getMessage());
                loading.postValue(false);
            }
        });
    }

    public void removeBookmark(int movieId) {
        repository.removeBookmark(movieId, new MovieRepository.RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                loadBookmarks();
            }

            @Override
            public void onError(Exception error) {
                BookmarksViewModel.this.error.postValue(error.getMessage());
            }
        });
    }
}
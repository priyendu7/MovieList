package com.example.inshort_assignment.ui.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.inshort_assignment.data.database.entities.Movie;
import com.example.inshort_assignment.data.repository.MovieRepository;

public class MovieDetailsViewModel extends ViewModel {
    private MutableLiveData<Movie> movieDetails;
    private MutableLiveData<Boolean> bookmarkStatus;
    private MutableLiveData<Boolean> loading;
    private MutableLiveData<String> error;
    private MovieRepository repository;

    public MovieDetailsViewModel(MovieRepository repository) {
        this.repository = repository;
        this.movieDetails = new MutableLiveData<>();
        this.bookmarkStatus = new MutableLiveData<>();
        this.loading = new MutableLiveData<>();
        this.error = new MutableLiveData<>();
    }

    public LiveData<Movie> getMovieDetails() {
        return movieDetails;
    }

    public LiveData<Boolean> getBookmarkStatus() {
        return bookmarkStatus;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadMovieDetails(int movieId) {
        loading.setValue(true);

        repository.getMovieDetails(movieId, new MovieRepository.RepositoryCallback<Movie>() {
            @Override
            public void onSuccess(Movie result) {
                movieDetails.postValue(result);
                loading.postValue(false);
            }

            @Override
            public void onError(Exception error) {
                MovieDetailsViewModel.this.error.postValue(error.getMessage());
                loading.postValue(false);
            }
        });
    }

    public void checkBookmarkStatus(int movieId) {
        repository.isBookmarked(movieId, new MovieRepository.RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                bookmarkStatus.postValue(result);
            }

            @Override
            public void onError(Exception error) {
                // Handle error silently for bookmark status
            }
        });
    }

    public void toggleBookmark(Movie movie) {
        Boolean currentStatus = bookmarkStatus.getValue();
        if (currentStatus != null && currentStatus) {
            // Remove bookmark
            repository.removeBookmark(movie.getId(), new MovieRepository.RepositoryCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    bookmarkStatus.postValue(false);
                }

                @Override
                public void onError(Exception error) {
                    MovieDetailsViewModel.this.error.postValue(error.getMessage());
                }
            });
        } else {
            // Add bookmark
            repository.bookmarkMovie(movie, new MovieRepository.RepositoryCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    bookmarkStatus.postValue(true);
                }

                @Override
                public void onError(Exception error) {
                    MovieDetailsViewModel.this.error.postValue(error.getMessage());
                }
            });
        }
    }
}

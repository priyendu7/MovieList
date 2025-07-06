package com.example.inshort_assignment.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.inshort_assignment.data.repository.MovieRepository;
import com.example.inshort_assignment.ui.bookmarks.BookmarksViewModel;
import com.example.inshort_assignment.ui.details.MovieDetailsViewModel;
import com.example.inshort_assignment.ui.home.HomeViewModel;
import com.example.inshort_assignment.ui.search.SearchViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private MovieRepository repository;

    public ViewModelFactory(MovieRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(repository);
        } else if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(repository);
        } else if (modelClass.isAssignableFrom(BookmarksViewModel.class)) {
            return (T) new BookmarksViewModel(repository);
        } else if (modelClass.isAssignableFrom(MovieDetailsViewModel.class)) {
            return (T) new MovieDetailsViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

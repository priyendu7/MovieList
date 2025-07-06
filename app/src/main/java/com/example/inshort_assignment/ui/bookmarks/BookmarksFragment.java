package com.example.inshort_assignment.ui.bookmarks;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inshort_assignment.R;
import com.example.inshort_assignment.data.api.ApiService;
import com.example.inshort_assignment.data.database.MovieDatabase;
import com.example.inshort_assignment.data.database.entities.BookmarkedMovie;
import com.example.inshort_assignment.data.repository.MovieRepository;
import com.example.inshort_assignment.ui.details.MovieDetailsActivity;
import com.example.inshort_assignment.viewmodel.ViewModelFactory;

public class BookmarksFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvNoBookmarks;

    private BookmarksViewModel viewModel;
    private BookmarksAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        recyclerView = view.findViewById(R.id.recycler_bookmarks);
        progressBar = view.findViewById(R.id.progress_bar);
        tvNoBookmarks = view.findViewById(R.id.tv_no_bookmarks);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupViewModel();
        observeViewModel();

        // Load bookmarks
        viewModel.loadBookmarks();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadBookmarks();
    }

    private void setupRecyclerView() {
        adapter = new BookmarksAdapter(this::navigateToMovieDetails, this::removeBookmark);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        MovieDatabase database = MovieDatabase.getDatabase(requireContext());
        MovieRepository repository = new MovieRepository(
                ApiService.getApiService(),
                database.movieDao()
        );

        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(BookmarksViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getBookmarkedMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                adapter.setMovies(movies);
                tvNoBookmarks.setVisibility(movies.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            if (loading != null) {
                progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMovieDetails(BookmarkedMovie movie) {
        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
        intent.putExtra("movie_id", movie.getMovieId());
        intent.putExtra("movie_title", movie.getTitle());
        startActivity(intent);
    }

    private void removeBookmark(BookmarkedMovie movie) {
        viewModel.removeBookmark(movie.getMovieId());
    }
}

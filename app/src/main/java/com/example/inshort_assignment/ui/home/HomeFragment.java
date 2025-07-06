package com.example.inshort_assignment.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.inshort_assignment.R;
import com.example.inshort_assignment.data.api.ApiService;
import com.example.inshort_assignment.data.database.MovieDatabase;
import com.example.inshort_assignment.data.database.entities.Movie;
import com.example.inshort_assignment.data.repository.MovieRepository;
import com.example.inshort_assignment.ui.details.MovieDetailsActivity;
import com.example.inshort_assignment.viewmodel.ViewModelFactory;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerTrending;
    private RecyclerView recyclerNowPlaying;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;

    private HomeViewModel viewModel;
    private MovieAdapter trendingAdapter;
    private MovieAdapter nowPlayingAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerTrending = view.findViewById(R.id.recycler_trending);
        recyclerNowPlaying = view.findViewById(R.id.recycler_now_playing);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        progressBar = view.findViewById(R.id.progress_bar);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerViews();
        setupViewModel();
        observeViewModel();

        // Load movies
        viewModel.loadMovies(false);

        // Swipe to refresh
        swipeRefresh.setOnRefreshListener(() -> viewModel.loadMovies(true));
    }

    private void setupRecyclerViews() {
        trendingAdapter = new MovieAdapter(this::navigateToMovieDetails);
        nowPlayingAdapter = new MovieAdapter(this::navigateToMovieDetails);

        recyclerTrending.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        recyclerTrending.setAdapter(trendingAdapter);

        recyclerNowPlaying.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        recyclerNowPlaying.setAdapter(nowPlayingAdapter);
    }

    private void setupViewModel() {
        MovieDatabase database = MovieDatabase.getDatabase(requireContext());
        MovieRepository repository = new MovieRepository(
                ApiService.getApiService(),
                database.movieDao()
        );

        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getTrendingMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                trendingAdapter.setMovies(movies);
            }
        });

        viewModel.getNowPlayingMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                nowPlayingAdapter.setMovies(movies);
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            if (loading != null) {
                progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
                swipeRefresh.setRefreshing(loading);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMovieDetails(Movie movie) {
        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
        intent.putExtra("movie_id", movie.getId());
        intent.putExtra("movie_title", movie.getTitle());
        startActivity(intent);
    }
}

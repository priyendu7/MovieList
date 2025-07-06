package com.example.inshort_assignment.ui.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inshort_assignment.R;
import com.example.inshort_assignment.data.api.ApiService;
import com.example.inshort_assignment.data.database.MovieDatabase;
import com.example.inshort_assignment.data.database.entities.Movie;
import com.example.inshort_assignment.data.repository.MovieRepository;
import com.example.inshort_assignment.ui.details.MovieDetailsActivity;
import com.example.inshort_assignment.ui.home.MovieAdapter;
import com.example.inshort_assignment.viewmodel.ViewModelFactory;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ProgressBar progressBar;
    private TextView tvNoResults;

    private SearchViewModel viewModel;
    private MovieAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recycler_search);
        searchView = view.findViewById(R.id.search_view);
        progressBar = view.findViewById(R.id.progress_bar);
        tvNoResults = view.findViewById(R.id.tv_no_results);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupViewModel();
        setupSearchView();
        observeViewModel();
    }
    @Override
    public void onResume() {
        super.onResume();

        if (getUserVisibleHint()) {
            focusSearchViewAndOpenKeyboard();
        }
    }

    private void setupRecyclerView() {
        adapter = new MovieAdapter(this::navigateToMovieDetails);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext() ,2));
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        MovieDatabase database = MovieDatabase.getDatabase(requireContext());
        MovieRepository repository = new MovieRepository(
                ApiService.getApiService(),
                database.movieDao()
        );

        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(SearchViewModel.class);
    }

    private void setupSearchView() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.searchMovies(newText);
                return true;
            }
        });
    }

    private void observeViewModel() {
        viewModel.getSearchResults().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null) {
                adapter.setMovies(movies);
                tvNoResults.setVisibility(movies.isEmpty() ? View.VISIBLE : View.GONE);
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

    private void navigateToMovieDetails(Movie movie) {
        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
        intent.putExtra("movie_id", movie.getId());
        intent.putExtra("movie_title", movie.getTitle());
        startActivity(intent);
    }

    private void focusSearchViewAndOpenKeyboard() {
        SearchView searchView = getView().findViewById(R.id.search_view);

        if (searchView != null) {
            searchView.setIconified(false);
            searchView.setFocusable(true);
            searchView.setFocusableInTouchMode(true);
            searchView.requestFocus();

            searchView.postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 100);
        }
    }
}

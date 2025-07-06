package com.example.inshort_assignment.ui.details;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.inshort_assignment.R;
import com.example.inshort_assignment.data.api.ApiService;
import com.example.inshort_assignment.data.database.MovieDatabase;
import com.example.inshort_assignment.data.database.entities.Movie;
import com.example.inshort_assignment.data.repository.MovieRepository;
import com.example.inshort_assignment.viewmodel.ViewModelFactory;

public class MovieDetailsActivity extends AppCompatActivity {
    private ImageView ivBackdrop;
    private ImageView ivPoster;
    private TextView tvTitle;
    private TextView tvOverview;
    private TextView tvRating;
    private TextView tvReleaseDate;
    private Button btnBookmark;
    private Button btnShare;
    private ProgressBar progressBar;

    private MovieDetailsViewModel viewModel;
    private int movieId;
    private String movieTitle;
    private Movie currentMovie;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Get movie data from intent
        movieId = getIntent().getIntExtra("movie_id", 0);
        movieTitle = getIntent().getStringExtra("movie_title");

        // Handle deep link
        handleDeepLink(getIntent());

        initViews();
        setupViewModel();
        setupClickListeners();
        observeViewModel();

        // Load movie details
        if (movieId > 0) {
            viewModel.loadMovieDetails(movieId);
            viewModel.checkBookmarkStatus(movieId);
        }

        swipeRefresh.setOnRefreshListener(() -> {
            viewModel.loadMovieDetails(movieId);
            viewModel.checkBookmarkStatus(movieId);
                }
        );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleDeepLink(intent);
    }

    private void handleDeepLink(Intent intent) {
        Uri data = intent.getData();
        if (data != null) {
            String movieIdStr = data.getQueryParameter("movie_id");
            if (movieIdStr != null) {
                movieId = Integer.parseInt(movieIdStr);
                movieTitle = data.getQueryParameter("movie_title");
            }
        }
    }

    private void initViews() {
        ivBackdrop = findViewById(R.id.iv_backdrop);
        ivPoster = findViewById(R.id.iv_poster);
        tvTitle = findViewById(R.id.tv_title);
        tvOverview = findViewById(R.id.tv_overview);
        tvRating = findViewById(R.id.tv_rating);
        tvReleaseDate = findViewById(R.id.tv_release_date);
        btnBookmark = findViewById(R.id.btn_bookmark);
        btnShare = findViewById(R.id.btn_share);
        progressBar = findViewById(R.id.progress_bar);
        swipeRefresh = findViewById(R.id.swipe_refresh);

        if (movieTitle != null) {
            setTitle(movieTitle);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViewModel() {
        MovieDatabase database = MovieDatabase.getDatabase(this);
        MovieRepository repository = new MovieRepository(
                ApiService.getApiService(),
                database.movieDao()
        );

        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(MovieDetailsViewModel.class);
    }

    private void setupClickListeners() {
        btnBookmark.setOnClickListener(v -> {
            if (currentMovie != null) {
                viewModel.toggleBookmark(currentMovie);
            }
        });

        btnShare.setOnClickListener(v -> {
            if (currentMovie != null) {
                shareMovie(currentMovie);
            }
        });
    }

    private void observeViewModel() {
        viewModel.getMovieDetails().observe(this, movie -> {
            if (movie != null) {
                currentMovie = movie;
                populateMovieDetails(movie);
            }
        });

        viewModel.getBookmarkStatus().observe(this, isBookmarked -> {
            if (isBookmarked != null) {
                btnBookmark.setText(isBookmarked ? "Remove Bookmark" : "Add Bookmark");
                btnBookmark.setBackgroundColor(
                        isBookmarked ? getColor(R.color.colorAccent) : getColor(R.color.colorPrimary)
                );
            }
        });

        viewModel.getLoading().observe(this, loading -> {
            if (loading != null) {
                progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
                swipeRefresh.setRefreshing(loading);
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateMovieDetails(Movie movie) {
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        tvRating.setText(String.format("Rating: %.1f/10", movie.getVoteAverage()));
        tvReleaseDate.setText(String.format("Release Date: %s", movie.getReleaseDate()));

        // Load backdrop image
        String backdropUrl = ApiService.IMAGE_BASE_URL + movie.getBackdropPath();
        Glide.with(this)
                .load(backdropUrl)
                .placeholder(R.drawable.placeholder_movie)
                .error(R.drawable.placeholder_movie)
                .into(ivBackdrop);

        // Load poster image
        String posterUrl = ApiService.IMAGE_BASE_URL + movie.getPosterPath();
        Glide.with(this)
                .load(posterUrl)
                .placeholder(R.drawable.placeholder_movie)
                .error(R.drawable.placeholder_movie)
                .into(ivPoster);
    }

    private void shareMovie(Movie movie) {
        String shareText = String.format(
                "Check out this movie: %s\n\n%s\n\nRating: %.1f/10\n\nOpen in app: %s",
                movie.getTitle(),
                movie.getOverview(),
                movie.getVoteAverage(),
                createDeepLink(movie)
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Movie Recommendation: " + movie.getTitle());

        startActivity(Intent.createChooser(shareIntent, "Share Movie"));
    }

    private String createDeepLink(Movie movie) {
        return String.format(
                "moviesdb://movie?movie_id=%d&movie_title=%s",
                movie.getId(),
                Uri.encode(movie.getTitle())
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

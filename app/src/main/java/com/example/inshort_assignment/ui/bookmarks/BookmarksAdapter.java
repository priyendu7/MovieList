package com.example.inshort_assignment.ui.bookmarks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inshort_assignment.R;
import com.example.inshort_assignment.data.api.ApiService;
import com.example.inshort_assignment.data.database.entities.BookmarkedMovie;

import java.util.ArrayList;
import java.util.List;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.BookmarkViewHolder> {
    private List<BookmarkedMovie> movies;
    private OnBookmarkClickListener listener;
    private OnBookmarkRemoveListener removeListener;

    public interface OnBookmarkClickListener {
        void onBookmarkClick(BookmarkedMovie movie);
    }

    public interface OnBookmarkRemoveListener {
        void onBookmarkRemove(BookmarkedMovie movie);
    }

    public BookmarksAdapter(OnBookmarkClickListener listener, OnBookmarkRemoveListener removeListener) {
        this.movies = new ArrayList<>();
        this.listener = listener;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bookmark, parent, false);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        BookmarkedMovie movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovies(List<BookmarkedMovie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    class BookmarkViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPoster;
        private TextView tvTitle;
        private TextView tvRating;
        private TextView tvReleaseDate;
        private ImageView ivRemove;

        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.iv_poster);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvReleaseDate = itemView.findViewById(R.id.tv_release_date);
            ivRemove = itemView.findViewById(R.id.iv_remove);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onBookmarkClick(movies.get(position));
                }
            });

            ivRemove.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && removeListener != null) {
                    removeListener.onBookmarkRemove(movies.get(position));
                }
            });
        }

        public void bind(BookmarkedMovie movie) {
            tvTitle.setText(movie.getTitle());
            tvRating.setText(String.format("%.1f", movie.getVoteAverage()));
            tvReleaseDate.setText(movie.getReleaseDate());

            String posterUrl = ApiService.IMAGE_BASE_URL + movie.getPosterPath();
            Glide.with(itemView.getContext())
                    .load(posterUrl)
                    .placeholder(R.drawable.placeholder_movie)
                    .error(R.drawable.placeholder_movie)
                    .into(ivPoster);
        }
    }
}

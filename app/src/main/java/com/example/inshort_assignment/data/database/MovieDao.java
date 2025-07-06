package com.example.inshort_assignment.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.inshort_assignment.data.database.entities.BookmarkedMovie;
import com.example.inshort_assignment.data.database.entities.Movie;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("select * from movies where category = :category")
    List<Movie> getMoviesByCategories(String category);

    @Query("select * from movies where title like '%' || :query || '%'")
    List<Movie> searchMovies(String query);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<Movie> movies);
    @Query("Delete from movies where category = :category")
    void deleteMoviesByCategory(String category);
    @Query("select * from bookmarked_movies order by bookmarkedAt desc")
    List<BookmarkedMovie> getBookmarkedMovies();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bookmarkMovie(BookmarkedMovie movie);
    @Query("delete from bookmarked_movies where movieId = :movieId")
    void removeBookmark(int movieId);

    @Query("select exists(select 1 from bookmarked_movies where movieId = :movieId)")
    boolean isBookmarked(int movieId);
}
